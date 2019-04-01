package de.tud.ke.rulelearning.model;

import org.jetbrains.annotations.NotNull;
import weka.core.Attribute;
import weka.core.Instance;

import java.io.Serializable;
import java.util.Objects;

public abstract class Condition implements Comparable<Condition>, Serializable {

    private static final long serialVersionUID = 1L;

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
    public int compareTo(@NotNull final Condition o) {
        return Integer.compare(index(), o.index());
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
