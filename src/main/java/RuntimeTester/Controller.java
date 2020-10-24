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
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Controller implements Initializable {
    public static final String darkThemeCSS = "https://raw.githubusercontent.com/joffrey-bion/javafx-themes/master/css/modena_dark.css";
    public static final String lightThemeCSS = "https://raw.githubusercontent.com/StaticallyTypedRice/GoliathCSS/master/src/goliath.css/classes/goliath/css/Goliath-Light.css";     //TODO: Find good light theme
    //BENCHMARKING
    private static final ArrayList<String> trendOptions = new ArrayList<String>(Arrays.asList("Bee Movie script (small)", "real tweets (medium)",
            "a bunch of songs (colossal)", "a bunch of songs (large)",
            "a custom webpage"));
    private final Boolean GC_TurboMode = true;
    @FXML
    public BorderPane mainBorderView;
    public Button buttom_darkMode;
    public VBox reflexiveButtonArea;
    public Label stepTimeDisplay;
    private static final int originalRuntime = 250;
    public Slider GC_SimulationSpeed;
    public Label GC_SimulationSpeedTitle;
    public Label GC_AdjustmentFactorTitle;
    public Label GC_TurboFactorTitle;
    private int graphSpeed = 250;
    private ScheduledExecutorService scheduledExecutorService;
    private HashMap<String, BenchmarkItem> customBenchmarks;
    @FXML
    private Slider GC_TurboFactor, GC_AdjustmentFactor;
    @FXML
    private Button GC_Reset, GC_Help, GC_Refresh;

    /**
     * Private helper method
     *
     * @param directory
     *            The directory to start with
     * @param pckgname
     *            The package name to search for. Will be needed for getting the
     *            Class object.
     * @param classes
     *            if a file isn't loaded but still is in the directory
     * @throws ClassNotFoundException
     */
    private static void checkDirectory(File directory, String pckgname,
                                       ArrayList<Class<?>> classes) throws ClassNotFoundException {
        File tmpDirectory;

        if (directory.exists() && directory.isDirectory()) {
            final String[] files = directory.list();

            for (final String file : files) {
                if (file.endsWith(".class")) {
                    try {
                        classes.add(Class.forName(pckgname + '.'
                                + file.substring(0, file.length() - 6)));
                    } catch (final NoClassDefFoundError e) {
                        // do nothing. this class hasn't been found by the
                        // loader, and we don't care.
                    }
                } else if ((tmpDirectory = new File(directory, file))
                        .isDirectory()) {
                    checkDirectory(tmpDirectory, pckgname + "." + file, classes);
                }
            }
        }
    }

    /**
     * Private helper method.
     *
     * @param connection
     *            the connection to the jar
     * @param pckgname
     *            the package name to search for
     * @param classes
     *            the current ArrayList of all classes. This method will simply
     *            add new classes.
     * @throws ClassNotFoundException
     *             if a file isn't loaded but still is in the jar file
     * @throws IOException
     *             if it can't correctly read from the jar file.
     */
    private static void checkJarFile(JarURLConnection connection,
                                     String pckgname, ArrayList<Class<?>> classes)
            throws ClassNotFoundException, IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        String name;

        for (JarEntry jarEntry = null; entries.hasMoreElements()
                && ((jarEntry = entries.nextElement()) != null);) {
            name = jarEntry.getName();

            if (name.contains(".class")) {
                name = name.substring(0, name.length() - 6).replace('/', '.');

                if (name.contains(pckgname)) {
                    classes.add(Class.forName(name));
                }
            }
        }
    }

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname
     *            the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException
     *             if something went wrong
     */
    public static ArrayList<Class<?>> getClassesForPackage(String pckgname)
            throws ClassNotFoundException {
        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        try {
            final ClassLoader cld = Thread.currentThread()
                    .getContextClassLoader();

            if (cld == null)
                throw new ClassNotFoundException("Can't get class loader.");

            final Enumeration<URL> resources = cld.getResources(pckgname
                    .replace('.', '/'));
            URLConnection connection;

            for (URL url = null; resources.hasMoreElements()
                    && ((url = resources.nextElement()) != null);) {
                try {
                    connection = url.openConnection();

                    if (connection instanceof JarURLConnection) {
                        checkJarFile((JarURLConnection) connection, pckgname,
                                classes);
                    } else if (connection != null) {    //instanceof FileURLConnection
                        try {
                            checkDirectory(
                                    new File(URLDecoder.decode(url.getPath(),
                                            "UTF-8")), pckgname, classes);
                        } catch (final UnsupportedEncodingException ex) {
                            throw new ClassNotFoundException(
                                    pckgname
                                            + " does not appear to be a valid package (Unsupported encoding)",
                                    ex);
                        }
                    } else
                        throw new ClassNotFoundException(pckgname + " ("
                                + url.getPath()
                                + ") does not appear to be a valid package");
                } catch (final IOException ioex) {
                    throw new ClassNotFoundException(
                            "IOException was thrown when trying to get all resources for "
                                    + pckgname, ioex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new ClassNotFoundException(
                    pckgname
                            + " does not appear to be a valid package (Null pointer exception)",
                    ex);
        } catch (final IOException ioex) {
            throw new ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for "
                            + pckgname, ioex);
        }

        return classes;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addBenchmarks();
        addListeners();
        adjustSpeedofMathMethods();
        enableDarkTheme();
        initalizeGraph();
    }

    public void addBenchmarksFromPackageNames(List<String> pkgNames){
        ArrayList testClasses = new ArrayList();
        for(String s : pkgNames){
            try {
                testClasses.addAll(getClassesForPackage(s));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        addBenchmarks(testClasses);
    }

    public void addBenchmarksFromPackages(List<Package> packages){
        ArrayList testClasses = new ArrayList();
        for(Package p : packages){
            try {
                testClasses.addAll(getClassesForPackage(p.getName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        addBenchmarks(testClasses);
    }

    /**
     * Reflects through the module to get all the packages, excluding com.sun. and javafx. packages
     * Adds all @benchmark tagged methods to the tests.
     */
    public void addBenchmarks(List<Class<?>> testClasses) {
        System.out.println("Classes passed : " + testClasses.toString());

        try {
            for(String s : this.getClass().getModule().getPackages()){
                //System.out.println("package named " + s + " was found in module");
                if(!s.startsWith("javafx.") && !s.startsWith("com.sun.")) { //Ignores things we can't reflect
                    System.out.println("Querying package " + s);
                    try {
                        testClasses.addAll(getClassesForPackage(s));
                    } catch (Exception ignored) {

                    }
                }
            }


            for(Class c : testClasses) {
                System.out.println("Querying class " + c.getName());
                reflexiveGetBenchmarkables(c);
            }
            addReflexiveBenchmarks();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reflects through the module to get all the packages, excluding com.sun. and javafx. packages
     * Adds all @benchmark tagged methods to the tests.
     */
    private void addBenchmarks() {

        ArrayList<Class<?>> testClasses = new ArrayList<>();
        try{ testClasses.addAll(getClassesForPackage("a2")); } catch (ClassNotFoundException e) {
            e.printStackTrace(); }
        addBenchmarks(testClasses);
    }

    private static int compareBenchmarkItems(BenchmarkItem a, BenchmarkItem b) {
        if (a.getCategory().equals("Other")) return Integer.MAX_VALUE;       //Unspecified at the bottom
        //if(a.getCategory().equals("Math demos")) return Integer.MIN_VALUE;  //Math demos at the top
        return a.getCategory().compareTo(b.getCategory());                  //Else alphabetical
    }

    private void addReflexiveBenchmarks() {
        List<BenchmarkItem> items = new ArrayList<>(customBenchmarks.values());
        reflexiveButtonArea.getChildren().clear();
        items.sort(Controller::compareBenchmarkItems);
        if(customBenchmarks.size() == 0) return;
        String curCat = items.get(0).getCategory();
        if (!curCat.isBlank()) {
            Label l = new Label(curCat);
            reflexiveButtonArea.getChildren().add(l);
        }
        for (BenchmarkItem item : items) {
            if (!item.getCategory().equals(curCat)) {
                curCat = item.getCategory();
                Label l = new Label(curCat);
              reflexiveButtonArea.getChildren().add(l);
            }
            reflexiveButtonArea.getChildren().add(item.getCheckbox());
        }
    }

    private void initalizeGraph() {

        try {
            scheduledExecutorService.shutdownNow();
        } catch (NullPointerException e) {
        }
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();
        if(graphSpeed > 1000) graphSpeed = originalRuntime;
        GC_SimulationSpeed.setMouseTransparent(false);
        GC_SimulationSpeed.setOpacity(1);
        GC_SimulationSpeed.setMax(1000);
        graphSpeed = (int) GC_SimulationSpeed.getValue();



        // defining the axes
        xAxis.setLabel("Size of Dataset");
        xAxis.setAnimated(true);
        //if(graphSpeed > 20) xAxis.setAnimated(true);
        yAxis.setLabel("Runtime (nanoseconds)");
        yAxis.setAnimated(false);
        if(graphSpeed > 300) yAxis.setAnimated(true);


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
        Runnable r = new Runnable() {
            @Override
            public void run() {
                {

                    counter.get();
                    if (GC_TurboMode) {
                        double amount = 1.0;
                        amount *= GC_TurboFactor.getValue();
                        long count = Math.round(amount);
                        counter.addAndGet(count);
                    } else {
                        counter.getAndIncrement();
                    }
                    for (Map.Entry<XYChart.Series<String, Number>, Long[]> entry : plotsRunTime.entrySet()) {
                        entry.setValue(new Long[]{ComputeRuntime(entry.getKey().getName(), counter.get()), counter.get()});
                    }
                    scheduledExecutorService.schedule(this,graphSpeed,TimeUnit.MILLISECONDS);

                    Platform.runLater(() -> {
                        graphSpeed = (int) GC_SimulationSpeed.getValue();
                        if(graphSpeed > 300) yAxis.setAnimated(true);
                        for (Map.Entry<XYChart.Series<String, Number>, Long[]> entry : plotsRunTime.entrySet()) {
                            entry.getKey().getData().add(new XYChart.Data<String, Number>(Long.toString(entry.getValue()[1]), entry.getValue()[0]));
                            if (entry.getKey().getData().size() > 45 * (mainBorderView.getWidth() / 1000)) {
                                lineChart.setVerticalGridLinesVisible(false);
                                lineChart.setHorizontalGridLinesVisible(false);
                                lineChart.setCreateSymbols(false);
                            } else {
                                lineChart.setVerticalGridLinesVisible(true);
                                lineChart.setHorizontalGridLinesVisible(true);
                                lineChart.setCreateSymbols(true);
                            }
                        }
                    });
                }
            }
        };
        scheduledExecutorService.schedule(r,graphSpeed,TimeUnit.MILLISECONDS);

        //TODO: loading icon when runtime is slow, notification or status at bottom to show what the period is.

    }

    private void reflexiveGetBenchmarkables(Class<?> c) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(customBenchmarks == null) customBenchmarks = new HashMap<>();

        for (Method m : c.getMethods()) {
            Annotation[] annotations = m.getAnnotations();
            if (annotations.length == 0) continue;
            for (Annotation a : annotations) {
                if (a instanceof benchmark) {
                    benchmark bm = (benchmark) a;
                    BenchmarkItem item = new BenchmarkItem(m, bm);
                    if(!customBenchmarks.containsValue(item)) customBenchmarks.put(item.getCheckbox().getText(), item);
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

        GC_SimulationSpeed.setTooltip(new Tooltip("Adjust the delay between calculations. If this is too slow, the program will auto adjust to increase accordingly."));
        GC_AdjustmentFactor.setTooltip(new Tooltip("Adjust the coefficient term for the theoretical methods."));
        GC_TurboFactorTitle.setTooltip(new Tooltip("The amount of things added to the dataset at each step"));
        GC_SimulationSpeedTitle.setTooltip(new Tooltip("Adjust the delay between calculations. If this is too slow, the program will auto adjust to increase accordingly."));
        GC_AdjustmentFactorTitle.setTooltip(new Tooltip("Adjust the coefficient term for the theoretical methods."));

        GC_AdjustmentFactor.setOnMouseEntered(e -> adjustSpeedofMathMethods());
        GC_AdjustmentFactor.setOnMouseClicked(e -> adjustSpeedofMathMethods());
        GC_AdjustmentFactor.setOnTouchPressed(e -> adjustSpeedofMathMethods());

    }

    private void adjustSpeedofMathMethods() {
        BenchmarkDefinitions.setAdjustmentFactor(GC_AdjustmentFactor.getValue());
        stepTimeDisplay.setText(String.valueOf(BenchmarkDefinitions.getSimulationSpeed()));
    }

    private void toggleDarkTheme() {
        if (mainBorderView.getStylesheets().contains(darkThemeCSS)) {
            buttom_darkMode.setText("Dark theme");

            disableDarkTheme();
        } else {
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
    private long ComputeRuntime(String input, Long count) {

        try {
            long runtime = customBenchmarks.get(input).run(count);
            if (runtime < 0) {
                return runtime * -1;
            } else {
                if((runtime / 100000) >= graphSpeed && !customBenchmarks.get(input).isTheoretical()){
                    if((runtime / 100000) >= (GC_SimulationSpeed.getMax() / 1.5)) {
                        graphSpeed = Math.toIntExact(Math.round((runtime / 100000.0) * 1.5));
                        GC_SimulationSpeed.setMouseTransparent(true);
                        GC_SimulationSpeed.setOpacity(0.5);
                        GC_SimulationSpeed.setMax(graphSpeed);
                        GC_SimulationSpeed.setValue(graphSpeed);
                    }else{  //TODO: Account for spikes.
                        graphSpeed = Math.toIntExact(Math.round((runtime / 100000.0) * 1.5));
                        GC_SimulationSpeed.setValue(graphSpeed);
                    }
                }

                return runtime;
            }
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

    private class BenchmarkItem {
        private final String category;
        private final Method invokable;

        public boolean isTheoretical() {
            return theoretical;
        }

        private final boolean theoretical;
        private final CheckBox box;
        private final Object testClass;
        private final String expectedRuntime;
        private long counter;
        private String name;
        private String description;

        public BenchmarkItem(Method m, benchmark a) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
            name = a.name();
            description = a.category();
            expectedRuntime = a.expectedEfficiency();
            theoretical = a.theoretical();
            counter = 0l;
            if (!m.getReturnType().equals(Long.class) && !m.getReturnType().equals(long.class))
                throw new IllegalArgumentException("Benchmark item must return Long or long");
            if (m.getParameterCount() != 1 ||
                    (!m.getParameters()[0].getType().equals(Long.class) &&
                            !m.getParameters()[0].getType().equals(long.class)))
                throw new IllegalArgumentException("Benchmark item must take a Long, long, int, or Integer as input");
            invokable = m;
            //Constructor c = invokable.getClass().getConstructor();
            //c.setAccessible(true);
            testClass = new BenchmarkDefinitions();
            invokable.setAccessible(true);
            box = new CheckBox(getName());
            box.setSelected(true);
            box.setOnAction(this::bindButton);
            Tooltip t = new Tooltip();
            StringBuilder sb = new StringBuilder();
            sb.append(a.category()).append(" : ");
            sb.append(invokable.getName());
            category = a.category();
            if (!a.description().equals("")) {
                sb.append(" ").append(a.description());
            }
            t.setText(sb.toString());
            box.setTooltip(t);
        }

        public long getCounter() {
            return counter;
        }

        public void setCounter(long counter) {
            this.counter = counter;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            StringBuilder sb = new StringBuilder();

            if (name != null && !name.equals("")) {
                sb.append(name);
            } else {
                sb.append(invokable.getName());
            }

            if (!expectedRuntime.equals("O(?)")) sb.append(" ").append(expectedRuntime);
            return sb.toString();
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long run(Long intensity) throws InvocationTargetException, IllegalAccessException, InstantiationException {
            counter = intensity;
            return (Long) invokable.invoke(testClass, intensity);
        }

        public long iterate() throws InvocationTargetException, IllegalAccessException, InstantiationException {
            counter++;
            return run(counter);
        }

        public CheckBox getCheckbox() {
            return box;
        }

        private void bindButton(ActionEvent e) {
            initalizeGraph();
        }

        public String getCategory() {
            return category;
        }

        public int compareTo(BenchmarkItem benchmarkItem, BenchmarkItem benchmarkItem1) {
            return benchmarkItem.getCategory().compareTo(benchmarkItem1.getCategory());
        }
    }
    //public static final String darkThemeCss = "https://raw.githubusercontent.com/StaticallyTypedRice/GoliathCSS/master/src/goliath.css/classes/goliath/css/Goliath-Envy.css";    //TODO: this dark theme very cool
    //public static final String lightThemeCSS = "https://raw.githubusercontent.com/bullheadandplato/MaterialFX/master/material-fx-v0_3.css";
}
