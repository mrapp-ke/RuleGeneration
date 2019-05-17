package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.MEstimate;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;

import java.util.function.BiFunction;

public class MainMEstimateParameterTuning {

    public static void main(String... args) {
        BatchExperiment<BaseConfiguration> batchExperiment = new BatchExperiment<>(
                (baseConfiguration, args1) -> baseConfiguration, args);
        StoppingCriterionConfiguration.Builder configurationBuilder =
                new StoppingCriterionConfigurationBuilderFactory<>(StoppingCriterionConfiguration.Builder::new)
                        .create(batchExperiment.getConfiguration(), args);

        final BiFunction<String, RuleGenerationConfiguration, AbstractRuleGenerationLearner> learnerFactory1 =
                RandomForestRuleGenerationLearner::new;
        final BiFunction<String, StoppingCriterionConfiguration, StoppingCriterionLearner> learnerFactory2 =
                StoppingCriterionLearner::new;


        for (Heuristic heuristic : new MainCoveringMEstimate.HeuristicIterable()) {
            if (heuristic instanceof MEstimate && ((MEstimate) heuristic).getM() <= 4096) {
                configurationBuilder.setCoveringHeuristic(Provider.singleton(heuristic));
                batchExperiment.addExperiment(sharedData -> new RuleGenerationExperiment(sharedData,
                        configurationBuilder.build(), learnerFactory1, ""));

                for (double threshold : MainStoppingCriterionMEstimate.THRESHOLDS) {
                    configurationBuilder.setStoppingCriterionThreshold(Provider.singleton(threshold));
                    batchExperiment.addExperiment(sharedData -> new StoppingCriterionExperiment(sharedData,
                            configurationBuilder.build(), learnerFactory2, ""));
                }
            }
        }

        batchExperiment.run();
    }

}
