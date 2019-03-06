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

import java.util.Map;

/**
 * @author Michael Rapp <mrapp@ke-tu-darmstadt.de>
 */
public class LabelWiseCovering implements Covering {

    private static final Logger LOG = LoggerFactory.getLogger(LabelWiseCovering.class);

    private final boolean revalidate;

    private final StoppingCriterion stoppingCriterion;

    private RuleSet coverLabelWise(final RuleSet ruleSet, final DataSet trainingDataSet,
                                   final int labelIndex, final LabelStats labelStats, final Heuristic heuristic,
                                   final boolean targetPrediction) throws InvalidDataFormatException {
        MultiLabelInstances instances = new MultiLabelInstances(
                new Instances(trainingDataSet.getDataSet().getDataSet()),
                trainingDataSet.getDataSet().getLabelsMetaData());
        int uncoveredCount = targetPrediction ? labelStats.getP(labelIndex) : labelStats.getN(labelIndex);
        RuleSet rules = getRulesByLabelIndex(ruleSet, labelIndex, targetPrediction);
        RuleSet result = new RuleSet();

        while (uncoveredCount > 0) {
            LOG.info("{} uncovered labels remaining...", uncoveredCount);
            Rule bestRule = pollBestRule(rules, labelIndex, heuristic);

            if (bestRule != null) {
                Map<Integer, TrainingInstance> coveredInstances = trainingDataSet.getCoveredInstances(bestRule);

                for (TrainingInstance instance : coveredInstances.values()) {
                    instances.getDataSet().remove(instance.getIndex());

                    if (instance.stringValue(labelIndex).equals(targetPrediction ? "1" : "0")) {
                        uncoveredCount--;
                    }

                    if (revalidate) {
                        revalidateRules(rules, instance, labelIndex, targetPrediction);
                    }
                }

                result.add(bestRule);
            } else {
                break;
            }
        }

        return result;
    }

    private void revalidateRules(final RuleSet rules, final Instance coveredInstance, final int labelIndex,
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

    private RuleSet getRulesByLabelIndex(final RuleSet ruleSet, final int labelIndex, final boolean targetPrediction) {
        RuleSet rules = new RuleSet();

        for (Rule rule : ruleSet) {
            Condition condition = rule.getHead().getCondition(labelIndex);

            if (condition instanceof NominalCondition && ((NominalCondition) condition).getValue().equals(
                    targetPrediction ? "1" : "0")) {
                rules.add(rule);
            }
        }

        return rules;
    }

    private Rule pollBestRule(final RuleSet ruleSet, final int labelIndex, final Heuristic heuristic) {
        Rule bestRule = null;
        double bestH = 0;

        for (Rule rule : ruleSet) {
            Head head = rule.getHead();
            ConfusionMatrix confusionMatrix = head.getLabelWiseConfusionMatrix(labelIndex);
            double h = heuristic.evaluateConfusionMatrix(confusionMatrix);
            head.setLabelWiseHeuristicValue(labelIndex, h);

            if (bestRule == null || h > bestH) {
                bestRule = rule;
                bestH = h;
            }
        }

        if (bestRule != null) {
            ruleSet.remove(bestRule);

            if (stoppingCriterion != null && stoppingCriterion.isSatisfied(bestRule, labelIndex)) {
                LOG.info("Stopping criterion met");
                return null;
            }

            return bestRule;
        }

        return null;
    }

    public LabelWiseCovering(final boolean revalidate, final StoppingCriterion stoppingCriterion) {
        this.revalidate = revalidate;
        this.stoppingCriterion = stoppingCriterion;
    }

    @Override
    public RuleSet getCoveringRules(final RuleSet ruleSet, final DataSet trainingDataSet,
                                    final LabelStats labelStats, final Heuristic heuristic) throws Exception {
        RuleSet result = new RuleSet();

        for (int labelIndex : trainingDataSet.getLabelIndices()) {
            LOG.info("Covering label {}...", labelIndex);
            boolean targetPrediction = trainingDataSet.getTargetPrediction(labelIndex);
            result.addAll(coverLabelWise(ruleSet, trainingDataSet, labelIndex, labelStats, heuristic,
                    targetPrediction));
        }

        return result;
    }

}
