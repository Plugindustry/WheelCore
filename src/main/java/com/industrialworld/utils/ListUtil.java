package com.industrialworld.utils;

import java.util.List;
import java.util.Random;

public class ListUtil {
    public static <T> void randomizeList(List<T> list) {
        Random r = new Random();

        for (int i = list.size() - 1; i >= 1; i--) {
            int j = r.nextInt(i + 1);

            T o = list.get(i);
            list.set(i, list.get(j));
            list.set(j, o);
        }
    }
}
