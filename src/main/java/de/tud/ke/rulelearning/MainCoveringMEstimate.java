package de.tud.ke.rulelearning;

import de.mrapp.util.IteratorUtil;
import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.MEstimate;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class MainCoveringMEstimate {

    public static class HeuristicIterable implements Iterable<Heuristic> {

        @NotNull
        @Override
        public Iterator<Heuristic> iterator() {
            return new Iterator<Heuristic>() {

                private final Iterator<Double> mIterator = IteratorUtil.INSTANCE.createConcatenatedIterator(
                        Collections.singleton(0d).iterator(),
                        IntStream.range(0, 19).mapToDouble(i -> Math.pow(2, i)).iterator());

                @Override
                public boolean hasNext() {
                    return mIterator.hasNext();
                }

                @Override
                public Heuristic next() {
                    double m = mIterator.next();
                    return new MEstimate(m);
                }

            };
        }
    }

    public static void main(final String... args) {
        BatchExperiment<BaseConfiguration> batchExperiment = new BatchExperiment<>(
                (baseConfiguration, args1) -> baseConfiguration, args);
        RuleGenerationConfiguration.Builder configurationBuilder =
                new RuleGenerationConfigurationBuilderFactory<>(RuleGenerationConfiguration.Builder::new)
                        .create(batchExperiment.getConfiguration(), args);

        final BiFunction<String, RuleGenerationConfiguration, AbstractRuleGenerationLearner> learnerFactory =
                RandomForestRuleGenerationLearner::new;

        for (Heuristic heuristic : new HeuristicIterable()) {
            configurationBuilder.setCoveringHeuristic(heuristic);
            batchExperiment.addExperiment(sharedData -> new RuleGenerationExperiment(sharedData,
                    configurationBuilder.build(), learnerFactory, ""));
        }

        batchExperiment.run();
    }

}
