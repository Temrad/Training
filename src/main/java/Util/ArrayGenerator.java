package Util;

import java.io.InputStream;
import java.util.Random;
import java.util.stream.IntStream;

public class  ArrayGenerator {
    public static int[] getArray(int size) {
        Random rand = new Random(47);
        int[] arr = new int[size];

        for(int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(100) - 50;
        }
        return arr;
    }
}
