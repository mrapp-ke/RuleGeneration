package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.experiments.*;
import de.tud.ke.rulelearning.heuristics.MEstimate;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.RandomForestRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;

import java.nio.file.Path;
import java.util.function.BiFunction;

public class MainMEstimate {

    private static final double[][] BIRDS_THRESHOLDS = new double[][]{
            {0.5900, 0.2333, 0.2571},
            {0.6500, 0.1600, 0.1920},
            {0.7700, 0.1091, 0.1708},
            {0.8688, 0.1400, 0.2900},
            {0.6929, 0.2167, 0.1692},
            {0.6600, 0.0929, 0.2250},
            {0.4900, 0.1500, 0.2580},
            {0.5800, 0.1500, 0.3042},
            {0.6700, 0.1417, 0.2214},
            {0.7250, 0.1700, 0.2022}
    };

    private static final double[][] BIRDS_HEURISTICS = new double[][]{
            {14.80, 5.00, 5.86},
            {8.20, 10.20, 85.72},
            {18.40, 3.36, 35.00},
            {5.75, 2.50, 2.20},
            {18.57, 8.50, 6.92},
            {22.40, 7.57, 4.62},
            {12.80, 11.33, 10.52},
            {9.20, 4.62, 4.17},
            {17.20, 3.83, 15.14},
            {8.83, 2.60, 15.35}
    };

    private static final double[][] CAL500_THRESHOLDS = new double[][]{
            {0.4500, 0.0500, 0.5250},
            {0.4600, 0.0500, 0.5250},
            {0.4700, 0.0500, 0.5250},
            {0.4500, 0.0500, 0.5250},
            {0.4800, 0.0500, 0.5250},
            {0.4400, 0.0500, 0.5250},
            {0.4800, 0.0500, 0.5250},
            {0.4600, 0.0500, 0.5250},
            {0.4600, 0.0500, 0.5250},
            {0.4400, 0.0500, 0.5250}
    };

    private static final double[][] CAL500_HEURISTICS = new double[][]{
            {2099.20, 909.60, 585.07},
            {588.80, 813.33, 585.07},
            {601.60, 459.50, 585.07},
            {934.40, 238.40, 585.07},
            {332.80, 847.20, 585.07},
            {1587.20, 340.80, 585.07},
            {1049.60, 848.80, 585.07},
            {1382.40, 68.00, 585.07},
            {1638.40, 522.00, 585.07},
            {1779.20, 4.40, 585.07}
    };

    private static final double[][] EMOTIONS_THRESHOLDS = new double[][]{
            {0.6100, 0.4250, 0.4278},
            {0.5300, 0.3500, 0.3944},
            {0.5700, 0.4286, 0.4500},
            {0.5800, 0.4143, 0.5500},
            {0.7083, 0.4000, 0.4556},
            {0.6083, 0.3167, 0.5611},
            {0.5500, 0.3786, 0.4444},
            {0.5000, 0.4000, 0.3773},
            {0.6000, 0.3286, 0.3833},
            {0.6300, 0.3750, 0.4150}
    };

    private static final double[][] EMOTIONS_HEURISTICS = new double[][]{
            {48.80, 16.67, 37.44},
            {136.00, 15.80, 20.56},
            {64.40, 24.86, 36.86},
            {112.00, 15.29, 34.00},
            {216.00, 12.40, 111.33},
            {54.67, 19.67, 51.33},
            {156.80, 9.43, 156.89},
            {64.00, 41.00, 48.73},
            {128.40, 22.43, 39.00},
            {92.80, 36.00, 40.40}
    };

    private static final double[][] ENRON_THRESHOLDS = new double[][]{
            {0.4000, 0.1400, 0.3071},
            {0.2300, 0.1250, 0.2917},
            {0.1700, 0.0900, 0.2583},
            {0.1200, 0.0700, 0.2071},
            {0.3600, 0.1000, 0.2000},
            {0.2900, 0.1250, 0.3500},
            {0.4500, 0.1250, 0.3227},
            {0.3300, 0.1100, 0.2071},
            {0.3500, 0.1333, 0.3676},
            {0.4600, 0.1500, 0.2250}
    };

