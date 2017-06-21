/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
#include <jni.h>


#include "Environment.h"
#include "includemos.h"
#include "Petri.h"

Environment environment;
Petri petri;

#ifndef _Included_lib_CppColony
#define _Included_lib_CppColony
#ifdef __cplusplus
extern "C" {
#endif


//Funcion de utilidad para lanzar excepciones.
void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg);
/*
 * Class:     lib_CppColony
 * Method:    createMO
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_lib_CppColony_createColony
  (JNIEnv *, jobject, jint, jstring );

/*
 * Class:     lib_CppColony
 * Method:    createMO
 * Signature: (IIIF)Z
 */
JNIEXPORT jboolean JNICALL Java_lib_CppColony_createMO
  (JNIEnv *, jobject, jint, jint, jint, jfloat);

/*
 * Class:     lib_CppColony
 * Method:    kill
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_lib_CppColony_kill
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     lib_CppColony
 * Method:    end
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_lib_CppColony_end
  (JNIEnv *, jobject, jint);

/*
 * Class:     lib_CppColony
 * Method:    move
 * Signature: (II)Llib/Movement;
 */
JNIEXPORT jobject JNICALL Java_lib_CppColony_move
  (JNIEnv *, jobject,jobject, jint, jint);
/*
 * Class:     lib_CppColony
 * Method:    getAuthor
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_lib_CppColony_getAuthor
  (JNIEnv *, jobject, jint);

/*
 * Class:     lib_CppColony
 * Method:    getName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_lib_CppColony_getName
  (JNIEnv *, jobject, jint);

/*
 * Class:     lib_CppColony
 * Method:    getAffiliation
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_lib_CppColony_getAffiliation
  (JNIEnv *, jobject, jint);

/*
 * Class:     lib_CppColony
 * Method:    mitosis
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_lib_CppColony_mitosis
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     lib_CppColony
 * Method:    update
 * Signature: (IIFII)V
 */
JNIEXPORT void JNICALL Java_lib_CppColony_update
  (JNIEnv *, jobject, jint, jint, jfloat, jint, jint);

/*
 * Class:     lib_CppColony
 * Method:    clearAll
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_lib_CppColony_clearAll
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif

