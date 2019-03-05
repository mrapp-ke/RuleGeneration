package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class Evaluator {

    private static final Logger LOG = LoggerFactory.getLogger(Evaluator.class);

    private void aggregate(final boolean targetPrediction, final boolean trueLabel, final double weight,
                           final Head head, final int labelIndex, final ConfusionMatrix confusionMatrix,
                           final ConfusionMatrix stats) {
        Condition condition = head.getCondition(labelIndex);

        if (condition instanceof NominalCondition) {
            NominalCondition nominalCondition = (NominalCondition) condition;

            if (trueLabel == targetPrediction) {
                if (nominalCondition.getValue().equals(targetPrediction ? "1" : "0")) {
                    confusionMatrix.addTruePositives(weight);

                    if (stats != null) {
                        stats.addTruePositives(weight);
                    }
                } else {
                    confusionMatrix.addFalseNegatives(weight);

                    if (stats != null) {
                        stats.addFalseNegatives(weight);
                    }
                }
            } else {
                if (nominalCondition.getValue().equals(targetPrediction ? "1" : "0")) {
                    confusionMatrix.addFalsePositives(weight);

                    if (stats != null) {
                        stats.addFalsePositives(weight);
                    }
                } else {
                    confusionMatrix.addTrueNegatives(weight);

                    if (stats != null) {
                        stats.addTrueNegatives(weight);
                    }
                }
            }
        } else {
            if (trueLabel == targetPrediction) {
                confusionMatrix.addFalseNegatives(weight);

                if (stats != null) {
                    stats.addFalseNegatives(weight);
                }
            } else {
                confusionMatrix.addTrueNegatives(weight);

                if (stats != null) {
                    stats.addTrueNegatives(weight);
                }
            }
        }
    }

    public void evaluate(final DataSet dataSet, final Collection<Rule> rules) {
        int i = 1;

        for (Rule rule : rules) {
            LOG.info("Evaluating rule {} / {}...", i, rules.size());
            evaluate(dataSet, rule);
            i++;
        }
    }

    public void evaluate(final DataSet dataSet, final Rule rule) {
        Map<Integer, TrainingInstance> coveredInstances = dataSet.getCoveredInstances(rule);
        rule.setCoverage(coveredInstances.size());
        Head head = rule.getHead();
        ConfusionMatrix globalConfusionMatrix = new ConfusionMatrix();

        for (int labelIndex : dataSet.getLabelIndices()) {
            int positives = dataSet.getPositiveExamples(labelIndex);
            int negatives = dataSet.getDataSet().getNumInstances() - positives;
            boolean targetPrediction = negatives >= positives;
            ConfusionMatrix labelWiseConfusionMatrix = new ConfusionMatrix();

            for (TrainingInstance trainingInstance : coveredInstances.values()) {
                boolean trueLabel = trainingInstance.stringValue(labelIndex).equals("1");
                aggregate(targetPrediction, trueLabel, trainingInstance.weight(), head, labelIndex,
                        labelWiseConfusionMatrix, globalConfusionMatrix);
            }

            double remainingPositives = (targetPrediction ? positives : negatives)
                    - labelWiseConfusionMatrix.getNumberOfPositives();
            double remainingNegatives = (targetPrediction ? negatives : positives)
                    - labelWiseConfusionMatrix.getNumberOfNegatives();
            labelWiseConfusionMatrix.addFalseNegatives(remainingPositives);
            globalConfusionMatrix.addFalseNegatives(remainingPositives);
            labelWiseConfusionMatrix.addTrueNegatives(remainingNegatives);
            globalConfusionMatrix.addTrueNegatives(remainingNegatives);
            head.setLabelWiseConfusionMatrix(labelIndex, labelWiseConfusionMatrix);
        }

        rule.setConfusionMatrix(globalConfusionMatrix);
    }

}
