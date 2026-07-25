#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <stdlib.h>

#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, "minipro_jni", __VA_ARGS__))

// Extern declaration of minipro main entry point.
extern int minipro_main(int argc, char **argv, int usb_fd);

JNIEXPORT jint JNICALL
Java_com_echosmart_flashlabs_hardware_MiniproNative_runMinipro(JNIEnv *env, jobject thiz, jobjectArray args, jint usb_fd) {
    int argc = (*env)->GetArrayLength(env, args);
    char **argv = (char **)malloc((argc + 1) * sizeof(char *));

    for (int i = 0; i < argc; i++) {
        jstring string = (jstring) (*env)->GetObjectArrayElement(env, args, i);
        const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
        argv[i] = strdup(rawString);
        (*env)->ReleaseStringUTFChars(env, string, rawString);
    }
    argv[argc] = NULL;

    LOGI("Calling minipro_main with %d arguments", argc);
    int result = minipro_main(argc, argv, usb_fd);

    for (int i = 0; i < argc; i++) {
        free(argv[i]);
    }
    free(argv);

    return result;
}
