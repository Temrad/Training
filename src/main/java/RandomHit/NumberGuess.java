package RandomHit;

import java.util.Random;

public class NumberGuess {
    private Random rand = new Random(760);
    private int border = 10000;

    public int oneRandom() {
        int num = rand.nextInt(border);
        int counter = 0;
        do{
            counter++;
        }
        while (rand.nextInt(border) != num);
        return counter;
    }

    public int bothRandom() {
        int num;
        int counter = 0;
        do{
            num = rand.nextInt(border);
            counter++;
        }
        while (rand.nextInt(border) != num);
        return counter;
    }

    public static void main(String[] args) {

       new Thread(new Runnable() {
            public void run() {
                NumberGuess ng = new NumberGuess();
                final int count = 1000;
                int sum = 0;
                for(int i = 0; i < count; i++) {
                    sum += ng.oneRandom();
                }
                System.out.println("one rand, 1k tries: " + sum/count);
            }
        }).start();

       new Thread(new Runnable() {
            public void run() {
                NumberGuess ng = new NumberGuess();
                final int count = 1000;
                int sum = 0;
                for(int i = 0; i < count; i++) {
                    sum += ng.bothRandom();
                }
                System.out.println("both rand, 1k tries: " + sum/count);
            }
        }).start();
    }
}
