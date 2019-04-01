package de.tud.ke.rulelearning.learner.covering;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.model.*;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LabelWiseCovering implements Covering {

    private static final Logger LOG = LoggerFactory.getLogger(LabelWiseCovering.class);

    private final Comparator<Rule> comparator = new Measurable.Comparator<>(Rule.TIE_BREAKER);

    private final boolean revalidate;

    private void coverLabelWise(final RuleCollection allRules, final RuleCollection result, final DataSet trainingDataSet,
                                final int labelIndex, final LabelStats labelStats, final Heuristic heuristic,
                                final boolean targetPrediction) throws InvalidDataFormatException {
        MultiLabelInstances instances = new MultiLabelInstances(
                new Instances(trainingDataSet.getDataSet().getDataSet()),
                trainingDataSet.getDataSet().getLabelsMetaData());
        int uncoveredCount = targetPrediction ? labelStats.getP(labelIndex) : labelStats.getN(labelIndex);
        List<Rule> rules = getRulesByLabelIndex(allRules, labelIndex, targetPrediction);

        while (uncoveredCount > 0) {
            LOG.info("{} uncovered labels remaining...", uncoveredCount);
            Rule bestRule = pollBestRule(rules, labelIndex, heuristic);

            if (bestRule != null) {
                for (int i = instances.getNumInstances() - 1; i >= 0; i--) {
                    Instance instance = instances.getDataSet().get(i);

                    if (bestRule.covers(instance)) {
                        instances.getDataSet().remove(i);

                        if (instance.stringValue(labelIndex).equals(targetPrediction ? "1" : "0")) {
                            uncoveredCount--;
                        }

                        if (revalidate) {
                            revalidateRules(rules, instance, labelIndex, targetPrediction);
                        }
                    }
                }

                result.add(bestRule);
            } else {
                break;
            }
        }
    }

    private void revalidateRules(final List<Rule> rules, final Instance coveredInstance, final int labelIndex,
                                 final boolean targetPrediction) {
        boolean trueLabel = coveredInstance.stringValue(labelIndex).equals("1");

        for (Rule rule : rules) {
            Head head = rule.getHead();
            ConfusionMatrix confusionMatrix = head.getLabelWiseConfusionMatrix(labelIndex);

            if (trueLabel == targetPrediction) {
                if (rule.covers(coveredInstance)) {
                    confusionMatrix.setNumberOfTruePositives(
                            confusionMatrix.getNumberOfTruePositives() - coveredInstance.weight());
                } else {
                    confusionMatrix.setNumberOfFalseNegatives(
                            confusionMatrix.getNumberOfFalseNegatives() - coveredInstance.weight());
                }
            } else {
                if (rule.covers(coveredInstance)) {
                    confusionMatrix.setNumberOfFalsePositives(
                            confusionMatrix.getNumberOfFalsePositives() - coveredInstance.weight());
                } else {
                    confusionMatrix.setNumberOfTrueNegatives(
                            confusionMatrix.getNumberOfTrueNegatives() - coveredInstance.weight());
                }
            }
        }
    }

    private List<Rule> getRulesByLabelIndex(final RuleCollection ruleSet, final int labelIndex, final boolean targetPrediction) {
        List<Rule> rules = new ArrayList<>();

        for (Rule rule : ruleSet) {
            Condition condition = rule.getHead().getCondition(labelIndex);

            if (condition instanceof NominalCondition && ((NominalCondition) condition).getValue().equals(
                    targetPrediction ? "1" : "0")) {
                rules.add(rule);
            }
        }

        return rules;
    }

    private Rule pollBestRule(final List<Rule> rules, final int labelIndex, final Heuristic heuristic) {
        Integer bestIndex = null;
        Rule bestRule = null;

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            Head head = rule.getHead();
            ConfusionMatrix confusionMatrix = head.getLabelWiseConfusionMatrix(labelIndex);
            double h = heuristic.evaluateConfusionMatrix(confusionMatrix);
            head.setLabelWiseHeuristicValue(labelIndex, h);
            rule.setHeuristicValue(h);

            if (bestRule == null || comparator.compare(rule, bestRule) < 0) {
                bestIndex = i;
                bestRule = rule;
            }
        }

        if (bestIndex != null) {
            return rules.remove((int) bestIndex);
        }

        return null;
    }

    public LabelWiseCovering(final boolean revalidate) {
        this.revalidate = revalidate;
    }

    @Override
    public RuleCollection getCoveringRules(final RuleCollection rules, final DataSet trainingDataSet,
                                           final LabelStats labelStats, final Heuristic heuristic) throws Exception {
        RuleSet ruleSet = new RuleSet();

        for (int labelIndex : trainingDataSet.getLabelIndices()) {
            LOG.info("Covering label {}...", labelIndex);
            boolean targetPrediction = trainingDataSet.getTargetPrediction(labelIndex);
            coverLabelWise(rules, ruleSet, trainingDataSet, labelIndex, labelStats, heuristic,
                    targetPrediction);
        }

        return ruleSet;
    }

}
