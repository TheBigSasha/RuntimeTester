package RuntimeTester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalDouble;

public class PolynomialBenchmark {
    private static int speed = 0;

    private static void determineSpeed(){
        if(speed != 0) return;
        ArrayList<Integer> runs = new ArrayList<>();
        int[] testCase = new int[]{325325, 532626, 1,124,124125,43627,747,54,74, 77667, 78595967, 6565, 43549, 7857};
        for(int i = 0; i <50; i++) {
            long startTime = System.nanoTime();
            Arrays.sort(testCase);
            long endTime = System.nanoTime();
            runs.add((int) (endTime - startTime));
        }
        runs.remove(0); runs.remove(1);
        OptionalDouble avg = runs.stream().mapToInt(a -> a).average();
        if(avg.isPresent()) {
            speed = (int) Math.round(avg.getAsDouble());
        }else{
            speed = runs.get(15);
        }
        if(speed == 0){
            speed = 10000;
        }
        System.out.println("Slowness of your computer is " + speed);
    }

    public PolynomialBenchmark(){
        determineSpeed();
    }

    @benchmark(name = "pop", category = "linkedList methods")
    public Long testPop(Long size){
        //System.out.println("Invoked benchmark for size " + size);
        return size * speed;        //TODO: make this actually work
    }

    @benchmark(name = "sort", expectedEfficiency = "o(n^2)")
    public Long slowSort(Long size){
        return size * size * speed;
    }

    @benchmark(name = "fastSort", description = "lol kek", expectedEfficiency = "o(n log(n))")
    public long fastSort(long size){
        return (int) (size * Math.log(size)) * speed;
    }
}
