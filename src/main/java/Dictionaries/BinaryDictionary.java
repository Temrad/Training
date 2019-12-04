package Dictionaries;

import Dictionaries.Dictionary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BinaryDictionary implements Dictionary {
    private List<String> list = new ArrayList<>();

    public BinaryDictionary(String fileName) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String word;

        try {
            do {
                word = reader.readLine();
                if(word==null) break;
                list.add(word);
            } while (true);
            list.sort(String::compareTo);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findInclusions(String root) {
        List<String> resultList = new ArrayList<>();
        int index, countUp, countDown;

        index = Collections.binarySearch(list, root,
                (midVal, givenRoot) -> resize(midVal, givenRoot).compareTo(givenRoot));

        countDown = index - 1;
        while (countDown > 0 && resize(list.get(countDown), root).compareTo(root) == 0)
            countDown--;

        countUp = countDown + 1;
        while (countUp < list.size() && resize(list.get(countUp), root).compareTo(root) == 0)
            resultList.add(list.get(countUp++));

        return resultList;
    }

    private String resize(String resized, String resizeTo) {
        if(resized.length() <= resizeTo.length())
            return resized;
        return resized.substring(0, resizeTo.length());

    }
}
