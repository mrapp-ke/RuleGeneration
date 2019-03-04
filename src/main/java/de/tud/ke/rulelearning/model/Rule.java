package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import org.jetbrains.annotations.NotNull;
import weka.core.Instance;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a multi-label rule consisting of a body and a head.
 *
 * @author Michael Rapp <mrapp@ke.tu-darmstadt.de>
 */
public class Rule implements Measurable, Comparable<Rule>, Serializable {

    private final Body body;

    private final Head head;

    private final MeasurableImpl measurable = new MeasurableImpl();

    private int coverage;

    private double weight = 1;

    public Rule() {
        this(new Body(), new Head());
    }

    public Rule(final Body body, final Head head) {
        this.body = body;
        this.head = head;
    }

    public Body getBody() {
        return body;
    }

    public Head getHead() {
        return head;
    }

    public void setCoverage(final int coverage) {
        this.coverage = coverage;
    }

    public int getCoverage() {
        return coverage;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(final double weight) {
        this.weight = weight;
    }

    public boolean covers(final Instance instance) {
        return body.covers(instance);
    }

    @Override
    public double getHeuristicValue() {
        return measurable.getHeuristicValue();
    }

    @Override
    public void setHeuristicValue(final double heuristicValue) {
        this.measurable.setHeuristicValue(heuristicValue);
    }

    @Override
    public ConfusionMatrix getConfusionMatrix() {
        return measurable.getConfusionMatrix();
    }

    @Override
    public void setConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        this.measurable.setConfusionMatrix(confusionMatrix);
    }

    @Override
    public int compareTo(@NotNull final Rule o) {
        return measurable.compareTo(o);
    }

    @Override
    public String toString() {
        return head + " <-- " + body + ", confusionMatrix = " + getConfusionMatrix() +
                ", heuristicValue = " + getHeuristicValue() + ", coverage = " + coverage +
                ", weight = " + weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, head);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(body, rule.body) &&
                Objects.equals(head, rule.head);
    }

}
