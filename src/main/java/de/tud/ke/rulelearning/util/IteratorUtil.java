package de.tud.ke.rulelearning.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public final class IteratorUtil {

    private IteratorUtil() {

    }

    public static <T> Iterable<T> concatIterables(final Iterable<T> iterable1, final Iterable<T> iterable2) {
        return () -> concatIterators(iterable1.iterator(), iterable2.iterator());
    }

    public static <T> Iterator<T> concatIterators(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return iterator1.hasNext() || iterator2.hasNext();
            }

            @Override
            public T next() {
                try {
                    return iterator1.next();
                } catch (NoSuchElementException e) {
                    return iterator2.next();
                }
            }

        };
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

    public static <T1, T2> Iterable<T2> createNestedIterable(final Iterable<T1> outerIterable,
                                                             final Function<T1, Iterator<T2>> nestedIteratorFactory) {
        return () -> createNestedIterator(outerIterable.iterator(), nestedIteratorFactory);
    }

    public static <T1, T2> Iterator<T2> createNestedIterator(final Iterator<T1> outerIterator,
                                                             final Function<T1, Iterator<T2>> nestedIteratorFactory) {
        return new Iterator<T2>() {

            private Iterator<T2> innerIterator = null;

            private T2 next = computeNext();

            private T2 computeNext() {
                T2 result = null;

                while (result == null && ((innerIterator != null && innerIterator.hasNext()) ||
                        outerIterator.hasNext())) {
                    if (innerIterator != null && innerIterator.hasNext()) {
                        result = innerIterator.next();
                    } else {
                        innerIterator = nestedIteratorFactory.apply(outerIterator.next());
                    }
                }

                return result;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public T2 next() {
                if (hasNext()) {
                    T2 result = next;
                    next = computeNext();
                    return result;
                }

                throw new NoSuchElementException();
            }

        };
    }

    public static <T> Iterable<T> createFilteredIterable(final Iterable<T> iterable, final Predicate<T> predicate) {
        return () -> createFilteredIterator(iterable.iterator(), predicate);
    }

    public static <T> Iterator<T> createFilteredIterator(final Iterator<T> iterator, final Predicate<T> predicate) {
        return new Iterator<T>() {

            private T next = computeNext();

            private T computeNext() {
                T result = null;

                while (result == null && iterator.hasNext()) {
                    T next = iterator.next();

                    if (predicate.test(next)) {
                        result = next;
                    }
                }

                return result;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public T next() {
                if (hasNext()) {
                    T result = next;
                    next = computeNext();
                    return result;
                }

                throw new NoSuchElementException();
            }

        };
    }

    public static <T> boolean equals(final Iterable<T> iterable1, final Iterable<T> iterable2) {
        return equals(iterable1.iterator(), iterable2.iterator());
    }

    public static <T> boolean equals(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        while (iterator1.hasNext()) {
            if (iterator2.hasNext()) {
                T first = iterator1.next();
                T second = iterator2.next();

                if (!Objects.equals(first, second)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return !iterator2.hasNext();
    }

    public static <T1, T2> Iterable<T2> createMappedIterable(final Iterable<T1> iterable,
                                                             final Function<T1, T2> mapper) {
        return () -> createMappedIterator(iterable.iterator(), mapper);
    }

    public static <T1, T2> Iterator<T2> createMappedIterator(final Iterator<T1> iterator,
                                                             final Function<T1, T2> mapper) {
        return new Iterator<T2>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T2 next() {
                T1 next = iterator.next();
                return mapper.apply(next);
            }

        };
    }

}
