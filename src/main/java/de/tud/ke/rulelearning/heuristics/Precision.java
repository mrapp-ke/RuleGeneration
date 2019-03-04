package de.tud.ke.rulelearning.heuristics;

public class Precision extends Heuristic {

    @Override
    public double evaluateConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        double predictedPositive = confusionMatrix.getNumberOfPredictedPositive();
        return predictedPositive != 0 ? confusionMatrix.getNumberOfTruePositives() / predictedPositive : 0;
    }

    @Override
    public String toString() {
        return "precision";
    }

}
