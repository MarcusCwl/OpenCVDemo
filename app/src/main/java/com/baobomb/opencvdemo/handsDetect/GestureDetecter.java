package com.baobomb.opencvdemo.handsDetect;

import android.content.Context;
import android.util.Log;

import org.opencv.core.Point;

/**
 * Created by LEAPSY on 2016/11/28.
 */

public class GestureDetecter {

    public boolean gestureDetecting;
    public Point startPoint;
    public Point newPoint;
    public Context context;
    double startX;
    double startY;
    double moveXDistance;
    double moveYDistance;
    GestureSensor gestureSensor;

    public GestureDetecter(Context context) {
        this.context = context;
    }

    public void setGestureSensor(GestureSensor gestureSensor) {
        this.gestureSensor = gestureSensor;
    }

    public void detectGesture() {
        if (gestureDetecting && gestureSensor != null) {
            double newX = newPoint.x;
            double newY = newPoint.y;
            moveXDistance = newX - startX;
            moveYDistance = newY - startY;

            if (moveXDistance > 450 && moveYDistance > 150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureRightDown();
            } else if (moveXDistance > 450 && moveYDistance < -150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureRightUp();
            } else if (moveXDistance < -450 && moveYDistance > 150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureLeftDown();
            } else if (moveXDistance < -450 && moveYDistance < -150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureLeftUp();
            } else if (moveXDistance > 450 && moveYDistance < 150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureRight();
            } else if (moveXDistance < -450 && moveYDistance < 150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureLeft();
            } else if (moveXDistance < 450 && moveYDistance < -150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureUp();
            } else if (moveXDistance < 450 && moveYDistance > 150) {
                Log.d("SENSOR", "start X : " + String.valueOf(startX) + "start Y : "+String.valueOf(startY));
                Log.d("SENSOR", "new X : " + String.valueOf(newX) + "new Y : "+String.valueOf(newY));
                gestureSensor.gestureDown();
            }
        }
    }

    public void setStartPoint(Point startPoint) {
        if (this.startPoint == null) {
            this.startPoint = startPoint;
            startX = startPoint.x;
            startY = startPoint.y;
        }
    }

    public void setNewPoint(Point newPoint) {
        this.newPoint = newPoint;
    }

    public void setGestureDetecting(boolean gestureDetecting) {
        if (!gestureDetecting) {
            startPoint = null;
            newPoint = null;
            startX = 0;
            startY = 0;
            this.gestureDetecting = gestureDetecting;
            Log.d("SENSOR", "Not detecting");
        } else {
            this.gestureDetecting = gestureDetecting;
            Log.d("SENSOR", "detecting");
        }
    }

    public boolean isGestureDetecting() {
        return gestureDetecting;
    }

    public interface GestureSensor {
        void gestureUp();

        void gestureDown();

        void gestureLeft();

        void gestureRight();

        void gestureRightUp();

        void gestureRightDown();

        void gestureLeftUp();

        void gestureLeftDown();
    }
}
