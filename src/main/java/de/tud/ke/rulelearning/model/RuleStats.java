package de.tud.ke.rulelearning.model;

import weka.core.Instance;

import java.util.HashMap;
import java.util.Map;

public class RuleStats {

    public static class CoveringStats {

        private int totalCount = 0;

        private int coveredCount = 0;

        public int getTotalCount() {
            return totalCount;
        }

        public int getCoveredCount() {
            return coveredCount;
        }

        public double getCoveredRatio() {
            return totalCount > 0 ? (double) coveredCount / (double) totalCount : 0d;
        }

    }

    private final CoveringStats instanceCoveringStats = new CoveringStats();

    private final Map<Integer, CoveringStats> labelCoveringStats = new HashMap<>();

    private final Map<Integer, CoveringStats> labelPredictionStats = new HashMap<>();

    private final Map<Integer, CoveringStats> labelConditionStats = new HashMap<>();

    private int ruleCount;

    private int minHeadSize;

    private int maxHeadSize;

    private int totalHeadSize;

    private int minBodySize;

    private int maxBodySize;

    private int totalBodySize;

    private int positivePredictions;

    private void countCoveredInstances(final DataSet dataSet,
                                       final Iterable<Rule> rules) {
        for (Instance instance : dataSet) {
            for (Rule rule : rules) {
                if (rule.covers(instance)) {
                    instanceCoveringStats.coveredCount++;
                    break;
                }
            }

            instanceCoveringStats.totalCount++;
        }
    }

    private void countCoveredLabels(final DataSet dataSet,
                                    final Iterable<Rule> rules) {
        for (int labelIndex : dataSet.getLabelIndices()) {
            countCoveredLabels(dataSet, rules, labelIndex);
        }
    }

    private void countCoveredLabels(final DataSet dataSet,
                                    final Iterable<Rule> rules, final int labelIndex) {
        boolean targetPrediction = dataSet.getTargetPrediction(labelIndex);

        for (Instance instance : dataSet) {
            boolean isRelevant = instance.stringValue(labelIndex).equals("1");

            if (isRelevant) {
                CoveringStats coveringStats = getLabelCoveringStats(labelIndex);
                coveringStats.totalCount++;
                CoveringStats predictionStats = getLabelPredictionStats(labelIndex);
                CoveringStats conditionStats = getLabelConditionStats(labelIndex);

                for (Rule rule : rules) {
                    if (rule.covers(instance)) {
                        Condition condition = rule.getHead().getCondition(labelIndex);

                        if (condition instanceof NominalCondition &&
                                ((NominalCondition) condition).getValue().equals(targetPrediction ? "1" : "0")) {
                            coveringStats.coveredCount++;
                            predictionStats.totalCount++;
                            predictionStats.coveredCount += rule.getHead().size();
                            conditionStats.totalCount++;
                            conditionStats.coveredCount += rule.getBody().size();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void countRuleStats(final Iterable<Rule> rules) {
        boolean firstRule = true;

        for (Rule rule : rules) {
            ruleCount++;
            minBodySize = firstRule ? rule.getBody().size() :
                    Math.min(minBodySize, rule.getBody().size());
            maxBodySize = firstRule ? rule.getBody().size() :
                    Math.max(maxBodySize, rule.getBody().size());
            totalBodySize += rule.getBody().size();
            minHeadSize = firstRule ? rule.getHead().size() :
                    Math.min(minHeadSize, rule.getHead().size());
            maxHeadSize = firstRule ? rule.getHead().size() :
                    Math.max(maxHeadSize, rule.getHead().size());
            totalHeadSize += rule.getHead().size();
            positivePredictions += rule.getHead().getConditions().stream()
                    .filter(condition -> condition instanceof NominalCondition &&
                            ((NominalCondition) condition).getValue().equals("1")).count();
            firstRule = false;
        }
    }

    public RuleStats(final DataSet dataSet, final Iterable<Rule> rules) {
        countCoveredInstances(dataSet, rules);
        countCoveredLabels(dataSet, rules);
        countRuleStats(rules);
    }

    public CoveringStats getInstanceCoveringStats() {
        return instanceCoveringStats;
    }

    public CoveringStats getLabelCoveringStats(final int labelIndex) {
        return labelCoveringStats.computeIfAbsent(labelIndex, x -> new CoveringStats());
    }

    public CoveringStats getLabelPredictionStats(final int labelIndex) {
        return labelPredictionStats.computeIfAbsent(labelIndex, x -> new CoveringStats());
    }

    public CoveringStats getLabelConditionStats(final int labelIndex) {
        return labelConditionStats.computeIfAbsent(labelIndex, x -> new CoveringStats());
    }

    public int getRuleCount() {
        return ruleCount;
    }

    public int getMinHeadSize() {
        return minHeadSize;
    }

    public int getMaxHeadSize() {
        return maxHeadSize;
    }

    public double getAvgHeadSize() {
        return (double) totalHeadSize / (double) ruleCount;
    }

    public int getMinBodySize() {
        return minBodySize;
    }

    public int getMaxBodySize() {
        return maxBodySize;
    }

    public double getAvgBodySize() {
        return (double) totalBodySize / (double) ruleCount;
    }

    public double getPositivePredictionRatio() {
        return (double) positivePredictions / (double) totalHeadSize;
    }

}
