package RuntimeTester;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainWindowDesign.fxml"));
        primaryStage.setTitle("Runtime Efficiency Wizard - COMP250");
        Scene s = new Scene(root);
        primaryStage.setScene(s);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest(e -> exitProgram());
        primaryStage.show();
    }

    private void exitProgram() {
        System.exit(0);
    }

}
