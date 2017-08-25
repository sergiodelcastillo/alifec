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

#ifndef _Included_alifec_core_simulation_CppColony
#define _Included_alifec_core_simulation_CppColony
#ifdef __cplusplus
extern "C" {
#endif


//Funcion de utilidad para lanzar excepciones.
void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg);
/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    createMO
 * Signature: (I)Z
 */
 
JNIEXPORT jboolean JNICALL Java_alifec_core_simulation_CppColony_createColony
  (JNIEnv *, jobject, jint, jstring );

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    createMO
 * Signature: (IIIF)Z
 */
JNIEXPORT jboolean JNICALL Java_alifec_core_simulation_CppColony_createMO
  (JNIEnv *, jobject, jint, jint, jint, jfloat);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    kill
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_alifec_core_simulation_CppColony_kill
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    end
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_alifec_core_simulation_CppColony_end
  (JNIEnv *, jobject, jint);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    move
 * Signature: (II)Llib/Movement;
 */
JNIEXPORT jobject JNICALL Java_alifec_core_simulation_CppColony_move
  (JNIEnv *, jobject,jobject, jint, jint);
/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    getAuthor
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_alifec_core_simulation_CppColony_getAuthor
  (JNIEnv *, jobject, jint);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    getName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_alifec_core_simulation_CppColony_getName
  (JNIEnv *, jobject, jint);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    getAffiliation
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_alifec_core_simulation_CppColony_getAffiliation
  (JNIEnv *, jobject, jint);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    mitosis
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_alifec_core_simulation_CppColony_mitosis
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    update
 * Signature: (IIFII)V
 */
JNIEXPORT void JNICALL Java_alifec_core_simulation_CppColony_update
  (JNIEnv *, jobject, jint, jint, jfloat, jint, jint);

/*
 * Class:     alifec_core_simulation_CppColony
 * Method:    clearAll
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_alifec_core_simulation_CppColony_clearAll
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif

