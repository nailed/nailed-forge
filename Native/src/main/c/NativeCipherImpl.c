#include "jk_5_nailed_encryption_NativeCipherImpl.h"
#include <jni.h>
#include <stdio.h>

jlong Java_jk_15_nailed_encryption_NativeCipherImpl_init(JNIEnv* env, jobject obj, jbyteArray key){
    return 0;
}
void Java_jk_15_nailed_encryption_NativeCipherImpl_free (JNIEnv* env, jobject obj, jlong key){
    printf("Test\n");
    return;
}
void Java_jk_15_nailed_encryption_NativeCipherImpl_cipher (JNIEnv* env, jobject obj, jboolean forEncryption, jlong key, jbyteArray iv, jlong in, jlong out, jint length){
    return;
}
