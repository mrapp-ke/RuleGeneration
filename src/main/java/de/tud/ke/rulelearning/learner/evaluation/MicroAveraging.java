package de.tud.ke.rulelearning.learner.evaluation;

import de.mrapp.util.datastructure.Pair;
import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.model.Head;
import de.tud.ke.rulelearning.model.DataSet;
import de.tud.ke.rulelearning.model.Rule;
import weka.core.Instance;

import java.util.Set;

public class MicroAveraging extends AveragingStrategy {

    public MicroAveraging(final AggregationStrategy aggregationStrategy) {
        super(aggregationStrategy);
    }

    @Override
    protected Pair<Double, Integer> evaluate(final DataSet dataSet, final Rule rule,
                                             final Heuristic heuristic, Set<Integer> relevantLabels,
                                             final ConfusionMatrix stats) {
        int coverage = 0;

        for (Instance instance : dataSet) {
            boolean covers = rule.covers(instance);

            if (covers) {
                coverage++;
            }

            Head head = rule.getHead();

            for (int labelIndex : relevantLabels) {
                aggregate(dataSet, covers, head, instance, labelIndex, stats, null);
            }
        }

        double heuristicValue = heuristic.evaluateConfusionMatrix(stats);
        return Pair.Companion.create(heuristicValue, coverage);
    }

    @Override
    public String toString() {
        return "mm";
    }

}