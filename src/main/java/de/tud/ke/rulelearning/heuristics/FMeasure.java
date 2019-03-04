package de.tud.ke.rulelearning.heuristics;

import de.mrapp.util.Condition;

public class FMeasure extends Heuristic {

    private final double beta;

    public FMeasure() {
        this(1d);
    }

    public FMeasure(final double beta) {
        Condition.INSTANCE.ensureAtLeast(beta, 0, "The value of the beta-parameter must be at least 0");
        this.beta = beta;
    }

    public double getBeta() {
        return beta;
    }

    @Override
    public double evaluateConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        if (beta == 0) {
            return new Precision().evaluateConfusionMatrix(confusionMatrix);
        } else {
            double r = new Recall().evaluateConfusionMatrix(confusionMatrix);

            if (beta == Double.POSITIVE_INFINITY) {
                return r;
            } else {
                double p = new Precision().evaluateConfusionMatrix(confusionMatrix);
                double numerator = (Math.pow(beta, 2) + 1) * r * p;
                double denominator = Math.pow(beta, 2) * p + r;
                return denominator > 0 ? numerator / denominator : 0;
            }
        }
    }

    @Override
    public String toString() {
        return "F" + beta + "measure";
    }

}