    private static final double[][] ENRON_HEURISTICS = new double[][]{
            {14.40, 6.20, 3.14},
            {14.40, 6.67, 9.50},
            {19.20, 7.40, 5.33},
            {10.60, 5.80, 9.00},
            {16.00, 6.00, 10.00},
            {10.40, 8.67, 14.80},
            {4.60, 3.50, 9.64},
            {10.60, 4.60, 4.86},
            {11.20, 4.17, 65.06},
            {16.00, 3.00, 3.67}
    };

    private static final double[][] FLAGS_THRESHOLDS = new double[][]{
            {0.4643, 0.4318, 0.6955},
            {0.3929, 0.4722, 0.5893},
            {0.3900, 0.4100, 0.4870},
            {0.4350, 0.3750, 0.3952},
            {0.5000, 0.4286, 0.2776},
            {0.5500, 0.4900, 0.2237},
            {0.4600, 0.3875, 0.4650},
            {0.2900, 0.3900, 0.3857},
            {0.4562, 0.3286, 0.4152},
            {0.4818, 0.3367, 0.3250}
    };

    private static final double[][] FLAGS_HEURISTICS = new double[][]{
            {1100.00, 701.27, 36.23},
            {96.00, 64.89, 209.16},
            {64.00, 58.40, 28.52},
            {848.00, 792.20, 769.24},
            {21.80, 16.29, 454.16},
            {25.80, 29.80, 1887.26},
            {70.80, 24.38, 26.05},
            {56.20, 15.30, 53.45},
            {466.00, 34.29, 22.45},
            {1387.82, 1103.13, 378.21}
    };

    private static final double[][] GENBASE_THRESHOLDS = new double[][]{
            {0.9222, 0.8834, 0.8834},
            {0.9265, 0.9204, 0.9204},
            {0.9132, 0.9040, 0.9040},
            {0.9438, 0.9416, 0.9416},
            {0.9060, 0.9060, 0.9060},
            {0.9353, 0.9353, 0.9353},
            {0.9259, 0.9259, 0.9259},
            {0.9128, 0.9128, 0.9128},
            {0.9320, 0.9320, 0.9070},
            {0.9312, 0.9263, 0.9217}
    };

    private static final double[][] GENBASE_HEURISTICS = new double[][]{
            {395.27, 393.86, 393.86},
            {350.85, 341.46, 341.46},
            {224.91, 263.90, 263.90},
            {565.02, 574.24, 574.24},
            {494.31, 494.31, 494.31},
            {434.56, 434.56, 434.56},
            {535.62, 535.62, 535.62},
            {430.10, 430.10, 430.10},
            {489.80, 489.80, 495.87},
            {593.48, 580.35, 553.81}
    };

    private static final double[][] MEDICAL_THRESHOLDS = new double[][]{
            {0.5400, 0.3929, 0.4889},
            {0.4600, 0.3333, 0.3333},
            {0.4700, 0.4600, 0.3687},
            {0.4500, 0.3857, 0.3375},
            {0.3400, 0.3900, 0.3786},
            {0.4300, 0.3300, 0.3500},
            {0.4100, 0.4000, 0.4250},
            {0.4500, 0.3700, 0.4071},
            {0.5000, 0.4000, 0.4818},
            {0.4900, 0.3500, 0.3571}
    };

    private static final double[][] MEDICAL_HEURISTICS = new double[][]{
            {8.80, 7.43, 6.00},
            {32.00, 5.67, 6.67},
            {10.40, 10.00, 13.00},
            {7.20, 6.86, 8.00},
            {10.40, 6.40, 8.57},
            {35.20, 32.00, 24.57},
            {14.40, 4.80, 7.67},
            {7.20, 5.60, 5.71},
            {20.80, 8.57, 6.91},
            {9.60, 7.25, 6.86}
    };

    private static final double[][] SCENE_THRESHOLDS = new double[][]{
            {0.5200, 0.3300, 0.3917},
            {0.4700, 0.2900, 0.4500},
            {0.5100, 0.3300, 0.4167},
            {0.5300, 0.3000, 0.4700},
            {0.5300, 0.3000, 0.3944},
            {0.5300, 0.2900, 0.4333},
            {0.5500, 0.2900, 0.3778},
            {0.5100, 0.3167, 0.4000},
            {0.4400, 0.3400, 0.4000},
            {0.5000, 0.3357, 0.3625}
    };

