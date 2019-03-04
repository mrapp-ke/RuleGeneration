package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.util.IteratorUtil;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.GroundTruth;
import org.jetbrains.annotations.NotNull;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class DataSet implements Iterable<Instance>, Serializable {

    private final MultiLabelInstances dataSet;

    private final Map<Integer, Instances> instancesSortedByNumericAttributes;

    private final Map<Integer, Map<String, Instances>> instancesIndexedByNominalAttributes;

    private final Set<Integer> labelIndices;

    private Map<Integer, Map<String, Instances>> createInstancesIndexedByNominalAttributes(
            final Collection<Attribute> featureAttributes,
            final Collection<Attribute> labelAttributes) {
        Map<Integer, Map<String, Instances>> result = new HashMap<>(
                featureAttributes.size() + labelAttributes.size(), 1f);

        for (Attribute attribute : IteratorUtil.concatIterables(featureAttributes, labelAttributes)) {
            if (attribute.isNominal()) {
                for (String value : IteratorUtil
                        .createForLoopIterable(attribute, Attribute::numValues, Attribute::value)) {
                    Instances filteredInstances = new Instances(dataSet.getDataSet());
                    int index = attribute.index();

                    for (int i = filteredInstances.size() - 1; i >= 0; i--) {
                        Instance instance = filteredInstances.get(i);

                        if (instance.isMissing(index) | !value.equals(instance.stringValue(index))) {
                            filteredInstances.remove(i);
                        }
                    }

                    if (!filteredInstances.isEmpty()) {
                        Map<String, Instances> map = result.computeIfAbsent(index,
                                x -> new HashMap<>(attribute.numValues(), 1f));
                        map.put(value, filteredInstances);
                    }
                }
            }
        }
        return result;
    }

    private Map<Integer, Instances> createInstancesSortedByNumericAttributes(
            final Collection<Attribute> featureAttributes) {
        Map<Integer, Instances> result = new HashMap<>(featureAttributes.size(), 1f);
        featureAttributes.stream().filter(Attribute::isNumeric)
                .forEach(attribute -> {
                    Instances sortedInstances = new Instances(dataSet.getDataSet());
                    int index = attribute.index();
                    sortedInstances.sort(index);
                    result.put(index, sortedInstances);
                });
        return result;
    }

    public DataSet(final MultiLabelInstances dataSet) {
        this.dataSet = dataSet;
        Collection<Attribute> featureAttributes = dataSet.getFeatureAttributes();
        this.instancesSortedByNumericAttributes = createInstancesSortedByNumericAttributes(
                featureAttributes);
        this.instancesIndexedByNominalAttributes = createInstancesIndexedByNominalAttributes(
                featureAttributes, dataSet.getLabelAttributes());
        this.labelIndices = Arrays.stream(dataSet.getLabelIndices()).boxed()
                .collect(Collectors.toSet());
    }

    public MultiLabelInstances getDataSet() {
        return dataSet;
    }

    public Set<Integer> getLabelIndices() {
        return labelIndices;
    }

    public boolean isLabel(final Attribute attribute) {
        return isLabel(attribute.index());
    }

    public boolean isLabel(final int index) {
        return labelIndices.contains(index);
    }

    public GroundTruth getGroundTruth(final Instance instance) {
        int[] labelIndices = dataSet.getLabelIndices();
        boolean[] trueLabels = new boolean[labelIndices.length];

        for (int i = 0; i < labelIndices.length; i++) {
            trueLabels[i] = getLabelValue(instance, labelIndices[i]);
        }

        return new GroundTruth(trueLabels);
    }

    public boolean getLabelValue(final Instance instance, final int labelIndex) {
        return instance.stringValue(labelIndex).equals("1");
    }

    public Instances getInstancesByNominalAttribute(final Attribute attribute, final String value,
                                                    final AttributeType attributeType) {
        if (!attribute.isNominal()) {
            throw new IllegalArgumentException("Attribute must be nominal");
        }

        return getInstancesByNominalAttribute(attribute.index(), value, attributeType);
    }

    public Instances getInstancesByNominalAttribute(final int index, final String value,
                                                    final AttributeType attributeType) {
        if (attributeType == AttributeType.ALL ||
                (attributeType == AttributeType.FEATURE && !isLabel(index)) ||
                (attributeType == AttributeType.LABEL && isLabel(index))) {
            Map<String, Instances> map = instancesIndexedByNominalAttributes.get(index);
            return map != null ? map.get(value) : null;
        }

        return null;
    }

    public boolean hasInstancesWithNominalAttribute(final Attribute attribute, final String value,
                                                    final AttributeType attributeType) {
        if (!attribute.isNominal()) {
            throw new IllegalArgumentException("Attribute must be nominal");
        }

        return hasInstancesWithNominalAttribute(attribute.index(), value, attributeType);
    }

    public boolean hasInstancesWithNominalAttribute(final int index, final String value,
                                                    final AttributeType attributeType) {
        Instances instances = getInstancesByNominalAttribute(index, value, attributeType);
        return instances != null && !instances.isEmpty();
    }

    public Instances getInstancesSortedByNumericAttribute(final Attribute attribute) {
        if (!attribute.isNumeric()) {
            throw new IllegalArgumentException("Attribute must be numeric");
        }

        return getInstancesSortedByNumericAttribute(attribute.index());
    }

    public Instances getInstancesSortedByNumericAttribute(final int index) {
        return instancesSortedByNumericAttributes.get(index);
    }

    public boolean getTargetPrediction(final int labelIndex) {
        int occurrences = getInstancesByNominalAttribute(labelIndex, "1", AttributeType.LABEL).size();
        return occurrences < (getDataSet().getNumInstances() / 2);
    }

    @NotNull
    @Override
    public Iterator<Instance> iterator() {
        return IteratorUtil.createForLoopIterator(dataSet.getDataSet(), Instances::size, Instances::get);
    }

    @Override
    public String toString() {
        return dataSet.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSet that = (DataSet) o;
        return Objects.equals(dataSet, that.dataSet);
    }

}
