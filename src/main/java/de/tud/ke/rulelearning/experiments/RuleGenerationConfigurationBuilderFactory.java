package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.HeuristicFactory;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.util.ConfigUtil;

import java.util.function.Function;

public class RuleGenerationConfigurationBuilderFactory<BuilderType extends RuleGenerationConfiguration.AbstractBuilder<BuilderType>> {

    private final Function<BaseConfiguration, BuilderType> builderFactory;

    public RuleGenerationConfigurationBuilderFactory(final Function<BaseConfiguration, BuilderType> builderFactory) {
        this.builderFactory = builderFactory;
    }

    public BuilderType create(final BaseConfiguration baseConfiguration, final String[] args) {
        boolean saveRuleCsvFile = ConfigUtil.getBooleanArgument("save-rule-csv-file", args, false);
        int maxRules = ConfigUtil.getIntArgument("min-rules", args, 300000);
        String coveringType = ConfigUtil.getOptionalArgument("covering", args);
        Covering.Type covering = coveringType != null ? Covering.Type.fromValue(coveringType) : null;
        Heuristic coveringHeuristic = HeuristicFactory.create(ConfigUtil.getOptionalArgument("covering-heuristic", args));
        return builderFactory.apply(baseConfiguration)
                .setRuleCsvFileSaved(saveRuleCsvFile)
                .setMinRules(maxRules)
                .setCovering(covering)
                .setCoveringHeuristic(coveringHeuristic);
    }

}
