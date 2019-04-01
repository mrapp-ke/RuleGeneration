package de.tud.ke.rulelearning.model;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Objects;

public class TrainingInstance implements Instance, Serializable {

    private static final long serialVersionUID = 1L;

    private final int index;

    private final Instance instance;

    public TrainingInstance(final int index, final Instance instance) {
        this.index = index;
        this.instance = instance;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public Attribute attribute(int i) {
        return this.instance.attribute(i);
    }

    @Override
    public Attribute attributeSparse(int i) {
        return instance.attributeSparse(i);
    }

    @Override
    public Attribute classAttribute() {
        return this.instance.classAttribute();
    }

    @Override
    public int classIndex() {
        return this.instance.classIndex();
    }

    @Override
    public boolean classIsMissing() {
        return this.instance.classIsMissing();
    }

    @Override
    public double classValue() {
        return this.instance.classValue();
    }

    @Override
    public Instance copy(double[] values) {
        return new TrainingInstance(index, instance.copy(values));
    }

    @Override
    public Instances dataset() {
        return this.instance.dataset();
    }

    @Override
    public void deleteAttributeAt(int i) {
        this.instance.deleteAttributeAt(i);
    }

    @Override
    public Enumeration<Attribute> enumerateAttributes() {
        return this.instance.enumerateAttributes();
    }

    @Override
    public boolean equalHeaders(Instance instance) {
        return this.instance.equalHeaders(instance);
    }

    @Override
    public String equalHeadersMsg(Instance instance) {
        return this.instance.equalHeadersMsg(instance);
    }

    @Override
    public boolean hasMissingValue() {
        return this.instance.hasMissingValue();
    }

    @Override
    public int index(int i) {
        return this.instance.index(i);
    }

    @Override
    public void insertAttributeAt(int i) {
        this.instance.insertAttributeAt(i);
    }

    @Override
    public boolean isMissing(int i) {
        return this.instance.isMissing(i);
    }

    @Override
    public boolean isMissingSparse(int i) {
        return this.instance.isMissingSparse(i);
    }

    @Override
    public boolean isMissing(Attribute attribute) {
        return this.instance.isMissing(attribute);
    }

    @Override
    public Instance mergeInstance(Instance instance) {
        return this.instance.mergeInstance(instance);
    }

    @Override
    public int numAttributes() {
        return this.instance.numAttributes();
    }

    @Override
    public int numClasses() {
        return this.instance.numClasses();
    }

    @Override
    public int numValues() {
        return this.instance.numValues();
    }

    @Override
    public void replaceMissingValues(double[] doubles) {
        this.instance.replaceMissingValues(doubles);
    }

    @Override
    public void setClassMissing() {
        this.instance.setClassMissing();
    }

    @Override
    public void setClassValue(double v) {
        this.instance.setClassValue(v);
    }

    @Override
    public void setClassValue(String s) {
        this.instance.setClassValue(s);
    }

    @Override
    public void setDataset(Instances instances) {
        this.instance.setDataset(instances);
    }

    @Override
    public void setMissing(int i) {
        this.instance.setMissing(i);
    }

    @Override
    public void setMissing(Attribute attribute) {
        this.instance.setMissing(attribute);
    }

    @Override
    public void setValue(int i, double v) {
        this.instance.setValue(i, v);
    }

    @Override
    public void setValueSparse(int i, double v) {
        this.instance.setValueSparse(i, v);
    }

    @Override
    public void setValue(int i, String s) {
        this.instance.setValue(i, s);
    }

    @Override
    public void setValue(Attribute attribute, double v) {
        this.instance.setValue(attribute, v);
    }

    @Override
    public void setValue(Attribute attribute, String s) {
        this.instance.setValue(attribute, s);
    }

    @Override
    public void setWeight(double v) {
        this.instance.setWeight(v);
    }

    @Override
    public Instances relationalValue(int i) {
        return this.instance.relationalValue(i);
    }

    @Override
    public Instances relationalValue(Attribute attribute) {
        return this.instance.relationalValue(attribute);
    }

    @Override
    public String stringValue(int i) {
        return this.instance.stringValue(i);
    }

    @Override
    public String stringValue(Attribute attribute) {
        return this.instance.stringValue(attribute);
    }

    @Override
    public double[] toDoubleArray() {
        return this.instance.toDoubleArray();
    }

    @Override
    public String toStringNoWeight(int i) {
        return this.instance.toStringNoWeight(i);
    }

    @Override
    public String toStringNoWeight() {
        return this.instance.toStringNoWeight();
    }

    @Override
    public String toStringMaxDecimalDigits(int i) {
        return this.instance.toStringMaxDecimalDigits(i);
    }

    @Override
    public String toString(int i, int i1) {
        return this.instance.toString(i, i1);
    }

    @Override
    public String toString(int i) {
        return this.instance.toString(i);
    }

    @Override
    public String toString(Attribute attribute, int i) {
        return this.instance.toString(attribute, i);
    }

    @Override
    public String toString(Attribute attribute) {
        return this.instance.toString(attribute);
    }

    @Override
    public double value(int i) {
        return this.instance.value(i);
    }

    @Override
    public double valueSparse(int i) {
        return this.instance.valueSparse(i);
    }

    @Override
    public double value(Attribute attribute) {
        return this.instance.value(attribute);
    }

    @Override
    public double weight() {
        return this.instance.weight();
    }

    @Override
    public Object copy() {
        return new TrainingInstance(this.index, (Instance) this.instance.copy());
    }

    @Override
    public String toString() {
        return this.instance.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.index);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        TrainingInstance other = (TrainingInstance) obj;
        return this.index == other.index;
    }

}
