package alifec.core.persistence.dto;

/**
 * Created by Sergio Del Castillo on 14/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class StartBattle {

    private int firstColonyId;
    private String firstColony;
    private int secondColonyId;
    private String secondColony;
    private int nutrientId;
    private String nutrient;

    public StartBattle(int firstColonyId, String firstColony, int secondColonyId, String secondColony, int nutrientId, String nutrient) {
        this.firstColonyId = firstColonyId;
        this.firstColony = firstColony;
        this.secondColonyId = secondColonyId;
        this.secondColony = secondColony;
        this.nutrientId = nutrientId;
        this.nutrient = nutrient;
    }

    public int getFirstColonyId() {
        return firstColonyId;
    }

    public void setFirstColonyId(int firstColonyId) {
        this.firstColonyId = firstColonyId;
    }

    public String getFirstColony() {
        return firstColony;
    }

    public void setFirstColony(String firstColony) {
        this.firstColony = firstColony;
    }

    public int getSecondColonyId() {
        return secondColonyId;
    }

    public void setSecondColonyId(int secondColonyId) {
        this.secondColonyId = secondColonyId;
    }

    public String getSecondColony() {
        return secondColony;
    }

    public void setSecondColony(String secondColony) {
        this.secondColony = secondColony;
    }

    public int getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(int nutrientId) {
        this.nutrientId = nutrientId;
    }

    public String getNutrient() {
        return nutrient;
    }

    public void setNutrient(String nutrient) {
        this.nutrient = nutrient;
    }
}
