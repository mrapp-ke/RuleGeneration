package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class MainStoppingCriterionMEstimate {

    public static final List<Double> THRESHOLDS = Arrays.asList(1d, 0.95, 0.9, 0.85, 0.8, 0.75, 0.7, 0.65, 0.6, 0.55, 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05);

    public static void main(final String... args) {
        BatchExperiment<BaseConfiguration> batchExperiment = new BatchExperiment<>(
                (baseConfiguration, args1) -> baseConfiguration, args);
        StoppingCriterionConfiguration.Builder configurationBuilder =
                new StoppingCriterionConfigurationBuilderFactory<>(StoppingCriterionConfiguration.Builder::new)
                        .create(batchExperiment.getConfiguration(), args);

        final BiFunction<String, StoppingCriterionConfiguration, StoppingCriterionLearner> learnerFactory =
                StoppingCriterionLearner::new;

        for (Heuristic heuristic : new MainCoveringMEstimate.HeuristicIterable()) {
            for (double threshold : THRESHOLDS) {
                configurationBuilder.setCoveringHeuristic(heuristic);
                configurationBuilder.setStoppingCriterionThreshold(threshold);
                batchExperiment.addExperiment(sharedData -> new StoppingCriterionExperiment(sharedData,
                        configurationBuilder.build(), learnerFactory, ""));
            }
        }

        batchExperiment.run();
    }

}
