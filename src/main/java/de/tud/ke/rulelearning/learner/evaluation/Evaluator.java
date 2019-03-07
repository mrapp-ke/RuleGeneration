package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.model.DataSet;
import de.tud.ke.rulelearning.model.Rule;
import de.tud.ke.rulelearning.model.TrainingInstance;

import java.util.Collection;
import java.util.Map;

public class Evaluator {

    private void aggregate(final boolean targetPrediction, final boolean trueLabel, final double weight,
                           final ConfusionMatrix confusionMatrix, final ConfusionMatrix stats) {
        if (trueLabel == targetPrediction) {
            confusionMatrix.addTruePositives(weight);

            if (stats != null) {
                stats.addTruePositives(weight);
            }
        } else {
            confusionMatrix.addFalsePositives(weight);

            if (stats != null) {
                stats.addFalsePositives(weight);
            }
        }
    }

    public void evaluate(final DataSet dataSet, final Collection<Rule> rules) {
        for (Rule rule : rules) {
            evaluate(dataSet, rule);
        }
    }

    public void evaluate(final DataSet dataSet, final Rule rule) {
        Map<Integer, TrainingInstance> coveredInstances = dataSet.getCoveredInstances(rule);
        rule.setCoverage(coveredInstances.size());
        ConfusionMatrix globalConfusionMatrix = new ConfusionMatrix();

        for (int labelIndex : dataSet.getLabelIndices()) {
            int positives = dataSet.getPositiveExamples(labelIndex);
            int negatives = dataSet.getDataSet().getNumInstances() - positives;
            boolean targetPrediction = negatives >= positives;
            ConfusionMatrix labelWiseConfusionMatrix = new ConfusionMatrix();

            for (TrainingInstance trainingInstance : coveredInstances.values()) {
                boolean trueLabel = trainingInstance.stringValue(labelIndex).equals("1");
                aggregate(targetPrediction, trueLabel, trainingInstance.weight(), labelWiseConfusionMatrix,
                        globalConfusionMatrix);
            }

            double remainingPositives = (targetPrediction ? positives : negatives)
                    - labelWiseConfusionMatrix.getNumberOfTruePositives();
            double remainingNegatives = (targetPrediction ? negatives : positives)
                    - labelWiseConfusionMatrix.getNumberOfFalsePositives();
            labelWiseConfusionMatrix.addFalseNegatives(remainingPositives);
            globalConfusionMatrix.addFalseNegatives(remainingPositives);
            labelWiseConfusionMatrix.addTrueNegatives(remainingNegatives);
            globalConfusionMatrix.addTrueNegatives(remainingNegatives);
            rule.getHead().setLabelWiseConfusionMatrix(labelIndex, labelWiseConfusionMatrix);
        }

        rule.setConfusionMatrix(globalConfusionMatrix);
    }

}
