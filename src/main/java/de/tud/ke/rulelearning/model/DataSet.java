package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.util.IteratorUtil;
import de.tud.ke.rulelearning.util.MappedList;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.GroundTruth;
import org.jetbrains.annotations.NotNull;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataSet implements Iterable<Instance>, Serializable {

    private final MultiLabelInstances dataSet;

    private final Map<Integer, Instances> instancesSortedByNumericAttributes;

    private final Map<Integer, Map<String, Map<Integer, TrainingInstance>>> instancesIndexedByNominalAttributes;

    private final Set<Integer> labelIndices;

    private Map<Integer, Map<String, Map<Integer, TrainingInstance>>> createInstancesIndexedByNominalAttributes(
            final Collection<Attribute> featureAttributes,
            final Collection<Attribute> labelAttributes) {
        Map<Integer, Map<String, Map<Integer, TrainingInstance>>> result = new HashMap<>(
                featureAttributes.size() + labelAttributes.size(), 1f);

        for (Attribute attribute : IteratorUtil.concatIterables(featureAttributes, labelAttributes)) {
            if (attribute.isNominal()) {
                for (String value : IteratorUtil
                        .createForLoopIterable(attribute, Attribute::numValues, Attribute::value)) {
                    int index = attribute.index();
                    Map<Integer, TrainingInstance> filteredInstances = new HashMap<>();

                    for (Instance instance : this) {
                        if (!instance.isMissing(index) && value.equals(instance.stringValue(index))) {
                            TrainingInstance trainingInstance = (TrainingInstance) instance;
                            filteredInstances.put(trainingInstance.getIndex(), trainingInstance);
                        }
                    }

                    if (!filteredInstances.isEmpty()) {
                        Map<String, Map<Integer, TrainingInstance>> map = result.computeIfAbsent(index,
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

    private Map<Integer, TrainingInstance> getInstancesByNominalAttribute(
            final Attribute attribute, final String value, final AttributeType attributeType,
            final Map<Integer, TrainingInstance> instances) {
        Map<Integer, TrainingInstance> allInstances = getInstancesByNominalAttribute(attribute, value, attributeType);
        return intersect(allInstances, instances);
    }

    private Map<Integer, TrainingInstance> getInstancesByNumericAttribute(
            final Attribute attribute, final NumericCondition.Comparator comparator, final double value,
            final Map<Integer, TrainingInstance> instances) {
        Map<Integer, TrainingInstance> allInstances = getInstancesByNumericAttribute(attribute, comparator, value);
        return intersect(allInstances, instances);
    }

    private Map<Integer, TrainingInstance> intersect(final Map<Integer, TrainingInstance> instances1,
                                                     final Map<Integer, TrainingInstance> instances2) {
        if (instances2 != null) {
            return instances2.entrySet().stream().filter(entry -> instances1.containsKey(entry.getKey())).collect(
                    HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
        }

        return instances1;
    }

    private Map<Integer, TrainingInstance> getInstancesByNumericAttribute(final Attribute attribute,
                                                                          final NumericCondition.Comparator comparator,
                                                                          final double value) {
        Instances sortedInstances = getInstancesSortedByNumericAttribute(attribute);
        List<Double> mappedInstances = new MappedList<>(sortedInstances, instance -> instance.value(attribute));
        int index = Math.abs(Collections.binarySearch(mappedInstances, value, Double::compare));
        int absIndex = Math.abs(index);
        IntStream intStream;

        switch (comparator) {
            case LESS:
                intStream = IntStream.range(0, absIndex);
                break;
            case LESS_OR_EQUAL:
                intStream = index >= 0 ? IntStream.rangeClosed(0, index) : IntStream.range(0, absIndex);
                break;
            case EQUAL:
                intStream = index >= 0 ? IntStream.rangeClosed(index, index) : IntStream.range(absIndex, absIndex);
                break;
            case GREATER:
                intStream = index >= 0 ? IntStream.range(index + 1, mappedInstances.size()) :
                        IntStream.range(absIndex, mappedInstances.size());
                break;
            case GREATER_OR_EQUAL:
                intStream = index >= 0 ? IntStream.range(index, mappedInstances.size()) :
                        IntStream.range(absIndex, mappedInstances.size());
                break;
            default:
                throw new IllegalArgumentException("Unknown comparator: " + comparator);
        }

        return intStream.mapToObj(i -> (TrainingInstance) sortedInstances.get(i))
                .collect(HashMap::new, (map, instance) -> map.put(instance.getIndex(), instance), HashMap::putAll);
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

    public Map<Integer, TrainingInstance> getInstancesByNominalAttribute(final Attribute attribute, final String value,
                                                                         final AttributeType attributeType) {
        if (!attribute.isNominal()) {
            throw new IllegalArgumentException("Attribute must be nominal");
        }

        return getInstancesByNominalAttribute(attribute.index(), value, attributeType);
    }

    public Map<Integer, TrainingInstance> getInstancesByNominalAttribute(final int index, final String value,
                                                                         final AttributeType attributeType) {
        if (attributeType == AttributeType.ALL ||
                (attributeType == AttributeType.FEATURE && !isLabel(index)) ||
                (attributeType == AttributeType.LABEL && isLabel(index))) {
            Map<String, Map<Integer, TrainingInstance>> map = instancesIndexedByNominalAttributes.get(index);
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
        Map<Integer, TrainingInstance> instances = getInstancesByNominalAttribute(index, value, attributeType);
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
        int positives = getPositiveExamples(labelIndex);
        int negatives = dataSet.getNumInstances() - positives;
        return negatives >= positives;
    }

    public int getPositiveExamples(final int labelIndex) {
        Map<Integer, TrainingInstance> instances = getInstancesByNominalAttribute(labelIndex, "1", AttributeType.LABEL);
        return instances != null ? instances.size() : 0;
    }

    public Map<Integer, TrainingInstance> getCoveredInstances(final Rule rule) {
        Map<Integer, TrainingInstance> instances = null;

        for (Condition condition : rule.getBody()) {
            if (condition instanceof NominalCondition) {
                NominalCondition nominalCondition = (NominalCondition) condition;
                instances = getInstancesByNominalAttribute(nominalCondition.getAttribute(),
                        nominalCondition.getValue(), AttributeType.FEATURE, instances);
            } else {
                NumericCondition numericCondition = (NumericCondition) condition;
                instances = getInstancesByNumericAttribute(numericCondition.getAttribute(),
                        numericCondition.getComparator(), numericCondition.getValue(), instances);
            }
        }

        return instances != null ? instances : Collections.emptyMap();
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
