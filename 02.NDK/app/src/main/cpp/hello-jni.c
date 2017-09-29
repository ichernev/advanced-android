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
#include <string.h>
#include <stdio.h>
#include <malloc.h>
#include <jni.h>
#include <android/log.h>

const char *TAG = "JNI";

/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   hello-jni/app/src/main/java/com/example/hellojni/HelloJni.java
 */

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

    return (*env)->NewStringUTF(env, "Hello from JNI !  Compiled with ABI " ABI ".");
}

#define ensureNoException(env, ret) \
    if ((*env)->ExceptionCheck(env) == JNI_TRUE) { \
        jthrowable ex = (*env)->ExceptionOccurred(env); \
        (*env)->ExceptionClear(env); \
        (*env)->Throw(env, ex); \
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
Java_com_example_hellojni_HelloJni_runWithArgs(JNIEnv *env, jobject instance, jboolean b, jint i,
                                               jdouble d, jstring s_, jobject o) {
    const char *s = (*env)->GetStringUTFChars(env, s_, 0);

    jclass objectClass = (*env)->GetObjectClass(env, o);
    ensureNoException(env, NULL);
    jmethodID toStringID = (*env)->GetMethodID(env, objectClass, "toString", "()Ljava/lang/String;");
    (*env)->DeleteLocalRef(env, objectClass);
    jstring oToS = (*env)->CallObjectMethod(env, o, toStringID);
    ensureNoException(env, NULL);

    const char *s2 = (*env)->GetStringUTFChars(env, oToS, 0);

    for (int i = 0; i < 10000; ++i) {
        (*env)->PushLocalFrame(env, 16);

        jstring s = (*env)->NewStringUTF(env, "Iskren");

        (*env)->PopLocalFrame(env, NULL);
    }

    int total = 0;
    total += snprintf(NULL, 0, "%d", (int) b);
    total += snprintf(NULL, 0, " %d", (int) i);
    total += snprintf(NULL, 0, " %.2lf", (double) d);
    total += 1 + strlen(s);
    total += 1 + strlen(s2);

    char *resUtf8 = malloc(total + 1);
    int total2 = snprintf(resUtf8, total+1, "%d %d %.2lf %s %s", (int) b, (int) i, (double) d, s, s2);
    if (total2 != total) {
        __android_log_print(ANDROID_LOG_ERROR, TAG,
                            "Error computing buffer size expected:%d actual:%d",
                            total, total2);
        return NULL;
    }

    jstring res = (*env)->NewStringUTF(env, resUtf8);
    free(resUtf8);
    (*env)->ReleaseStringUTFChars(env, oToS, s2);
    (*env)->ReleaseStringUTFChars(env, s_, s);

    return res;
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_hellojni_HelloJni_reversedByteArray(JNIEnv *env, jobject instance,
                                                     jbyteArray inp_) {
    jbyte *inp = (*env)->GetByteArrayElements(env, inp_, NULL);

    jsize inpLength = (*env)->GetArrayLength(env, inp_);
    jbyteArray res = (*env)->NewByteArray(env, inpLength);

    jbyte *resBytes = (*env)->GetByteArrayElements(env, res, NULL);
    for (int i = 0; i < inpLength; ++i) {
        resBytes[i] = inp[inpLength - 1 - i];
    }
    (*env)->ReleaseByteArrayElements(env, res, resBytes, 0);

    (*env)->ReleaseByteArrayElements(env, inp_, inp, 0);

    return res;
}
