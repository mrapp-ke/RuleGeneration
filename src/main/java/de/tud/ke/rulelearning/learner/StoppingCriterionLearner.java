package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.StoppingCriterionConfiguration;
import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.Precision;
import de.tud.ke.rulelearning.learner.prediction.Predictor;
import de.tud.ke.rulelearning.learner.prediction.RuleSetPredictor;
import de.tud.ke.rulelearning.model.*;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import weka.core.Instance;
import weka.core.TechnicalInformation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StoppingCriterionLearner extends AbstractMultiLabelRuleLearner<StoppingCriterionConfiguration> {

    public StoppingCriterionLearner(final String name, final StoppingCriterionConfiguration configuration) {
        this(name, configuration, new MultiplePredictionStats());
    }

    public StoppingCriterionLearner(final String name, final StoppingCriterionConfiguration configuration,
                                    final MultiplePredictionStats predictionStats) {
        super(name, configuration, predictionStats);
    }

    @Override
    protected RuleCollection buildModel(final DataSet trainingDataSet) {
        throw new RuntimeException("Unable to load model");
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

    @Override
    protected RuleCollection finalizeModel(final DataSet trainingDataSet, final RuleCollection model) {
        DecisionList finalizedModel = (DecisionList) model;
        double threshold = getConfiguration().getStoppingCriterionThreshold();

        if (threshold < 1) {
            List<Rule> rules = new ArrayList<>(finalizedModel);
            final Heuristic precision = new Precision();
            rules.sort(((Comparator<Rule>) (rule1, rule2) -> {
                Condition condition1 = rule1.getHead().getConditions().iterator().next();
                Condition condition2 = rule2.getHead().getConditions().iterator().next();
                ConfusionMatrix confusionMatrix1 = rule1.getHead().getLabelWiseConfusionMatrix(condition1.index());
                ConfusionMatrix confusionMatrix2 = rule2.getHead().getLabelWiseConfusionMatrix(condition2.index());
                double h1 = precision.evaluateConfusionMatrix(confusionMatrix1);
                double h2 = precision.evaluateConfusionMatrix(confusionMatrix2);
                return Double.compare(h1, h2);
            }).reversed());


            int numRules = (int) Math.round(rules.size() * threshold);
            Rule lastRule = rules.get(numRules - 1);

            for (int i = rules.size() - 1; i >= numRules; i--) {
                Rule rule = rules.get(i);

                if (rule.compareTo(lastRule) != 0) {
                    rules.remove(i);
                }
            }

            return new RuleList(rules);
        }

        return finalizedModel;
    }

    @Override
    protected String getModelSaveFilePath(final Path saveFilePath) {
        String directory = saveFilePath.toAbsolutePath().toString();
        String fileName = getModelName();

        if (getConfiguration().isCrossValidationUsed()) {
            fileName = fileName + "_fold-" + getCurrentFold() + "-" + getConfiguration().getCrossValidationFolds();
        }

        return Paths.get(directory, fileName + ".finalized.model").toAbsolutePath().toString();
    }

    @Override
    protected AbstractMultiLabelLearner<StoppingCriterionConfiguration, RuleCollection, Stats> copy() {
        return new StoppingCriterionLearner(getName(), getConfiguration(), getPredictionStats());
    }

    @Override
    public TechnicalInformation getTechnicalInformation() {
        throw new UnsupportedOperationException();
    }

}
