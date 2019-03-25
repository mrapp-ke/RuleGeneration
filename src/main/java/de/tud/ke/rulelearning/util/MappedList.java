package de.tud.ke.rulelearning.util;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Function;

public class MappedList<T1, T2> extends AbstractList<T2> implements RandomAccess {

    private final List<T1> list;

    private final Function<T1, T2> mappingFunction;

    public MappedList(final List<T1> list, final Function<T1, T2> mappingFunction) {
        this.list = list;
        this.mappingFunction = mappingFunction;
    }

    @Override
    public T2 get(final int i) {
        return mappingFunction.apply(list.get(i));
    }

    @Override
    public int size() {
        return list.size();
    }

}
