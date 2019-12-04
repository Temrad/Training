package BranchPredictor;

public class ArrayFiller {
    public static int[] getArray(int size, int value) {
        int[] arr = new int[size];
        for(int i = 0; i < size; i++) {
            arr[i] = value;
        }
        return arr;
    }
}
