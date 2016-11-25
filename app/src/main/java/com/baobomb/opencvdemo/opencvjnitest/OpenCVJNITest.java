package com.baobomb.opencvdemo.opencvjnitest;

/**
 * Created by LEAPSY on 2016/11/25.
 */

public class OpenCVJNITest {

    static {
        System.loadLibrary("opencvjnitest");
    }

    public void testJNI(long rgba) {
        test(rgba);
    }

    public native void test(long inputImage);
}
