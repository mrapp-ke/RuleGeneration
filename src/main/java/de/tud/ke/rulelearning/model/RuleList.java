package de.tud.ke.rulelearning.model;

import org.jetbrains.annotations.NotNull;
import weka.core.Instance;

import java.util.*;
import java.util.stream.Collectors;

public class RuleList implements RuleCollection, List<Rule>, RandomAccess {

    private final List<Rule> list;

    public RuleList() {
        this.list = new ArrayList<>();
    }

    public RuleList(final List<Rule> list) {
        this.list = list;
    }

    @Override
    public RuleCollection getCoveringRules(Instance instance) {
        return list.stream().filter(rule -> rule.covers(instance)).collect(Collectors.toCollection(RuleList::new));
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Rule> iterator() {
        return list.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        return list.toArray(ts);
    }

    @Override
    public boolean add(Rule rule) {
        return list.add(rule);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Rule> collection) {
        return list.addAll(collection);
    }

    @Override
    public boolean addAll(int i, @NotNull Collection<? extends Rule> collection) {
        return list.addAll(i, collection);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return list.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Rule get(int i) {
        return list.get(i);
    }

    @Override
    public Rule set(int i, Rule rule) {
        return list.set(i, rule);
    }

    @Override
    public void add(int i, Rule rule) {
        list.add(i, rule);
    }

    @Override
    public Rule remove(int i) {
        return list.remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<Rule> listIterator() {
        return list.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<Rule> listIterator(int i) {
        return list.listIterator(i);
    }

    @NotNull
    @Override
    public List<Rule> subList(int i, int i1) {
        return list.subList(i, i1);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        Iterator<Rule> iterator = iterator();

        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().toString());

            if (iterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleList rules1 = (RuleList) o;
        return Objects.equals(list, rules1.list);
    }


}
