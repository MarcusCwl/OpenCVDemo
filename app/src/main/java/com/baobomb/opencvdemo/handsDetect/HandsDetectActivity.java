package com.baobomb.opencvdemo.handsDetect;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.baobomb.opencvdemo.R;
import com.baobomb.opencvdemo.SingleApplication;
import com.baobomb.opencvdemo.handsDetect.imageProcessing.ColorBlobDetector;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by BAOBOMB on 2016/11/24.
 */

public class HandsDetectActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener {
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    //    private Mat mGray;
//    private Mat empty;
    private int height;
    private int width;
    double iThreshold = 0;

    private Scalar mBlobColorHsv;
    private Scalar mBlobColorRgba;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private boolean mIsColorSelected = false;

    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;
    private Scalar CONTOUR_COLOR_WHITE;
    int xOffset;
    int yOffset;
//    int numberOfFingers = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateNumberOfFingers(msg.what);
        }
    };

    public void updateNumberOfFingers(int numberOfFingers) {
        Log.d("BAO", "Finger detect : " + String.valueOf(numberOfFingers));
    }

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
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
        mOpenCvCameraView.setOnTouchListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.d("Hands", String.valueOf(width) + " : " + String.valueOf(height));
        this.height = height;
        this.width = width;
        //空矩陣
//        mGray = new Mat();
        //空矩陣
        mRgba = new Mat();
        //宣告矩陣 大小等同frame長 寬 型態為無正負號整數型態 四通道浮點數
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        //顏色取樣器
        mDetector = new ColorBlobDetector();
        //空矩陣
        mSpectrum = new Mat();
        //4元素向量 灰階像素強度為255 且顏色為黑
        mBlobColorRgba = new Scalar(255);
        //4元素向量 灰階像素強度為255 且顏色為黑
        mBlobColorHsv = new Scalar(255);
        //Size物件 寬為200 高為64
        SPECTRUM_SIZE = new Size(200, 64);
        //4元素向量 灰階像素強度為255 且顏色為紅色
        CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
        //4元素向量 灰階像素強度為255 且顏色為白色
        CONTOUR_COLOR_WHITE = new Scalar(255, 255, 255, 255);
    }

    @Override
    public void onCameraViewStopped() {
//        mGray.release();
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        //相機寬
        int cols = mRgba.cols();
        //相機高
        int rows = mRgba.rows();
        Log.d("Hands", "Camera size = " + String.valueOf(cols) + " : " + String.valueOf(rows));
        Log.d("Hands", "Screen size = " + String.valueOf(mOpenCvCameraView.getWidth()) + " : " + String.valueOf(mOpenCvCameraView.getHeight()));
        //X軸偏移量
        xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        //Y軸偏移量
        yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;
        Log.d("Hands", String.valueOf(xOffset) + " : " + String.valueOf(yOffset));
        //獲取點擊的座標 轉換為在Frame上的座標點 (如果Frame沒辦法填滿相機，則需要透過減去偏移量 來轉換為在圖片上的實際座標)
        int x = (int) event.getX() - xOffset;
        int y = (int) event.getY() - yOffset;
        Log.d("Hands", "TouchEvent image coordinates = " + (int) event.getX() + " : " + (int) event.getY());
        Log.d("Hands", "Touch image coordinates = " + x + " : " + y);
        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) {
            return false;
        }
        //觸摸區域
        Rect touchedRect = new Rect();
        //設定觸摸區域左上角座標 X & Y 若是小於5 則設為0
        touchedRect.x = (x > 5) ? x - 5 : 0;
        touchedRect.y = (y > 5) ? y - 5 : 0;
        //設定觸摸區域寬高 若小於圖像矩陣寬高 則各設為5 若大於圖像矩陣寬高 則設為觸摸區域左上角到圖像右下角
        touchedRect.width = (x + 5 < cols) ? x + 5 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y + 5 < rows) ? y + 5 - touchedRect.y : rows - touchedRect.y;
        //取出觸摸區域的圖像矩陣
        Mat touchedRegionRgba = mRgba.submat(touchedRect);
        //宣告 HSV顏色型態圖像矩陣
        Mat touchedRegionHsv = new Mat();
        //將取出的觸摸區域圖像矩陣 轉換為HSV型態
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
        //計算已轉換為HSV型態的觸摸區域圖像矩陣的平均色
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        Log.d("Hands", "觸摸區域圖像矩陣長度" + mBlobColorHsv.val.length);
        for (int i = 0; i < mBlobColorHsv.val.length; i++) {
            mBlobColorHsv.val[i] /= pointCount;
        }
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        Log.d("Hands", "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");
        //設定HSV顏色矩陣 給 顏色檢測器
        mDetector.setHsvColor(mBlobColorHsv);
        //顏色檢測完畢，將顏色檢測完畢的圖像矩陣 縮放為需要的大小
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        mIsColorSelected = true;
        touchedRegionRgba.release();
        touchedRegionHsv.release();
        return false; // don't need subsequent touch events
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);
        return new Scalar(pointMatRgba.get(0, 0));
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //宣告一個透明畫布矩陣
//        empty = new Mat(height,width,CvType.CV_8UC4);
        mRgba = inputFrame.rgba();
