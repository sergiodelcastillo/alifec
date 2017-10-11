#include "controller_java_CppColony.h"

JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_createColony
  (JNIEnv *env, jobject ob, jint id, jstring name){
	const char *c_name = env->GetStringUTFChars(name, NULL);
	Petri::getInstance()->setLoaded(false);
	
	if(c_name == NULL)
		return JNI_FALSE;
	
	if(environment.addColony(c_name, id)){
		return JNI_TRUE;
	}
	
	return JNI_FALSE;

}

JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_createMO
  (JNIEnv *env, jobject ob, jint id, jint x, jint y, jfloat ene){
   //avoid the use of petri class.
	Petri::getInstance()->setLoaded(false);
	
	CppColony<Microorganism> *col = environment.getColony(id);

	if(col == NULL) return JNI_FALSE;

	col->add(x, y, ene);
	return JNI_TRUE; 
}

JNIEXPORT void JNICALL Java_controller_java_CppColony_end
  (JNIEnv *env, jobject ob, jint id){
  	Petri::getInstance()->setLoaded(false);
	environment.delColony(id);
}

JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_kill
  (JNIEnv *env, jobject ob, jint id, jint index){
	Petri::getInstance()->setLoaded(false);
    CppColony<Microorganism> *col = environment.getColony(id);

    if(col->kill(index))
		return JNI_TRUE;
	 return JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_controller_java_CppColony_getName
  (JNIEnv *env, jobject ob,jobject _petri, jint id){
	CppColony<Microorganism> *col = environment.getColony(id);
	string s;

  	Petri::getInstance()->setPetri(env, _petri);
   		
	if(col == 0)
		s =  "Can not find MO.";
	else
		s = col->getName();

	jstring jst = env->NewStringUTF (s.c_str());
	return jst;
}

JNIEXPORT jstring JNICALL Java_controller_java_CppColony_getAuthor
  (JNIEnv *env, jobject ob,jobject _petri, jint id){
	CppColony<Microorganism> *col = environment.getColony(id);
	string s;

	Petri::getInstance()->setPetri(env, _petri);

	if(col == 0)
		s =  "Can not find MO.";
	else
		s = col->getAuthor();

	jstring jst = env->NewStringUTF (s.c_str());
	return jst;
}

JNIEXPORT jstring JNICALL Java_controller_java_CppColony_getAffiliation
  (JNIEnv *env, jobject ob,jobject _petri, jint id){
	CppColony<Microorganism> *col = environment.getColony(id);
	string s;

	Petri::getInstance()->setPetri(env, _petri);

	if(col == 0)
		s =  "Canot find MO.";
	else
		s = col->getAffiliation();

	jstring jst = env->NewStringUTF (s.c_str());
	return jst;
}
 
JNIEXPORT jobject JNICALL Java_controller_java_CppColony_move
  (JNIEnv *env, jobject ob,jobject _petri, jint id, jint index){ 

	CppColony<Microorganism> *col = environment.getColony(id);

	Movement mov= {0,0};

	if(col != NULL){
    	Petri::getInstance()->setPetri(env, _petri);

	    switch(col->move(index, mov)){
	        case OK: break;
	        cout << mov.dx << " " << mov.dy << endl;    
	        case FAIL_INDEX: break;
	            mov.dx = mov.dy = 0;
	        case FAIL_MO:
		        JNI_ThrowByName(env, "java/lang/Exception", "move error");
		        break;
	    }
    }

   	jclass clazz = env->FindClass("controller/java/Movement");
	jmethodID methodID = env->GetMethodID(clazz,"<init>", "(II)V");
	jobject obj = env->NewObject( clazz, methodID, mov.dx, mov.dy);
	
	return obj;	
}

JNIEXPORT jboolean JNICALL Java_controller_java_CppColony_mitosis
  (JNIEnv *env, jobject ob,jobject _petri, jint id, jint indexMO){
	CppColony<Microorganism> *col = environment.getColony(id);
	  
	Petri::getInstance()->setPetri(env, _petri);
		
	if(col == 0)
		return JNI_FALSE;

	bool mit = false;
	switch(col->mitosis(indexMO, mit)){
		case OK: 	
			break;
		case FAIL_INDEX: 
		  	mit = false;
		  	break;
		case FAIL_MO:
		 	JNI_ThrowByName(env, "java/lang/Exception", "mitosis error");
		   break;
	}
	
	return mit ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_controller_java_CppColony_update
  (JNIEnv *env, jobject ob, jint id, jint indexMO, jfloat ene, jint x, jint y){
   Petri::getInstance()->setLoaded(false);
	CppColony<Microorganism> *col = environment.getColony(id);
	
	if(col == 0) return;
		
	col->update(indexMO, x, y, ene);
}


JNIEXPORT void JNICALL Java_controller_java_CppColony_clearAll
  (JNIEnv *env, jobject ob, jint id){
  	Petri::getInstance()->setLoaded(false);
	CppColony<Microorganism> *col = environment.getColony(id);
	
	if(col == 0) return;
	col->clear();
}

void JNI_ThrowByName(JNIEnv *env, const char *name, const char *msg){
    jclass cls = env->FindClass(name);
    // if cls is NULL, an exception has already been thrown 
    if (cls != NULL) {
        env->ThrowNew( cls, msg);
    }
    // free the local ref 
    env->DeleteLocalRef(cls);
}


