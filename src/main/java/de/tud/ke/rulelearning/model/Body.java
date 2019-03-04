package de.tud.ke.rulelearning.model;

import weka.core.Instance;

public class Body extends ConditionSet {

    public Body(final Condition... conditions) {
        super(conditions);
    }

    public Body(final Iterable<Condition> conditions) {
        super(conditions);
    }

    public boolean covers(final Instance instance) {
        return getConditions().stream().allMatch(condition -> condition.covers(instance));
    }

}