//        mGray = inputFrame.gray();
        iThreshold = 8700;
        //Imgproc.blur(mRgba, mRgba, new Size(5,5));
        Imgproc.GaussianBlur(mRgba, mRgba, new org.opencv.core.Size(3, 3), 1, 1);
        //Imgproc.medianBlur(mRgba, mRgba, 3);
        //如果沒有選取顏色 則回傳空畫布
        if (!mIsColorSelected) {
            SingleApplication.getInstance().gestureDetecter.setGestureDetecting(false);
//            return empty;
            return mRgba;
        }
        List<MatOfPoint> contours = mDetector.getContours();
        mDetector.process(mRgba);
        //如果檢測不到選取顏色 則回傳空畫布
        if (contours.size() <= 0) {
            SingleApplication.getInstance().gestureDetecter.setGestureDetecting(false);
//            return empty;
            return mRgba;
        }

        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(0).toArray()));
        double boundWidth = rect.size.width;
        double boundHeight = rect.size.height;
        int boundPos = 0;
        for (int i = 1; i < contours.size(); i++) {
            rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));
            if (rect.size.width * rect.size.height > boundWidth * boundHeight) {
                boundWidth = rect.size.width;
                boundHeight = rect.size.height;
                boundPos = i;
            }
        }
        Rect boundRect = Imgproc.boundingRect(new MatOfPoint(contours.get(boundPos).toArray()));
        //在手部正方形區域 畫上白色矩形
