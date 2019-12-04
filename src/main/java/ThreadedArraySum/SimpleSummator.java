package ThreadedArraySum;

public class SimpleSummator {
    public void sum(int[] arr) {
        long start = System.currentTimeMillis();
        int result = 0;

        for(int i = 0; i < arr.length; i++) {
            for(int m = 0; m < 10; m++)
                arr[i] = (arr[i]*=1000)/1000;
            result += arr[i];
        }
        System.out.println((double)(System.currentTimeMillis() - start)/1000 + "s; Result = " + result);
    }
}
