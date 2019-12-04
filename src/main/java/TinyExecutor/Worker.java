package TinyExecutor;

import java.util.concurrent.*;

public class Worker implements Runnable {
    private BlockingQueue<Pair<FutureImpl>> tasks = new LinkedBlockingQueue<>();
    private Thread workingThread;

    Worker() {
        workingThread = new Thread(this);
        workingThread.start();
    }

    @Override
    public void run() {
        Pair<FutureImpl> pair = new Pair<>(null, null);
        FutureImpl future;
        Object result;

        do {
            try {
                try {
                    pair = tasks.take();
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }

                future = pair.getFuture();
                synchronized (future) {
                    if ( future.isCancelled() )
                        continue;
                    future.setStarted();
                }
                result = pair.getCallable().call();
                future.setResult(result);
                Thread.interrupted();
            }
            catch (Exception e) {
                pair.getFuture().exception(e);
            }
        } while (true);
    }

    public Future submit(Callable callable) {
        Future future = new FutureImpl(workingThread);

        tasks.add(new Pair(callable, future));
        return future;
    }

    private static class FutureImpl<T> implements Future {
        private volatile boolean isCancelled;
        private volatile boolean isDone;
        private volatile boolean hasStarted;
        private Thread associatedThread;
        private Exception exception;
        private T result;

        private FutureImpl() {}

        FutureImpl (Thread thread) {
            associatedThread = thread;
        }

        @Override
        public synchronized boolean cancel(boolean mayInterruptIfRunning) {
            if(isDone || isCancelled)
                return false;
            if(hasStarted && mayInterruptIfRunning)
                associatedThread.interrupt();
            isCancelled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public boolean isDone() {
            return isDone;
        }

        @Override
        public synchronized T get() throws InterruptedException, ExecutionException {
            while ( !isDone && exception == null )
                wait();
            if( exception != null )
                throw new ExecutionException(exception);
            return result;
        }

        @Override
        public synchronized T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            long waitUntil = currTime() + unit.toMillis(timeout);
            long currTime = 0;

            while (!isDone && (currTime = currTime()) < waitUntil && exception == null)
                wait(waitUntil - currTime);
            if( currTime >= waitUntil )
                throw new TimeoutException();
            if( exception != null )
                throw new ExecutionException(exception);
            return result;
        }

        synchronized void setResult(T result) {
            isDone = true;
            this.result = result;
            notifyAll();
        }

        synchronized void exception(Exception exception) {
            this.exception = exception;
            notifyAll();
        }

        public void setStarted() {
            this.hasStarted = true;
        }

        private static long currTime() {
            return System.currentTimeMillis();
        }
    }

    private static class Pair<F extends Future> {
        Callable callable;
        F future;

        Pair(Callable callable, F future) {
            this.callable = callable;
            this.future = future;
        }

        public Callable getCallable() {
            return callable;
        }

        public F getFuture() {
            return future;
        }
    }
}
