package RuntimeTester;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BenchmarkDefinitions {
    private static Random rand;
    private static int speed = 0;
    private static final int seed = 24601;

    public static double getAdjustmentFactor() {
        return adjustmentFactor;
    }

    public static void setAdjustmentFactor(double adjustmentFactor) {
        BenchmarkDefinitions.adjustmentFactor = adjustmentFactor;
    }

    private static double adjustmentFactor = 1d;

    public static int getSimulationSpeed(){
        return (int) Math.round((double) speed * adjustmentFactor);
    }

    private static void determineSpeed(){
        if(speed != 0) return;
        ArrayList<Integer> runs = new ArrayList<>();
        int[] testCase = new int[]{325325, 532626};
        for(int i = 0; i <50; i++) {
            long startTime = System.nanoTime();
            Arrays.sort(testCase);
            long endTime = System.nanoTime();
            runs.add((int) (endTime - startTime));
        }

        for(int i = 0; i <30; i++) {
            runs.add((int) (arraysSort(2) / 14));
        }
        runs.remove(0); runs.remove(1);
        OptionalDouble avg = runs.stream().mapToInt(a -> a).average();
        if(avg.isPresent()) {
            speed = (int) Math.round(avg.getAsDouble());
        }
        if(speed == 0){
            speed = 1000;
        }
        System.out.println("Slowness of your computer is " + speed);
    }

    public BenchmarkDefinitions() {
        rand = new Random(seed);
        determineSpeed();
    }

    /**
     * Generates a date matching the format in the teacher supplied debugger
     *
     * @return a date
     */
    private static String nextDateString() {
        int year = 2009 + rand.nextInt(11);
        int month = 1 + rand.nextInt(11);
        int day = 1 + rand.nextInt(27);
        int hour = rand.nextInt(23);
        int minute = rand.nextInt(59);
        int second = rand.nextInt(59);
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        sb.append("-");
        if (month < 10) {
            sb.append("0" + month);
        } else {
            sb.append(month);
        }
        sb.append("-");
        if (day < 10) {
            sb.append("0" + day);
        } else {
            sb.append(day);
        }
        sb.append(" ");
        if (hour < 10) {
            sb.append("0" + hour);
        } else {
            sb.append(hour);
        }
        sb.append(":");
        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append(minute);
        }
        sb.append(":");
        if (second < 10) {
            sb.append("0" + second);
        } else {
            sb.append(second);
        }
        return sb.toString();
    }

    private static Date nextDate(){
        try {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(nextDateString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @benchmark(name = "ArrayList.sort", expectedEfficiency = "O(n log(n))", category = "Java Builtin")
    public static long arraysSort(long size) {
        ArrayList<Date> dataset = new ArrayList<>();
        for (long i = 0; i < size; i++) {
            dataset.add(nextDate());
        }
        long startTime = System.nanoTime();
        dataset.sort(Date::compareTo);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    @benchmark(name = "traverse list", category = "Math demos", expectedEfficiency = "O(N)", theoretical = true)
    public Long n(Long size) {
        //System.out.println("Invoked benchmark for size " + size);
        return size * getSimulationSpeed();
    }

    @benchmark(name = "sort", expectedEfficiency = "o(n^2)", category = "Math demos", theoretical = true)
    public Long nSquared(Long size) {
        return Math.round(Math.pow(size , 2) * getSimulationSpeed());
    }

    @benchmark(name = "fast sorting ", category = "Math demos", expectedEfficiency = "o(n log(n))", description = "This is the speed at which many optimal sorting algorithms run", theoretical = true)
    public long nLogN(long size) {
        return (long) (size * Math.log(size)) * getSimulationSpeed();
    }

    @benchmark(name = "superfast", description = " this one is sanic fast", expectedEfficiency = "O(1)", category = "Math demos", theoretical = true)
    public long one(long size) {
        return getSimulationSpeed();
    }

   /* @benchmark(name="the worst", expectedEfficiency = "O(n!)", category = "Math demos")
    public static long nFactorial(long size){
        long n=1;
        long factorial = getSimulationSpeed();
        for(n=1; n<= size; n++){
            factorial=factorial*n;
        }
        return factorial;
    }*/     //This breaks the graph by exploding to massive size

    @benchmark(name = "queue.deqeue", expectedEfficiency = "O(1)", category = "Java Builtin")
    public long enqueueTest(long size) {
        Queue<Date> dataset = new ConcurrentLinkedQueue<>();
        for (long i = 0; i < size; i++) {
            dataset.add(nextDate());
        }
        long startTime = System.nanoTime();
        dataset.poll();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    @benchmark(name = "queue.get from middle", expectedEfficiency = "O(N)", category = "Java Builtin")
    public long getFromMiddleOfQueue(long size){
        Queue<Date> dataset = new ConcurrentLinkedQueue<>();
        for(long i = 0; i < size; i++){
            dataset.add(nextDate());
        }
        long startTime = System.nanoTime();
        for(int i = 0; i < size/2 ; i++){
            dataset.poll();
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    //TODO: assignment methods can be added here by writing methods with @benchmark annotations
}
