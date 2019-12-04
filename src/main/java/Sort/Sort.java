package Sort;

import Util.ArrayGenerator;

import java.util.Arrays;

public class Sort {

    private interface DoDeed {void doSmth();}

    public static void main(String[] args) {
        int arrSize = 100*1000*1000;

        System.out.println("Simple: "+measureTime(()->
            Arrays.sort(ArrayGenerator.getArray(arrSize)))+"s");

        System.out.println("Concurrent: "+measureTime(()->
            Arrays.parallelSort(ArrayGenerator.getArray(arrSize)))+"s");
    }

    private static double measureTime(DoDeed action) {
        long start = System.currentTimeMillis();

        action.doSmth();
        return (double)(System.currentTimeMillis()-start)/1000;
    }
}
