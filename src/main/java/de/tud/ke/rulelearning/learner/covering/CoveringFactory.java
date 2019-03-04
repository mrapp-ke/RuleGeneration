package de.tud.ke.rulelearning.learner.covering;

public class CoveringFactory {

    public static Covering create(final Covering.Type type, final StoppingCriterion.Type stoppingCriterion) {
        if (type != null) {
            switch (type) {
                case LABEL_WISE:
                    return new LabelWiseCovering(true, StoppingCriterionFactory.create(stoppingCriterion));
                case LABEL_WISE_NO_REVALIDATION:
                    return new LabelWiseCovering(false, StoppingCriterionFactory.create(stoppingCriterion));
                default:
                    throw new IllegalArgumentException("Unknown covering type: " + type);
            }
        }

        return null;
    }

}
