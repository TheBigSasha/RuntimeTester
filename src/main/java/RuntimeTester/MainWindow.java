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

    public static void launchGrapher(){
        launch();
    }

    //TODO: launch upon instantiation of object with tested .class as parameter
    //TODO: singleton class controller, can be started by call to its build method, takes class or collection<class> as input.
    //Includes adder method for more tester classes
    //Finalizes with a show method to display the window! <inner class for Application?>
    //This can be packaged as jar and distributed
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
