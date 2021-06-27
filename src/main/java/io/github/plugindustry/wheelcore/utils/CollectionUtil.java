package io.github.plugindustry.wheelcore.utils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionUtil {
    public static <T extends Cloneable> Collection<T> unmodifiableCopyOnReadCollection(Collection<? extends T> c, Function<T, T> cloner) {
        return new UnmodifiableCopyOnReadCollection<>(c, cloner);
    }

    public static <T extends Cloneable> Set<T> unmodifiableCopyOnReadSet(Set<? extends T> s, Function<T, T> cloner) {
        return new UnmodifiableCopyOnReadSet<>(s, cloner);
    }

    static class UnmodifiableCopyOnReadCollection<E extends Cloneable> implements Collection<E>, Serializable {
        final Collection<? extends E> c;
        final Function<E, E> cloner;

        UnmodifiableCopyOnReadCollection(Collection<? extends E> c, Function<E, E> cloner) {
            if (c == null)
                throw new NullPointerException();
            this.c = c;
            this.cloner = cloner;
        }

        public int size() {
            return c.size();
        }

        public boolean isEmpty() {
            return c.isEmpty();
        }

        public boolean contains(Object o) {
            return c.contains(o);
        }

        public Object[] toArray() {
            throw new UnsupportedOperationException("toArray is not supported.");
        }

        public <T> T[] toArray(@Nonnull T[] a) {
            throw new UnsupportedOperationException("toArray is not supported.");
        }

        public String toString() {
            return c.toString();
        }

        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> i = c.iterator();

                public boolean hasNext() {
                    return i.hasNext();
                }

                public E next() {
                    return cloner.apply(i.next());
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    i.forEachRemaining(o -> action.accept(cloner.apply(o)));
                }
            };
        }

        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(@Nonnull Collection<?> coll) {
            return c.containsAll(coll);
        }

        public boolean addAll(@Nonnull Collection<? extends E> coll) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(@Nonnull Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(@Nonnull Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            c.forEach(o -> action.accept(cloner.apply(o)));
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Spliterator<E> spliterator() {
            return new CopySpliterator<>(c.spliterator(), cloner);
        }

        @Override
        public Stream<E> stream() {
            return StreamSupport.stream(spliterator(), false);
        }

        @Override
        public Stream<E> parallelStream() {
            return StreamSupport.stream(spliterator(), true);
        }

        static class CopySpliterator<E> implements Spliterator<E> {
            private final Spliterator<? extends E> s;
            private final Function<E, E> cloner;

            public CopySpliterator(Spliterator<? extends E> s, Function<E, E> cloner) {
                this.s = s;
                this.cloner = cloner;
            }

            @Override
            public boolean tryAdvance(Consumer<? super E> action) {
                return s.tryAdvance(o -> action.accept(cloner.apply(o)));
            }

            @Override
            public void forEachRemaining(Consumer<? super E> action) {
                s.forEachRemaining(o -> action.accept(cloner.apply(o)));
            }

            @Override
            public Spliterator<E> trySplit() {
                return new CopySpliterator<E>(s.trySplit(), cloner);
            }

            @Override
            public long estimateSize() {
                return s.estimateSize();
            }

            @Override
            public long getExactSizeIfKnown() {
                return s.getExactSizeIfKnown();
            }

            @Override
            public int characteristics() {
                return s.characteristics();
            }

            @Override
            public boolean hasCharacteristics(int characteristics) {
                return s.hasCharacteristics(characteristics);
            }

            @Override
            @SuppressWarnings("unchecked")
            public Comparator<? super E> getComparator() {
                return (Comparator<? super E>) s.getComparator();
            }
        }
    }

    static class UnmodifiableCopyOnReadSet<E extends Cloneable> extends UnmodifiableCopyOnReadCollection<E> implements Set<E>, Serializable {

        UnmodifiableCopyOnReadSet(Set<? extends E> c, Function<E, E> cloner) {
            super(c, cloner);
        }

        public boolean equals(Object o) {
            return o == this || c.equals(o);
        }

        public int hashCode() {
            return c.hashCode();
        }
    }
}
