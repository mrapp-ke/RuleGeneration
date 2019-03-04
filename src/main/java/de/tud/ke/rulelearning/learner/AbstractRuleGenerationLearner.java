package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.RuleGenerationConfiguration;
import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.learner.covering.CoveringFactory;
import de.tud.ke.rulelearning.learner.evaluation.MultiLabelEvaluation;
import de.tud.ke.rulelearning.learner.evaluation.SingleLabelEvaluationStrategy;
import de.tud.ke.rulelearning.learner.prediction.Predictor;
import de.tud.ke.rulelearning.learner.prediction.RuleSetPredictor;
import de.tud.ke.rulelearning.model.*;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import weka.core.Instance;

public abstract class AbstractRuleGenerationLearner extends AbstractMultiLabelRuleLearner<RuleGenerationConfiguration> {

    private void adjustHeuristicValuesOfRules(final DataSet dataSet, final RuleSet ruleSet) {
        MultiLabelEvaluation evaluation = getConfiguration().getEvaluation();
        evaluateRules(dataSet, evaluation, ruleSet);
        adjustLabelWiseHeuristicValues(dataSet, ruleSet);
    }

    private void adjustLabelWiseHeuristicValues(final DataSet dataSet, final RuleSet ruleSet) {
        for (int labelIndex : dataSet.getLabelIndices()) {
            Heuristic heuristic = getConfiguration().getEvaluation().getHeuristic();
            MultiLabelEvaluation singleLabelEvaluation = new MultiLabelEvaluation(heuristic,
                    new SingleLabelEvaluationStrategy(labelIndex),
                    getConfiguration().getEvaluation().getAveragingStrategy(),
                    (rule, result) -> {
                        Head head = rule.getHead();
                        ConfusionMatrix confusionMatrix = result.getConfusionMatrix();
                        head.setLabelWiseConfusionMatrix(labelIndex, confusionMatrix);
                        head.setLabelWiseHeuristicValue(labelIndex, result.getHeuristicValue());
                    });
            evaluateRules(dataSet, singleLabelEvaluation, ruleSet);
        }
    }

    private void evaluateRules(final DataSet dataSet,
                               final MultiLabelEvaluation evaluation, final Iterable<Rule> rules) {
        if (evaluation != null) {
            for (Rule rule : rules) {
                evaluation.evaluate(dataSet, rule);
            }
        }
    }

    AbstractRuleGenerationLearner(final String name, final RuleGenerationConfiguration configuration,
                                  final MultiplePredictionStats predictionStats) {
        super(name, configuration, predictionStats);
    }

    @Override
    protected RuleSet postProcessModel(final DataSet trainingDataSet, final RuleSet model) {
        RuleSet postProcessedRuleSet = model;
        adjustHeuristicValuesOfRules(trainingDataSet, postProcessedRuleSet);

        if (getConfiguration().getMinPerformance() > 0) {
            final RuleSet filteredRuleSet = new RuleSet();
            postProcessedRuleSet.stream()
                    .filter(rule -> rule.getHeuristicValue() >= getConfiguration().getMinPerformance())
                    .forEach(filteredRuleSet::add);
            postProcessedRuleSet = filteredRuleSet;
        }

        return postProcessedRuleSet;
    }

    @Override
    protected RuleSet finalizeModel(final DataSet trainingDataSet, final RuleSet model)
            throws Exception {
        Covering covering = CoveringFactory.create(getConfiguration().getCovering(),
                getConfiguration().getStoppingCriterion());

        if (covering != null) {
            RuleSet coveringRules = new RuleSet();
            coveringRules.addAll(covering.getCoveringRules(model, trainingDataSet, getModelStats().getLabelStats(),
                    getConfiguration().getCoveringEvaluation().getHeuristic()));
            return coveringRules;
        } else {
            return model;
        }
    }

    @Override
    protected PredictionStats makePrediction(final DataSet trainingDataSet, final RuleSet model,
                                             final Instance instance) {
        RuleSet coveringRules = model.getCoveringRules(instance);
        Predictor<RuleSet> predictor = new RuleSetPredictor();
        MultiLabelOutput multiLabelOutput = predictor.makePrediction(trainingDataSet, coveringRules, instance,
                getModelStats().getLabelStats());
        GroundTruth groundTruth = trainingDataSet.getGroundTruth(instance);
        return new PredictionStats(trainingDataSet, multiLabelOutput, groundTruth, coveringRules);
    }

}
