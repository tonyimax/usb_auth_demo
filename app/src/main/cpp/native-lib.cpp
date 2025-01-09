#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_usb_1auth_1demo_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_example_usb_1auth_1demo_MainActivity_getIntArray(JNIEnv *env, jobject thiz) {
    jintArray result = env->NewIntArray(2);
    int temp[2];
    temp[0]=1;
    temp[1]=2;
    env->SetIntArrayRegion(result,0,2,temp);
    return result;
}