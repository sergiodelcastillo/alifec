/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

#include "lib_CppColony.h"

JNIEXPORT jboolean JNICALL Java_lib_CppColony_createColony 
(JNIEnv *env, jobject ob, jint id, jstring name){
	const char *c_name = env->GetStringUTFChars(name, NULL);
	
	if(c_name == NULL)
		return JNI_FALSE;

	if(environment.addColony(c_name, id))
		return JNI_TRUE;
	
	return JNI_FALSE;
}
 
JNIEXPORT jboolean JNICALL Java_lib_CppColony_createMO
  (JNIEnv *env, jobject ob, jint id, jint x, jint y, jfloat ene){
	CppColony<Microorganism> *col = environment.getColony(id);

	if(col == NULL) return JNI_FALSE;
	
	Position pos={x,y};
	col->add(pos, ene);
	return JNI_TRUE; 
}

JNIEXPORT jboolean JNICALL Java_lib_CppColony_kill
  (JNIEnv *env, jobject ob, jint id, jint index){
    CppColony<Microorganism> *col = environment.getColony(id);

    if(col->kill(index))
		return JNI_TRUE;
	 return JNI_FALSE;
  }

JNIEXPORT void JNICALL Java_lib_CppColony_end
  (JNIEnv *env, jobject ob, jint id){
	environment.delColony(id);
}
 
JNIEXPORT jobject JNICALL Java_lib_CppColony_move
  (JNIEnv *env, jobject ob,jobject _petri, jint id, jint index){
	CppColony<Microorganism> *col = environment.getColony(id);
	Movement mov= {0,0};	
	
	if(col == 0) return NULL;

	petri.setPetri(env, _petri);
	
	if(!col->move(index, mov)){
		JNU_ThrowByName(env, "java/lang/Exception", "Error de un MO (move)");
	}
	
	jclass clazz = env->FindClass("lib/Movement");
	jmethodID methodID = env->GetMethodID(clazz,"<init>", "(II)V");
	jobject obj = env->NewObject( clazz, methodID, mov.dx, mov.dy);
	
	return obj;	
}
    
void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg){
    jclass cls = env->FindClass(name);
    // if cls is NULL, an exception has already been thrown 
    if (cls != NULL) {
        env->ThrowNew( cls, msg);
    }
    // free the local ref 
    env->DeleteLocalRef(cls);
}

JNIEXPORT jstring JNICALL Java_lib_CppColony_getAuthor
  (JNIEnv *env, jobject ob, jint id){
	CppColony<Microorganism> *col = environment.getColony(id);
	string s;
	
	if(col == 0)
		s =  "Can not find MO.";
	else
		s = col->getAuthor();

	jstring jst = env->NewStringUTF (s.c_str());
	return jst;
  }

JNIEXPORT jstring JNICALL Java_lib_CppColony_getName
  (JNIEnv *env, jobject ob, jint id){
	CppColony<Microorganism> *col = environment.getColony(id);
	string s;
	
	if(col == 0)
		s =  "Can't find MO.";
	else
		s = col->getName();

	jstring jst = env->NewStringUTF (s.c_str());
	return jst;
  }

JNIEXPORT jstring JNICALL Java_lib_CppColony_getAffiliation
  (JNIEnv *env, jobject ob, jint id){
	CppColony<Microorganism> *col = environment.getColony(id);
	string s;
	
	if(col == 0)
		s =  "Can't find MO.";
	else
		s = col->getAffiliation();

	jstring jst = env->NewStringUTF (s.c_str());
	return jst;
  }

JNIEXPORT jboolean JNICALL Java_lib_CppColony_mitosis
  (JNIEnv *env, jobject ob, jint id, jint indexMO){
	CppColony<Microorganism> *col = environment.getColony(id);
	
	if(col == 0)
		return JNI_FALSE;

	bool mit = false;
	
	if(!col->mitosis(indexMO, mit)){
		JNU_ThrowByName(env, "java/lang/Exception", "Error de un MO (mitosis)");
	}
	
	return mit ? JNI_TRUE : JNI_FALSE;
  }

JNIEXPORT void JNICALL Java_lib_CppColony_update
  (JNIEnv *env, jobject ob, jint id, jint indexMO, jfloat ene, jint x, jint y){
	CppColony<Microorganism> *col = environment.getColony(id);
	
	if(col == 0) return;
	Position p={x,y};
		
	col->update(indexMO, p, ene);
  }


JNIEXPORT void JNICALL Java_lib_CppColony_clearAll
  (JNIEnv *env, jobject ob, jint id){
	CppColony<Microorganism> *col = environment.getColony(id);
	
	if(col == 0) return;
	col->clear();
 }
