package de.tud.ke.rulelearning.model;

import java.util.ArrayList;
import java.util.List;

public class MultipleRuleStats {

    private final List<RuleStats> ruleStats = new ArrayList<>();

    public void addRuleStats(final RuleStats ruleStats) {
        this.ruleStats.add(ruleStats);
    }

    public double getAvgInstanceCount() {
        int totalInstanceCount = 0;

        for (RuleStats stats : ruleStats) {
            totalInstanceCount += stats.getInstanceCoveringStats().getTotalCount();
        }

        return (double) totalInstanceCount / (double) ruleStats.size();
    }

    public double getAvgCoveredInstanceCount() {
        int totalCoveredInstanceCount = 0;

        for (RuleStats stats : ruleStats) {
            totalCoveredInstanceCount += stats.getInstanceCoveringStats().getCoveredCount();
        }

        return (double) totalCoveredInstanceCount / (double) ruleStats.size();
    }

    public double getAvgCoveredInstanceRatio() {
        double totalCoveredInstanceRatio = 0d;

        for (RuleStats stats : ruleStats) {
            totalCoveredInstanceRatio += stats.getInstanceCoveringStats().getCoveredRatio();
        }

        return totalCoveredInstanceRatio / (double) ruleStats.size();
    }

    public double getAvgRuleCount() {
        int totalRuleCount = 0;

        for (RuleStats stats : ruleStats) {
            totalRuleCount += stats.getRuleCount();
        }

        return (double) totalRuleCount / (double) ruleStats.size();
    }

    public double getAvgMinHeadSize() {
        int totalMinHeadSize = 0;

        for (RuleStats stats : ruleStats) {
            totalMinHeadSize += stats.getMinHeadSize();
        }

        return (double) totalMinHeadSize / (double) ruleStats.size();
    }

    public double getAvgMaxHeadSize() {
        int totalMaxHeadSize = 0;

        for (RuleStats stats : ruleStats) {
            totalMaxHeadSize += stats.getMaxHeadSize();
        }

        return (double) totalMaxHeadSize / (double) ruleStats.size();
    }

    public double getAvgHeadSize() {
        double totalAvgHeadSize = 0;

        for (RuleStats stats : ruleStats) {
            totalAvgHeadSize += stats.getAvgHeadSize();
        }

        return totalAvgHeadSize / (double) ruleStats.size();
    }

    public double getAvgMinBodySize() {
        int totalMinBodySize = 0;

        for (RuleStats stats : ruleStats) {
            totalMinBodySize += stats.getMinBodySize();
        }

        return (double) totalMinBodySize / (double) ruleStats.size();
    }

    public double getAvgMaxBodySize() {
        int totalMaxBodySize = 0;

        for (RuleStats stats : ruleStats) {
            totalMaxBodySize += stats.getMaxBodySize();
        }

        return (double) totalMaxBodySize / (double) ruleStats.size();
    }

    public double getAvgBodySize() {
        double totalAvgBodySize = 0;

        for (RuleStats stats : ruleStats) {
            totalAvgBodySize += stats.getAvgBodySize();
        }

        return totalAvgBodySize / (double) ruleStats.size();
    }

    public double getAvgPositivePredictionRatio() {
        double totalAvgPositivePredictionRatio = 0;

        for (RuleStats stats : ruleStats) {
            totalAvgPositivePredictionRatio += stats.getPositivePredictionRatio();
        }

        return totalAvgPositivePredictionRatio / (double) ruleStats.size();
    }

    public double getAvgLabelOccurrences(final int labelIndex) {
        int totalOccurrences = 0;

        for (RuleStats stats : ruleStats) {
            totalOccurrences += stats.getLabelCoveringStats(labelIndex).getTotalCount();
        }

        return (double) totalOccurrences / (double) ruleStats.size();
    }

    public double getAvgCoveredLabelOccurrences(final int labelIndex) {
        int totalCoveredOccurrences = 0;

        for (RuleStats stats : ruleStats) {
            totalCoveredOccurrences += stats.getLabelCoveringStats(labelIndex).getCoveredCount();
        }

        return (double) totalCoveredOccurrences / (double) ruleStats.size();
    }

    public double getAvgCoveredLabelRatio(final int labelIndex) {
        double totalCoveredRatio = 0;

        for (RuleStats stats : ruleStats) {
            totalCoveredRatio += stats.getLabelCoveringStats(labelIndex).getCoveredRatio();
        }

        return totalCoveredRatio / (double) ruleStats.size();
    }

    public double getAvgPredictions(final int labelIndex) {
        int totalPredictionCount = 0;

        for (RuleStats stats : ruleStats) {
            totalPredictionCount += stats.getLabelPredictionStats(labelIndex).getTotalCount();
        }

        return (double) totalPredictionCount / (double) ruleStats.size();
    }

    public double getAvgPredictionBodySize(final int labelIndex) {
        double totalAvgPredictionBodySize = 0;

        for (RuleStats stats : ruleStats) {
            totalAvgPredictionBodySize += stats.getLabelConditionStats(labelIndex)
                    .getCoveredRatio();
        }

        return totalAvgPredictionBodySize / (double) ruleStats.size();
    }

    public double getAvgPredictionHeadSize(final int labelIndex) {
        double totalAvgPredictionHeadSize = 0;

        for (RuleStats stats : ruleStats) {
            totalAvgPredictionHeadSize += stats.getLabelPredictionStats(labelIndex)
                    .getCoveredRatio();
        }

        return totalAvgPredictionHeadSize / (double) ruleStats.size();
    }

}
