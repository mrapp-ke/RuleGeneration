package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;

import java.util.function.BiFunction;

public class MainFMeasure {

    private static Heuristic[] getHeuristics(final String dataSetName) {
        switch (dataSetName) {
            case "birds":
                return new Heuristic[]{new FMeasure(0.69), new FMeasure(0.42), new FMeasure(0.41)};
            case "cal500":
                return new Heuristic[]{new FMeasure(0.5), new FMeasure(0.34), new FMeasure(0.48)};
            case "emotions":
                return new Heuristic[]{new FMeasure(0.65), new FMeasure(0.37), new FMeasure(0.38)};
            case "enron":
                return new Heuristic[]{new FMeasure(0.74), new FMeasure(0.42), new FMeasure(0.48)};
            case "flags":
                return new Heuristic[]{new FMeasure(0.63), new FMeasure(0.33), new FMeasure(0.38)};
            case "genbase":
                return new Heuristic[]{new FMeasure(0.67), new FMeasure(0.34), new FMeasure(0.41)};
            case "medical":
                return new Heuristic[]{new FMeasure(0.67), new FMeasure(0.34), new FMeasure(0.41)};
            case "scene":
                return new Heuristic[]{new FMeasure(0.72), new FMeasure(0.39), new FMeasure(0.46)};
            case "yeast":
                return new Heuristic[]{new FMeasure(0.69), new FMeasure(0.39), new FMeasure(0.46)};
            default:
                throw new RuntimeException("Unknown data set: " + dataSetName);
        }
    }

    private static double[] getThresholds(final String dataSetName) {
        switch (dataSetName) {
            case "birds":
                return new double[]{0.36, 0.3, 0.45};
            case "cal500":
                return new double[]{0.44, 0.32, 0.35};
            case "emotions":
                return new double[]{0.41, 0.29, 0.45};
            case "enron":
                return new double[]{0.44, 0.32, 0.44};
            case "flags":
                return new double[]{0.44, 0.31, 0.46};
            case "genbase":
                return new double[]{0.34, 0.2, 0.35};
            case "medical":
                return new double[]{0.4, 0.26, 0.41};
            case "scene":
                return new double[]{0.41, 0.28, 0.43};
            case "yeast":
                return new double[]{0.46, 0.32, 0.46};
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

        String dataSetName = MainMEstimate.getDataSetName(configurationBuilder.getArffFilePath());
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
