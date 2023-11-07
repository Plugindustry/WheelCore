package io.github.plugindustry.wheelcore.internal;

import org.apache.commons.lang3.reflect.FieldUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class UnsafeOperation {
    private final static Unsafe UNSAFE;

    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fieldsCopy(Object src, Object dest) {
        Class<?> clazz = src.getClass();
        for (Field field : FieldUtils.getAllFields(clazz)) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            try {
                Object value = field.get(src);
                field.set(dest, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}