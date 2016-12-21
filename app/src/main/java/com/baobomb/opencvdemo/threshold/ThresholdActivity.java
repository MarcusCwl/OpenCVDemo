package com.baobomb.opencvdemo.threshold;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baobomb.opencvdemo.R;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEAPSY on 2016/11/23.
 */

public class ThresholdActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2,View.OnClickListener {
    private CameraBridgeViewBase mOpenCvCameraView;
    public List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    public Mat hierarchy;
    public Mat mRgba;
    public Mat mGray;

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                this.finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ((ImageView) findViewById(R.id.back)).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    public void init() {
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        Imgproc.threshold(inputFrame.gray(), mRgba, 100,
                255, Imgproc.THRESH_BINARY);
//        Imgproc.findContours(mRgba, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
//        Imgproc.drawContours(mRgba, contours, -1, new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255));
        return mRgba;
    }
}
