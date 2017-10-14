package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;

public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception{
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CameraView.fxml"));

            BorderPane root = (BorderPane) loader.load();

            primaryStage.setTitle("JavaFX meets OpenCV");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();

            // set the proper behavior on closing the application
            CameraController controller = loader.getController();
            controller.init();
            primaryStage.setOnCloseRequest((we -> controller.setClosed()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        launch(args);

    }
}
