package alifec.simulation.controller;

/**
 * Created by Sergio Del Castillo on 12/05/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class KeyBasedModel {

    private int key;
    private String readableText;

    public KeyBasedModel(int key, String text) {
        this.key = key;
        this.readableText = text;
    }

    public int getKey() {
        return key;
    }

    @Override
    public String toString() {
        return readableText;
    }

}