//        Imgproc.rectangle(mRgba, boundRect.tl(), boundRect.br(), CONTOUR_COLOR_WHITE, 2, 8, 0);

        if (checkRealHands(boundRect.tl().y, boundRect.br().y)) {
            //在空畫布的手部最右上角區域劃上黑點
            Point center = new Point(boundRect.br().x, boundRect.tl().y);
            if (!SingleApplication.getInstance().gestureDetecter.isGestureDetecting()) {
                SingleApplication.getInstance().gestureDetecter.setGestureDetecting(true);
                SingleApplication.getInstance().gestureDetecter.setStartPoint(center);
            } else {
                SingleApplication.getInstance().gestureDetecter.setNewPoint(center);
                SingleApplication.getInstance().gestureDetecter.detectGesture();
            }
//        Imgproc.circle(empty, center, 40, new Scalar(255, 255, 255), -1);
            Imgproc.circle(mRgba, center, 40, new Scalar(255, 255, 255), -1);
            //在空畫布的黑點下方畫上黑點座標
            Point textPosition = new Point(boundRect.br().x + 10, boundRect.tl().y + 10);
//        Imgproc.putText(empty, "frame pos =" + (int) boundRect.br().x + "," + (int) boundRect.tl().y, textPosition, 0, 1, new Scalar(255, 255, 255), 3);
            Imgproc.putText(mRgba, "frame pos =" + (int) boundRect.br().x + "," + (int) boundRect.tl().y, textPosition, 0, 1, new Scalar(255, 255, 255), 3);
//      Log.d("Hands",
//                " Row start [" + (int) boundRect.tl().y
//                        + "] row end [" + (int) boundRect.br().y + "] Col start [" +
//                        (int) boundRect.tl().x + "] Col end [" +
//                        (int) boundRect.br().x + "]");
            Log.d("Hands",
                    " 右上角座標 X :" + ((int) boundRect.br().x - xOffset) + " Y :" + ((int) boundRect.tl().y + yOffset));
            double a = boundRect.br().y - boundRect.tl().y;
            a = a * 0.7;
            a = boundRect.tl().y + a;
            Log.d("BAO",
                    " A [" + a + "] br y - tl y = [" + (boundRect.br().y - boundRect.tl().y) + "]");
            //Core.rectangle( mRgba, boundRect.tl(), boundRect.br(), CONTOUR_COLOR, 2, 8, 0 );
            //對手部區域進行繪圖以及手指數量檢測
            Imgproc.rectangle(mRgba, boundRect.tl(), new Point(boundRect.br().x, a), CONTOUR_COLOR, 2, 8, 0);
            MatOfPoint2f pointMat = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(boundPos).toArray()), pointMat, 3, true);
            contours.set(boundPos, new MatOfPoint(pointMat.toArray()));
            MatOfInt hull = new MatOfInt();
            MatOfInt4 convexDefect = new MatOfInt4();
            Imgproc.convexHull(new MatOfPoint(contours.get(boundPos).toArray()), hull);
            if (hull.toArray().length < 3) return mRgba;
            Imgproc.convexityDefects(new MatOfPoint(contours.get(boundPos).toArray()), hull, convexDefect);
            List<MatOfPoint> hullPoints = new LinkedList<MatOfPoint>();
            List<Point> listPo = new LinkedList<Point>();
            for (int j = 0; j < hull.toList().size(); j++) {
                listPo.add(contours.get(boundPos).toList().get(hull.toList().get(j)));
            }
            MatOfPoint e = new MatOfPoint();
            e.fromList(listPo);
            hullPoints.add(e);
//        List<MatOfPoint> defectPoints = new LinkedList<MatOfPoint>();
//        List<Point> listPoDefect = new LinkedList<Point>();
//        for (int j = 0; j < convexDefect.toList().size(); j = j + 4) {
//            Point farPoint = contours.get(boundPos).toList().get(convexDefect.toList().get(j + 2));
//            Integer depth = convexDefect.toList().get(j + 3);
//            if (depth > iThreshold && farPoint.y < a) {
//                listPoDefect.add(contours.get(boundPos).toList().get(convexDefect.toList().get(j + 2)));
//            }
//            Log.d("BAO", "defects [" + j + "] " + convexDefect.toList().get(j + 3));
//        }

//        MatOfPoint e2 = new MatOfPoint();
//        e2.fromList(listPo);
//        defectPoints.add(e2);

//        Log.d("BAO", "hull: " + hull.toList());
//        Log.d("BAO", "defects: " + convexDefect.toList());
            Imgproc.drawContours(mRgba, hullPoints, -1, CONTOUR_COLOR, 3);
        }
//        int defectsTotal = (int) convexDefect.total();
//        Log.d("BAO", "Defect total " + defectsTotal);

//        int numberOfFingers = listPoDefect.size();
//        if (numberOfFingers > 5) {
//            numberOfFingers = 5;
//        }
//        Message msg = Message.obtain(handler);
//        msg.what = numberOfFingers;
//        msg.sendToTarget();

//        for (Point p : listPoDefect) {
//            Imgproc.circle(mRgba, p, 6, new Scalar(255, 0, 255));
//        }
//
        //回傳繪製好的畫布
//        return empty;
        return mRgba;
    }

    public boolean checkRealHands(double top, double bottom) {
        Log.d("Hands", String.valueOf(bottom - top));
        if (bottom - top > 250 && bottom - top < 500) {
            return true;
        } else {
            SingleApplication.getInstance().gestureDetecter.setGestureDetecting(false);
            return false;
        }

    }
}
