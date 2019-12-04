package DuplicateCounter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapCounter {
    Logger LOG = Logger.getLogger(this.getClass().getName());

    public void countDuplicates(int[] arr) {
        long start = System.currentTimeMillis();
        count(arr);
        LOG.log(Level.INFO, (double)(System.currentTimeMillis()-start)/1000 + "s");
    }

    private void count(int[] arr) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int outer : arr) {
            if(!map.containsKey(outer)) {
                map.put(outer, 1);
                continue;
            }
            map.put(outer, map.get(outer)+1);
        }
        //LOG.log(Level.INFO, map.toString());
    }
}
