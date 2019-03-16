package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.HeuristicFactory;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.util.ConfigUtil;

public final class StoppingCriterionConfigurationBuilderFactory {

    private StoppingCriterionConfigurationBuilderFactory() {

    }

    public static StoppingCriterionConfiguration.Builder create(final BaseConfiguration baseConfiguration,
                                                             final String[] args) {
        boolean saveRuleCsvFile = ConfigUtil.getBooleanArgument("save-rule-csv-file", args, false);
        double threshold = ConfigUtil.getDoubleArgument("stopping-criterion-threshold", args, 1);
        String coveringType = ConfigUtil.getOptionalArgument("covering", args);
        Covering.Type covering = coveringType != null ? Covering.Type.fromValue(coveringType) : null;
        Heuristic coveringHeuristic = HeuristicFactory.create(ConfigUtil.getOptionalArgument("covering-heuristic", args));
        return new StoppingCriterionConfiguration.Builder(baseConfiguration)
                .setRuleCsvFileSaved(saveRuleCsvFile)
                .setCovering(covering)
                .setCoveringHeuristic(coveringHeuristic)
                .setStoppingCriterionThreshold(threshold);
    }

}
