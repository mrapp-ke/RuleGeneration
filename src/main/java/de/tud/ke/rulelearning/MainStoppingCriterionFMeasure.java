package de.tud.ke.rulelearning;

import de.mrapp.util.IteratorUtil;
import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class MainStoppingCriterionFMeasure {

    public static void main(final String... args) {
        BatchExperiment<BaseConfiguration> batchExperiment = new BatchExperiment<>(
                (baseConfiguration, args1) -> baseConfiguration, args);
        StoppingCriterionConfiguration.Builder configurationBuilder =
                StoppingCriterionConfigurationBuilderFactory.create(batchExperiment.getConfiguration(), args);

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
