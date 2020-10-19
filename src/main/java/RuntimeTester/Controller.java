package RuntimeTester;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Controller implements Initializable {
    @FXML
    public BorderPane mainBorderView;
    //private TwitterBenchmark tBM;
    private ScheduledExecutorService scheduledExecutorService;
    private HashMap<String, BenchmarkItem> customBenchmarks;
    //BENCHMARKING
    @FXML
    private Pane Benchmarking;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Pane paneView;
    @FXML
    private CheckBox GC_FastSort, GC_Remove, GC_Rehash, GC_Values, GC_Constructor, GC_Keys, GC_Get, GC_Put,
            GC_ArrayListMergeSort, GC_ProfSlowSort, GC_Iter, GC_hasNext, GC_Next, GC_J_Constructor,
            GC_J_Put, GC_J_Get, GC_J_Remove, GC_J_Values, GC_J_Keys;
    @FXML
    private CheckBox GC_Twit_Trending, GC_Twit_Constructor, GC_Twit_ByDate, GC_Twit_ByAuth, GC_Twit_Add,
            GC_Twit_ConstructorII, GC_Twit_TrendingII, GC_TurboMode;
    //FUN DEMOS
    private static final ArrayList<String> trendOptions = new ArrayList<String>(Arrays.asList("Bee Movie script (small)", "real tweets (medium)",
            "a bunch of songs (colossal)", "a bunch of songs (large)",
            "a custom webpage"));
    @FXML
    private Slider GC_StopWordFactor, GC_StopWordFactorII, GC_TurboFactor;
    @FXML
    private Button GC_Reset, GC_Help, GC_Refresh;

    @FXML
    private ArrayList<CheckBox> toggles;

    @FXML
    private ButtonBar reflexive_button_area;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggles = new ArrayList<>(Arrays.asList(GC_FastSort, GC_Remove, GC_Rehash, GC_Values, GC_Constructor, GC_Keys, GC_Get, GC_Put,
                GC_ArrayListMergeSort, GC_ProfSlowSort, GC_Iter, GC_hasNext, GC_Next, GC_J_Constructor,
                GC_J_Put, GC_J_Get, GC_J_Remove, GC_J_Values, GC_J_Keys, GC_Twit_Trending, GC_Twit_Constructor, GC_Twit_ByDate, GC_Twit_ByAuth, GC_Twit_Add,
                GC_Twit_ConstructorII, GC_Twit_TrendingII));
        try {
            reflexiveGetBenchmarkables();
            addReflexiveBenchmarks();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        addListeners();
        initalizeGraph();

    }

    private void addReflexiveBenchmarks() {
        for(BenchmarkItem item : customBenchmarks.values()){
            reflexive_button_area.getButtons().add(item.getCheckbox());
        }
    }

    private class BenchmarkItem{
        public long getCounter() {
            return counter;
        }

        public void setCounter(long counter) {
            this.counter = counter;
        }

        public String getName() {
            StringBuilder sb = new StringBuilder();

            if(name != null && !name.equals("")){ sb.append(name);}else {
                sb.append(invokable.getName());
            }

            if(!expectedRuntime.equals("O(?)")) sb.append(" ").append(expectedRuntime);
            return sb.toString();
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private final Method invokable;
        private long counter;
        private String name;
        private String description;
        private CheckBox box;
        private Object testClass;
        private String expectedRuntime;

        public BenchmarkItem(Method m, benchmark a) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            name = a.name();
            description = a.category();
            expectedRuntime = a.expectedEfficiency();
            counter = 0l;
            if(!m.getReturnType().equals(Long.class) && !m.getReturnType().equals(long.class)) throw new IllegalArgumentException("Benchmark item must return Long or long");
            if(m.getParameterCount() != 1 ||
                    (!m.getParameters()[0].getType().equals(Long.class) &&
                            !m.getParameters()[0].getType().equals(long.class))) throw new IllegalArgumentException("Benchmark item must take a Long, long, int, or Integer as input");
            invokable = m;
            //Constructor c = invokable.getClass().getConstructor();      //TODO: How in the fuck constructor calls?
            //c.setAccessible(true);
            testClass  = new PolynomialBenchmark();
            invokable.setAccessible(true);
            box = new CheckBox(getName());
            box.setOnAction(this::bindButton);
            Tooltip t = new Tooltip();
            StringBuilder sb = new StringBuilder();
            sb.append("Declared method name: " + invokable.getName());
            if(a.description() != ""){
                sb.append(a.description());
            }
            t.setText(sb.toString());
            box.setTooltip(t);
        }

        public Long run(Long intensity) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            counter = intensity;
            System.out.println("Invoking run of " + invokable.getName());
            return (Long) invokable.invoke(testClass, intensity);
        }

        public long iterate() throws InvocationTargetException, IllegalAccessException, InstantiationException {
            counter++;
            return run(counter);
        }

        public CheckBox getCheckbox(){
         return box;
        }

        private void bindButton(ActionEvent e) {
            initalizeGraph();
        }

    }

    private void reflexiveGetBenchmarkables() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        customBenchmarks = new HashMap<>();
        /*Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.getUrlForClass(PolynomialBenchmark.class))
                .setScanners(new MethodAnnotationsScanner()));

        Set<Method> methods = reflections.getMethodsAnnotatedWith(benchmark.class);*/
        for(Method m : PolynomialBenchmark.class.getMethods()) {        //TODO: generalize
            Annotation[] annotations = m.getAnnotations();
            if (annotations.length == 0) continue;
            for (Annotation a : annotations) {
                if (a instanceof benchmark) {
                    benchmark bm = (benchmark) a;
                    BenchmarkItem item =new BenchmarkItem(m,bm);
                    customBenchmarks.put(item.getCheckbox().getText(), item);
                }
            }
        }
    }



    @FXML

    private void addListeners() {
        GC_Reset.setOnAction(e -> resetButtons());
        GC_Refresh.setOnAction(e -> initalizeGraph());
        GC_Help.setOnAction(e -> openHelpPage());
    }

    private void openHelpPage() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://sashaphoto.ca/COMP250FinalDebuggerHelp/"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void resetButtons() {
        for (BenchmarkItem item : customBenchmarks.values()) {
            item.getCheckbox().setSelected(false);
            item.setCounter(0);
        }
        initalizeGraph();
    }

    private void initalizeGraph() {
        try {
            scheduledExecutorService.shutdownNow();
        } catch (NullPointerException e) {
        }
        paneView.getChildren().clear();
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();


        // defining the axes
        xAxis.setLabel("Size of Dataset");
        xAxis.setAnimated(true);
        yAxis.setLabel("Runtime (nanoseconds)");
        yAxis.setAnimated(false);

        // creating the line chart with two axis created above
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Runtime Efficiency");
        lineChart.setAnimated(true); // animations
        ArrayList<XYChart.Series<String, Number>> plots = new ArrayList<XYChart.Series<String, Number>>();
        HashMap<XYChart.Series<String, Number>, Long> plotsRunTime = new HashMap<XYChart.Series<String, Number>, Long>();


        for (BenchmarkItem item : customBenchmarks.values()) {  //TODO: do this better for reflection
            if (item.getCheckbox().isSelected()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(item.getName());  //TODO: Better naming
                plotsRunTime.put(series, 0L);
            }
        }

        lineChart.getData().addAll(plotsRunTime.keySet());
        mainBorderView.setCenter(lineChart);
        scheduledExecutorService = Executors.newScheduledThreadPool(lineChart.getData().size() + 1);

        AtomicLong counter = new AtomicLong();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            counter.getAndIncrement();
            for (Map.Entry<XYChart.Series<String, Number>, Long> entry : plotsRunTime.entrySet()) {
                entry.setValue(ComputeRuntime(entry.getKey().getName(), counter.get()));
            }

            Platform.runLater(() -> {
                for (Map.Entry<XYChart.Series<String, Number>, Long> entry : plotsRunTime.entrySet()) {
                    entry.getKey().getData().add(new XYChart.Data<String, Number>(Long.toString(counter.get()), entry.getValue()));
                    if (entry.getKey().getData().size() > 75) {
                        lineChart.setVerticalGridLinesVisible(false);
                        lineChart.setHorizontalGridLinesVisible(false);
                        lineChart.setCreateSymbols(false);
                    }
                }
            });
        }, 0, 250, TimeUnit.MILLISECONDS);

    }

    private long ComputeRuntime(String input, Long count) {
        System.out.println("Computing runtime of " + input + " at count " + count);
        if (GC_TurboMode.isSelected()) {
            System.out.println("turbo is on");
            double amount = Double.valueOf(count);
            amount *= GC_TurboFactor.getValue();
            count = Math.round(amount);
        }
          try{
              return customBenchmarks.get(input).run(count);
          } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
              e.printStackTrace();
              return 0L;
          }
    }

    private void enableDarkTheme() {
        anchorPane.getStylesheets().add("/RuntimeTester/mondea_dark.css");
    }

    private void disableDarkTheme() {
        anchorPane.getStylesheets().remove("/RuntimeTester/mondea_dark.css");
    }
}