    private static final double[][] SCENE_HEURISTICS = new double[][]{
            {19.20, 8.80, 9.83},
            {30.40, 9.20, 16.00},
            {12.40, 10.80, 12.00},
            {6.00, 4.20, 3.60},
            {15.20, 5.71, 12.89},
            {9.60, 4.00, 9.67},
            {14.80, 10.00, 12.67},
            {13.60, 10.67, 6.80},
            {24.00, 12.80, 16.00},
            {28.00, 9.43, 15.50}
    };

    private static final double[][] YEAST_THRESHOLDS = new double[][]{
            {0.2000, 0.1100, 0.1700},
            {0.2200, 0.0900, 0.1571},
            {0.2300, 0.1400, 0.1750},
            {0.2100, 0.1200, 0.1500},
            {0.2100, 0.1200, 0.1687},
            {0.2000, 0.1000, 0.1417},
            {0.2300, 0.1083, 0.1600},
            {0.2400, 0.1100, 0.1800},
            {0.2200, 0.1100, 0.1417},
            {0.2100, 0.1200, 0.1500}
    };

    private static final double[][] YEAST_HEURISTICS = new double[][]{
            {57.60, 15.20, 25.60},
            {51.20, 8.00, 17.43},
            {48.00, 8.80, 21.33},
            {64.00, 10.00, 31.43},
            {83.20, 21.60, 15.75},
            {64.00, 18.40, 29.33},
            {44.80, 20.33, 10.20},
            {48.00, 6.80, 5.00},
            {41.60, 4.80, 12.00},
            {80.00, 6.40, 20.67}
    };

    public static String getDataSetName(final Path arffFilePath) {
        String fileName = arffFilePath.getFileName().toString().toLowerCase();
        fileName = fileName.endsWith(".arff") ? fileName.substring(0, fileName.length() - ".arff".length()) : fileName;
        return fileName.endsWith("-train") ? fileName.substring(0, fileName.length() - "-train".length()) : fileName;
    }

    private static double[][] getHeuristics(final String dataSetName) {
        switch (dataSetName) {
            case "birds":
                return BIRDS_HEURISTICS;
            case "cal500":
                return CAL500_HEURISTICS;
            case "emotions":
                return EMOTIONS_HEURISTICS;
            case "enron":
                return ENRON_HEURISTICS;
            case "flags":
                return FLAGS_HEURISTICS;
            case "genbase":
                return GENBASE_HEURISTICS;
            case "medical":
                return MEDICAL_HEURISTICS;
            case "scene":
                return SCENE_HEURISTICS;
            case "yeast":
                return YEAST_HEURISTICS;
            default:
                throw new RuntimeException("Unknown data set: " + dataSetName);
        }
    }

    private static double[][] getThresholds(final String dataSetName) {
        switch (dataSetName) {
            case "birds":
                return BIRDS_THRESHOLDS;
            case "cal500":
                return CAL500_THRESHOLDS;
            case "emotions":
                return EMOTIONS_THRESHOLDS;
            case "enron":
                return ENRON_THRESHOLDS;
            case "flags":
                return FLAGS_THRESHOLDS;
            case "genbase":
                return GENBASE_THRESHOLDS;
            case "medical":
                return MEDICAL_THRESHOLDS;
            case "scene":
                return SCENE_THRESHOLDS;
            case "yeast":
                return YEAST_THRESHOLDS;
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
        final double[][] heuristics = getHeuristics(dataSetName);
        final double[][] thresholds = getThresholds(dataSetName);

        for (int i = 0; i < heuristics[0].length; i++) {
            final int index = i;
            configurationBuilder.setCoveringHeuristic(fold -> new MEstimate(heuristics[fold][index]));
            configurationBuilder.setStoppingCriterionThreshold(fold -> thresholds[fold][index]);

            batchExperiment.addExperiment(sharedData -> new RuleGenerationExperiment(sharedData,
                    configurationBuilder.build(), ruleGenerationLearnerFactory, ""));
            batchExperiment.addExperiment(sharedData -> new StoppingCriterionExperiment(sharedData,
                    configurationBuilder.build(), stoppingCriterionLearnerFactory, ""));
        }

        batchExperiment.run();
    }

}
