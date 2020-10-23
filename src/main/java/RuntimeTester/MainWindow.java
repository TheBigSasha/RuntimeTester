package RuntimeTester;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The frontend and API for the Runtime Efficiency Tester
 * @author Sasha Aleshchenko - alexander.aleshchenko@mail.mcgill.ca
 * Documentation available at RuntimeTester.github.io
 */
public class MainWindow extends Application {
    private static Controller c;
    private static List<Class<?>> startupClasses = new ArrayList<>();

    /**
     * Start the application with arguments
     * @param args args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start up the GUI.
     */
    public static void launchGrapher(){
        launch();
    }

    public static void launchGrapher(Class<?> extraTests){
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
        c.addBenchmarksFromPackageNames(Collections.singletonList("Tutorial7"));
        primaryStage.setTitle("Runtime Efficiency Wizard - COMP250");
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
