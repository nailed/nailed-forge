#ifdef _WIN32
#define WIN32
#endif

#include <jni.h>

#include <stdio.h>
#include <string.h>
#include <wchar.h>

#include <locale.h>

#ifndef WIN32
    #include <stdint.h>
    #include <sys/mman.h>
    #include <unistd.h>
    #include <sys/stat.h>
    #include <fcntl.h>
#else
    #include <windows.h>
#endif

#include "jk_5_nailed_mumblelink_Mumble.h"
#include "linkedMem.h"
#include "mumble.h"

/**
 *  This method initializes the shared memory with mumble.
 *  There are a few return codes that reflect the state
 *   - 0: No error. We successfully initialized the shared memory
 *   - 1: OpenFileMappingW failed to return a handle (WIN32 only)
 *   - 2: MapViewOfFile failed to return a structure (WIN32 only)
 *   - 3: shm_open returned a negative integer (unix only)
 *   - 4: mmap failed to return a structure (unix only)
 *
 *  @author jk-5
 */
JNIEXPORT jint JNICALL
Java_jk_15_nailed_mumblelink_Mumble_init(JNIEnv* env, jobject obj){
    #ifdef WIN32
        //Attempt to open a handle for the linked memory
        HANDLE hMapObject = OpenFileMappingW(FILE_MAP_ALL_ACCESS, FALSE, L"MumbleLink");
        if(hMapObject == NULL){
            return 1;
        }

        //Now link the handle to our LinkedMem object
        lm = (LinkedMem_t *) MapViewOfFile(hMapObject, FILE_MAP_ALL_ACCESS, 0, 0, sizeof (LinkedMem));
        if(lm == NULL){
            CloseHandle(hMapObject);
            hMapObject = NULL;
            return 2;
        }
    #else
        char memname[256];
        snprintf(memname, 256, "/MumbleLink.%d", getuid()); //Write /MumbleLink.UID to memname

        int shmfd = shm_open(memname, O_RDWR, S_IRUSR | S_IWUSR); //Open the shared memory object

        if(shmfd < 0){
            return 3;
        }

        //Map the linked memory to our LinkedMem object. Open it for READ and WRITE and let the changes be shared
        //Also, use an offset of 0
        lm = (LinkedMem_t *) (mmap(NULL, sizeof (struct LinkedMem), PROT_READ | PROT_WRITE, MAP_SHARED, shmfd, 0));

        if(lm == (void *)(-1)){
            //mmap failed to link the memory. Release it and return an error
            lm = NULL;
            return 4;
        }
    #endif

    return 0;
}

/**
 *  This method updates the shared mumble memory.
 *  It adds the coordinates, yaw and pitch and some more meta like applicaion name and context
 *  The following return codes are possible
 *   - 0: No error. The shared memory is updated
 *   - 1: Shared memory object was empty. Call init() to initialize it
 *
 *  @author jk-5
 */
JNIEXPORT jint JNICALL
Java_jk_15_nailed_mumblelink_Mumble_update(JNIEnv* env, jobject obj,
        jfloatArray p_fAvatarPosition, jfloatArray p_fAvatarFront,
        jfloatArray p_fAvatarTop, jstring p_name, jstring p_description, jfloatArray p_fCameraPosition,
        jfloatArray p_fCameraFront, jfloatArray p_fCameraTop, jstring p_identity,
        jstring p_context) {

    if(!lm){
        //We don't have linked memory. Return an error
        return 1;
    }

    //When mumble asks for the application name, convert the jstrings to UTF char arrays and write it to the linked memory
    if (lm->uiVersion != 2) {
        copyConvertWCharT(env, (lm->name), p_name);
        copyConvertWCharT(env, (lm->description), p_description);
        lm->uiVersion = 2;
    }
    lm->uiTick++; //Notify mumble that we have updated the memory

    //Add all our game info
    (*env)->GetFloatArrayRegion(env, p_fAvatarFront, 0, 3, lm->fAvatarFront);
    (*env)->GetFloatArrayRegion(env, p_fAvatarTop, 0, 3, lm->fAvatarTop);
    (*env)->GetFloatArrayRegion(env, p_fAvatarPosition, 0, 3, lm->fAvatarPosition);
    (*env)->GetFloatArrayRegion(env, p_fCameraPosition, 0, 3, lm->fCameraPosition);
    (*env)->GetFloatArrayRegion(env, p_fCameraFront, 0, 3, lm->fCameraFront);
    (*env)->GetFloatArrayRegion(env, p_fCameraTop, 0, 3, lm->fCameraTop);

    copyConvertWCharT(env, (lm->identity), p_identity); //Write the identity

    lm->context_len = copyConvertUC(env, (lm->context), p_context); //Update the context length and write the context

    //printf("\nUpdate: \nname: %ls\ndescription: %ls\nidentity: %ls\ncontext: %s\ncontext_len: %i\n", lm->name, lm->description, lm->identity, lm->context, lm->context_len);
    //printf("fCameraFront: %f, %f, %f\n", lm->fCameraFront[0], lm->fCameraFront[1], lm->fCameraFront[2]);

    return 0;
}

void strtowstr(const char *from, wchar_t *to, int length){
    while(length--){
        to[length] = (wchar_t) from[length];
    }
}

void copyConvertWCharT(JNIEnv* env, wchar_t* target, jstring source) {
    //Convert java string to primitive
    const char * str = (*env)->GetStringUTFChars(env, source, NULL);
    //Convert const char* to wchar_t*
    strtowstr(str, target, strlen(str));
    //Release the JNI representation. We don't need this anymore
    (*env)->ReleaseStringUTFChars(env, source, (char*) str);
}

size_t copyConvertUC(JNIEnv* env, unsigned char* target, jstring source) {
    //Convert java string to primitive
    const char * utf16_name_cc = (*env)->GetStringUTFChars(env, source, NULL);
    //String length
    size_t size = strlen(utf16_name_cc);
    //Get the wchar_t representation
    const unsigned char * utf16_name_cuc = (unsigned char*) utf16_name_cc;
    //Copy the stuff to the shared memory
    memcpy(target, utf16_name_cuc, (size) * (sizeof(const char)));
    //Release unneeded JNI string reference
    (*env)->ReleaseStringUTFChars(env, source, utf16_name_cc);
    return size;
}
