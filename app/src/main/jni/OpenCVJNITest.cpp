//
// Created by LEAPSY on 2016/11/25.
//
//include my header
#include "OpenCVJNITest.h"
//include opencv core
#include <opencv2/core/core.hpp>
//include string object
#include <string>
//include vector object
#include <vector>
//include android log object
#include <android/log.h>

#define LOG_TAG "OpenCVJNITest"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))


using namespace std;
using namespace cv;

JNIEXPORT void JNICALL Java_com_baobomb_opencvdemo_opencvjnitest_OpenCVJNITest_test
        (JNIEnv *env, jobject, jlong nativemat){

    Mat* rgba=(Mat*)nativemat;
    circle(*rgba, Point(100,100), 10, Scalar(5,128,255,255));
}

