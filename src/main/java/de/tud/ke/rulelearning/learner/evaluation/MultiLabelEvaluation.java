package de.tud.ke.rulelearning.learner.evaluation;

import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.model.DataSet;
import de.tud.ke.rulelearning.model.Rule;

import java.util.Set;

public class MultiLabelEvaluation {

    public interface ResultHandler {

        void handleResult(Rule rule, AveragingStrategy.Result result);

    }

    private final Heuristic heuristic;

    private final EvaluationStrategy evaluationStrategy;

    private final AveragingStrategy averagingStrategy;

    private final ResultHandler resultHandler;

    public MultiLabelEvaluation(final Heuristic heuristic, final EvaluationStrategy evaluationStrategy,
                                final AveragingStrategy averagingStrategy) {
        this(heuristic, evaluationStrategy, averagingStrategy, (rule, result) -> {
            rule.setConfusionMatrix(result.getConfusionMatrix());
            rule.setHeuristicValue(result.getHeuristicValue());
            rule.setCoverage(result.getCoverage());
        });
    }

    public MultiLabelEvaluation(final Heuristic heuristic, final EvaluationStrategy evaluationStrategy,
                                final AveragingStrategy averagingStrategy, final ResultHandler resultHandler) {
        this.heuristic = heuristic;
        this.evaluationStrategy = evaluationStrategy;
        this.averagingStrategy = averagingStrategy;
        this.resultHandler = resultHandler;
    }

    public final void evaluate(final DataSet dataSet, final Rule rule) {
        Set<Integer> relevantLabels = evaluationStrategy.getRelevantLabels(dataSet, rule.getHead());
        AveragingStrategy.Result result = averagingStrategy.evaluate(dataSet, rule, heuristic, relevantLabels);
        resultHandler.handleResult(rule, result);
    }

    public final Heuristic getHeuristic() {
        return heuristic;
    }

    public final EvaluationStrategy getEvaluationStrategy() {
        return evaluationStrategy;
    }

    public final AveragingStrategy getAveragingStrategy() {
        return averagingStrategy;
    }

    @Override
    public String toString() {
        return evaluationStrategy.toString() + "_" + averagingStrategy.toString() + "_" + heuristic.toString();
    }

}