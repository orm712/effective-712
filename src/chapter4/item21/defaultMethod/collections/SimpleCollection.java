package chapter4.item21.defaultMethod.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

public interface SimpleCollection<E> extends Collection {
    default boolean removeIf(Predicate filter) {
        Objects.requireNonNull(filter);
        boolean result = false;
        for (Iterator<E> it = iterator(); it.hasNext(); ) {
            if(filter.test(it.next())) {
                it.remove();
                result = true;
            }
        }
        return result;
    }
}
