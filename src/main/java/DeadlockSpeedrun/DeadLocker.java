package DeadlockSpeedrun;

import java.util.logging.Level;
import java.util.logging.Logger;

class DeadLocker implements Runnable {
    private long startTime;
    private Object monitor1, monitor2;
    Logger LOG = Logger.getLogger(this.getClass().getName());

    public DeadLocker(long startTime, Object monitor1, Object monitor2) {
        this.startTime = startTime;
        this.monitor1 = monitor1;
        this.monitor2 = monitor2;
    }

    public void lock() {
        synchronized (monitor1) {
            //System.out.println(Thread.currentThread().getName()+"locked monitor 1");
            LOG.log(Level.INFO, Thread.currentThread().getName()+"locked monitor 1");
            synchronized (monitor2) {
                //System.out.println(Thread.currentThread().getName()+"locked monitor 2");
                LOG.log(Level.INFO, Thread.currentThread().getName()+"locked monitor 2");

            }
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(startTime - System.currentTimeMillis());
        } catch (InterruptedException e){}

        int counter = 0;
        do {
            //System.out.println(Thread.currentThread().getName()+" loop #"+counter++);
            LOG.log(Level.INFO, Thread.currentThread().getName()+" loop #"+counter++);
            lock();
        } while (true);
    }
}
