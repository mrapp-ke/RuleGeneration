package de.tud.ke.rulelearning.learner.covering;

public class CoveringFactory {

    public static Covering create(final Covering.Type type) {
        if (type != null) {
            switch (type) {
                case LABEL_WISE:
                    return new LabelWiseCovering(true);
                case LABEL_WISE_NO_REVALIDATION:
                    return new LabelWiseCovering(false);
                default:
                    throw new IllegalArgumentException("Unknown covering type: " + type);
            }
        }

        return null;
    }

}
