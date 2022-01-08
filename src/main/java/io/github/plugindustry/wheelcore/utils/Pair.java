package io.github.plugindustry.wheelcore.utils;

public class Pair<U, V> {
    public U first;
    public V second;

    private Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }
}
