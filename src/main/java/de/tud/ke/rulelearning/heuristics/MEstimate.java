package de.tud.ke.rulelearning.heuristics;

import de.mrapp.util.Condition;

public class MEstimate extends Heuristic {

    private final double m;

    public MEstimate(final double m) {
        Condition.INSTANCE.ensureAtLeast(m, 0, "The value of the m-parameter must be at least 0");
        this.m = m;
    }

    public double getM() {
        return m;
    }

    @Override
    public double evaluateConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        if (m == 0) {
            return new Precision().evaluateConfusionMatrix(confusionMatrix);
        } else if (m == Double.POSITIVE_INFINITY) {
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
