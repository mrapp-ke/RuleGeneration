package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.model.Head;
import de.tud.ke.rulelearning.model.DataSet;

import java.util.Collections;
import java.util.Set;

public class SingleLabelEvaluationStrategy implements EvaluationStrategy {

    private final int labelIndex;

    public SingleLabelEvaluationStrategy(final int labelIndex) {
        this.labelIndex = labelIndex;
    }

    @Override
    public final Set<Integer> getRelevantLabels(final DataSet dataSet, final Head head) {
        return Collections.singleton(labelIndex);
    }

    @Override
    public String toString() {
        return "single-label";
    }

}