package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.StoppingCriterionConfiguration;
import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.Heuristic;
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

    private static class RuleComparator implements java.util.Comparator<Rule> {

        private final Heuristic heuristic;

        RuleComparator(final Heuristic heuristic) {
            this.heuristic = heuristic;
        }

        @Override
        public int compare(final Rule rule1, final Rule rule2) {
            ConfusionMatrix confusionMatrix1 = rule1.getConfusionMatrix();
            ConfusionMatrix confusionMatrix2 = rule2.getConfusionMatrix();
            double h1 = heuristic.evaluateConfusionMatrix(confusionMatrix1);
            double h2 = heuristic.evaluateConfusionMatrix(confusionMatrix2);
            int comp = Double.compare(h2, h1);
            return comp != 0 ? comp : Rule.TIE_BREAKER.compare(rule1, rule2);
        }

    }

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
        double threshold = getConfiguration().getStoppingCriterionThreshold();

        if (threshold < 1) {
            List<Rule> rules = new ArrayList<>(model);
            final Heuristic heuristic = getConfiguration().getStoppingCriterionHeuristic() != null ?
                    getConfiguration().getStoppingCriterionHeuristic() : getConfiguration().getCoveringHeuristic();
            final Comparator<Rule> comparator = new RuleComparator(heuristic);
            rules.sort(comparator);

            int numRules = (int) Math.ceil(rules.size() * threshold);
            Rule lastRule = rules.get(numRules - 1);

            for (int i = rules.size() - 1; i >= numRules; i--) {
                if (comparator.compare(rules.get(i), lastRule) != 0) {
                    rules.remove(i);
                }
            }

            return new RuleList(rules);
        }

        return model;
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
