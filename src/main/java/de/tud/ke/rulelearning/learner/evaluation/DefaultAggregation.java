package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.model.Condition;
import de.tud.ke.rulelearning.model.Head;
import de.tud.ke.rulelearning.model.NominalCondition;
import de.tud.ke.rulelearning.model.DataSet;
import weka.core.Instance;

public class DefaultAggregation implements AggregationStrategy {

    @Override
    public void aggregate(final DataSet dataSet, final boolean covers, final Head head,
                          final Instance instance, final int labelIndex, final ConfusionMatrix confusionMatrix,
                          final ConfusionMatrix stats) {
        boolean labelValue = dataSet.getLabelValue(instance, labelIndex);
        boolean targetPrediction = dataSet.getTargetPrediction(labelIndex);

        if (covers) {
            Condition condition = head.getCondition(labelIndex);

            if (condition instanceof NominalCondition) {
                NominalCondition nominalCondition = (NominalCondition) condition;

                if (labelValue == targetPrediction) {
                    if (nominalCondition.getValue().equals(targetPrediction ? "1" : "0")) {
                        confusionMatrix.addTruePositives(instance.weight());

                        if (stats != null) {
                            stats.addTruePositives(instance.weight());
                        }
                    } else {
                        confusionMatrix.addFalseNegatives(instance.weight());

                        if (stats != null) {
                            stats.addFalseNegatives(instance.weight());
                        }
                    }
                } else {
                    if (nominalCondition.getValue().equals(targetPrediction ? "1" : "0")) {
                        confusionMatrix.addFalsePositives(instance.weight());

                        if (stats != null) {
                            stats.addFalsePositives(instance.weight());
                        }
                    } else {
                        confusionMatrix.addTrueNegatives(instance.weight());

                        if (stats != null) {
                            stats.addTrueNegatives(instance.weight());
                        }
                    }
                }
            } else {
                if (labelValue == targetPrediction) {
                    confusionMatrix.addFalseNegatives(instance.weight());

                    if (stats != null) {
                        stats.addFalseNegatives(instance.weight());
                    }
                } else {
                    confusionMatrix.addTrueNegatives(instance.weight());

                    if (stats != null) {
                        stats.addTrueNegatives(instance.weight());
                    }
                }
            }
        } else {
            if (labelValue == targetPrediction) {
                confusionMatrix.addFalseNegatives(instance.weight());

                if (stats != null) {
                    stats.addFalseNegatives(instance.weight());
                }
            } else {
                confusionMatrix.addTrueNegatives(instance.weight());

                if (stats != null) {
                    stats.addTrueNegatives(instance.weight());
                }
            }
        }
    }

}
