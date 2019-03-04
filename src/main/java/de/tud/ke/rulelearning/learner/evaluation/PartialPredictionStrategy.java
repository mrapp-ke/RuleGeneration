package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.model.Condition;
import de.tud.ke.rulelearning.model.Head;
import de.tud.ke.rulelearning.model.DataSet;

import java.util.Set;
import java.util.stream.Collectors;

public class PartialPredictionStrategy implements EvaluationStrategy {

    @Override
    public final Set<Integer> getRelevantLabels(final DataSet dataSet, final Head head) {
        return head.getConditions().stream().map(Condition::index).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "partial";
    }

}