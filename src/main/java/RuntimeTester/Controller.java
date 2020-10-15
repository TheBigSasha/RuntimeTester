package RuntimeTester;

import javafx.application.Platform;
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
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller implements Initializable {
    private HashTableBenchmark BM;
    private TwitterBenchmark tBM;
    private ScheduledExecutorService scheduledExecutorService;
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

    //UNIT TESTING
    @FXML
    private Pane UnitTesting;
    @FXML
    private MenuItem UT_RunAll, UT_RunBasicTwitter;
    @FXML
    private Button UT_RunBtn;
    @FXML
    private TextArea UnitTestTextArea;
    private ArrayList<CheckBox> toggles;
    @FXML
    private TextArea Fun_LastOpRuntime;
    @FXML
    private ProgressIndicator Fun_Progress;
    @FXML
    private Pane FunDemos;
    @FXML
    private Spinner<Integer> Fun_NumOfTweetsAbout, Fun_CommonWordsNum;
    @FXML
    private ChoiceBox<String> FunTrendingSelector;
    @FXML
    private Button Fun_GoTrends, Fun_GoSong, Fun_GoTweets, Fun_TweetsAboutRand, Fun_CommonWordsRand,
            Fun_GoWords, Fun_SingAboutRand, fun_Sashaphotolink;
    @FXML
    private TextField Fun_TweetsAbout, Fun_SongKeyword;
    @FXML
    private TextArea Fun_Output;
    @FXML
    private Slider Fun_StopWordFactor;
    //ALL
    @FXML
    private Label BM_Title, UT_Title, FUN_Title;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggles = new ArrayList<>(Arrays.asList(GC_FastSort, GC_Remove, GC_Rehash, GC_Values, GC_Constructor, GC_Keys, GC_Get, GC_Put,
                GC_ArrayListMergeSort, GC_ProfSlowSort, GC_Iter, GC_hasNext, GC_Next, GC_J_Constructor,
                GC_J_Put, GC_J_Get, GC_J_Remove, GC_J_Values, GC_J_Keys, GC_Twit_Trending, GC_Twit_Constructor, GC_Twit_ByDate, GC_Twit_ByAuth, GC_Twit_Add,
                GC_Twit_ConstructorII, GC_Twit_TrendingII));
        addListeners();
        BM = new HashTableBenchmark();
        tBM = new TwitterBenchmark(BM.getRand());
        //playOof();
        initalizeGraph();
    }

    @FXML
    protected void OpenBenchmarking() {
        //System.out.println("Switching to Benchmarking View");
        UnitTesting.setVisible(false);
        FunDemos.setVisible(false);
        UT_Title.setOpacity(0.5);
        FUN_Title.setOpacity(0.5);
        BM_Title.setOpacity(1);
        Benchmarking.setVisible(true);
        //initalizeGraph(0);
    }

    @FXML
    protected void OpenUnitTesting() {
        //System.out.println("Switching to Unit Testing View");
        Benchmarking.setVisible(false);
        FunDemos.setVisible(false);
        //scheduledExecutorService.shutdownNow();
        BM_Title.setOpacity(0.5);
        FUN_Title.setOpacity(0.5);
        UT_Title.setOpacity(1);
        UnitTesting.setVisible(true);
    }

    @FXML
    protected void OpenFun() {
        //System.out.println("Switching to fun view");
        BM_Title.setOpacity(0.5);
        FUN_Title.setOpacity(1);
        UT_Title.setOpacity(0.5);
        UnitTesting.setVisible(false);
        Benchmarking.setVisible(false);
        FunDemos.setVisible(true);

    }

    private void addListeners() {
        UT_RunBtn.setOnAction(e -> runAllTests());
        UT_RunAll.setOnAction(e -> runUnitTests());
        UT_RunBasicTwitter.setOnAction(e -> runBasicTwitterTest());
        GC_Reset.setOnAction(e -> resetButtons());
        GC_Refresh.setOnAction(e -> initalizeGraph());
        GC_Help.setOnAction(e -> openHelpPage());
        Fun_GoWords.setOnAction(e -> fun_words(false));
        Fun_GoSong.setOnAction(e -> fun_sing(false));
        Fun_GoTrends.setOnAction(e -> fun_trend());
        Fun_GoTweets.setOnAction(e -> fun_tweets(false));
        fun_Sashaphotolink.setOnAction(e -> openSashaPhoto());
        FunTrendingSelector.getItems().addAll(trendOptions);
        FunTrendingSelector.setValue(trendOptions.get(0));
        for (CheckBox box : toggles) {
            box.setOnMouseClicked(e -> initalizeGraph());
        }
        Fun_CommonWordsNum.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int i) {
                setValue(getValue() - i);
                if (getValue() <= 0) {
                    setValue(1);
                }
                if (getValue() > 200) {
                    setValue(200);
                }
            }

            @Override
            public void increment(int i) {
                setValue(getValue() + i);
                if (getValue() <= 0) {
                    setValue(1);
                }
                if (getValue() > 200) {
                    setValue(200);
                }
            }
        });
        Fun_CommonWordsNum.getValueFactory().setValue(5);
        Fun_NumOfTweetsAbout.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int i) {
                setValue(getValue() - i);
                if (getValue() <= 0) {
                    setValue(1);
                }
                if (getValue() > 200) {
                    setValue(200);
                }
            }

            @Override
            public void increment(int i) {
                setValue(getValue() + i);
                if (getValue() <= 0) {
                    setValue(1);
                }
                if (getValue() > 200) {
                    setValue(200);
                }
            }
        });
        Fun_NumOfTweetsAbout.getValueFactory().setValue(5);
        Fun_SingAboutRand.setOnAction(e -> fun_sing(true));
        Fun_CommonWordsRand.setOnAction(e -> fun_words(true));
        Fun_TweetsAboutRand.setOnAction(e -> fun_tweets(true));
    }

    private void openSashaPhoto() {
        /*if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://sashaphoto.ca/"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }*/
    }

    private void fun_words(boolean random) {
        if (random) {
            Fun_CommonWordsNum.getValueFactory().setValue(1 + BM.getRand().nextInt(49));
        } else {
            Fun_Output.setText("");
            ArrayList<String> words = BM.getRand().nextStopWords(Fun_CommonWordsNum.getValue());
            for (String s : words) {
                Fun_Output.appendText(s + "\n");
            }
        }
    }

    private void fun_sing(boolean random) {
        if (random) {
            String[] content = BM.getRand().nextContent().split(" ");
            Fun_SongKeyword.setText(content[content.length / (2)].replace(",", "").strip().trim());
        } else {
            try {
                Fun_Output.setText(BM.getRand().sing(Fun_SongKeyword.getText()));
            } catch (Exception e) {
                Fun_Output.setText(BM.getRand().sing());
            }
        }

    }

    private void fun_trend() {
        Fun_Progress.setOpacity(1);
        Fun_Progress.setProgress(0.1);
        String choice = FunTrendingSelector.getValue();
        long StartTime = System.nanoTime();
        if (choice.equals(trendOptions.get(0))) { //Bee Movie Script
            Fun_Output.setText(tBM.getRand().nextTrend(0, (int) Fun_StopWordFactor.getValue()));
        } else if (choice.equals(trendOptions.get(1))) {  //Real Tweets
            Fun_Output.setText(tBM.getRand().nextTrend(1, (int) Fun_StopWordFactor.getValue()));
        } else if (choice.equals(trendOptions.get(2))) { //A bunch of songs
            Fun_Output.setText(tBM.getRand().nextTrend(2, (int) Fun_StopWordFactor.getValue()));
        } else if (choice.equals(trendOptions.get(3))) {
            Fun_Output.setText(tBM.getRand().nextTrend(3, (int) Fun_StopWordFactor.getValue()));
        } else if (choice.equals(trendOptions.get(4))) { //Custom URL
            //TODO:
            Fun_Output.setText("URL Trend Finder: Waiting...");
            final ArrayList<String> URLchoices = new ArrayList<>(Arrays.asList("https://www.mcgill.ca/study/2019-2020/courses/comp-250", "https://stackoverflow.com/questions/240546/remove-html-tags-from-a-string",
                    "https://twitter.com/explore", "https://en.wikipedia.org/wiki/Python_(programming_language)", "https://en.wikipedia.org/wiki/Special:Random",
                    "https://www.geeksforgeeks.org/java-util-hashmap-in-java-with-examples/", "https://news.google.com/"));
            TextInputDialog dialog = new TextInputDialog(URLchoices.get(BM.getRand().nextInt(URLchoices.size() - 1)));
            dialog.setTitle("URL Prompt");
            dialog.setHeaderText("Run trend analysis on a custom URL");
            dialog.setContentText("Enter a valid URL:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                try {
                    Fun_Output.setText(tBM.getRand().nextTrend(s, (int) Fun_StopWordFactor.getValue()));
                } catch (IOException e) {
                    Fun_Output.setText(e.getMessage());
                }
            });
        }
        long endTime = System.nanoTime();
        Fun_Progress.setProgress(1);
        Fun_LastOpRuntime.setText((endTime - StartTime) + " nanos");
        Fun_Progress.setOpacity(0);

    }

    private void fun_tweets(boolean random) {
        if (random) {
            Fun_NumOfTweetsAbout.getValueFactory().setValue(1 + BM.getRand().nextInt(49));
            String[] content = BM.getRand().nextContent().split(" ");
            Fun_TweetsAbout.setText(content[content.length / (2)].replace(",", "").strip().trim());
        } else {
            Tweet[] generated = BM.getRand().nextTweets(Fun_NumOfTweetsAbout.getValue(), true, Fun_TweetsAbout.getText());
            Fun_Output.setText("");
            for (Tweet t : generated) {
                Fun_Output.appendText(t.toString() + "\n");
            }
        }
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
        for (CheckBox box : toggles) {
            box.setSelected(false);
        }
        initalizeGraph();
    }

    private void runUnitTests() {
        HashTableUnitTester UT = new HashTableUnitTester(BM.getRand());
        UnitTestTextArea.setText(UT.runTests());
    }

    private void runBasicTwitterTest() {
        try {
            UnitTestTextArea.appendText("\n\n\n" + tBM.basicTwitterTest());
        } catch (NullPointerException e) {
            tBM = new TwitterBenchmark();
            try {
                UnitTestTextArea.appendText("\n\n\n" + tBM.basicTwitterTest());
            } catch (Exception f) {
                f.printStackTrace();
                UnitTestTextArea.appendText("\n\n\n FAILED TO RUN BASIC TWITTER TEST");
            }
        } catch (Exception e) {
            UnitTestTextArea.appendText("\n\n\n FAILED TO RUN BASIC TWITTER TEST");
        }
    }

    private void runAllTests() {
        UnitTestTextArea.setEditable(false);
        UnitTestTextArea.setWrapText(true);
        UnitTestTextArea.setText("Running tests. This will take a while.");
        runUnitTests();
        runBasicTwitterTest();
        UnitTestTextArea.appendText("\n \n \nMore tests coming soon <3");
    }

    private void initalizeGraph() {
        try {
            scheduledExecutorService.shutdownNow();
        } catch (NullPointerException e) {
            //System.out.println("Starting plot");
        }
        paneView.getChildren().clear();
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();


        // defining the axes
        xAxis.setLabel("Size of HashTable");
        xAxis.setAnimated(true);
        yAxis.setLabel("Runtime (nano)");
        yAxis.setAnimated(false);

        // creating the line chart with two axis created above
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Runtime Efficiency");
        lineChart.setAnimated(true); // animations
        ArrayList<XYChart.Series<String, Number>> plots = new ArrayList<XYChart.Series<String, Number>>();
        HashMap<XYChart.Series<String, Number>, Long> plotsRunTime = new HashMap<XYChart.Series<String, Number>, Long>();


        for (CheckBox box : toggles) {
            if (box.isSelected()) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(box.getText());  //TODO: Better naming
                plotsRunTime.put(series, 0L);
            }
        }

        lineChart.getData().addAll(plotsRunTime.keySet());
        lineChart.setPrefSize(900, 500);
        paneView.getChildren().add(lineChart);

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        AtomicInteger counter = new AtomicInteger();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            counter.getAndIncrement();
            for (Map.Entry<XYChart.Series<String, Number>, Long> entry : plotsRunTime.entrySet()) {
                entry.setValue(ComputeRuntime(entry.getKey().getName(), counter.get()));
            }

            Platform.runLater(() -> {
                for (Map.Entry<XYChart.Series<String, Number>, Long> entry : plotsRunTime.entrySet()) {
                    entry.getKey().getData().add(new XYChart.Data<String, Number>(Integer.toString(counter.get()), entry.getValue()));
                    if (entry.getKey().getData().size() > 75) {
                        lineChart.setVerticalGridLinesVisible(false);
                        lineChart.setHorizontalGridLinesVisible(false);
                        lineChart.setCreateSymbols(false);
                    }
                }
            });
        }, 0, 250, TimeUnit.MILLISECONDS);

    }

    private long ComputeRuntime(String input, int count) {
        if (GC_TurboMode.isSelected()) {
            count *= GC_TurboFactor.getValue();
        }
        if (input.equals(GC_FastSort.getText())) {
            return BM.timedSort(count);
        } else if (input.equals(GC_Remove.getText())) {
            return BM.timedRemove(count);
        } else if (input.equals(GC_Rehash.getText())) {
            return BM.timedRehash(count);
        } else if (input.equals(GC_Values.getText())) {
            return BM.timedValues(count);
        } else if (input.equals(GC_Constructor.getText())) {
            return BM.timedMyHashTable(count);
        } else if (input.equals(GC_Keys.getText())) {
            return BM.timedKeys(count);
        } else if (input.equals(GC_Get.getText())) {
            return BM.timedGetAtSize(count);
        } else if (input.equals(GC_Put.getText())) {
            return BM.timedPutAtSize(count);
        } else if (input.equals(GC_ArrayListMergeSort.getText())) {
            return BM.timedSortReference(count);
        } else if (input.equals(GC_ProfSlowSort.getText())) {
            return BM.timedSlowSort(count);
        } else if (input.equals(GC_Iter.getText())) {
            return BM.timedInterator(count);
        } else if (input.equals(GC_Next.getText())) {
            return BM.timedIteratorNext(count);
        } else if (input.equals(GC_J_Constructor.getText())) {
            return BM.timedMyHashTableReference(count);
        } else if (input.equals(GC_J_Put.getText())) {
            return BM.timedPutReference(count);
        } else if (input.equals(GC_J_Get.getText())) {
            return BM.timedGetAtSizeRefernce(count);
        } else if (input.equals(GC_J_Remove.getText())) {
            return BM.timedRemoveReference(count);
        } else if (input.equals(GC_J_Values.getText())) {
            return BM.timedValuesReference(count);
        } else if (input.equals(GC_J_Keys.getText())) {
            return BM.timedKeysReference(count);
        } else if (input.equals(GC_Twit_Trending.getText())) {
            return tBM.timedTwitterTrending(count, (int) (count * GC_StopWordFactor.getValue()));
        } else if (input.equals(GC_Twit_Constructor.getText())) {
            return tBM.timedTwitterConstructor(count, (int) (count * GC_StopWordFactor.getValue()));
        } else if (input.equals(GC_Twit_ByDate.getText())) {
            return tBM.timedTwitterByDate(count);
        } else if (input.equals(GC_Twit_ByAuth.getText())) {
            return tBM.timedTwitterByAuth(count);
        } else if (input.equals(GC_Twit_Add.getText())) {
            return tBM.timedTwitterAdd(count);
        } else if (input.equals(GC_Twit_ConstructorII.getText())) {
            return tBM.timedTwitterConstructor(count, (int) (count * GC_StopWordFactorII.getValue()));
        } else if (input.equals(GC_Twit_TrendingII.getText())) {
            return tBM.timedTwitterTrending(count, (int) (count * GC_StopWordFactorII.getValue()));
        } else {
            return 0L;
        }
    }

    private void enableDarkTheme() {
        anchorPane.getStylesheets().add(" /COMP250_A4_W2020_Test_Visualizer_JFX/mondea_dark.css");
    }

    private void disableDarkTheme() {
        anchorPane.getStylesheets().remove(" /COMP250_A4_W2020_Test_Visualizer_JFX/mondea_dark.css");
    }
}
