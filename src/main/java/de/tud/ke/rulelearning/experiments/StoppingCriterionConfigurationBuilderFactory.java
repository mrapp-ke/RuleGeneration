package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.heuristics.HeuristicFactory;
import de.tud.ke.rulelearning.util.ConfigUtil;

import java.util.function.Function;

public final class StoppingCriterionConfigurationBuilderFactory<BuilderType extends StoppingCriterionConfiguration.AbstractBuilder<BuilderType>>
        extends RuleGenerationConfigurationBuilderFactory<BuilderType> {

    public StoppingCriterionConfigurationBuilderFactory(final Function<BaseConfiguration, BuilderType> builderFactory) {
        super(builderFactory);
    }

    public BuilderType create(final BaseConfiguration baseConfiguration, final String[] args) {
        Heuristic stoppingCriterionHeuristic = HeuristicFactory.create(ConfigUtil.getOptionalArgument("stopping-criterion-heuristic", args));
        double threshold = ConfigUtil.getDoubleArgument("stopping-criterion-threshold", args, 1);
        return super.create(baseConfiguration, args)
                .setStoppingCriterionHeuristic(Provider.singleton(stoppingCriterionHeuristic))
                .setStoppingCriterionThreshold(Provider.singleton(threshold));
    }

}
