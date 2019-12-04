package Dictionaries;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class HashDictionary extends HashMap<String, List<String>> implements Dictionary {

    HashDictionary(String fileName) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String word;

        try {
            while (true) {
                word = reader.readLine();
                if(word == null) break;
                addWord(word);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addWord(String word) {
        for (int i = 0; i <= word.length(); i++)
            addEntry(word.substring(0, i), word);
    }

     private void addEntry(String key, String word) {
        if(!containsKey(key))
            put(key, new LinkedList<>());
        get(key).add(word);
     }

     public List<String> findInclusions(String root) {
        return get(root);
     }
}
