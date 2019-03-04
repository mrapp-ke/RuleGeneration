package de.tud.ke.rulelearning.learner.covering;

public class StoppingCriterionFactory {

    public static StoppingCriterion create(final StoppingCriterion.Type type) {
        if (type != null) {
            switch (type) {
                case COVERAGE:
                    return new CoverageStoppingCriterion();
                default:
                    throw new IllegalArgumentException("Unknown stopping criterion: " + type);
            }
        }

        return null;
    }

}
