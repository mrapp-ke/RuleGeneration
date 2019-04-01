package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;

import java.util.HashMap;
import java.util.Map;

public class Head extends ConditionSet {

    private static final long serialVersionUID = 1L;

    private final Map<Integer, Measurable> labelWiseMeasurables = new HashMap<>();

    public Head(final Condition... conditions) {
        super(conditions);
    }

    public Head(final Iterable<Condition> conditions) {
        super(conditions);
    }

    public ConfusionMatrix getLabelWiseConfusionMatrix(final int index) {
        return labelWiseMeasurables.computeIfAbsent(index, x -> new MeasurableImpl()).getConfusionMatrix();
    }

    public void setLabelWiseConfusionMatrix(final int index, final ConfusionMatrix confusionMatrix) {
        Measurable measurable = labelWiseMeasurables.computeIfAbsent(index, x -> new MeasurableImpl());
        measurable.setConfusionMatrix(confusionMatrix);
    }

    public double getLabelWiseHeuristicValue(final int index) {
        Measurable measurable = labelWiseMeasurables.get(index);
        return measurable != null ? measurable.getHeuristicValue() : 0d;
    }

    public void setLabelWiseHeuristicValue(final int index, final double heuristicValue) {
        Measurable measurable = labelWiseMeasurables.computeIfAbsent(index, x -> new MeasurableImpl());
        measurable.setHeuristicValue(heuristicValue);
    }

}
