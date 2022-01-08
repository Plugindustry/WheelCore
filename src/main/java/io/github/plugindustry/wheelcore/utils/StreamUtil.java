package io.github.plugindustry.wheelcore.utils;

import java.util.Optional;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class StreamUtil {
    private static final Random r = new Random();

    public static <T> Optional<T> randomPick(Stream<T> stream) {
        return stream.reduce(new BinaryOperator<T>() {
            private int curSize = 0;

            @Override
            public T apply(T t, T t2) {
                ++curSize;
                return r.nextInt(curSize) + 1 == curSize ? t2 : t;
            }
        });
    }
}
