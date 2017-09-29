/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <cstring>
#include <cstdlib>
#include <jni.h>
#include <pthread.h>
#include <thread>
#include <android/log.h>

const char *TAG = "JNI";

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   hello-jni/app/src/main/java/com/example/hellojni/HelloJni.java
 */

extern "C" {

/*
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* aReserved);
 */

JNIEXPORT jstring JNICALL
Java_com_example_iskren_a02ndk_MainActivity_stringFromJNI( JNIEnv* env,
                                                           jobject thiz );

JNIEXPORT jstring JNICALL
Java_com_example_iskren_a02ndk_MainActivity_runWithArgs(JNIEnv *env, jobject instance, jboolean b, jint i,
                                                        jdouble d, jstring s_, jobject o);

JNIEXPORT jbyteArray JNICALL
Java_com_example_iskren_a02ndk_MainActivity_reversedByteArray(JNIEnv *env, jobject instance,
                                                              jbyteArray inp_);


JNIEXPORT void JNICALL
Java_com_example_iskren_a02ndk_MainActivity_callThread(JNIEnv *env, jobject thiz);

}

JavaVM *vm;

/*
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* theVM, void* aReserved) {
    // cache java VM
    vm = theVM;

    //__android_log_print(ANDROID_LOG_INFO, TAG, "accepting vm");
}*/

JNIEXPORT jstring JNICALL
Java_com_example_iskren_a02ndk_MainActivity_stringFromJNI( JNIEnv* env,
                                                           jobject thiz )
{
#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif

    return env->NewStringUTF("Hello from JNI !  Compiled with ABI " ABI ".");
}

#define ensureNoException(env, ret) \
    if ((env)->ExceptionCheck() == JNI_TRUE) { \
        jthrowable ex = (env)->ExceptionOccurred(); \
        (env)->ExceptionClear(); \
        (env)->Throw(ex); \
        return (ret); \
    }

//void ensureNoException(JNIEnv *env) {
//    if ((*env)->ExceptionCheck(env) == JNI_TRUE) {
//        // exception happened
//        __android_log_print(ANDROID_LOG_ERROR, TAG, "Exception happened");
//        jthrowable ex = (*env)->ExceptionOccurred(env);
//        (*env)->ExceptionClear(env);
//        (*env)->Throw(env, ex);
//    }
//}

JNIEXPORT jstring JNICALL
Java_com_example_iskren_a02ndk_MainActivity_runWithArgs(JNIEnv *env, jobject instance, jboolean b, jint i,
                                               jdouble d, jstring s_, jobject o) {
    const char *s = env->GetStringUTFChars(s_, 0);

    jclass objectClass = env->GetObjectClass(o);
    ensureNoException(env, NULL);
    jmethodID toStringID = env->GetMethodID(objectClass, "toString", "()Ljava/lang/String;");
    env->DeleteLocalRef(objectClass);
    jstring oToS = (jstring) env->CallObjectMethod(o, toStringID);
    ensureNoException(env, NULL);

    const char *s2 = env->GetStringUTFChars(oToS, 0);

    for (int i = 0; i < 10000; ++i) {
        env->PushLocalFrame(16);

        jstring s = env->NewStringUTF("Iskren");

        env->PopLocalFrame(NULL);
    }

    int total = 0;
    total += snprintf(NULL, 0, "%d", (int) b);
    total += snprintf(NULL, 0, " %d", (int) i);
    total += snprintf(NULL, 0, " %.2lf", (double) d);
    total += 1 + strlen(s);
    total += 1 + strlen(s2);

    char *resUtf8 = (char *) malloc(total + 1);
    int total2 = snprintf(resUtf8, total+1, "%d %d %.2lf %s %s", (int) b, (int) i, (double) d, s, s2);
    if (total2 != total) {
        __android_log_print(ANDROID_LOG_ERROR, TAG,
                            "Error computing buffer size expected:%d actual:%d",
                            total, total2);
        return NULL;
    }

    jstring res = env->NewStringUTF(resUtf8);
    free(resUtf8);
    env->ReleaseStringUTFChars(oToS, s2);
    env->ReleaseStringUTFChars(s_, s);

    return res;
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_iskren_a02ndk_MainActivity_reversedByteArray(JNIEnv *env, jobject instance,
                                                     jbyteArray inp_) {
    jbyte *inp = env->GetByteArrayElements(inp_, NULL);

    jsize inpLength = env->GetArrayLength(inp_);
    jbyteArray res = env->NewByteArray(inpLength);

    jbyte *resBytes = env->GetByteArrayElements(res, NULL);
    for (int i = 0; i < inpLength; ++i) {
        resBytes[i] = inp[inpLength - 1 - i];
    }
    env->ReleaseByteArrayElements(res, resBytes, 0);

    env->ReleaseByteArrayElements(inp_, inp, 0);

    return res;
}


extern "C" {
void *inThread(void *data);
}


void *inThread(void *data) {
    JNIEnv *env;

    vm->AttachCurrentThread(&env, NULL);

    // int a = *((int *) data);
    int *res = (int *) malloc(sizeof(int));
    *res = 42;

    __android_log_print(ANDROID_LOG_ERROR, TAG, "in thread %d", *((int *) data));
    //__android_log_print(ANDROID_LOG_ERROR, TAG, "in thread %d", a);
    //__android_log_print(ANDROID_LOG_ERROR, TAG, "pre attach");
    // vm->AttachCurrentThread(&env, data);
    //__android_log_print(ANDROID_LOG_ERROR, TAG, "in here");
    vm->DetachCurrentThread();

    //vm->DetachCurrentThread();

    return (void *) res;
}

void callCThread() {
//    int *a = (int *) malloc(sizeof(int));
//    *a = 5;
    int aa = 6;
    static pthread_t thread;
    static pthread_attr_t thread_attr;

    pthread_attr_init(&thread_attr);
    __android_log_print(ANDROID_LOG_INFO, TAG, "Before stuff");
    int res = pthread_create(&thread, &thread_attr, &inThread, (void *) &aa);
    __android_log_print(ANDROID_LOG_INFO, TAG, "after pthread_create");
    if (res != 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Failed to start thread %d", res);
        return;
    }

    void *result;
    pthread_join(thread, &result);

    __android_log_print(ANDROID_LOG_INFO, TAG, "Got result from thread %d",
                        *((int *) result));
}

/*
void callCppThread() {
    std::thread thread(inThread);
    thread.join();
}*/

JNIEXPORT void JNICALL
Java_com_example_iskren_a02ndk_MainActivity_callThread(JNIEnv *env, jobject thiz) {
    env->GetJavaVM(&vm);

    callCThread();
}