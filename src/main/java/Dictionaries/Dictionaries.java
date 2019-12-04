package Dictionaries;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Dictionaries {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        String fileName = "src/main/resources/words_alpha2";
        Dictionary dictionary = new DictionaryTimer(
                new HashDictionary(fileName));
        Dictionary dictionary1 = new DictionaryTimer(
                new BinaryDictionary(fileName));
        Scanner scan = new Scanner(System.in);

        System.out.println("ready for input");
        String line;
        do {
            line = scan.nextLine();
            if(line.equals("!")) break;
            System.out.println("Hash: \n"+dictionary.findInclusions(line));
            System.out.println("Binary: \n"+dictionary1.findInclusions(line));
        } while (true);

    }
}
