package com.example.helpmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import androidx.appcompat.app.ActionBar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class ObjectDetectionPage extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private FirebaseAuth authProfile;
   private TextView test_t;


    private static final String TAG="ObjectDetectionPage";



    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private objectDetectorClass objectDetectorClass;
    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
    @Override
    public void onManagerConnected(int status) {
        switch (status){
            case LoaderCallbackInterface
                    .SUCCESS:{
                Log.i(TAG,"OpenCv Is loaded");
                mOpenCvCameraView.enableView();
            }
            default:
            {
                super.onManagerConnected(status);

            }
            break;
        }
    }
    };
    public ObjectDetectionPage(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //test = findViewById(R.id.HelpMeApp);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Set BackgroundDrawable
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Object Detection Page");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#97d3dd"));
        actionBar.setBackgroundDrawable(colorDrawable);


        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // if camera permission is not given it will ask for it on device
        if (ContextCompat.checkSelfPermission(ObjectDetectionPage.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ObjectDetectionPage.this,
                    new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_object_detection);

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        try{
            // input size is 300 for this model
            objectDetectorClass=new objectDetectorClass(getAssets(),
                    "ssd_mobilenet.tflite",
                    "labelmap.txt",300);
            Log.d("ObjectDetectionPage","Model is successfully loaded");
        }
        catch (IOException e){
            Log.d("ObjectDetectionPage","Getting some error");
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()){
            //if load success
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            //if not loaded
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width ,int height){
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray =new Mat(height,width,CvType.CV_8UC1);
    }

    public void onCameraViewStopped(){
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){

        test_t = findViewById(R.id.HelpMeApp);
        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();

        // now call that function
        Mat out=new Mat();
        out=objectDetectorClass.recognizeImage(mRgba);



        return out;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item1) {
            authProfile.signOut();
            Toast.makeText(this, "Logged Out of HelpMeApp", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, RegisterPage.class);

            //Clear stack to prevent user coming back to Settings Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //Close setting activity
        }
        else if (id == R.id.actionBarMain) {
            Intent intent = new Intent(ObjectDetectionPage.this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.actionBarSetting) {
            Intent intent = new Intent(ObjectDetectionPage.this, SettingsPage.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_refresh) {
            //Refresh page/activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }
        else {
            Toast.makeText(this, "Something went wrong! Please try again!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}