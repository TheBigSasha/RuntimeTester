package RuntimeTester;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //TODO: Make CSS theme work properly! (Gradle issue), Make dark theme toggleable
        Parent root = FXMLLoader.load(getClass().getResource("MainWindowDesign.fxml"));
        primaryStage.setTitle("Runtime Efficiency Wizard - <3 sashaphoto.ca Tweet Visualizer");
        primaryStage.setScene(new Scene(root, 900, 800));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://sashaphoto.ca/COMP250ThankYou/"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        primaryStage.show();
    }

}
