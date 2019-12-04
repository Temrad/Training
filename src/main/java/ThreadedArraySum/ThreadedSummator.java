package ThreadedArraySum;

public class ThreadedSummator {
    public void sum(int[] arr, int numThreads) {
        long start = System.currentTimeMillis();
        Task[] threads = new Task[numThreads];
        int quant = arr.length/numThreads, leftover = arr.length%numThreads;
        int result = 0;

        for(int i = 0; i < numThreads; i++) {
            threads[i] = new Task();
            threads[i].init(arr, quant*i, i==numThreads-1?quant+leftover:quant).start();
        }
        for (Task t : threads) {
            try {
                t.join();
            }
            catch (InterruptedException e){}
            result+=t.getResult();
        }

        System.out.println((double)(System.currentTimeMillis() - start)/1000 + "s"+"; Result = " + result);
    }

    private class Task extends Thread {
        int[] arr;
        int offset;
        int quant;
        int result;

        Task(){}

        public Task init(int[] arr, int offset, int quant) {
            this.arr = arr;
            this.offset = offset;
            this.quant = quant;
            return this;
        }

        public int getResult() {
            return result;
        }

        public void run() {
            for(int i = offset; i < offset+quant; i++) {
                for(int m = 0; m < 10; m++)
                    arr[i] = (arr[i]*=1000)/1000;
                result += arr[i];
            }
        }
    }
}
