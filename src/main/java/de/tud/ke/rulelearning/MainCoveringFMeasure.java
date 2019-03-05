package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.evaluation.DefaultAggregation;
import de.tud.ke.rulelearning.learner.evaluation.MicroAveraging;
import de.tud.ke.rulelearning.learner.evaluation.MultiLabelEvaluation;
import de.tud.ke.rulelearning.learner.evaluation.PartialPredictionStrategy;
import de.tud.ke.rulelearning.util.IteratorUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class MainCoveringFMeasure {

    private static class HeuristicIterable implements Iterable<Heuristic> {

        @NotNull
        @Override
        public Iterator<Heuristic> iterator() {
            return new Iterator<Heuristic>() {

                private final Iterator<Double> betaIterator = IteratorUtil.concatIterators(IteratorUtil.concatIterators(
                        Arrays.asList(0.0d, 0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d, 1.0d).iterator(),
                        IntStream.range(0, 8).mapToDouble(i -> Math.pow(2, i)).iterator()),
                        Collections.singleton(Double.POSITIVE_INFINITY).iterator());

                @Override
                public boolean hasNext() {
                    return betaIterator.hasNext();
                }

                @Override
                public Heuristic next() {
                    double beta = betaIterator.next();
                    return new FMeasure(beta);
                }

            };
        }
    }

    public static void main(final String... args) {
        BatchExperiment<BaseConfiguration> batchExperiment = new BatchExperiment<>(
                (baseConfiguration, args1) -> baseConfiguration, args);
        RuleGenerationConfiguration.Builder configurationBuilder =
                RuleGenerationConfigurationBuilderFactory.create(batchExperiment.getConfiguration(), args);

        final BiFunction<String, RuleGenerationConfiguration, AbstractRuleGenerationLearner> learnerFactory =
                RandomForestRuleGenerationLearner::new;

        for (Heuristic heuristic : new HeuristicIterable()) {
            MultiLabelEvaluation coveringEvaluation = new MultiLabelEvaluation(heuristic,
                    new PartialPredictionStrategy(), new MicroAveraging(new DefaultAggregation()));
            configurationBuilder.setCoveringEvaluation(coveringEvaluation);
            batchExperiment.addExperiment(sharedData -> new RuleGenerationExperiment(sharedData,
                    configurationBuilder.build(), learnerFactory, ""));
        }

        batchExperiment.run();
    }

}