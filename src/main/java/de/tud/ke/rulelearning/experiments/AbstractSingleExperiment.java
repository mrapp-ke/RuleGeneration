package de.tud.ke.rulelearning.experiments;

import de.tud.ke.mlc.common.out.EvaluationCsvPrinter;
import de.tud.ke.mlc.common.out.EvaluationCsvPrinter.EvaluationResult;
import de.tud.ke.rulelearning.learner.AbstractMultiLabelLearner;
import de.tud.ke.rulelearning.model.MultiplePredictionStats;
import de.tud.ke.rulelearning.model.PredictionStats;
import de.tud.ke.rulelearning.model.TrainingInstance;
import de.tud.ke.rulelearning.out.*;
import de.tud.ke.rulelearning.out.MultipleEvaluationCsvPrinter.MultipleEvaluationResult;
import mulan.classifier.MultiLabelLearner;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract base class for any multi-label classification experiment.
 *
 * @author Michael Rapp <mrapp@ke.tu-darmstadt.de>
 */
public abstract class AbstractSingleExperiment<ConfigType extends BaseConfiguration, ModelType, StatsType,
        LearnerType extends AbstractMultiLabelLearner<ConfigType, ModelType, StatsType>>
        extends AbstractExperiment<ConfigType> {

    public static class SharedData {

        MultiLabelInstances trainingInstances;

        MultiLabelInstances testInstances;

        final Map<String, EvaluationCsvPrinter> evaluationCsvPrinters = new HashMap<>();

        final Map<String, MultipleEvaluationCsvPrinter> multipleEvaluationCsvPrinters = new HashMap<>();

        final Map<String, RuleStatsCsvPrinter> ruleStatsCsvPrinters = new HashMap<>();

        final Map<String, MultipleRuleStatsCsvPrinter> multipleRuleStatsCsvPrinters = new HashMap<>();

    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSingleExperiment.class);

    private final SharedData sharedData;

    private MultiLabelInstances createOrReuseTrainingInstances() throws InvalidDataFormatException {
        MultiLabelInstances trainingInstances =
                sharedData != null ? sharedData.trainingInstances : null;

        if (trainingInstances == null) {
            trainingInstances = createTrainingInstances(getConfiguration());
        }

        if (sharedData != null) {
            sharedData.trainingInstances = trainingInstances;
        }

        return trainingInstances;
    }

    private MultiLabelInstances createOrReuseTestInstances() throws InvalidDataFormatException {
        MultiLabelInstances testInstances = sharedData != null ? sharedData.testInstances : null;

        if (testInstances == null) {
            testInstances = createTestInstances(getConfiguration());
        }

        if (sharedData != null) {
            sharedData.testInstances = testInstances;
        }

        return testInstances;
    }

    private void savePredictionStatisticsToDisk(final MultiplePredictionStats predictionStats,
                                                final String outputFileName,
                                                final MultiLabelInstances dataSet) {
        if (getConfiguration().isPredictionCsvFileSaved()) {
            String fileName = outputFileName.toLowerCase().endsWith(".csv") ? outputFileName : outputFileName + ".csv";
            String outputDir = getConfiguration().getOutputDirPath().toAbsolutePath().toString();

            try (PredictionStatsCsvPrinter csvPrinter = new PredictionStatsCsvPrinter(outputDir, fileName,
                    dataSet)) {
                for (PredictionStats stats : predictionStats) {
                    csvPrinter.print(stats);
                }
            } catch (IOException e) {
                LOG.error("Failed to save prediction statistics to disk", e);
            }
        }
    }

    private void saveEvaluationResultsToDisk(final Evaluation evaluation,
                                             final String outputFileName,
                                             final MultiLabelInstances dataSet) {
        String fileName = outputFileName.toLowerCase().endsWith(".csv") ? outputFileName :
                outputFileName + ".csv";
        EvaluationCsvPrinter csvPrinter = createOrReuseEvaluationCsvPrinter(fileName, dataSet);

        if (csvPrinter != null) {
            try {
                csvPrinter.print(new EvaluationResult(getName(), evaluation));
            } catch (IOException e) {
                LOG.error("Failed to save evaluation results to disk", e);
            } finally {
                closeCsvPrinterIfNotShared(csvPrinter);
            }
        }
    }

    private void saveMultipleEvaluationResultsToDisk(final MultipleEvaluation evaluation,
                                                     final String outputFileName,
                                                     final MultiLabelInstances dataSet) {
        String fileName = outputFileName.toLowerCase().endsWith(".csv") ? outputFileName :
                outputFileName + ".csv";
        MultipleEvaluationCsvPrinter csvPrinter = createOrReuseMultipleEvaluationCsvPrinter(fileName, dataSet);

        if (csvPrinter != null) {
            try {
                csvPrinter.print(new MultipleEvaluationResult(getName(), evaluation));
            } catch (IOException e) {
                LOG.error("Failed to save evaluation results to disk", e);
            } finally {
                closeCsvPrinterIfNotShared(csvPrinter);
            }
        }
    }

    public AbstractSingleExperiment(final ConfigurationFactory<ConfigType> configFactory,
                                    final String[] args) {
        super(configFactory, args);
        this.sharedData = null;
    }

    public AbstractSingleExperiment(final SharedData sharedData, final ConfigType configuration) {
        super(configuration);
        this.sharedData = sharedData;
    }

    protected abstract LearnerType createLearner();

    protected void validateOnTestSet(final LearnerType learner, final MultiLabelInstances trainingData)
            throws Exception {
        LOG.info("Creating model from training data...");
        trainModel(learner, trainingData);

        if (getConfiguration().getTestArffFilePath() != null) {
            learner.getPredictionStats().clear();
            LOG.info("Loading test data...");
            MultiLabelInstances testData = createOrReuseTestInstances();
            LOG.debug("Characteristics of test data set:\n\n{}\n", testData);
            LOG.info("Evaluating model on test data...");
            Evaluation testDataEvaluation = evaluate(learner, trainingData, testData,
                    "evaluation_test_data");
            LOG.debug("Evaluation results on test data:\n\n{}", testDataEvaluation);
            savePredictionStatisticsToDisk(learner.getPredictionStats(),
                    getName() + "_predictions_test_data", trainingData);
        }
    }

    protected MultipleEvaluation crossValidate(final LearnerType learner, final MultiLabelInstances data,
                                               final int folds) throws Exception {
        Evaluator evaluator = new Evaluator();
        return evaluator.crossValidate(learner, data, folds);
    }

    protected void trainModel(final MultiLabelLearner learner, final MultiLabelInstances trainingData) {
        try {
            learner.build(trainingData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to train model on training data", e);
        }
    }

    protected MultiLabelInstances createTrainingInstances(final ConfigType configuration) throws
            InvalidDataFormatException {
        MultiLabelInstances multiLabelInstances = new MultiLabelInstances(
                configuration.getArffFilePath().toAbsolutePath().toString(),
                configuration.getXmlLabelsDefFilePath().toAbsolutePath().toString());
        Instances instances = multiLabelInstances.getDataSet();
        Instances trainingInstances = new Instances(instances, instances.numInstances());

        for (int i = 0; i < instances.numInstances(); i++) {
            trainingInstances.add(new TrainingInstance(i, instances.get(i)));
        }

        return new MultiLabelInstances(trainingInstances, multiLabelInstances.getLabelsMetaData());
    }

    protected MultiLabelInstances createTestInstances(final ConfigType configuration) throws
            InvalidDataFormatException {
        return new MultiLabelInstances(
                configuration.getTestArffFilePath().toAbsolutePath().toString(),
                configuration.getXmlLabelsDefFilePath().toAbsolutePath().toString());
    }

    protected Evaluation evaluate(final MultiLabelLearner model,
                                  final MultiLabelInstances trainingData,
                                  final MultiLabelInstances testData,
                                  final String outputFileName) {
        try {
            Evaluator evaluator = new Evaluator();
            Evaluation evaluation = evaluator.evaluate(model, testData, trainingData);
            saveEvaluationResultsToDisk(evaluation, outputFileName, trainingData);
            return evaluation;
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate model", e);
        }
    }

    protected final MultipleEvaluationCsvPrinter createOrReuseMultipleEvaluationCsvPrinter(
            final String outputFileName, final MultiLabelInstances dataSet) {
        if (getConfiguration().getOutputDirPath() != null) {
            MultipleEvaluationCsvPrinter csvPrinter = sharedData != null ?
                    sharedData.multipleEvaluationCsvPrinters.get(outputFileName) : null;

            if (csvPrinter == null) {
                String outputDir = getConfiguration().getOutputDirPath().toAbsolutePath()
                        .toString();
                csvPrinter = new MultipleEvaluationCsvPrinter(outputDir, outputFileName, dataSet);
            }

            if (sharedData != null) {
                sharedData.multipleEvaluationCsvPrinters.put(outputFileName, csvPrinter);
            }

            return csvPrinter;
        }

        return null;
    }

    protected final EvaluationCsvPrinter createOrReuseEvaluationCsvPrinter(final String outputFileName,
                                                                           final MultiLabelInstances dataSet) {
        if (getConfiguration().getOutputDirPath() != null) {
            EvaluationCsvPrinter csvPrinter =
                    sharedData != null ? sharedData.evaluationCsvPrinters.get(outputFileName) :
                            null;

            if (csvPrinter == null) {
                String outputDir = getConfiguration().getOutputDirPath().toAbsolutePath()
                        .toString();
                csvPrinter = new EvaluationCsvPrinter(outputDir, outputFileName, dataSet);
            }

            if (sharedData != null) {
                sharedData.evaluationCsvPrinters.put(outputFileName, csvPrinter);
            }

            return csvPrinter;
        }

        return null;
    }

    protected final RuleStatsCsvPrinter createOrReuseRuleStatsCsvPrinter(final String outputFileName,
                                                                         final MultiLabelInstances dataSet) {
        if (getConfiguration().getOutputDirPath() != null) {
            RuleStatsCsvPrinter csvPrinter =
                    sharedData != null ? sharedData.ruleStatsCsvPrinters.get(outputFileName) : null;

            if (csvPrinter == null) {
                String outputDir = getConfiguration().getOutputDirPath().toAbsolutePath()
                        .toString();
                csvPrinter = new RuleStatsCsvPrinter(outputDir, outputFileName, dataSet);
            }

            if (sharedData != null) {
                sharedData.ruleStatsCsvPrinters.put(outputFileName, csvPrinter);
            }

            return csvPrinter;
        }

        return null;
    }

    protected final MultipleRuleStatsCsvPrinter createOrReuseMultipleRuleStatsCsvPrinter(
            final String outputFileName, final MultiLabelInstances dataSet) {
        if (getConfiguration().getOutputDirPath() != null) {
            MultipleRuleStatsCsvPrinter csvPrinter =
                    sharedData != null ?
                            sharedData.multipleRuleStatsCsvPrinters.get(outputFileName) : null;

            if (csvPrinter == null) {
                String outputDir = getConfiguration().getOutputDirPath().toAbsolutePath()
                        .toString();
                csvPrinter = new MultipleRuleStatsCsvPrinter(outputDir, outputFileName, dataSet);
            }

            if (sharedData != null) {
                sharedData.multipleRuleStatsCsvPrinters.put(outputFileName, csvPrinter);
            }

            return csvPrinter;
        }

        return null;
    }

    protected final void closeCsvPrinterIfNotShared(final CsvPrinter<?> csvPrinter) {
        if (sharedData == null) {
            csvPrinter.close();
        }
    }

    protected AbstractMultiLabelLearner.Callback<ModelType, StatsType> createCrossValidationCallback() {
        return null;
    }

    protected AbstractMultiLabelLearner.Callback<ModelType, StatsType> createLearnerCallback() {
        return null;
    }

    @Override
    public final void run() {
        long startTime = System.currentTimeMillis();
        LOG.info("Running experiment \"{}\"...", getClass().getSimpleName());
        LOG.info("Configuration:\n\n{}\n", getConfiguration());

        if (sharedData == null) {
            deleteOutputFiles();
        }

        LearnerType learner = createLearner();

        try {
            if (getConfiguration().isCrossValidationUsed()) {
                LOG.info("Loading data...");
                MultiLabelInstances data = createOrReuseTrainingInstances();
                int folds = getConfiguration().getCrossValidationFolds();
                LOG.info("Performing {}-fold cross validation...", folds);
                learner.setCallback(createCrossValidationCallback());
                MultipleEvaluation multipleEvaluation = crossValidate(learner, data, folds);
                LOG.debug("Cross validation results:\n\n{}", multipleEvaluation.toString());
                List<Evaluation> evaluations = multipleEvaluation.getEvaluations();

                for (int i = 0; i < evaluations.size(); i++) {
                    saveEvaluationResultsToDisk(evaluations.get(i), "evaluation_fold_" + (i + 1),
                            data);
                }

                saveMultipleEvaluationResultsToDisk(multipleEvaluation, "evaluation_overall", data);
                savePredictionStatisticsToDisk(learner.getPredictionStats(),
                        getName() + "_predictions_overall", data);
            } else {
                LOG.info("Loading training data...");
                MultiLabelInstances trainingData = createOrReuseTrainingInstances();
                learner.setCallback(createLearnerCallback());
                validateOnTestSet(learner, trainingData);
            }
        } catch (InvalidDataFormatException e) {
            throw new RuntimeException(String.format("Malformed data set: %s", e.getMessage()), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            LOG.info("Experiment \"{}\" finished after {} ms", getClass().getSimpleName(), duration);
        }
    }

}
