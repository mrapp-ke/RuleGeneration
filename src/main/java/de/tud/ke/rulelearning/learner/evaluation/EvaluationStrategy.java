package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.model.Head;
import de.tud.ke.rulelearning.model.DataSet;

import java.util.Set;

public interface EvaluationStrategy {

    Set<Integer> getRelevantLabels(DataSet dataSet, Head head);

}