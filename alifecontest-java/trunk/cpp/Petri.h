/**
 * @author Sergio Del Castillo
 * @email sergio.jose.delcastillo@gmail.com
 */
 
#ifndef PETRI_H
#define PETRI_H

#include <jni.h>
#include "Data.h"

class Petri{
	private:
		JNIEnv *env;
		jobject petri;
		
	public:
		void setPetri(JNIEnv *env, jobject petri){
			this->env = env;
			this->petri = petri; 
		}
		/*
		 * Retorna el identificador de la colonia a la que pertenece el MO que está en la posicion (x, y).
		 * 0 : No hay ningun MO en esa posicion
		 * 1 : Identificador de la colonia 1
		 * 2 : Identificador de la colonia 2 
		 */int getOponent(int x, int y){
			jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getOponent", "(II)I");
			jint id = env->CallIntMethod(petri, methodID, x, y);
			return id;
		}
		/*
		 * Energia que posee el MO que está en la posicion (x,y)
		 * Se retorna 0 si no hay MO en la posicion (x,y)
		 */float getEnergy(int x, int y)  {
			jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getEnergy", "(II)F");
			jfloat ene = env->CallFloatMethod(petri, methodID, x, y);
			return ene;
		}
		
		/*
		 * Retorna la cantidad de nutrientes que hay en la posicion (x,y)
		 */float getNutrient(int x, int y) {
			jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getNutrient", "(II)F");
			jfloat nut = env->CallFloatMethod(petri, methodID, x, y);
			return nut;
		}
		
		/*
		 * Retorna informacion acerca de la distribucion de nutrientes actual.
		 * 1  : plano inclinado (Inclined  Plane)
		 * 2  : barra vertical (Vertical Bar)
		 * 3  : anillo (Rings)
		 * 4  : grilla (Lattice)
		 * 5  : dos gausianas (Two Gaussians)
		 * 100: distrubucion uniforme (Famine)
		 */int getDistNutri(){
			jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"getDistNutri", "()I");
			jint dist = env->CallBooleanMethod(petri, methodID);
			return dist;			
		}
		
		/*
		 * retorna true si el MO que esta en la posicion (x1,y1) puede atacar al 
		 * MO que esta en la posición (x2,y2)
		 */bool canCompite(int x1, int y1, int x2, int y2){
			jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"canCompite", "(IIII)Z");
			jboolean can_compite = env->CallBooleanMethod(petri, methodID, x1, y1, x2, y2);
			return can_compite;
		}

		/*
		 * retorna true si el MO que esta en la posicion (x1,y1) puede atacar al 
		 * MO que esta en la posición (x2,y2)
		 */bool canCompite(Position a, Position b)  {
			return canCompite(a.x, a.y, b.x, b.y);
		}

		/*
		 * Retorna true si esta dentro del circulo de petri
		 */bool inDish(int x, int y) {
			jmethodID methodID = env->GetMethodID(env->GetObjectClass(petri),"inDish", "(II)Z");
			jboolean indish = env->CallBooleanMethod(petri, methodID, x, y);
			return indish;
		}
		
		/*
		 * Retorna true si esta dentro del circulo de petri
		 */bool inDish(Position pos){
			return inDish(pos.x, pos.y);
		}
};


#endif
