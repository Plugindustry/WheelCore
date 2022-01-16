package io.github.plugindustry.wheelcore.utils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionUtil {
    @Nonnull
    public static <T> Collection<T> unmodifiableCopyOnReadCollection(@Nonnull Collection<? extends T> c, @Nonnull Function<T, T> cloner) {
        return new UnmodifiableCopyOnReadCollection<>(c, cloner);
    }

    @Nonnull
    public static <T> Set<T> unmodifiableCopyOnReadSet(@Nonnull Set<? extends T> s, @Nonnull Function<T, T> cloner) {
        return new UnmodifiableCopyOnReadSet<>(s, cloner);
    }

    @Nonnull
    public static <T> List<T> unmodifiableCopyOnReadList(@Nonnull List<? extends T> list, @Nonnull Function<T, T> cloner) {
        return (list instanceof RandomAccess ?
                new UnmodifiableCopyOnReadRandomAccessList<>(list, cloner) :
                new UnmodifiableCopyOnReadList<>(list, cloner));
    }

    static class UnmodifiableCopyOnReadCollection<E> implements Collection<E> {
        final Collection<? extends E> c;
        final Function<E, E> cloner;

        UnmodifiableCopyOnReadCollection(@Nonnull Collection<? extends E> c, @Nonnull Function<E, E> cloner) {
            this.c = Objects.requireNonNull(c);
            this.cloner = cloner;
        }

        @Override
        public int size() {
            return c.size();
        }

        @Override
        public boolean isEmpty() {
            return c.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return c.contains(o);
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(@Nonnull T[] a) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return c.toString();
        }

        @Override
        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> i = c.iterator();

                @Override
                public boolean hasNext() {
                    return i.hasNext();
                }

                @Override
                public E next() {
                    return cloner.apply(i.next());
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    i.forEachRemaining(o -> action.accept(cloner.apply(o)));
                }
            };
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(@Nonnull Collection<?> coll) {
            return c.containsAll(coll);
        }

        @Override
        public boolean addAll(@Nonnull Collection<? extends E> coll) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(@Nonnull Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(@Nonnull Collection<?> coll) {
            throw new UnsupportedOperationException();
        }

        @Override
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
                return new CopySpliterator<>(s.trySplit(), cloner);
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

    static class UnmodifiableCopyOnReadSet<E> extends UnmodifiableCopyOnReadCollection<E> implements Set<E> {

        UnmodifiableCopyOnReadSet(@Nonnull Set<? extends E> c, @Nonnull Function<E, E> cloner) {
            super(c, cloner);
        }

        public boolean equals(Object o) {
            return o == this || c.equals(o);
        }

        public int hashCode() {
            return c.hashCode();
        }
    }

    static class UnmodifiableCopyOnReadList<E> extends UnmodifiableCopyOnReadCollection<E> implements List<E> {
        final List<? extends E> list;

        UnmodifiableCopyOnReadList(@Nonnull List<? extends E> list, @Nonnull Function<E, E> cloner) {
            super(list, cloner);
            this.list = list;
        }

        public boolean equals(Object o) {
            return o == this || list.equals(o);
        }

        public int hashCode() {
            return list.hashCode();
        }

        @Override
        public E get(int index) {
            return cloner.apply(list.get(index));
        }

        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        @Override
        public boolean addAll(int index, @Nonnull Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(final int index) {
            return new ListIterator<E>() {
                private final ListIterator<? extends E> i = list.listIterator(index);

                @Override
                public boolean hasNext() {
                    return i.hasNext();
                }

                @Override
                public E next() {
                    return cloner.apply(i.next());
                }

                @Override
                public boolean hasPrevious() {
                    return i.hasPrevious();
                }

                @Override
                public E previous() {
                    return cloner.apply(i.previous());
                }

                @Override
                public int nextIndex() {
                    return i.nextIndex();
                }

                @Override
                public int previousIndex() {
                    return i.previousIndex();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(E e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(E e) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    i.forEachRemaining(o -> action.accept(cloner.apply(o)));
                }
            };
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableCopyOnReadList<>(list.subList(fromIndex, toIndex), cloner);
        }
    }

    static class UnmodifiableCopyOnReadRandomAccessList<E> extends UnmodifiableCopyOnReadList<E> implements RandomAccess {
        UnmodifiableCopyOnReadRandomAccessList(@Nonnull List<? extends E> list, @Nonnull Function<E, E> cloner) {
            super(list, cloner);
        }

        public List<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableCopyOnReadRandomAccessList<>(list.subList(fromIndex, toIndex), cloner);
        }
    }
}
