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
jclass cls;
jmethodID mid;

void do_echo(JNIEnv env) {
     //找到java中的类
}

JNIEXPORT void JNICALL Java_com_baobomb_opencvdemo_opencvjnitest_OpenCVJNITest_test
        (JNIEnv *env, jobject obj, jlong nativemat){
    Mat* rgba=(Mat*)nativemat;
    circle(*rgba, Point(100,100), 10, Scalar(5,128,255,255));
      cls = env->FindClass("com/baobomb/opencvdemo/opencvjnitest/OpenCVJNITest");
         //再找类中的方法
      mid = env->GetStaticMethodID(cls, "echo", "(Ljava/lang/String;)V");
         if (mid == NULL)
         {
             LOGD("int error");
             return;
         }
         //打印接收到的数据
         LOGD("from java");
         //回调java中的方法
         env->CallStaticVoidMethod(cls, mid ,env->NewStringUTF("你好haha"));

}

