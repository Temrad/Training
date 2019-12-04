package TinyExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        TinyExecutor tinyExecutor = new TinyExecutor();

        String s = "Lisa";
        Future<String> f1 = tinyExecutor.submit(new SayHi(s));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        f1.cancel(true);
        try {
                System.out.println(f1.get(15000, TimeUnit.MILLISECONDS));
        } catch (Exception e) {throw new RuntimeException(e);}
    }

    static class SayHi implements Callable<String> {
        String toWhom;

        SayHi(String s) {
            toWhom = s;
        }
        @Override
        public String call() throws Exception {
            long i = 0;
            do{
                i++;
            } while (i < 10000000000L);
            return Thread.interrupted()? "Interrupted" : "Hello " + toWhom;
        }
    }
}
