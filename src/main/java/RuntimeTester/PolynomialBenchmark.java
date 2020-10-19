package RuntimeTester;

public class PolynomialBenchmark {
    public PolynomialBenchmark(){

    }
    //TODO: set what is now 10,000 to be a time based on how fast your system is
    @benchmark(name = "pop", category = "linkedList methods")
    public Long testPop(Long size){
        System.out.println("Invoked benchmark for size " + size);
        return size * 10000;        //TODO: make this actually work
    }

    @benchmark(name = "sort", expectedEfficiency = "o(n^2)")
    public Long slowSort(Long size){
        return size * size * 10000;
    }
}
