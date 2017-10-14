package app.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by HassanMahmud on 14/10/2017.
 */
public class LandingController {

    @FXML
    private Button loadLiveCamera;

    @FXML
    private Button loadStillImage;

    private Stage primaryStage = null;

    public void init(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void loadLiveDetectScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/CameraView.fxml"));
            BorderPane root = (BorderPane) loader.load();

            LiveDetectController controller = loader.getController();
            controller.init();

            this.primaryStage.setScene(new Scene(root, 800, 600));
            this.primaryStage.setOnCloseRequest((we -> controller.setClosed()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadStillImageScene(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/ImageView.fxml"));
            BorderPane root = (BorderPane) loader.load();

            ImageSelectController controller = loader.getController();
            controller.init();

            this.primaryStage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
