package de.tud.ke.rulelearning.experiments;

import java.util.Objects;

@FunctionalInterface
public interface Provider<T> {

    static <T> Provider<T> singleton(final T t) {
        return new Provider<T>() {

            @Override
            public T get(int fold) {
                return t;
            }

            @Override
            public String toString() {
                return Objects.toString(t);
            }

            @Override
            public int hashCode() {
                return Objects.hash(t);
            }

            @Override
            public boolean equals(final Object obj) {
                if (obj == null)
                    return false;
                if (obj == this)
                    return true;
                if (obj.getClass() != getClass())
                    return false;
                Provider<?> other = (Provider<?>) obj;
                return Objects.equals(t, other.get(0));
            }

        };
    }

    T get(int fold);

}
