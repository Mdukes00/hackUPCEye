package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import Utils.Utils;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CameraController {
    @FXML
    private Button toggle_camera_btn;
    @FXML
    private ImageView current_frame;

    private boolean isCameraActive;
    private VideoCapture capture;
    private ScheduledExecutorService timer;
    private CascadeClassifier faceCascade;
    private CascadeClassifier eyeCascade;
    private int absoluteFaceSize;
    private int absoluteEyeSize;
    private int imgCount = 0;

    /**
     * Init the controller, at start time
     */
    protected void init()
    {
        this.isCameraActive = false;
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.eyeCascade = new CascadeClassifier();
        this.absoluteFaceSize = 0;
        this.absoluteEyeSize = 0;

        // set a fixed width for the frame
        current_frame.setFitWidth(600);
        // preserve image ratio
        current_frame.setPreserveRatio(true);
        this.faceCascade.load("./cascades/haarcascades/haarcascade_frontalface_default.xml");
        this.eyeCascade.load("./cascades/haarcascades/haarcascade_eye.xml");
    }

    @FXML
    protected void toggleCamera(ActionEvent event){

        if(!this.isCameraActive) {

            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened())
            {
                this.isCameraActive = true;

                Runnable frameGrabber = () -> {
                    Mat frame = grabFrame();
                    // convert and show the frame
                    Image imageToShow = Utils.mat2Image(frame);
                    updateImageView(current_frame, imageToShow);
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

            }else{
                System.err.println("Cannot open the capture video feed.");
            }

        }else{

            this.isCameraActive = false;

            stopAcquisition();
        }
    }

    private void stopAcquisition(){
        if (this.timer!=null && !this.timer.isShutdown())
        {
            try
            {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened())
        {
            // release the camera
            this.capture.release();
        }
    }

    private Mat grabFrame(){

        Mat frame = new Mat();

        if(this.capture.isOpened()){
            try {
            this.capture.read(frame);

                if (!frame.empty()) {
                    //Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                    detectAndDisplay(frame);
                }

            }catch(Exception ex){
                System.err.println("Trouble grabbing and converting the frame.");
            }
        }
        return frame;
    }

    private void updateImageView(ImageView view, Image image){
        Utils.onFxThread(view.imageProperty(), image);
    }

    public void setClosed() {
        stopAcquisition();
    }

    private void detectAndDisplay(Mat frame)
    {
        // convert the frame in gray scale
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(frame, frame);

        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0)
        {
            int height = frame.rows();
            if (Math.round(height * 0.2f) > 0)
            {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        if (this.absoluteEyeSize == 0)
        {
            int height = frame.rows();
            if (Math.round(height * 0.05f) > 0)
            {
                this.absoluteEyeSize = Math.round(height * 0.05f);
            }
        }

        //detect faces
        MatOfRect faces = new MatOfRect();


        this.faceCascade.detectMultiScale(frame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++){
            Imgproc.putText(frame, "Face", new Point(facesArray[i].x,facesArray[i].y-5), 1, 2, new Scalar(0,0,255));
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
//
//            Mat roi = new Mat(frame, facesArray[i]);
//            MatOfRect eyes = new MatOfRect();
//            this.eyeCascade.detectMultiScale(roi, eyes);
//
//            Rect[] eyesArray = eyes.toArray();
//            for(int j = 0; j < eyesArray.length; j++)
//                Imgproc.rectangle(roi, eyesArray[j].tl(), eyesArray[j].br(), new Scalar(0, 255, 0, 255), 2);
        }

        //detect eyes
        MatOfRect eyes = new MatOfRect();
        this.eyeCascade.detectMultiScale(frame, eyes);

        // each rectangle in eyes is a eye: draw them!
        Rect[] eyesArray = eyes.toArray();
        for(int j = 0; j < eyesArray.length; j++){
            Imgproc.putText(frame, "Eye", new Point(eyesArray[j].x,eyesArray[j].y-5), 1, 2, new Scalar(0,0,255));
            Imgproc.rectangle(frame, eyesArray[j].tl(), eyesArray[j].br(), new Scalar(0, 255, 0, 255), 2);

            Rect rectCrop = new Rect(eyesArray[j].x, eyesArray[j].y, eyesArray[j].width, eyesArray[j].height);
            Mat image_roi = new Mat(frame,rectCrop);
            Imgcodecs.imwrite("./crop_eye_"+(++imgCount)+".jpg",image_roi);
        }
    }
}
