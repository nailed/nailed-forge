#include "jk_5_nailed_encryption_NativeCipherImpl.h"
#include <openssl/aes.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

jlong Java_jk_15_nailed_encryption_NativeCipherImpl_init(JNIEnv* env, jobject obj, jbyteArray key){
    AES_KEY *aes_key = malloc(sizeof(AES_KEY));

    jboolean isKeyCopy;
    unsigned char *key_bytes = (unsigned char*)(*env)->GetByteArrayElements(env, key, &isKeyCopy);
    int key_length = (*env)->GetArrayLength(env, key) * 8;

    AES_set_encrypt_key(key_bytes, key_length, aes_key);

    if(isKeyCopy){
        (*env)->ReleaseByteArrayElements(env, key, (jbyte*)key_bytes, JNI_ABORT);
    }
    return (long) aes_key;
}
void Java_jk_15_nailed_encryption_NativeCipherImpl_free (JNIEnv* env, jobject obj, jlong key){
    printf("Test\n");
}
void Java_jk_15_nailed_encryption_NativeCipherImpl_cipher (JNIEnv* env, jobject obj, jboolean forEncryption, jlong key, jbyteArray iv, jlong in, jlong out, jlong length){
    
}
