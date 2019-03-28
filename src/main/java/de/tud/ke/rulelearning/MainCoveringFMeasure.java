package de.tud.ke.rulelearning;

import de.mrapp.util.IteratorUtil;
import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainCoveringFMeasure {

    public static class HeuristicIterable implements Iterable<Heuristic> {

        @NotNull
        @Override
        public Iterator<Heuristic> iterator() {
            return new Iterator<Heuristic>() {

                private final Iterator<Double> betaIterator =
                        IteratorUtil.INSTANCE.createConcatenatedIterator(
                                Collections.singleton(0d).iterator(),
                                IteratorUtil.INSTANCE.createConcatenatedIterator(
                                        Stream.of(3d, 2.5d, 2d, 1.5d, 1d, 0.5d, 0d).map(i -> 1d - Math.pow(2, i) / 10d).iterator(),
                                        IteratorUtil.INSTANCE.createConcatenatedIterator(
                                                IntStream.range(0, 11).mapToDouble(i -> Math.pow(2, i)).iterator(),
                                                Collections.singleton(Double.POSITIVE_INFINITY).iterator()
                                        )
                                )
                        );

                @Override
                public boolean hasNext() {
                    return betaIterator.hasNext();
                }

                @Override
                public Heuristic next() {
                    double beta = betaIterator.next();

                    if (beta != Double.POSITIVE_INFINITY) {
                        beta = BigDecimal.valueOf(beta).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }

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
            System.out.println(((FMeasure) heuristic).getBeta());
            configurationBuilder.setCoveringHeuristic(heuristic);
            batchExperiment.addExperiment(sharedData -> new RuleGenerationExperiment(sharedData,
                    configurationBuilder.build(), learnerFactory, ""));
        }

        //batchExperiment.run();
    }

}
