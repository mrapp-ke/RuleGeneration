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
import de.tud.ke.rulelearning.model.RuleSet;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import weka.core.Instance;

public abstract class AbstractRuleGenerationLearner extends AbstractMultiLabelRuleLearner<RuleGenerationConfiguration> {

    AbstractRuleGenerationLearner(final String name, final RuleGenerationConfiguration configuration,
                                  final MultiplePredictionStats predictionStats) {
        super(name, configuration, predictionStats);
    }

    @Override
    protected RuleSet postProcessModel(final DataSet trainingDataSet, final RuleSet model) {
        Evaluator evaluator = new Evaluator();
        evaluator.evaluate(trainingDataSet, model);
        return model;
    }

    @Override
    protected RuleSet finalizeModel(final DataSet trainingDataSet, final RuleSet model)
            throws Exception {
        Covering covering = CoveringFactory.create(getConfiguration().getCovering(),
                getConfiguration().getStoppingCriterion());

        if (covering != null) {
            RuleSet coveringRules = new RuleSet();
            coveringRules.addAll(covering.getCoveringRules(model, trainingDataSet, getModelStats().getLabelStats(),
                    getConfiguration().getCoveringHeuristic()));
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
