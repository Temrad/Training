package DuplicateCounter;

import Util.ArrayGenerator;

public class DuplicateCounter {
    public static void main(String[] args) {
        int[] arr = ArrayGenerator.getArray(30*1000*1000);

        new NaiveCounter().countDuplicates(arr);
        new MapCounter().countDuplicates(arr);
    }
}
