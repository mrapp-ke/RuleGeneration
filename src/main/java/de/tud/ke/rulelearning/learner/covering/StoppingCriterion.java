package de.tud.ke.rulelearning.learner.covering;

import de.tud.ke.rulelearning.model.Rule;

public interface StoppingCriterion {

    enum Type {

        COVERAGE("coverage");

        private final String value;

        Type(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Type fromValue(final String value) {
            for (Type type : values()) {
                if (type.getValue().equals(value)) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Invalid enum value: " + value);
        }

    }

    boolean isSatisfied(final Rule rule, final int labelIndex);

}
