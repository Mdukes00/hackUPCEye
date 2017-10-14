package app;

import app.Controllers.LandingController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;
import app.Controllers.LiveDetectController;

public class Main extends Application {

    Scene landingScene, liveFeedScene, stillImageScene;

    public Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        try
        {
            this.primaryStage = primaryStage;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/LandingView.fxml"));
            BorderPane root = (BorderPane) loader.load();

            primaryStage.setTitle("JavaFX meets OpenCV");
            primaryStage.setScene(new Scene(root, 400, 300));
            primaryStage.show();

            // set the proper behavior on closing the application
            LandingController controller = loader.getController();
            controller.init(primaryStage);
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
