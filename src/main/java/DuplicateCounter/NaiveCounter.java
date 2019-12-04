package DuplicateCounter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NaiveCounter {
    Logger LOG = Logger.getLogger(this.getClass().getName());

    public void countDuplicates(int[] arr) {
        long start = System.currentTimeMillis();
        count(arr);
        LOG.log(Level.INFO, (double)(System.currentTimeMillis()-start)/1000 + "s");
    }

    private void count(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int outer : arr) {
            if(map.containsKey(outer))
                continue;
            int counter = 0;
            for (int inner : arr) {
                if(inner == outer)
                    counter++;
            }
            map.put(outer, counter);
        }
        //LOG.log(Level.INFO, map.toString());
    }
}
