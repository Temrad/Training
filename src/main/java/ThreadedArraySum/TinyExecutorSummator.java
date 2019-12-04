package ThreadedArraySum;

import TinyExecutor.TinyExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class TinyExecutorSummator {
    private TinyExecutor tinyExecutor = new TinyExecutor();

    public void sum(int[] arr, int numTasks) {
        long start = System.currentTimeMillis();
        int result = 0;
        int quant = arr.length/numTasks, leftover = arr.length%numTasks;
        Future<Integer>[] futures = new Future[numTasks];

        for(int i = 0; i < numTasks; i++) {
            futures[i] = tinyExecutor.submit(
                    new SumTask(arr, quant * i, i == numTasks - 1 ? quant + leftover : quant)
            );
        }
        for (Future<Integer> f :
                futures) {
            try {
                result += f.get();
            }  catch (Exception e) {}

        }
        System.out.println((double)(System.currentTimeMillis() - start)/1000 + "s"+"; Result = " + result);
    }

    private static class SumTask implements Callable<Integer> {
        private int[] arr;
        int offset;
        int quantity;
        int result;

        public SumTask(int[] arr, int offset, int quantity) {
            this.arr = arr;
            this.offset = offset;
            this.quantity = quantity;
        }

        @Override
        public Integer call() throws Exception {
            for(int i = offset; i < offset+quantity; i++) {
                for(int m = 0; m < 10; m++)
                    arr[i] = (arr[i]*=1000)/1000;
                result += arr[i];
            }
            return result;
        }
    }
}
