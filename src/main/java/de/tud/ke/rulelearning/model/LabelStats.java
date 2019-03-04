package de.tud.ke.rulelearning.model;

import weka.core.Instance;

import java.util.HashMap;
import java.util.Map;

public class LabelStats {

    private final DataSet dataSet;

    private Map<Integer, Integer> labelOccurrencesInData = new HashMap<>();

    private Map<Integer, Integer> labelOccurrencesInRules = new HashMap<>();

    private int instanceCount = 0;

    private int ruleCount = 0;

    private void countLabelOccurrencesInData(final DataSet dataSet) {
        for (Instance instance : dataSet) {
            for (int labelIndex : dataSet.getLabelIndices()) {
                if (dataSet.getLabelValue(instance, labelIndex)) {
                    int count = labelOccurrencesInData.computeIfAbsent(labelIndex, x -> 0);
                    labelOccurrencesInData.put(labelIndex, count + 1);
                }

            }

            instanceCount++;
        }
    }

    private void countLabelOccurrencesInRules(final DataSet dataSet,
                                              final Iterable<Rule> rules) {
        for (Rule rule : rules) {
            for (Condition labelCondition : rule.getHead().getConditions()) {
                if (labelCondition instanceof NominalCondition &&
                        ((NominalCondition) labelCondition).getValue().equals("1")) {
                    int labelIndex = labelCondition.index();
                    int count = labelOccurrencesInRules.computeIfAbsent(labelIndex, x -> 0);
                    labelOccurrencesInRules.put(labelIndex, count + 1);
                }
            }

            ruleCount++;
        }
    }

    public LabelStats(final DataSet dataSet) {
        this.dataSet = dataSet;
        countLabelOccurrencesInData(dataSet);
    }

    public void addRules(final Iterable<Rule> rules) {
        countLabelOccurrencesInRules(dataSet, rules);
    }

    public int getP(final int labelIndex) {
        Integer count = labelOccurrencesInData.get(labelIndex);
        return count != null ? count : 0;
    }

    public int getN(final int labelIndex) {
        Integer count = labelOccurrencesInData.get(labelIndex);
        return count != null ? instanceCount - count : 0;
    }

    public double getLabelDensityInData(final int labelIndex) {
        Integer count = labelOccurrencesInData.get(labelIndex);
        return count != null && instanceCount > 0 ? ((double) count / (double) instanceCount) : 0d;
    }

    public double getLabelDensityInRules(final int labelIndex) {
        Integer count = labelOccurrencesInRules.get(labelIndex);
        return count != null && ruleCount > 0 ? ((double) count / (double) ruleCount) : 0d;
    }

}
