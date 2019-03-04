package de.tud.ke.rulelearning.heuristics;

public class Recall extends Heuristic {

    @Override
    public double evaluateConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        double truePositives = confusionMatrix.getNumberOfTruePositives();
        double falseNegatives = confusionMatrix.getNumberOfFalseNegatives();
        double denominator = truePositives + falseNegatives;
        return denominator != 0 ? truePositives / denominator : 0;
    }

    @Override
    public String toString() {
        return "recall";
    }

}
