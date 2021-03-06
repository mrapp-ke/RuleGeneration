package de.tud.ke.rulelearning.model;

import weka.core.Attribute;
import weka.core.Instance;

import java.util.Objects;
import java.util.function.BiFunction;

public class NumericCondition extends Condition {

    public enum Comparator {

        GREATER_OR_EQUAL(">=", (v1, v2) -> v1 >= v2),

        LESS("<", (v1, v2) -> v1 < v2);

        private final String sign;

        private final BiFunction<Double, Double, Boolean> comparator;

        Comparator(final String sign, final BiFunction<Double, Double, Boolean> comparator) {
            this.sign = sign;
            this.comparator = comparator;
        }

        public boolean test(final double actualValue, final double expectedValue) {
            return comparator.apply(actualValue, expectedValue);
        }

        public String getSign() {
            return sign;
        }

    }

    private static final long serialVersionUID = 1L;

    private final double value;

    private final Comparator comparator;

    public NumericCondition(final Attribute attribute, final double value,
                            final Comparator comparator) {
        super(attribute);
        this.value = value;
        this.comparator = comparator;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean covers(final Instance instance) {
        int index = index();
        return !instance.isMissing(index) && comparator.test(instance.value(index), value);
    }

    @Override
    public String toString() {
        return getAttribute().name() + " " + comparator.sign + " " + value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, comparator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NumericCondition that = (NumericCondition) o;
        return Double.compare(that.value, value) == 0 &&
                comparator == that.comparator;
    }

}
