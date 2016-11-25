LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_CAMERA_MODULES:=off
#OPENCV_INSTALL_MODULES:=off
#OPENCV_LIB_TYPE:=SHARED
include D:/AndroidCode/OpenCV-2.4.11-android-sdk/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES  := OpenCVJNITest.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := opencvjnitest

include $(BUILD_SHARED_LIBRARY)
