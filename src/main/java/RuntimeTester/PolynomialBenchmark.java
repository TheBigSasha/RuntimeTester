package RuntimeTester;

public class PolynomialBenchmark {
    @benchmark(name = "pop", category = "linkedList methods")
    public long testPop(int size){
        return size;        //TODO: make this actually work
    }
}
