package Lock;


import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyLock implements Lock {
    private volatile Thread owner;
    /**
     * Acquires the lock.
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until the
     * lock has been acquired.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>A {@code Lock} implementation may be able to detect erroneous use
     * of the lock, such as an invocation that would cause deadlock, and
     * may throw an (unchecked) exception in such circumstances.  The
     * circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     */
    public synchronized void lock() {
        boolean wasInterrupted = Thread.interrupted();
        acquireLock();
        if ( wasInterrupted )
            Thread.currentThread().interrupt();
    }

    private void acquireLock() {
        boolean interrupted;
        do {
            try {
                lockInterruptibly();
                interrupted = false;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        } while ( interrupted );
    }

    /**
     * Acquires the lock unless the current thread is
     * {@linkplain Thread#interrupt interrupted}.
     *
     * <p>Acquires the lock if it is available and returns immediately.
     *
     * <p>If the lock is not available then the current thread becomes
     * disabled for thread scheduling purposes and lies dormant until
     * one of two things happens:
     *
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported.
     * </ul>
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring the
     * lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The ability to interrupt a lock acquisition in some
     * implementations may not be possible, and if possible may be an
     * expensive operation.  The programmer should be aware that this
     * may be the case. An implementation should document when this is
     * the case.
     *
     * <p>An implementation can favor responding to an interrupt over
     * normal method return.
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would
     * cause deadlock, and may throw an (unchecked) exception in such
     * circumstances.  The circumstances and the exception type must
     * be documented by that {@code Lock} implementation.
     *
     * @throws InterruptedException if the current thread is
     *                              interrupted while acquiring the lock (and interruption
     *                              of lock acquisition is supported)
     */
    public synchronized void lockInterruptibly() throws InterruptedException {
        if ( isOwner() )
            return;
        while( isLocked() )
            this.wait();
        owner = Thread.currentThread();
    }

    /**
     * Acquires the lock only if it is free at the time of invocation.
     *
     * <p>Acquires the lock if it is available and returns immediately
     * with the value {@code true}.
     * If the lock is not available then this method will return
     * immediately with the value {@code false}.
     *
     * <p>A typical usage idiom for this method would be:
     * <pre> {@code
     * Lock lock = ...;
     * if (lock.tryLock()) {
     *   try {
     *     // manipulate protected state
     *   } finally {
     *     lock.unlock();
     *   }
     * } else {
     *   // perform alternative actions
     * }}</pre>
     * <p>
     * This usage ensures that the lock is unlocked if it was acquired, and
     * doesn't try to unlock if the lock was not acquired.
     *
     * @return {@code true} if the lock was acquired and
     * {@code false} otherwise
     */
    public synchronized boolean tryLock() {
        if ( isLocked() )
          return false;
        lock();
        return true;
    }

    /**
     * Acquires the lock if it is free within the given waiting time and the
     * current thread has not been {@linkplain Thread#interrupt interrupted}.
     *
     * <p>If the lock is available this method returns immediately
     * with the value {@code true}.
     * If the lock is not available then
     * the current thread becomes disabled for thread scheduling
     * purposes and lies dormant until one of three things happens:
     * <ul>
     * <li>The lock is acquired by the current thread; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
     * current thread, and interruption of lock acquisition is supported; or
     * <li>The specified waiting time elapses
     * </ul>
     *
     * <p>If the lock is acquired then the value {@code true} is returned.
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while acquiring
     * the lock, and interruption of lock acquisition is supported,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * <p>If the specified waiting time elapses then the value {@code false}
     * is returned.
     * If the time is
     * less than or equal to zero, the method will not wait at all.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The ability to interrupt a lock acquisition in some implementations
     * may not be possible, and if possible may
     * be an expensive operation.
     * The programmer should be aware that this may be the case. An
     * implementation should document when this is the case.
     *
     * <p>An implementation can favor responding to an interrupt over normal
     * method return, or reporting a timeout.
     *
     * <p>A {@code Lock} implementation may be able to detect
     * erroneous use of the lock, such as an invocation that would cause
     * deadlock, and may throw an (unchecked) exception in such circumstances.
     * The circumstances and the exception type must be documented by that
     * {@code Lock} implementation.
     *
     * @param time the maximum time to wait for the lock
     * @param unit the time unit of the {@code time} argument
     * @return {@code true} if the lock was acquired and {@code false}
     * if the waiting time elapsed before the lock was acquired
     * @throws InterruptedException if the current thread is interrupted
     *                              while acquiring the lock (and interruption of lock
     *                              acquisition is supported)
     */
    public synchronized boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if( !isLocked() ) {
            lock();
            return true;
        }
        return waitForLock(time, unit);
    }

    private boolean waitForLock (long time, TimeUnit unit) throws InterruptedException{
        long timeMillis = unit.toMillis(time);
        long waitUntil = System.currentTimeMillis() + timeMillis;

        do {
            this.wait(timeMillis);
        }
        while (!outOfTime(waitUntil) && !tryLock());
        return isOwner();
    }

    private boolean outOfTime(long waitUntil) {
        return System.currentTimeMillis() >= waitUntil;
    }
    /**
     * Releases the lock.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>A {@code Lock} implementation will usually impose
     * restrictions on which thread can release a lock (typically only the
     * holder of the lock can release it) and may throw
     * an (unchecked) exception if the restriction is violated.
     * Any restrictions and the exception
     * type must be documented by that {@code Lock} implementation.
     */
    public synchronized void unlock() {
        if( isOwner() ) {
            owner = null;
            this.notify();
        }
    }

    /**
     * Returns a new {@link Condition} instance that is bound to this
     * {@code Lock} instance.
     *
     * <p>Before waiting on the condition the lock must be held by the
     * current thread.
     * A call to {@link Condition#await()} will atomically release the lock
     * before waiting and re-acquire the lock before the wait returns.
     *
     * <p><b>Implementation Considerations</b>
     *
     * <p>The exact operation of the {@link Condition} instance depends on
     * the {@code Lock} implementation and must be documented by that
     * implementation.
     *
     * @return A new {@link Condition} instance for this {@code Lock} instance
     * @throws UnsupportedOperationException if this {@code Lock}
     *                                       implementation does not support conditions
     */
    public Condition newCondition() {
        return null;
    }

    private boolean isLocked() {
        return owner != null;
    }
    private boolean isOwner() {return owner == Thread.currentThread();}

    private class MyCondition implements Condition {

        /**
         * Causes the current thread to wait until it is signalled or
         * {@linkplain Thread#interrupt interrupted}.
         *
         * <p>The lock associated with this {@code Condition} is atomically
         * released and the current thread becomes disabled for thread scheduling
         * purposes and lies dormant until <em>one</em> of four things happens:
         * <ul>
         * <li>Some other thread invokes the {@link #signal} method for this
         * {@code Condition} and the current thread happens to be chosen as the
         * thread to be awakened; or
         * <li>Some other thread invokes the {@link #signalAll} method for this
         * {@code Condition}; or
         * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
         * current thread, and interruption of thread suspension is supported; or
         * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
         * </ul>
         *
         * <p>In all cases, before this method can return the current thread must
         * re-acquire the lock associated with this condition. When the
         * thread returns it is <em>guaranteed</em> to hold this lock.
         *
         * <p>If the current thread:
         * <ul>
         * <li>has its interrupted status set on entry to this method; or
         * <li>is {@linkplain Thread#interrupt interrupted} while waiting
         * and interruption of thread suspension is supported,
         * </ul>
         * then {@link InterruptedException} is thrown and the current thread's
         * interrupted status is cleared. It is not specified, in the first
         * case, whether or not the test for interruption occurs before the lock
         * is released.
         *
         * <p><b>Implementation Considerations</b>
         *
         * <p>The current thread is assumed to hold the lock associated with this
         * {@code Condition} when this method is called.
         * It is up to the implementation to determine if this is
         * the case and if not, how to respond. Typically, an exception will be
         * thrown (such as {@link IllegalMonitorStateException}) and the
         * implementation must document that fact.
         *
         * <p>An implementation can favor responding to an interrupt over normal
         * method return in response to a signal. In that case the implementation
         * must ensure that the signal is redirected to another waiting thread, if
         * there is one.
         *
         * @throws InterruptedException if the current thread is interrupted
         *                              (and interruption of thread suspension is supported)
         */
        public void await() throws InterruptedException {
            if( !isOwner() )
                throw new IllegalMonitorStateException(
                        "Thread must hold the lock associated with this Condition when this method is called.");
            unlock();
            synchronized (this) {
                this.wait();
            }
            lock();
        }

        /**
         * Causes the current thread to wait until it is signalled.
         *
         * <p>The lock associated with this condition is atomically
         * released and the current thread becomes disabled for thread scheduling
         * purposes and lies dormant until <em>one</em> of three things happens:
         * <ul>
         * <li>Some other thread invokes the {@link #signal} method for this
         * {@code Condition} and the current thread happens to be chosen as the
         * thread to be awakened; or
         * <li>Some other thread invokes the {@link #signalAll} method for this
         * {@code Condition}; or
         * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
         * </ul>
         *
         * <p>In all cases, before this method can return the current thread must
         * re-acquire the lock associated with this condition. When the
         * thread returns it is <em>guaranteed</em> to hold this lock.
         *
         * <p>If the current thread's interrupted status is set when it enters
         * this method, or it is {@linkplain Thread#interrupt interrupted}
         * while waiting, it will continue to wait until signalled. When it finally
         * returns from this method its interrupted status will still
         * be set.
         *
         * <p><b>Implementation Considerations</b>
         *
         * <p>The current thread is assumed to hold the lock associated with this
         * {@code Condition} when this method is called.
         * It is up to the implementation to determine if this is
         * the case and if not, how to respond. Typically, an exception will be
         * thrown (such as {@link IllegalMonitorStateException}) and the
         * implementation must document that fact.
         */
        public synchronized void awaitUninterruptibly() {
            boolean wasInterrupted = Thread.interrupted();
            uninterruptibleWait();
            if ( wasInterrupted )
                Thread.currentThread().interrupt();
        }

        private void uninterruptibleWait() {
            boolean interrupted;
            do {
                try {
                    await();
                    interrupted = false;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            } while ( interrupted );
        }
        /**
         * Causes the current thread to wait until it is signalled or interrupted,
         * or the specified waiting time elapses.
         *
         * <p>The lock associated with this condition is atomically
         * released and the current thread becomes disabled for thread scheduling
         * purposes and lies dormant until <em>one</em> of five things happens:
         * <ul>
         * <li>Some other thread invokes the {@link #signal} method for this
         * {@code Condition} and the current thread happens to be chosen as the
         * thread to be awakened; or
         * <li>Some other thread invokes the {@link #signalAll} method for this
         * {@code Condition}; or
         * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
         * current thread, and interruption of thread suspension is supported; or
         * <li>The specified waiting time elapses; or
         * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
         * </ul>
         *
         * <p>In all cases, before this method can return the current thread must
         * re-acquire the lock associated with this condition. When the
         * thread returns it is <em>guaranteed</em> to hold this lock.
         *
         * <p>If the current thread:
         * <ul>
         * <li>has its interrupted status set on entry to this method; or
         * <li>is {@linkplain Thread#interrupt interrupted} while waiting
         * and interruption of thread suspension is supported,
         * </ul>
         * then {@link InterruptedException} is thrown and the current thread's
         * interrupted status is cleared. It is not specified, in the first
         * case, whether or not the test for interruption occurs before the lock
         * is released.
         *
         * <p>The method returns an estimate of the number of nanoseconds
         * remaining to wait given the supplied {@code nanosTimeout}
         * value upon return, or a value less than or equal to zero if it
         * timed out. This value can be used to determine whether and how
         * long to re-wait in cases where the wait returns but an awaited
         * condition still does not hold. Typical uses of this method take
         * the following form:
         *
         * <pre> {@code
         * boolean aMethod(long timeout, TimeUnit unit) {
         *   long nanos = unit.toNanos(timeout);
         *   lock.lock();
         *   try {
         *     while (!conditionBeingWaitedFor()) {
         *       if (nanos <= 0L)
         *         return false;
         *       nanos = theCondition.awaitNanos(nanos);
         *     }
         *     // ...
         *   } finally {
         *     lock.unlock();
         *   }
         * }}</pre>
         *
         * <p>Design note: This method requires a nanosecond argument so
         * as to avoid truncation errors in reporting remaining times.
         * Such precision loss would make it difficult for programmers to
         * ensure that total waiting times are not systematically shorter
         * than specified when re-waits occur.
         *
         * <p><b>Implementation Considerations</b>
         *
         * <p>The current thread is assumed to hold the lock associated with this
         * {@code Condition} when this method is called.
         * It is up to the implementation to determine if this is
         * the case and if not, how to respond. Typically, an exception will be
         * thrown (such as {@link IllegalMonitorStateException}) and the
         * implementation must document that fact.
         *
         * <p>An implementation can favor responding to an interrupt over normal
         * method return in response to a signal, or over indicating the elapse
         * of the specified waiting time. In either case the implementation
         * must ensure that the signal is redirected to another waiting thread, if
         * there is one.
         *
         * @param nanosTimeout the maximum time to wait, in nanoseconds
         * @return an estimate of the {@code nanosTimeout} value minus
         * the time spent waiting upon return from this method.
         * A positive value may be used as the argument to a
         * subsequent call to this method to finish waiting out
         * the desired time.  A value less than or equal to zero
         * indicates that no time remains.
         * @throws InterruptedException if the current thread is interrupted
         *                              (and interruption of thread suspension is supported)
         */
        public long awaitNanos(long nanosTimeout) throws InterruptedException {
            return 0;
        }

        /**
         * Causes the current thread to wait until it is signalled or interrupted,
         * or the specified waiting time elapses. This method is behaviorally
         * equivalent to:
         * <pre> {@code awaitNanos(unit.toNanos(time)) > 0}</pre>
         *
         * @param time the maximum time to wait
         * @param unit the time unit of the {@code time} argument
         * @return {@code false} if the waiting time detectably elapsed
         * before return from the method, else {@code true}
         * @throws InterruptedException if the current thread is interrupted
         *                              (and interruption of thread suspension is supported)
         */
        public boolean await(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        /**
         * Causes the current thread to wait until it is signalled or interrupted,
         * or the specified deadline elapses.
         *
         * <p>The lock associated with this condition is atomically
         * released and the current thread becomes disabled for thread scheduling
         * purposes and lies dormant until <em>one</em> of five things happens:
         * <ul>
         * <li>Some other thread invokes the {@link #signal} method for this
         * {@code Condition} and the current thread happens to be chosen as the
         * thread to be awakened; or
         * <li>Some other thread invokes the {@link #signalAll} method for this
         * {@code Condition}; or
         * <li>Some other thread {@linkplain Thread#interrupt interrupts} the
         * current thread, and interruption of thread suspension is supported; or
         * <li>The specified deadline elapses; or
         * <li>A &quot;<em>spurious wakeup</em>&quot; occurs.
         * </ul>
         *
         * <p>In all cases, before this method can return the current thread must
         * re-acquire the lock associated with this condition. When the
         * thread returns it is <em>guaranteed</em> to hold this lock.
         *
         *
         * <p>If the current thread:
         * <ul>
         * <li>has its interrupted status set on entry to this method; or
         * <li>is {@linkplain Thread#interrupt interrupted} while waiting
         * and interruption of thread suspension is supported,
         * </ul>
         * then {@link InterruptedException} is thrown and the current thread's
         * interrupted status is cleared. It is not specified, in the first
         * case, whether or not the test for interruption occurs before the lock
         * is released.
         *
         *
         * <p>The return value indicates whether the deadline has elapsed,
         * which can be used as follows:
         * <pre> {@code
         * boolean aMethod(Date deadline) {
         *   boolean stillWaiting = true;
         *   lock.lock();
         *   try {
         *     while (!conditionBeingWaitedFor()) {
         *       if (!stillWaiting)
         *         return false;
         *       stillWaiting = theCondition.awaitUntil(deadline);
         *     }
         *     // ...
         *   } finally {
         *     lock.unlock();
         *   }
         * }}</pre>
         *
         * <p><b>Implementation Considerations</b>
         *
         * <p>The current thread is assumed to hold the lock associated with this
         * {@code Condition} when this method is called.
         * It is up to the implementation to determine if this is
         * the case and if not, how to respond. Typically, an exception will be
         * thrown (such as {@link IllegalMonitorStateException}) and the
         * implementation must document that fact.
         *
         * <p>An implementation can favor responding to an interrupt over normal
         * method return in response to a signal, or over indicating the passing
         * of the specified deadline. In either case the implementation
         * must ensure that the signal is redirected to another waiting thread, if
         * there is one.
         *
         * @param deadline the absolute time to wait until
         * @return {@code false} if the deadline has elapsed upon return, else
         * {@code true}
         * @throws InterruptedException if the current thread is interrupted
         *                              (and interruption of thread suspension is supported)
         */
        public boolean awaitUntil(Date deadline) throws InterruptedException {
            return false;
        }

        /**
         * Wakes up one waiting thread.
         *
         * <p>If any threads are waiting on this condition then one
         * is selected for waking up. That thread must then re-acquire the
         * lock before returning from {@code await}.
         *
         * <p><b>Implementation Considerations</b>
         *
         * <p>An implementation may (and typically does) require that the
         * current thread hold the lock associated with this {@code
         * Condition} when this method is called. Implementations must
         * document this precondition and any actions taken if the lock is
         * not held. Typically, an exception such as {@link
         * IllegalMonitorStateException} will be thrown.
         */
        public void signal() {

        }

        /**
         * Wakes up all waiting threads.
         *
         * <p>If any threads are waiting on this condition then they are
         * all woken up. Each thread must re-acquire the lock before it can
         * return from {@code await}.
         *
         * <p><b>Implementation Considerations</b>
         *
         * <p>An implementation may (and typically does) require that the
         * current thread hold the lock associated with this {@code
         * Condition} when this method is called. Implementations must
         * document this precondition and any actions taken if the lock is
         * not held. Typically, an exception such as {@link
         * IllegalMonitorStateException} will be thrown.
         */
        public void signalAll() {

        }
    }

    public static void main(String[] args) {
        final Object obj = new Object();
        Thread thr1 = new Thread(new Runnable() {
            public void run() {
                synchronized (obj) {
                    try {
                        obj.wait(5000);
                    } catch (InterruptedException e) {
                        System.out.println(Thread.currentThread().isInterrupted());
                    }
                }
            }
        });
        thr1.start();
        thr1.interrupt();
    }
}
