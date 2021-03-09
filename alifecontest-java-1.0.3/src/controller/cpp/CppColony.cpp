template <class MO > CppColony<Microorganism>* CppColony<MO>::createInstance(string type, int id){
    CppColony<Microorganism> *mo = NULL;

   if(type == "Movx"){
       mo = (CppColony < Microorganism > *) new CppColony< Movx>(id);
   }
   if(type == "MoveNever"){
       mo = (CppColony < Microorganism > *) new CppColony< MoveNever>(id);
   }

    return mo;
}

