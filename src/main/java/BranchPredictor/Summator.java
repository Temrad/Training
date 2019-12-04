package BranchPredictor;

 class Summator {
    public Summator sum(int[] arr) {
        long start = System.currentTimeMillis();
        long result = 0;

        for (int anArr : arr) {
            result += anArr > 0 ? anArr : 0;
        }
        System.out.println((double)(System.currentTimeMillis() - start)/1000 + "s; Result = " + result);
        return this;
    }
}
