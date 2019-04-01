package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;

import java.io.Serializable;
import java.util.Objects;

public class MeasurableImpl implements Measurable, Serializable {

    private static final long serialVersionUID = 1L;

    private double heuristicValue = 0d;

    private ConfusionMatrix confusionMatrix;

    @Override
    public double getHeuristicValue() {
        return heuristicValue;
    }

    @Override
    public void setHeuristicValue(final double heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    @Override
    public ConfusionMatrix getConfusionMatrix() {
        return confusionMatrix;
    }

    @Override
    public void setConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasurableImpl that = (MeasurableImpl) o;
        return Double.compare(that.heuristicValue, heuristicValue) == 0 &&
                Objects.equals(confusionMatrix, that.confusionMatrix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heuristicValue, confusionMatrix);
    }

}
