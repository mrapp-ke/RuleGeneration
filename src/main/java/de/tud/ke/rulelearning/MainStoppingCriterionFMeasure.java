package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;

import java.util.function.BiFunction;

public class MainStoppingCriterionFMeasure {

    public static void main(final String... args) {
        BatchExperiment<BaseConfiguration> batchExperiment = new BatchExperiment<>(
                (baseConfiguration, args1) -> baseConfiguration, args);
        StoppingCriterionConfiguration.Builder configurationBuilder =
                new StoppingCriterionConfigurationBuilderFactory<>(StoppingCriterionConfiguration.Builder::new)
                        .create(batchExperiment.getConfiguration(), args);

        final BiFunction<String, StoppingCriterionConfiguration, StoppingCriterionLearner> learnerFactory =
                StoppingCriterionLearner::new;

        for (Heuristic heuristic : new MainCoveringFMeasure.HeuristicIterable()) {
            for (double threshold : MainStoppingCriterionMEstimate.THRESHOLDS) {
                configurationBuilder.setCoveringHeuristic(heuristic);
                configurationBuilder.setStoppingCriterionThreshold(threshold);
                batchExperiment.addExperiment(sharedData -> new StoppingCriterionExperiment(sharedData,
                        configurationBuilder.build(), learnerFactory, ""));
            }
        }

        batchExperiment.run();
    }

}
