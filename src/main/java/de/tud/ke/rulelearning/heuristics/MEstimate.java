package de.tud.ke.rulelearning.heuristics;

public class MEstimate extends Heuristic {

    private final double m;

    public MEstimate(final double m) {
        this.m = m;
    }

    public double getM() {
        return m;
    }

    @Override
    public double evaluateConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        if (m == Double.POSITIVE_INFINITY) {
            return new Accuracy().evaluateConfusionMatrix(confusionMatrix);
        } else {
            return (confusionMatrix.getNumberOfTruePositives() + this.m * confusionMatrix.getNumberOfPositives() /
                    confusionMatrix.getNumberOfExamples()) / (confusionMatrix.getNumberOfPredictedPositive() + this.m);
        }
    }

    @Override
    public String toString() {
        return m + "-estimate";
    }

}
