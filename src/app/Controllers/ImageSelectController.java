package app.Controllers;

import Utils.Utils;
import com.sun.javafx.geom.Vec3f;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters.*;

import java.util.Vector;

/**
 * Created by HassanMahmud on 14/10/2017.
 */
public class ImageSelectController {

    private static String IMAGE_TO_PROCESS = "./Circle.jpg";

    @FXML
    private Button captureImage_btn;

    @FXML
    private ImageView imageView;

    private Mat currentMat;

    /**
     * Init the controller, at start time
     */
    public void init()
    {

        // set a fixed width for the frame
        imageView.setFitWidth(600);
        // preserve image ratio
        imageView.setPreserveRatio(true);

        this.currentMat = Imgcodecs.imread(IMAGE_TO_PROCESS);
        setMatAsCurrentImage();

    }

    private void updateImageView(ImageView view, Image image){

        Utils.onFxThread(view.imageProperty(), image);
    }

    public void captureImage(ActionEvent event) {
        try{
            this.currentMat = Imgcodecs.imread(IMAGE_TO_PROCESS);

            // convert and show the frame
            setMatAsCurrentImage();

        }catch(Exception ex){
            System.err.println(ex.getMessage());
        }
    }

    private void setMatAsCurrentImage(){
        Image imageToShow = Utils.mat2Image(this.currentMat);
        imageView.setImage(imageToShow);
    }

    public void processImage(ActionEvent event) {

        if (!this.currentMat.empty()) {
            Imgproc.cvtColor(currentMat, currentMat, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(currentMat, currentMat, new Size(9, 9), 2, 2);

            //Find Circles
            Mat circles = new Mat();
            Imgproc.HoughCircles(currentMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 60, 200, 20, 30, 0 );


            System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());
            double x = 0.0;
            double y = 0.0;
            int r = 0;
            /// Draw the circles detected
            //Rect[] pupilArray = pupil.();
            for( int i = 0; i < circles.cols(); i++)
            {
                double[] data = circles.get(0, 1);
                for(int j = 0 ; j < data.length ; j++){
                    x = data[0];
                    y = data[1];
                    r = (int) data[2];
                }
                Point center = new Point(x,y);
                // circle center
                Imgproc.circle(currentMat, center, 3, new Scalar(0,255,0), 1);
                // circle outline
                Imgproc.circle(currentMat, center, r, new Scalar(0,0,255), 1);
//
//
//                Point center = new Point(Round(data1), Round(circles[i][1]));
//                int radius = Round(circles[i][2]);
//                // circle center
//                Imgproc.circle( currentMat, center, 3, new Scalar(0,255,0), -1, 8, 0 );
//                // circle outline
//                Imgproc.circle( currentMat, center, radius, new Scalar(0,0,255), 3, 8, 0 );
            }
        }

        setMatAsCurrentImage();
    }

    int Round(double x){
        int y;

        if(x >= (int)x+0.5)
            y = (int)x++;
        else
            y = (int)x;

        return y;
    }
}
