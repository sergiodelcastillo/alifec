/**
 * @author Yeyo
 * @mail: sergio.jose.delcastillo@gmail.com
 */
 
#ifndef PETRI_H
#define PETRI_H
#include <jni.h>
#include "Data.h"
#include "Defs.h"

#include <iostream>
using namespace std;

class Petri{
    private:
		JNIEnv *env;
		jobject petri;
		bool loaded;

    private:
        inline bool ensure_index(int i, int j);


    public:
        static Petri * getInstance(){
            static Petri instance;

            return &instance;
        }

        inline void setLoaded(bool l);

        void setPetri(JNIEnv *env, jobject petri);

        int getOpponent(int x, int y);

        float getEnergy(int x, int y);

        float getNutrient(int x, int y);

        int getOpponent(Position p){
            return getOpponent(p.x, p.y);
        }

        float getEnergy(Position p){
            return getEnergy(p.x, p.y);
        }

        float getNutrient(Position p){
            return getNutrient(p.x, p.y);
        }

        int getDistNutri();

        int getBattleId();

        int getLiveTime();

        bool canCompete(int x1, int y1, int x2, int y2);

        inline bool canCompete(Position a, Position b)  {
            return canCompete(a.x, a.y, b.x, b.y);
        }

        inline bool inDish(int x, int y) ;

        inline bool inDish(Position pos){
            return inDish(pos.x, pos.y);
        }
};


bool Petri::ensure_index(int i, int j){
    return i >= 0 && i < Defs::DIAMETER && j >= 0 && j < Defs::DIAMETER;
}


void Petri::setLoaded(bool l){
    this->loaded = l;
}

void Petri::setPetri(JNIEnv *env, jobject petri){
    this->env = env;
    this->petri = petri;
    this->loaded = true;
}


int Petri::getOpponent(int x, int y){
    if(!loaded){
        cout << "not loaded" << endl;
        return -1;
    }
    if(!ensure_index(x, y)){
        return -1;
    }

    jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getOpponent", "(II)I");
    jint id = env->CallIntMethod(petri, methodID, x, y);
    return id;
}

float Petri::getEnergy(int x, int y)  {
    if(!loaded){
        cout << "not loaded" << endl;
        return 0.0f;
    }
    if(!ensure_index(x, y)){
        return 0.0f;
    }
    jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getEnergy", "(II)F");
    jfloat ene = env->CallFloatMethod(petri, methodID, x, y);
    return ene;
}


float Petri::getNutrient(int x, int y) {
    if(!loaded){
        cout << "not loaded" << endl;
        return 0.0f;
    }
    if(!ensure_index(x, y)){
        return 0.0f;
    }
    jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getNutrient", "(II)F");
    jfloat nut = env->CallFloatMethod(petri, methodID, x, y);

    return nut;
}

int Petri::getDistNutri(){
    if(!loaded){
        cout << "not loaded" << endl;
        return -1;
    }

    jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getDistNutri", "()I");
    jint dist = env->CallIntMethod(petri, methodID);
    return dist;
}

int Petri::getBattleId(){
    if(!loaded){
        cout << "not loaded" << endl;
        return -1; //invalid id.
    }

    jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getBattleId", "()I");
    jint battleId = env->CallIntMethod(petri, methodID);
    return battleId;
}
int Petri::getLiveTime(){
    if(!loaded){
        cout << "not loaded" << endl;
        return -1; //invalid id.
    }

    jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getLiveTime", "()I");
    jint liveTime = env->CallIntMethod(petri, methodID);
    return liveTime;
}

bool Petri::canCompete(int x1, int y1, int x2, int y2){
    if(!loaded){
        cout << "not loaded" << endl;
        return false;
    }
    if(!ensure_index(x1, y1) || !ensure_index(x2, y2)){
        return false;
    }
    jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"canCompete", "(IIII)Z");
    jboolean can_compete = env->CallBooleanMethod(petri, methodID, x1, y1, x2, y2);
    return can_compete == JNI_FALSE ? false: true;
}

bool Petri::inDish(int x, int y) {
/*if(!loaded){
cout << "not loaded" << endl;
return false;
}

jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"inDish", "(II)Z");
jboolean indish = env->CallBooleanMethod(petri, methodID, x, y);
return indish;                                                   */
    return (x-Defs::RADIUS)*(x-Defs::RADIUS) + (y-Defs::RADIUS)*(y-Defs::RADIUS) < Defs::RADIUS*Defs::RADIUS;
}

#endif
