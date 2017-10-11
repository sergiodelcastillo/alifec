template <class MO > CppColony<Microorganism>* CppColony<MO>::createInstance(string type, int id){
    CppColony<Microorganism> *mo = NULL;

   if(type == "MoveNever"){
       mo = (CppColony < Microorganism > *) new CppColony< MoveNever>(id);
   }
   if(type == "Movx"){
       mo = (CppColony < Microorganism > *) new CppColony< Movx>(id);
   }

    return mo;
}

