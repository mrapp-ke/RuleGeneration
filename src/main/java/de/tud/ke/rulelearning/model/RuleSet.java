package de.tud.ke.rulelearning.model;

import org.jetbrains.annotations.NotNull;
import weka.core.Instance;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class RuleSet implements Collection<Rule>, Serializable {

    private final Set<Rule> rules = new HashSet<>();

    public RuleSet() {

    }

    public RuleSet getCoveringRules(final Instance instance) {
        return rules.stream().filter(rule -> rule.covers(instance)).collect(Collectors.toCollection(RuleSet::new));
    }

    @Override
    public int size() {
        return rules.size();
    }

    @Override
    public boolean isEmpty() {
        return rules.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return rules.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Rule> iterator() {
        return rules.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return rules.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull final T[] ts) {
        return rules.toArray(ts);
    }

    @Override
    public boolean add(final Rule rule) {
        return rules.add(rule);
    }

    @Override
    public boolean remove(final Object o) {
        return rules.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull final Collection<?> collection) {
        return rules.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends Rule> collection) {
        return rules.addAll(collection);
    }

    @Override
    public boolean removeAll(@NotNull final Collection<?> collection) {
        return rules.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull final Collection<?> collection) {
        return rules.retainAll(collection);
    }

    @Override
    public void clear() {
        rules.clear();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        Iterator<Rule> iterator = iterator();

        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().toString());

            if (iterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(rules);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleSet rules1 = (RuleSet) o;
        return Objects.equals(rules, rules1.rules);
    }

}
