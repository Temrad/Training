package BranchPredictor;

import Util.ArrayGenerator;

public class Main {
    public static void main(String[] args) {
        new Summator().sum(ArrayGenerator.getArray(300*1000*1000))
                .sum(ArrayFiller.getArray(300*1000*1000, 2));
    }


}
