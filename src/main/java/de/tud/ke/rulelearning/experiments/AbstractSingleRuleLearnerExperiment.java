package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.learner.AbstractMultiLabelLearner;
import de.tud.ke.rulelearning.learner.AbstractMultiLabelRuleLearner;
import de.tud.ke.rulelearning.model.*;
import de.tud.ke.rulelearning.out.MultipleRuleStatsCsvPrinter;
import de.tud.ke.rulelearning.out.RuleCsvPrinter;
import de.tud.ke.rulelearning.out.RuleStatsCsvPrinter;
import mulan.data.MultiLabelInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * An abstract base class for any multi-label classification experiment.
 *
 * @author Michael Rapp <mrapp@ke.tu-darmstadt.de>
 */
public abstract class AbstractSingleRuleLearnerExperiment<ConfigType extends RuleLearnerConfiguration> extends
        AbstractSingleExperiment<ConfigType, RuleCollection, AbstractMultiLabelRuleLearner.Stats, AbstractMultiLabelRuleLearner<ConfigType>> {

    private class LearnerCallback implements
            AbstractMultiLabelRuleLearner.Callback<RuleCollection, AbstractMultiLabelRuleLearner.Stats> {

        @Override
        public void onModelBuilt(final DataSet trainingData, final int fold,
                                 final AbstractMultiLabelLearner<?, RuleCollection, AbstractMultiLabelRuleLearner.Stats> learner,
                                 final RuleCollection model, final AbstractMultiLabelRuleLearner.Stats stats) {
            saveModelStatisticsToDisk(stats.getRuleStats(), "built_model_statistics", trainingData.getDataSet());
        }

        @Override
        public void onModelFinalized(final DataSet trainingData, final int fold,
                                     final AbstractMultiLabelLearner<?, RuleCollection, AbstractMultiLabelRuleLearner.Stats> learner,
                                     final RuleCollection ruleSet, final AbstractMultiLabelRuleLearner.Stats stats) {
            saveRulesToDisk(ruleSet, getName() + "_rules", trainingData.getDataSet());
            saveModelStatisticsToDisk(stats.getRuleStats(), "finalized_model_statistics", trainingData.getDataSet());
        }

    }

    private class LearnerCrossValidationCallback implements
            AbstractMultiLabelRuleLearner.Callback<RuleCollection, AbstractMultiLabelRuleLearner.Stats> {

        private final MultipleRuleStats builtModelRuleStats = new MultipleRuleStats();

        private final MultipleRuleStats finalizedModelRuleStats = new MultipleRuleStats();

        @Override
        public void onModelBuilt(final DataSet trainingData, final int fold,
                                 final AbstractMultiLabelLearner<?, RuleCollection, AbstractMultiLabelRuleLearner.Stats> learner,
                                 final RuleCollection model, final AbstractMultiLabelRuleLearner.Stats stats) {
            saveModelStatisticsToDisk(stats.getRuleStats(), "built_model_statistics_fold_" + fold,
                    trainingData.getDataSet());
            builtModelRuleStats.addRuleStats(stats.getRuleStats());

            if (fold == getConfiguration().getCrossValidationFolds()) {
                saveMultipleModelStatisticsToDisk(builtModelRuleStats, "built_model_statistics_overall",
                        trainingData.getDataSet());
            }
        }

        @Override
        public void onModelFinalized(final DataSet trainingData, final int fold,
                                     final AbstractMultiLabelLearner<?, RuleCollection, AbstractMultiLabelRuleLearner.Stats> learner,
                                     final RuleCollection ruleSet, final AbstractMultiLabelRuleLearner.Stats stats) {
            saveRulesToDisk(ruleSet, getName() + "_rules_" + fold, trainingData.getDataSet());
            saveModelStatisticsToDisk(stats.getRuleStats(), "finalized_model_statistics_fold_" + fold,
                    trainingData.getDataSet());
            finalizedModelRuleStats.addRuleStats(stats.getRuleStats());

            if (fold == getConfiguration().getCrossValidationFolds()) {
                saveMultipleModelStatisticsToDisk(finalizedModelRuleStats, "finalized_model_statistics_overall",
                        trainingData.getDataSet());
            }
        }

    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSingleRuleLearnerExperiment.class);

    private void saveRulesToDisk(final Iterable<Rule> rules, final String outputFileName,
                                 final MultiLabelInstances dataSet) {
        if (getConfiguration().isRuleCsvFileSaved()) {
            String fileName = outputFileName.toLowerCase().endsWith(".csv") ? outputFileName : outputFileName + ".csv";
            String outputDir = getConfiguration().getOutputDirPath().toAbsolutePath().toString();

            try (RuleCsvPrinter csvPrinter = new RuleCsvPrinter(outputDir, fileName, dataSet)) {
                for (Rule rule : rules) {
                    csvPrinter.print(rule);
                }
            } catch (IOException e) {
                LOG.error("Failed to save rules to disk", e);
            }
        }
    }

    private void saveModelStatisticsToDisk(final RuleStats ruleStats, final String outputFileName,
                                           final MultiLabelInstances dataSet) {
        String fileName = outputFileName.toLowerCase().endsWith(".csv") ? outputFileName :
                outputFileName + ".csv";
        RuleStatsCsvPrinter csvPrinter = createOrReuseRuleStatsCsvPrinter(fileName, dataSet);

        if (csvPrinter != null) {
            try {
                csvPrinter.print(new RuleStatsCsvPrinter.RuleStatsResult(getName(), ruleStats));
            } catch (IOException e) {
                LOG.error("Failed to save model statistics to disk", e);
            } finally {
                closeCsvPrinterIfNotShared(csvPrinter);
            }
        }
    }

    private void saveMultipleModelStatisticsToDisk(final MultipleRuleStats ruleStats, final String outputFileName,
                                                   final MultiLabelInstances dataSet) {
        String fileName = outputFileName.toLowerCase().endsWith(".csv") ? outputFileName :
                outputFileName + ".csv";
        MultipleRuleStatsCsvPrinter csvPrinter = createOrReuseMultipleRuleStatsCsvPrinter(fileName,
                dataSet);

        if (csvPrinter != null) {
            try {
                csvPrinter.print(new MultipleRuleStatsCsvPrinter.MultipleRuleStatsResult(getName(), ruleStats));
            } catch (IOException e) {
                LOG.error("Failed to save model statistics to disk", e);
            } finally {
                closeCsvPrinterIfNotShared(csvPrinter);
            }
        }
    }

    public AbstractSingleRuleLearnerExperiment(final ConfigurationFactory<ConfigType> configFactory,
                                               final String[] args) {
        super(configFactory, args);
    }

    public AbstractSingleRuleLearnerExperiment(final SharedData sharedData, final ConfigType configuration) {
        super(sharedData, configuration);
    }

    @Override
    protected AbstractMultiLabelLearner.Callback<RuleCollection, AbstractMultiLabelRuleLearner.Stats> createLearnerCallback() {
        return new LearnerCallback();
    }

    @Override
    protected AbstractMultiLabelLearner.Callback<RuleCollection, AbstractMultiLabelRuleLearner.Stats> createCrossValidationCallback() {
        return new LearnerCrossValidationCallback();
    }

}
