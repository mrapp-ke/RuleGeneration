package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.HeuristicFactory;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.learner.covering.StoppingCriterion;
import de.tud.ke.rulelearning.learner.evaluation.*;
import de.tud.ke.rulelearning.util.ConfigUtil;

public final class RuleGenerationConfigurationBuilderFactory {

    private RuleGenerationConfigurationBuilderFactory() {

    }

    public static RuleGenerationConfiguration.Builder create(final BaseConfiguration baseConfiguration,
                                                             final String[] args) {
        int maxRules = ConfigUtil.getIntArgument("min-rules", args, 300000);
        String coveringType = ConfigUtil.getOptionalArgument("covering", args);
        Covering.Type covering = coveringType != null ? Covering.Type.fromValue(coveringType) : null;
        Heuristic coveringHeuristic = HeuristicFactory.create(ConfigUtil.getOptionalArgument("covering-heuristic", args));
        EvaluationStrategy evaluationStrategy = new PartialPredictionStrategy();
        AveragingStrategy averagingStrategy = new MicroAveraging(new DefaultAggregation());
        MultiLabelEvaluation coveringEvaluation = coveringHeuristic != null ?
                new MultiLabelEvaluation(coveringHeuristic, evaluationStrategy, averagingStrategy) :
                new MultiLabelEvaluation(new FMeasure(), evaluationStrategy, averagingStrategy);
        String stoppingCriterionType = ConfigUtil.getOptionalArgument("stopping-criterion", args);
        StoppingCriterion.Type stoppingCriterion = stoppingCriterionType != null ?
                StoppingCriterion.Type.fromValue(stoppingCriterionType) : null;
        return new RuleGenerationConfiguration.Builder(baseConfiguration)
                .setMinRules(maxRules)
                .setCovering(covering)
                .setCoveringEvaluation(coveringEvaluation)
                .setStoppingCriterion(stoppingCriterion);
    }

}
