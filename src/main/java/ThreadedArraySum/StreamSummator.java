package ThreadedArraySum;

import java.util.Arrays;

public class StreamSummator {
    public void sum(int[] arr) {
        long start = System.currentTimeMillis();
        int result;

        result = Arrays.stream(arr).parallel().sum();
        System.out.println((double)(System.currentTimeMillis() - start)/1000 + "s; Result = " + result);
    }
}
