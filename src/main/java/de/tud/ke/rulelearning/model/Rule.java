package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.TieBreaker;
import org.jetbrains.annotations.NotNull;
import weka.core.Instance;

import java.io.Serializable;
import java.util.Objects;

public class Rule implements Measurable, Comparable<Rule>, Serializable {

    public static final TieBreaker<Rule> TIE_BREAKER = (rule1, rule2) -> {
        Head head1 = rule1.getHead();
        Head head2 = rule2.getHead();
        double tp1 = head1.getConditions().stream().map(Condition::index)
                .map(head1::getLabelWiseConfusionMatrix)
                .reduce(0d, (tp, confusionMatrix) -> tp + confusionMatrix.getNumberOfTruePositives(),
                        (a, b) -> a + b);
        double tp2 = head2.getConditions().stream().map(Condition::index)
                .map(head2::getLabelWiseConfusionMatrix)
                .reduce(0d, (tp, confusionMatrix) -> tp + confusionMatrix.getNumberOfTruePositives(),
                        (a, b) -> a + b);
        int comp = Double.compare(tp2, tp1);

        if (comp == 0) {
            comp = Integer.compare(rule1.getBody().size(), rule2.getBody().size());
        }

        return comp;
    };

    private static final long serialVersionUID = 1L;

    private final Body body;

    private final Head head;

    private final MeasurableImpl measurable = new MeasurableImpl();

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
        int comp = body.compareTo(o.getBody());
        return comp != 0 ? comp : head.compareTo(o.getHead());
    }

    @Override
    public String toString() {
        return head + " <-- " + body + ", confusionMatrix = " + getConfusionMatrix() +
                ", heuristicValue = " + getHeuristicValue();
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
