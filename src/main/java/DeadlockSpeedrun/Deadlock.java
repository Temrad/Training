package DeadlockSpeedrun;

public class Deadlock {
    public static void main(String[] args) {
        Object obj1 = new Object();
        Object obj2 = new Object();
        long startTime = System.currentTimeMillis() + 1000;
        new Thread(new DeadLocker(startTime, obj1, obj2)).start();
        new Thread(new DeadLocker(startTime, obj2, obj1)).start();
    }


}
