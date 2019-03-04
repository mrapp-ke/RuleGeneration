package de.tud.ke.rulelearning.learner.evaluation;

import de.mrapp.util.datastructure.Pair;
import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.model.Head;
import de.tud.ke.rulelearning.model.DataSet;
import de.tud.ke.rulelearning.model.Rule;
import weka.core.Instance;

import java.util.Set;

public abstract class AveragingStrategy {

    public class Result {

        private final ConfusionMatrix confusionMatrix;

        private final double heuristicValue;

        private final int coverage;

        private Result(final ConfusionMatrix confusionMatrix, final double heuristicValue, final int coverage) {
            this.confusionMatrix = confusionMatrix;
            this.heuristicValue = heuristicValue;
            this.coverage = coverage;
        }

        public ConfusionMatrix getConfusionMatrix() {
            return confusionMatrix;
        }

        public double getHeuristicValue() {
            return heuristicValue;
        }

        public int getCoverage() {
            return coverage;
        }

    }

    private final AggregationStrategy aggregationStrategy;

    public AveragingStrategy(final AggregationStrategy aggregationStrategy) {
        this.aggregationStrategy = aggregationStrategy;
    }

    final void aggregate(final DataSet dataSet, final boolean covers, final Head head,
                         final Instance instance, final int labelIndex, final ConfusionMatrix confusionMatrix,
                         final ConfusionMatrix stats) {
        aggregationStrategy.aggregate(dataSet, covers, head, instance, labelIndex, confusionMatrix, stats);
    }

    public final Result evaluate(final DataSet dataSet, final Rule rule,
                                 final Heuristic heuristic, final Set<Integer> relevantLabels) {
        ConfusionMatrix stats = new ConfusionMatrix();
        Pair<Double, Integer> result = evaluate(dataSet, rule, heuristic, relevantLabels, stats);
        return new Result(stats, result.getFirst(), result.getSecond());
    }

    protected abstract Pair<Double, Integer> evaluate(final DataSet dataSet, final Rule rule,
                                                      final Heuristic heuristic, final Set<Integer> relevantLabels,
                                                      final ConfusionMatrix confusionMatrix);

}