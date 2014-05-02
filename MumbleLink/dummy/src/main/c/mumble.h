#ifndef MUMBLE_H
#define	MUMBLE_H

/**
 * convert and copy the source jstring to an exsisting wchar_t array
 *
 * @param env jni environment
 * @param target memory location
 * @param source jstring
 */
void copyConvertWCharT(JNIEnv* env, wchar_t* target, jstring source);

/**
 * convert and copy the source jstring to an exsisting wchar_t array
 *
 * @param env jni environment
 * @param target memory location
 * @param source jstring
 * @return size of the effected area in target
 */
size_t copyConvertUC(JNIEnv* env, unsigned char* target, jstring source);

#endif
