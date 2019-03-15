package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.RuleGenerationConfiguration;
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
import weka.core.Instance;

public abstract class AbstractRuleGenerationLearner extends AbstractMultiLabelRuleLearner<RuleGenerationConfiguration> {

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
            return covering.getCoveringRules(model, trainingDataSet, getModelStats().getLabelStats(),
                    getConfiguration().getCoveringHeuristic());
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
