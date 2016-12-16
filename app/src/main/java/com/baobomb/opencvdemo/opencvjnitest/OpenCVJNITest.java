package com.baobomb.opencvdemo.opencvjnitest;

import android.util.Log;

/**
 * Created by LEAPSY on 2016/11/25.
 */

public class OpenCVJNITest {

    static {
        System.loadLibrary("opencvjnitest");
    }

    public static void echo(String msg) {
        Log.d("Bao", "Echo message: " + msg);
    }

    public void testJNI(long rgba) {
        test(rgba);
    }

    public native void test(long inputImage);
}
