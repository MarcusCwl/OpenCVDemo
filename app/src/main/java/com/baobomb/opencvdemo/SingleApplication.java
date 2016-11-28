package com.baobomb.opencvdemo;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baobomb.opencvdemo.handsDetect.GestureDetecter;

/**
 * Created by LEAPSY on 2016/11/28.
 */

public class SingleApplication extends Application implements GestureDetecter.GestureSensor {
    public GestureDetecter gestureDetecter;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(SingleApplication.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
//            Log.d("SENSOR",(String)msg.obj);
        }
    };
    private static SingleApplication m_Instance;

    public SingleApplication() {
        super();
        m_Instance = this;
    }

    public static SingleApplication getInstance() {
        if (m_Instance == null) {
            synchronized (SingleApplication.class) {
                if (m_Instance == null) new SingleApplication();
            }
        }
        return m_Instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gestureDetecter = new GestureDetecter(this);
        gestureDetecter.setGestureSensor(this);

    }

    @Override
    public void gestureDown() {
        sendMessage("DOWN");
        gestureDetecter.setGestureDetecting(false);
    }

    @Override
    public void gestureLeft() {
        sendMessage("LEFT");
        gestureDetecter.setGestureDetecting(false);
    }

    @Override
    public void gestureLeftDown() {
        sendMessage("LEFT_DOWN");
        gestureDetecter.setGestureDetecting(false);
    }

    @Override
    public void gestureLeftUp() {
        sendMessage("LEFT_UP");
        gestureDetecter.setGestureDetecting(false);
    }

    @Override
    public void gestureRight() {
        sendMessage("RIGHT");
        gestureDetecter.setGestureDetecting(false);
    }

    @Override
    public void gestureRightDown() {
        sendMessage("RIGHT_DOWN");
        gestureDetecter.setGestureDetecting(false);
    }

    @Override
    public void gestureRightUp() {
        sendMessage("RIGHT_UP");
        gestureDetecter.setGestureDetecting(false);
    }

    @Override
    public void gestureUp() {
        sendMessage("UP");
        gestureDetecter.setGestureDetecting(false);
    }

    public void sendMessage(String obj){
        Message message = Message.obtain(handler);
        message.obj = obj;
        message.sendToTarget();
    }
}
