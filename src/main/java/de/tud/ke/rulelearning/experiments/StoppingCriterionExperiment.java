package de.tud.ke.rulelearning.experiments;

import de.mrapp.util.TextUtil;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.AbstractMultiLabelRuleLearner;
import de.tud.ke.rulelearning.learner.StoppingCriterionLearner;
import de.tud.ke.rulelearning.learner.covering.Covering;

import java.util.function.BiFunction;

public class StoppingCriterionExperiment extends AbstractSingleRuleLearnerExperiment<StoppingCriterionConfiguration> {

    private final BiFunction<String, StoppingCriterionConfiguration, StoppingCriterionLearner> learnerFactory;

    private final String approachName;

    private String getLearnerName() {
        String arffFileName = getConfiguration().getArffFilePath().getFileName().toString();
        String dataSetName = arffFileName.toLowerCase().endsWith(".arff") ? arffFileName
                .substring(0, arffFileName.length() - ".arff".length()) : arffFileName;
        return dataSetName + (TextUtil.INSTANCE.isNotEmpty(approachName) ? "_" + approachName : "");
    }

    public StoppingCriterionExperiment(
            final ConfigurationFactory<StoppingCriterionConfiguration> configFactory, final String[] args,
            final BiFunction<String, StoppingCriterionConfiguration, StoppingCriterionLearner> learnerFactory,
            final String approachName) {
        super(configFactory, args);
        this.learnerFactory = learnerFactory;
        this.approachName = approachName;
    }

    public StoppingCriterionExperiment(
            final SharedData sharedData, final StoppingCriterionConfiguration configuration,
            final BiFunction<String, StoppingCriterionConfiguration, StoppingCriterionLearner> learnerFactory,
            final String approachName) {
        super(sharedData, configuration);
        this.learnerFactory = learnerFactory;
        this.approachName = approachName;
    }

    @Override
    protected AbstractMultiLabelRuleLearner<StoppingCriterionConfiguration> createLearner() {
        return learnerFactory.apply(getLearnerName(), getConfiguration());
    }

    @Override
    public String getName() {
        String name = getLearnerName();
        Covering.Type covering = getConfiguration().getCovering();

        if (covering != null) {
            Heuristic heuristic = getConfiguration().getCoveringHeuristic().get(0);
            name = name + ("_" + covering.getValue() + "-covering_" + heuristic);
        }

        name = name + "_threshold=" + getConfiguration().getStoppingCriterionThreshold().get(0);
        return name;
    }

}