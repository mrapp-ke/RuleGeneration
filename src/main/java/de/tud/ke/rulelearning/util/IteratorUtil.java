package de.tud.ke.rulelearning.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class IteratorUtil {

    private IteratorUtil() {

    }

    public static <T1, T2> Iterable<T2> createForLoopIterable(final T1 data, final Function<T1, Integer> countGetter,
                                                              final BiFunction<T1, Integer, T2> accessor) {
        return () -> createForLoopIterator(data, countGetter, accessor);
    }

    public static <T1, T2> Iterator<T2> createForLoopIterator(final T1 data, final Function<T1, Integer> countGetter,
                                                              final BiFunction<T1, Integer, T2> accessor) {
        return new Iterator<T2>() {

            private final int count = countGetter.apply(data);

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < count;
            }

            @Override
            public T2 next() {
                if (hasNext()) {
                    T2 next = accessor.apply(data, index);
                    index++;
                    return next;
                }

                throw new NoSuchElementException();
            }

        };
    }

}
