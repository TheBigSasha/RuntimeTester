package RuntimeTester;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.*;

/**
 * The frontend and API for the Runtime Efficiency Tester
 * @author Sasha Aleshchenko - alexander.aleshchenko@mail.mcgill.ca
 * Documentation available at RuntimeTester.github.io
 */
public class Visualizer extends Application {
    private static final List<String> messages = new ArrayList<>();
    final double versionNumber = 1.2;
    static {
        final String[] hardMessages = new String[]{"made with <3 by sashaphoto.ca", "we are, we are, we are, we are the engineers", "stay safe out there", "always believe in yourself", "you can accomplish anything if you set your mind to it",
        "chicken chicken chicken chicken chicken chicken chicken chicken chicken chicken", "exeunt", "\uD83D\uDC1D", "also try Minecraft - server: McGill.world", "I believe in you!", "try using a hashmap",
        "at least it isn't O(N!)", "do you ever feel like a paper bag?", "hello from Montreal", "look at McGill Robotics :)", "sashaphoto.ca for all your photo/video needs", "check out COMP250 @ McGill",};
        messages.addAll(Arrays.asList(hardMessages));
        //TODO: query API for more messages

    }
    private static List<Class<?>> startupClasses = new ArrayList<>();
    private static Controller c;


    /**
     * Start the application with arguments
     * @param args args
     */
    public static void main(String[] args) {
        launch(args);
    }


    public static void launch(Class<?> extraTests){
        startupClasses.add(extraTests);
        launch();
    }

    /**
     * Starts the JAVAFX appliation. DO NOT CALL THIS METHOD.
     * Use launchGrapher() or main() instead.
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindowDesign.fxml"));
        Parent root = fxmlLoader.load();
        c = (Controller) fxmlLoader.getController();
        if(!startupClasses.isEmpty()) c.addBenchmarks(startupClasses);
        //c.addBenchmarksFromPackageNames(Collections.singletonList("Tutorial7"));
        Random r = new Random(System.currentTimeMillis());
        String extraTitle = messages.get(r.nextInt(messages.size()-1));
        primaryStage.setTitle("Runtime Efficiency Wizard " + versionNumber + " - " + extraTitle);
        Scene s = new Scene(root);
        primaryStage.setScene(s);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(e -> exitProgram());
        primaryStage.show();
    }

    /**
     * Exits the application
     */
    private static void exitProgram() {
        System.exit(0);
    }

    /**
     * This method is used to add benchmark classes to the tester.
     * Any classes passed to this method will be checked by the controller
     * for methods annotated with @benchmark and those methods will be added
     * to the program.
     *
     * @param benchmarkClasses list of classes to be added
     */
    public static void addBenchmarks(List<Class<?>> benchmarkClasses){
        c.addBenchmarks(benchmarkClasses);
    }

}
