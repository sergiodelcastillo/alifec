package alifec.core.validation;

import alifec.core.simulation.Agar;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleFromCsvValidator implements Validator<String> {

    //todo: create test
    @Override
    public boolean validate(String line) {
        if (line == null || line.isEmpty())
            return false;

        String[] tmp = line.split(",");

        //5 elements: name1, name2, nutrient, energy1, energy2
        if (tmp.length != 5)
            return false;


        return checkColonyName(tmp[0]) &&
                checkColonyName(tmp[1]) &&
                checkNutrientName(tmp[2]) &&
                checkPositiveFloat(tmp[3]) &&
                checkPositiveFloat(tmp[4]);
    }

    private boolean checkNutrientName(String nutri) {
        if(nutri == null || nutri.isEmpty() ) return false;

        return Agar.getNutrientByName(nutri) != null;
    }

    private boolean checkColonyName(String name){
        return name != null && name.trim().length() > 0;
    }
    private boolean checkPositiveFloat(String s) {
        try {
            return Float.parseFloat(s) >= 0.0f;
        } catch (Throwable t) {
            return false;
        }
    }
}
