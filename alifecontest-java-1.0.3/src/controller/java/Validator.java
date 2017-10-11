package controller.java;

import controller.java.contest.ContestMode;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sergio Del Castillo
 * Mail: yeyo@druidalabs.com
 * Date: Sep 19, 2010
 * Time: 9:00:05 PM
 */
public class Validator {

    /**
     * The name of contest must have the prefix "contest-" and end with alphanumeric string.
     *
     * @param name the name of contest
     * @return true if the name is valid
     */
    public static boolean validateContestName(String name) {
        return (name != null && name.toLowerCase().matches("\\Acontest-[\\w ]{0,30}"));
    }

    /**
     * A path is valid if it is not null and is an existing file.
     *
     * @param path the string to validate
     * @return true if the path is valid
     */
    public static boolean validatePath(String path) {
        return path != null && !path.trim().equals("") && new File(path).exists();
    }

    /**
     * @see controller.java.Validator#validatePath(String)
     */
    public static boolean validateMode(String mode) {
        try {
            return validateMode(Integer.parseInt(mode));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * The environment mode can be 0 or 1.
     *
     * @param mode environment mode
     * @return true if the path is valid
     */
    public static boolean validateMode(int mode) {
        return mode == ContestMode.COMPETITION_MODE || mode == ContestMode.PROGRAMMER_MODE;
    }

    /**
     * The pause field must be a non negative integer
     *
     * @param pause the pause between battles
     * @return true if the pause is valid
     */
    public static boolean validatePause(String pause) {
        try {
            return validatePause(Integer.parseInt(pause));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * @param pause
     * @return
     * @see controller.java.Validator#validatePause(String)
     */
    public static boolean validatePause(int pause) {
        return pause >= 0;
    }

    /**
     * .
     * The colony name is a string that the its length is between 3 and 50 and contain only letter,  numbers , - or _.
     *
     * @param name a string to validate
     * @return true if name is valid
     */
    public static boolean validateColonyName(String name) {
        return name != null && name.matches("[\\w\\-_ ]{3,50}");
    }

    /**
     * The author name is a string that the its length is between 3 and 50 and contain only letter,  numbers , -, ', or _.
     *
     * @param author a string to validate
     * @return true if the author is valid
     */
    public static boolean validateColonyAuthor(String author) {
        return author != null && author.matches("[\\w\\-_ '&]{3,50}");
    }

    /**
     * The affiliation name is a string that the its length is between 3 and 50 and contain only letter,  numbers , -,, or _.
     *
     * @param aff a string to validate
     * @return true if the author is valid
     */
    public static boolean validateColonyAffiliation(String aff) {
        return aff != null && aff.matches("[\\w\\-_ ]{3,50}");
    }

    public static boolean validateNutrientId(int id) {
            return id >= 0;
    }

}
