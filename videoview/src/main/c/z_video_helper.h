//
// Created by tt on 2021/1/9.
//

#ifndef OPENGLLEARN_Z_VIDEO_HELPER_H
#define OPENGLLEARN_Z_VIDEO_HELPER_H

//#ifdef __cplusplus
//extern "C"{
//#endif

#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <time64.h>

#define LOG_TAG "z_video"
#define zlog(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define JNI_PREFIX Java_com_example_videoview_ZVideoHelper_
//以下不能找到方法，但是编译不会报错，，宏函数中使用宏定义有限制？
//#define JNI_PREFIX_(NAME) JNI_PREFIX ## NAME
#define JNI_PREFIX_(NAME) Java_com_example_videoview_ZVideoHelper_ ## NAME
//JNI_PREFIX ## FUNC_NAME ##表示连接这两个字符
//##__VA_ARGS__ 宏前面加上##的作用在于，
//当可变参数的个数为0时，这里的##起到把前面多余的","去掉的作用(填充""字符串？),否则会编译出错
#define JNI_FUNC(RETURN_TYPE, FUNC_NAME, ...) JNIEXPORT RETURN_TYPE JNICALL \
Java_com_example_videoview_ZVideoHelper_ ## FUNC_NAME \
(JNIEnv *env, jclass thiz, ##__VA_ARGS__)

//宏示例，下面打印x参数名时需要使用#x
#define PSQR(x) __android_log_print(ANDROID_LOG_VERBOSE,\
LOG_TAG,\
"the square of "#x" is %d.\n",((x)*(x)))

//#ifdef __cplusplus
//};
//#endif

#endif //OPENGLLEARN_Z_VIDEO_HELPER_H
