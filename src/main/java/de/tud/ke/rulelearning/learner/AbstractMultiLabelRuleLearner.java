package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.BaseConfiguration;
import de.tud.ke.rulelearning.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMultiLabelRuleLearner<ConfigType extends BaseConfiguration> extends
        AbstractSerializableMultiLabelLearner<ConfigType, RuleSet, AbstractMultiLabelRuleLearner.Stats> {

    public static class Stats {

        private final LabelStats labelStats;

        private final RuleStats ruleStats;

        public Stats(final LabelStats labelStats, final RuleStats ruleStats) {
            this.ruleStats = ruleStats;
            this.labelStats = labelStats;
        }

        public final LabelStats getLabelStats() {
            return labelStats;
        }

        public final RuleStats getRuleStats() {
            return ruleStats;
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMultiLabelRuleLearner.class);

    public AbstractMultiLabelRuleLearner(final String name, final ConfigType configuration,
                                         final MultiplePredictionStats predictionStats) {
        super(name, configuration, predictionStats);
    }

    @Override
    protected Stats createModelStats(final DataSet trainingDataSet, final RuleSet model) {
        RuleStats ruleStats = new RuleStats(trainingDataSet, model);
        LabelStats labelStats = new LabelStats(trainingDataSet);
        labelStats.addRules(model);
        LOG.info("Learned model consists of {} rules", model.size());
        LOG.trace("{}", model);
        return new Stats(labelStats, ruleStats);
    }

}
