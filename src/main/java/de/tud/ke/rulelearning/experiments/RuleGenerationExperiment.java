package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.learner.AbstractMultiLabelRuleLearner;
import de.tud.ke.rulelearning.learner.AbstractRuleGenerationLearner;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.learner.evaluation.MultiLabelEvaluation;

import java.util.function.BiFunction;

public class RuleGenerationExperiment extends AbstractSingleRuleLearnerExperiment<RuleGenerationConfiguration> {

    private final BiFunction<String, RuleGenerationConfiguration, AbstractRuleGenerationLearner> learnerFactory;

    private final String approachName;

    private String getLearnerName() {
        String arffFileName = getConfiguration().getArffFilePath().getFileName().toString();
        String dataSetName = arffFileName.toLowerCase().endsWith(".arff") ? arffFileName
                .substring(0, arffFileName.length() - ".arff".length()) : arffFileName;
        return dataSetName + "_" + approachName;
    }

    public RuleGenerationExperiment(
            final ConfigurationFactory<RuleGenerationConfiguration> configFactory, final String[] args,
            final BiFunction<String, RuleGenerationConfiguration, AbstractRuleGenerationLearner> learnerFactory,
            final String approachName) {
        super(configFactory, args);
        this.learnerFactory = learnerFactory;
        this.approachName = approachName;
    }

    public RuleGenerationExperiment(
            final SharedData sharedData, final RuleGenerationConfiguration configuration,
            final BiFunction<String, RuleGenerationConfiguration, AbstractRuleGenerationLearner> learnerFactory,
            final String approachName) {
        super(sharedData, configuration);
        this.learnerFactory = learnerFactory;
        this.approachName = approachName;
    }

    @Override
    protected AbstractMultiLabelRuleLearner<RuleGenerationConfiguration> createLearner() {
        return learnerFactory.apply(getLearnerName(), getConfiguration());
    }

    @Override
    public String getName() {
        String name = getLearnerName();
        Covering.Type covering = getConfiguration().getCovering();

        if (covering != null) {
            MultiLabelEvaluation coveringEvaluation = getConfiguration().getCoveringEvaluation();
            return name + ("_" + covering.getValue() + "-covering_" + coveringEvaluation.getEvaluationStrategy() + "_" +
                    coveringEvaluation.getAveragingStrategy() + "_" + coveringEvaluation.getHeuristic());
        }

        return name;
    }

}