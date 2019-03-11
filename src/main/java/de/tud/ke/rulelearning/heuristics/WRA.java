package de.tud.ke.rulelearning.heuristics;

public class WRA extends Heuristic {

    @Override
    public double evaluateConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        double p = confusionMatrix.getNumberOfPredictedPositive();
        double n = confusionMatrix.getNumberOfPredictedNegative();
        double P = confusionMatrix.getNumberOfPositives();
        double N = confusionMatrix.getNumberOfNegatives();
        return (p + n) / (P + N) * (p / (p + n) - P / (P + N));
    }

}
