package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.RuleGenerationConfiguration;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.learner.covering.CoveringFactory;
import de.tud.ke.rulelearning.learner.evaluation.Evaluator;
import de.tud.ke.rulelearning.learner.prediction.Predictor;
import de.tud.ke.rulelearning.learner.prediction.RuleSetPredictor;
import de.tud.ke.rulelearning.model.DataSet;
import de.tud.ke.rulelearning.model.MultiplePredictionStats;
import de.tud.ke.rulelearning.model.PredictionStats;
import de.tud.ke.rulelearning.model.RuleCollection;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instance;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractRuleGenerationLearner extends AbstractMultiLabelRuleLearner<RuleGenerationConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRuleGenerationLearner.class);

    private RuleCollection getFinalizedRules(final DataSet trainingDataSet, final RuleCollection model,
                                             final Covering covering) throws Exception {
        Path modelDirPath = getConfiguration().getModelDirPath();
        RuleCollection finalizedModel = null;
        boolean loadedFromSaveFile = true;

        if (modelDirPath != null) {
            finalizedModel = loadFinalizedModel(modelDirPath);
        }

        if (finalizedModel == null) {
            loadedFromSaveFile = false;
            int fold = getConfiguration().isCrossValidationUsed() ? getCurrentFold() - 1 : 0;
            Heuristic heuristic = getConfiguration().getCoveringHeuristic().get(fold);
            finalizedModel = covering.getCoveringRules(model, trainingDataSet, getModelStats().getLabelStats(),
                    heuristic);
        }

        if (modelDirPath != null && !loadedFromSaveFile) {
            saveFinalizedModel(modelDirPath, finalizedModel);
        }

        return finalizedModel;
    }

    private RuleCollection loadFinalizedModel(final Path saveFilePath) {
        String filePath = getFinalizedModelSaveFilePath(saveFilePath);
        LOG.info("Loading finalized model from file {}...", filePath);
        RuleCollection model = null;

        try {
            model = loadFromFile(filePath);
        } catch (Exception e) {
            LOG.error("Failed to load finalized model from file {}", filePath);
        }

        return model;
    }

    private void saveFinalizedModel(final Path saveFilePath, final RuleCollection model) {
        String filePath = getFinalizedModelSaveFilePath(saveFilePath);

        try {
            saveToFile(filePath, model);
            LOG.info("Successfully saved finalized model to file {}", filePath);
        } catch (Exception e) {
            LOG.error("Failed to save finalized model to file {}", filePath, e);
        }
    }

    private String getFinalizedModelSaveFilePath(final Path saveFilePath) {
        String directory = saveFilePath.toAbsolutePath().toString();
        String fileName = getModelName();
        Covering.Type covering = getConfiguration().getCovering();

        if (covering != null) {
            int fold = getConfiguration().isCrossValidationUsed() ? getCurrentFold() - 1 : 0;
            Heuristic heuristic = getConfiguration().getCoveringHeuristic().get(fold);
            fileName = fileName + "_" + covering.getValue() + "-covering_" + heuristic;
        }

        if (getConfiguration().isCrossValidationUsed()) {
            fileName = fileName + "_fold-" + getCurrentFold() + "-" + getConfiguration().getCrossValidationFolds();
        }

        return Paths.get(directory, fileName + ".finalized.model").toAbsolutePath().toString();
    }

    AbstractRuleGenerationLearner(final String name, final RuleGenerationConfiguration configuration,
                                  final MultiplePredictionStats predictionStats) {
        super(name, configuration, predictionStats);
    }

    @Override
    protected RuleCollection postProcessModel(final DataSet trainingDataSet, final RuleCollection model) {
        Evaluator evaluator = new Evaluator();
        evaluator.evaluate(trainingDataSet, model);
        return model;
    }

    @Override
    protected RuleCollection finalizeModel(final DataSet trainingDataSet, final RuleCollection model)
            throws Exception {
        Covering covering = CoveringFactory.create(getConfiguration().getCovering());

        if (covering != null) {
            return getFinalizedRules(trainingDataSet, model, covering);
        } else {
            return model;
        }
    }

    @Override
    protected PredictionStats makePrediction(final DataSet trainingDataSet, final RuleCollection model,
                                             final Instance instance) {
        RuleCollection coveringRules = model.getCoveringRules(instance);
        Predictor<RuleCollection> predictor = new RuleSetPredictor();
        MultiLabelOutput multiLabelOutput = predictor.makePrediction(trainingDataSet, coveringRules, instance,
                getModelStats().getLabelStats());
        GroundTruth groundTruth = trainingDataSet.getGroundTruth(instance);
        return new PredictionStats(trainingDataSet, multiLabelOutput, groundTruth, coveringRules);
    }

}
