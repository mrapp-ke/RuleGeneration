package de.tud.ke.rulelearning.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

public abstract class ConditionSet implements Iterable<Condition>, Comparable<ConditionSet>,
        Serializable {

    private static final long serialVersionUID = 1L;

    private final NavigableMap<Integer, Condition> conditions = new TreeMap<>();

    public ConditionSet(final Condition... conditions) {
        this(Arrays.asList(conditions));
    }

    public ConditionSet(final Iterable<Condition> conditions) {
        addAllConditions(conditions);
    }

    public void addAllConditions(final Iterable<Condition> conditions) {
        conditions.forEach(this::addCondition);
    }

    public boolean addCondition(final Condition condition) {
        int index = condition.index();
        return conditions.putIfAbsent(index, condition) == null;
    }

    public int size() {
        return conditions.size();
    }

    public Collection<Condition> getConditions() {
        return Collections.unmodifiableCollection(conditions.values());
    }

    public Condition getCondition(final int index) {
        return conditions.get(index);
    }

    @NotNull
    @Override
    public Iterator<Condition> iterator() {
        return getConditions().iterator();
    }

    @Override
    public int compareTo(@NotNull final ConditionSet o) {
        int comp = Integer.compare(size(), o.size());

        if (comp == 0) {
            Iterator<Integer> iterator1 = conditions.navigableKeySet().iterator();
            Iterator<Integer> iterator2 = o.conditions.navigableKeySet().iterator();

            while (comp == 0 && iterator1.hasNext() && iterator2.hasNext()) {
                comp = Integer.compare(iterator1.next(), iterator2.next());
            }
        }

        return comp;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        Iterator<Condition> iterator = iterator();

        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next());

            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(conditions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionSet that = (ConditionSet) o;
        return Objects.equals(conditions, that.conditions);
    }

}
