#include <jni.h>
#include <string>
#include <vector>

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
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_usb_1auth_1demo_MainActivity_getByteArray(JNIEnv *env, jobject thiz) {
    int len=30;
    jbyteArray _byteArr = env->NewByteArray(len);
    std::vector<std::byte> _bytes(len);//生成30个元素
    for (int i = 0; i <len ; ++i) {
        _bytes.push_back(_bytes.at(i));
    }
    jbyte _jbytes[len];
    for (int i = 0; i <len ; ++i) {
        _jbytes[i]=static_cast<jbyte>(_bytes.at(i));
    }
    env->SetByteArrayRegion(_byteArr,0,len,_jbytes);

    return _byteArr;
}