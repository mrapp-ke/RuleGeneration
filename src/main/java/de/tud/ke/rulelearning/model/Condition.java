package de.tud.ke.rulelearning.model;

import weka.core.Attribute;
import weka.core.Instance;

import java.io.Serializable;
import java.util.Objects;

public abstract class Condition implements Serializable {

    private final Attribute attribute;

    public Condition(final Attribute attribute) {
        this.attribute = attribute;
    }

    public abstract boolean covers(final Instance instance);

    public Attribute getAttribute() {
        return attribute;
    }

    public int index() {
        return attribute.index();
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute.index());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condition condition = (Condition) o;
        return Objects.equals(attribute.index(), condition.attribute.index());
    }

}
