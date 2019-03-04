package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.model.Head;
import de.tud.ke.rulelearning.model.DataSet;
import weka.core.Instance;

public interface AggregationStrategy {

    void aggregate(DataSet dataSet, boolean covers, Head head, Instance instance, int labelIndex,
                   ConfusionMatrix confusionMatrix, ConfusionMatrix stats);

}
