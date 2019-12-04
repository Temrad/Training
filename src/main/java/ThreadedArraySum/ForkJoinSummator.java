package ThreadedArraySum;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ForkJoinSummator {
    private final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private int result = 0;

    public int sum(int[] arr) {
        long start = System.currentTimeMillis();
        ForkJoinPool pool = ForkJoinPool.commonPool();

        pool.invoke(new Task(arr, 0, arr.length));
        pool.awaitQuiescence(1, TimeUnit.SECONDS);
        System.out.println((double)(System.currentTimeMillis() - start)/1000 + "s"+"; Result = " + result);
        return result;
    }

    class Task extends RecursiveTask {
        private int[] arr;
        private int pos;
        private int quantity;

        public Task(int[] arr, int pos, int quantity) {
            this.arr = arr;
            this.pos = pos;
            this.quantity = quantity;
        }

        @Override
        protected Object compute() {
            if(quantity < arr.length/PROCESSORS + PROCESSORS)
                synchronized (ForkJoinSummator.this) {result += sum();}
            else {
                ForkJoinTask.invokeAll(subTasks());
            }
            return 0;
        }


        List<Task> subTasks() {
            int quant = arr.length/PROCESSORS, leftover = arr.length%PROCESSORS;
            List<Task> list = new ArrayList<>();

            for(int i = 0; i < PROCESSORS; i++) {
                list.add(new Task(arr, quant*i, i==PROCESSORS-1?quant+leftover:quant));
            }
            return list;
        }

        int sum() {
            int sum = 0, count = 0;

            while (count < quantity) {
                sum+=arr[pos++];
                count++;
            }
            return sum;
        }
    }
}
