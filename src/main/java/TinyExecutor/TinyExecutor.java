package TinyExecutor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class TinyExecutor {
    public static final int DEFAULT_THREAD_NUMBER = Runtime.getRuntime().availableProcessors();
    private final int numberOfThreads;
    private int lastIndex = 0;
    private LinkedList<Worker> workers;

    public TinyExecutor() {
        this(DEFAULT_THREAD_NUMBER);
    }

    public TinyExecutor(int threads) {
        if ( threads <= 0 )
            throw new IllegalArgumentException("Number of threads should be above zero");
        workers = new LinkedList<Worker>();
        for (int i = 0; i < threads; i++) {
            workers.add(new Worker());
        }
        numberOfThreads = threads;
    }

    public <T> Future<T> submit(Callable<T> c) {
       return  chooseWorker().submit(c);
    }

    public <T> Future<T>[] submitAll(Callable<T>[] arr) {
        Future[] futures = new Future[arr.length];

        for (int i = 0; i < arr.length; i++) {
            futures[i] = submit(arr[i]);
        }
        return futures;
    }

    private Worker chooseWorker() {
        return workers.get(Math.abs(lastIndex++%numberOfThreads));
    }
}
