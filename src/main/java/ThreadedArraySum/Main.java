package ThreadedArraySum;

import Util.ArrayGenerator;

import java.util.concurrent.ExecutorService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Available Processors: "+Runtime.getRuntime().availableProcessors());
        int [] arr = ArrayGenerator.getArray(300*1000*1000);

        SimpleSummator s1 = new SimpleSummator();
        s1.sum(arr);

        ThreadedSummator s2 = new ThreadedSummator();
        s2.sum(arr, Runtime.getRuntime().availableProcessors());
/*
        StreamSummator s3 = new StreamSummator();
        s3.sum(arr);

        ForkJoinSummator s4 = new ForkJoinSummator();
        s4.sum(arr);
*/
        TinyExecutorSummator s5 = new TinyExecutorSummator();
        s5.sum(arr, Runtime.getRuntime().availableProcessors());

        ThreadPoolExecutorSummator s6 = new ThreadPoolExecutorSummator();
        s6.sum(arr, Runtime.getRuntime().availableProcessors());

    }
}
