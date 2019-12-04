package Dictionaries;

import java.util.List;

public class DictionaryTimer implements Dictionary {
    private Dictionary dictionary;

    DictionaryTimer(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public List<String> findInclusions(String root) {
        long start = System.nanoTime();
        List<String> list = dictionary.findInclusions(root);
        System.out.println(System.nanoTime() - start + " nanos");
        return list;
    }
}
