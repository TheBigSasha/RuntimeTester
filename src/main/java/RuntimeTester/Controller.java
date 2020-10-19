package RuntimeTester;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;


import java.awt.*;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Controller implements Initializable {
    @FXML
    public BorderPane mainBorderView;
    public Button buttom_darkMode;
    public ScrollPane reflexScroller;
    public FlowPane reflexiveButtonArea;
    public Label stepTimeDisplay;

    private Boolean GC_TurboMode = true;
    private ScheduledExecutorService scheduledExecutorService;
    private HashMap<String, BenchmarkItem> customBenchmarks;
    int graphSpeed = 250;
    //BENCHMARKING
    private static final ArrayList<String> trendOptions = new ArrayList<String>(Arrays.asList("Bee Movie script (small)", "real tweets (medium)",
            "a bunch of songs (colossal)", "a bunch of songs (large)",
            "a custom webpage"));
    @FXML
    private Slider GC_TurboFactor, GC_AdjustmentFactor;
    @FXML
    private Button GC_Reset, GC_Help, GC_Refresh;

    public static final String darkThemeCSS = "https://raw.githubusercontent.com/joffrey-bion/javafx-themes/master/css/modena_dark.css";



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            reflexiveGetBenchmarkables();
            addReflexiveBenchmarks();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        addListeners();
        initalizeGraph();
        adjustSpeedofMathMethods();
        enableDarkTheme();

    }

    private void addReflexiveBenchmarks() {
        for(BenchmarkItem item : customBenchmarks.values()){
            reflexiveButtonArea.getChildren().add(item.getCheckbox());
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
            testClass  = new BenchmarkDefinitions();
            invokable.setAccessible(true);
            box = new CheckBox(getName());
            box.setSelected(true);
            box.setOnAction(this::bindButton);
            Tooltip t = new Tooltip();
            StringBuilder sb = new StringBuilder();
            sb.append("Declared method name: ").append(invokable.getName());
            if(!a.description().equals("")){
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
        for(Method m : BenchmarkDefinitions.class.getMethods()) {        //TODO: generalize
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
        buttom_darkMode.setOnAction(e -> toggleDarkTheme());
        GC_AdjustmentFactor.setOnMouseEntered(e -> adjustSpeedofMathMethods());
        GC_AdjustmentFactor.setOnMouseClicked(e -> adjustSpeedofMathMethods());
        GC_AdjustmentFactor.setOnTouchPressed(e -> adjustSpeedofMathMethods());

    }

    private void adjustSpeedofMathMethods() {
        BenchmarkDefinitions.setAdjustmentFactor(GC_AdjustmentFactor.getValue());
        stepTimeDisplay.setText(String.valueOf(BenchmarkDefinitions.getSimulationSpeed()));
    }

    private void toggleDarkTheme() {
        if(mainBorderView.getStylesheets().contains(darkThemeCSS)){
            buttom_darkMode.setText("Dark theme");

            disableDarkTheme();
        }else{
            buttom_darkMode.setText("Light theme");
            enableDarkTheme();
        }
    }

    private void openHelpPage() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://sashaphoto.ca/COMP250FinalDebuggerHelp/"));
            } catch (IOException | URISyntaxException e1) {
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
        HashMap<XYChart.Series<String, Number>, Long[]> plotsRunTime = new HashMap<XYChart.Series<String, Number>, Long[]>();


        for (BenchmarkItem item : customBenchmarks.values()) {  //TODO: do this better for reflection
            if (item.getCheckbox().isSelected()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(item.getName());  //TODO: Better naming
                Long[] content = new Long[2];
                content[0] = 0L;
                content[1] = 0L;
                plotsRunTime.put(series, content);
            }
        }

        lineChart.getData().addAll(plotsRunTime.keySet());
        mainBorderView.setCenter(lineChart);
        scheduledExecutorService = Executors.newScheduledThreadPool(lineChart.getData().size() + 1);

        AtomicLong counter = new AtomicLong();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            counter.get();
            if (GC_TurboMode) {
                System.out.println("turbo is on");
                double amount = 1.0;
                amount *= GC_TurboFactor.getValue();
                long count = Math.round(amount);
                counter.addAndGet(count);
            }else{
                counter.getAndIncrement();
            }
            for (Map.Entry<XYChart.Series<String, Number>, Long[]> entry : plotsRunTime.entrySet()) {
                entry.setValue(new Long[]{ComputeRuntime(entry.getKey().getName(), counter.get()), counter.get()});
            }

            Platform.runLater(() -> {
                for (Map.Entry<XYChart.Series<String, Number>, Long[]> entry : plotsRunTime.entrySet()) {
                    entry.getKey().getData().add(new XYChart.Data<String, Number>(Long.toString(entry.getValue()[1]), entry.getValue()[0]));
                    if (entry.getKey().getData().size() > 45 * (mainBorderView.getWidth() / 1000)) {
                        lineChart.setVerticalGridLinesVisible(false);
                        lineChart.setHorizontalGridLinesVisible(false);
                        lineChart.setCreateSymbols(false);
                    }else{
                        lineChart.setVerticalGridLinesVisible(true);
                        lineChart.setHorizontalGridLinesVisible(true);
                        lineChart.setCreateSymbols(true);
                    }
                }
            });
        }, 0, graphSpeed, TimeUnit.MILLISECONDS);

    }

    private long ComputeRuntime(String input, Long count) {
        System.out.println("Computing runtime of " + input + " at count " + count);

          try{
              System.out.println("Enter try");
              return customBenchmarks.get(input).run(count);
          } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
              e.printStackTrace();
              return 0L;
          }
    }

    private void enableDarkTheme() {
        buttom_darkMode.setText("Light theme");
        mainBorderView.getStylesheets().remove(lightThemeCSS);
        mainBorderView.getStylesheets().add(darkThemeCSS);

    }



    private void disableDarkTheme() {
        buttom_darkMode.setText("Dark theme");
        mainBorderView.getStylesheets().remove(darkThemeCSS);
        mainBorderView.getStylesheets().add(lightThemeCSS);
    }

    public static final String lightThemeCSS = "https://raw.githubusercontent.com/StaticallyTypedRice/GoliathCSS/master/src/goliath.css/classes/goliath/css/Goliath-Light.css";     //TODO: Find good light theme
    //public static final String darkThemeCss = "https://raw.githubusercontent.com/StaticallyTypedRice/GoliathCSS/master/src/goliath.css/classes/goliath/css/Goliath-Envy.css";    //TODO: this dark theme very cool
    //public static final String lightThemeCSS = "https://raw.githubusercontent.com/bullheadandplato/MaterialFX/master/material-fx-v0_3.css";
}
