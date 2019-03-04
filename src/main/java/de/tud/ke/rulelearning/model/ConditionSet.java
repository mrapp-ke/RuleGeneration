package de.tud.ke.rulelearning.model;

import java.io.Serializable;
import java.util.*;

public abstract class ConditionSet implements Iterable<Condition>, Serializable {

    private final Map<Integer, Condition> conditions = new LinkedHashMap<>();

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

        if (!conditions.containsKey(index)) {
            conditions.put(index, condition);
            return true;
        }

        return false;
    }

    public int size() {
        return conditions.size();
    }

    public Set<Integer> indices() {
        return conditions.keySet();
    }

    public Collection<Condition> getConditions() {
        return Collections.unmodifiableCollection(conditions.values());
    }

    public Condition getCondition(final int index) {
        return conditions.get(index);
    }

    @Override
    public Iterator<Condition> iterator() {
        return getConditions().iterator();
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
