package de.tud.ke.rulelearning.model;

import weka.core.Attribute;
import weka.core.Instance;

import java.util.Objects;

public class NominalCondition extends Condition {

    private final String value;

    public NominalCondition(final Attribute attribute, final String value) {
        super(attribute);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean covers(final Instance instance) {
        int index = index();
        return !instance.isMissing(index) && value.equals(instance.stringValue(index));
    }

    @Override
    public String toString() {
        return getAttribute().name() + " = " + value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NominalCondition that = (NominalCondition) o;
        return Objects.equals(value, that.value);
    }

}
