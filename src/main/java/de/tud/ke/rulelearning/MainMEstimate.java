package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.MEstimate;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;

import java.nio.file.Path;
import java.util.function.BiFunction;

public class MainMEstimate {

    public static String getDataSetName(final Path arffFilePath) {
        String fileName = arffFilePath.getFileName().toString().toLowerCase();
        fileName = fileName.endsWith(".arff") ? fileName.substring(0, fileName.length() - ".arff".length()) : fileName;
        return fileName.endsWith("-train") ? fileName.substring(0, fileName.length() - "-train".length()) : fileName;
    }

    private static Heuristic[] getHeuristics(final String dataSetName) {
        switch (dataSetName) {
            case "yeast":
                return new Heuristic[]{new MEstimate(58.24), new MEstimate(12.2), new MEstimate(19.28)};
            default:
                throw new RuntimeException("Unknown data set: " + dataSetName);
        }
    }

    private static double[] getThresholds(final String dataSetName) {
        switch (dataSetName) {
            case "yeast":
                return new double[]{0.217, 0.1127, 0.1586};
            default:
                throw new RuntimeException("Unknown data set: " + dataSetName);
        }
    }

    public static void main(final String... args) {
        BatchExperiment<BaseConfiguration> batchExperiment = new BatchExperiment<>(
                (baseConfiguration, args1) -> baseConfiguration, args);
        StoppingCriterionConfiguration.Builder configurationBuilder =
                new StoppingCriterionConfigurationBuilderFactory<>(StoppingCriterionConfiguration.Builder::new)
                        .create(batchExperiment.getConfiguration(), args);

        final BiFunction<String, RuleGenerationConfiguration, AbstractRuleGenerationLearner> ruleGenerationLearnerFactory =
                RandomForestRuleGenerationLearner::new;
        final BiFunction<String, StoppingCriterionConfiguration, StoppingCriterionLearner> stoppingCriterionLearnerFactory =
                StoppingCriterionLearner::new;

        String dataSetName = getDataSetName(configurationBuilder.getArffFilePath());
        Heuristic[] heuristics = getHeuristics(dataSetName);
        double[] thresholds = getThresholds(dataSetName);

        for (int i = 0; i < heuristics.length; i++) {
            configurationBuilder.setCoveringHeuristic(heuristics[i]);
            configurationBuilder.setStoppingCriterionThreshold(thresholds[i]);

            batchExperiment.addExperiment(sharedData -> new RuleGenerationExperiment(sharedData,
                    configurationBuilder.build(), ruleGenerationLearnerFactory, ""));
            batchExperiment.addExperiment(sharedData -> new StoppingCriterionExperiment(sharedData,
                    configurationBuilder.build(), stoppingCriterionLearnerFactory, ""));
        }

        batchExperiment.run();
    }

}
