package de.tud.ke.rulelearning.learner.covering;

import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.model.DataSet;
import de.tud.ke.rulelearning.model.LabelStats;
import de.tud.ke.rulelearning.model.RuleCollection;

public interface Covering {

    enum Type {

        LABEL_WISE("label-wise"),

        LABEL_WISE_NO_REVALIDATION("label-wise-no-revalidation");

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

    RuleCollection getCoveringRules(RuleCollection rules, DataSet trainingDataSet, LabelStats labelStats,
                                    Heuristic heuristic) throws Exception;

}
