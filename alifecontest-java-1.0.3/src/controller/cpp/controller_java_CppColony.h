/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
#include <jni.h>
#include "Petri.h"
#include "Defs.h"
#include "includemos.h"
#include "Environment.h"

Environment environment;

/* Header for class controller_java_CppColony */

#ifndef _Included_controller_java_CppColony
#define _Included_controller_java_CppColony
#ifdef __cplusplus
extern "C" {
#endif

//Funcion de utilidad para lanzar excepciones.
void JNI_ThrowByName(JNIEnv *env, const char *name, const char *msg);

/*
 * Class:     controller_java_CppColony
 * Method:    createColony
 * Signature: (ILjava/lang/String;)Z
 */
	JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_createColony
	  (JNIEnv *, jobject, jint, jstring);

/*
 * Class:     controller_java_CppColony
 * Method:    createMO
 * Signature: (IIIF)Z
 */
JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_createMO
  (JNIEnv *, jobject, jint, jint, jint, jfloat);

/*
 * Class:     controller_java_CppColony
 * Method:    end
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_controller_java_CppColony_end
  (JNIEnv *, jobject, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    kill
 * Signature: (II)Z
 */
JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_kill
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    getName
 * Signature: (Lcontroller/java/Petri;I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_controller_java_CppColony_getName
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    getAuthor
 * Signature: (Lcontroller/java/Petri;I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_controller_java_CppColony_getAuthor
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    getAffiliation
 * Signature: (Lcontroller/java/Petri;I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_controller_java_CppColony_getAffiliation
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    move
 * Signature: (Lcontroller/java/Petri;II)Lcontroller/java/Movement;
 */
JNIEXPORT jobject JNICALL Java_controller_java_CppColony_move
  (JNIEnv *, jobject, jobject, jint, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    mitosis
 * Signature: (Lcontroller/java/Petri;II)Z
 */
JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_mitosis
  (JNIEnv *, jobject, jobject, jint, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    update
 * Signature: (IIFII)V
 */
JNIEXPORT void JNICALL Java_controller_java_CppColony_update
  (JNIEnv *, jobject, jint, jint, jfloat, jint, jint);

/*
 * Class:     controller_java_CppColony
 * Method:    clearAll
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_controller_java_CppColony_clearAll
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
