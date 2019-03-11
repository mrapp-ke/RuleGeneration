package de.tud.ke.rulelearning.learner.prediction;

import de.tud.ke.rulelearning.model.*;
import mulan.classifier.MultiLabelOutput;
import weka.core.Instance;

public class RuleSetPredictor implements Predictor<RuleSet> {

    @Override
    public MultiLabelOutput makePrediction(final DataSet trainingDataSet, final RuleSet model,
                                           final Instance instance, final LabelStats labelStats) {
        int[] labelIndices = trainingDataSet.getDataSet().getLabelIndices();
        boolean[] bipartition = new boolean[labelIndices.length];

        for (int i = 0; i < bipartition.length; i++) {
            int labelIndex = labelIndices[i];
            boolean targetPrediction = trainingDataSet.getTargetPrediction(labelIndex);
            boolean prediction = !targetPrediction;

            for (Rule rule : model) {
                Head head = rule.getHead();
                Condition condition = head.getCondition(labelIndex);

                if (condition instanceof NominalCondition) {
                    NominalCondition nominalCondition = (NominalCondition) condition;

                    if (nominalCondition.getValue().equals(targetPrediction ? "1" : "0")) {
                        prediction = targetPrediction;
                        break;
                    }
                }
            }

            bipartition[i] = prediction;
        }

        return new MultiLabelOutput(bipartition);
    }

}
