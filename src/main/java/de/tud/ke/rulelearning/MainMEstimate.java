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
            case "birds":
                return new Heuristic[]{new MEstimate(53.5), new MEstimate(18.62), new MEstimate(9)};
            case "cal500":
                return new Heuristic[]{new MEstimate(22), new MEstimate(11.12), new MEstimate(9.5)};
            case "emotions":
                return new Heuristic[]{new MEstimate(46), new MEstimate(15.12), new MEstimate(5.5)};
            case "enron":
                return new Heuristic[]{new MEstimate(52), new MEstimate(19), new MEstimate(9.5)};
            case "flags":
                return new Heuristic[]{new MEstimate(50), new MEstimate(17.12), new MEstimate(7.5)};
            case "genbase":
                return new Heuristic[]{new MEstimate(53.5), new MEstimate(18.62), new MEstimate(9)};
            case "medical":
                return new Heuristic[]{new MEstimate(53), new MEstimate(18.12), new MEstimate(8.5)};
            case "scene":
                return new Heuristic[]{new MEstimate(52), new MEstimate(17.12), new MEstimate(9)};
            case "yeast":
                return new Heuristic[]{new MEstimate(50), new MEstimate(18.12), new MEstimate(8.5)};
            default:
                throw new RuntimeException("Unknown data set: " + dataSetName);
        }
    }

    private static double[] getThresholds(final String dataSetName) {
        switch (dataSetName) {
            case "birds":
                return new double[]{0.53, 0.32, 0.51};
            case "cal500":
                return new double[]{0.56, 0.34, 0.41};
            case "emotions":
                return new double[]{0.55, 0.3, 0.49};
            case "enron":
                return new double[]{0.58, 0.34, 0.51};
            case "flags":
                return new double[]{0.55, 0.33, 0.52};
            case "genbase":
                return new double[]{0.49, 0.23, 0.41};
            case "medical":
                return new double[]{0.53, 0.29, 0.47};
            case "scene":
                return new double[]{0.54, 0.31, 0.47};
            case "yeast":
                return new double[]{0.58, 0.33, 0.51};
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
