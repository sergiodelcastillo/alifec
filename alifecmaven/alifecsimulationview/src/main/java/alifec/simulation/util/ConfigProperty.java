package alifec.simulation.util;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;

/**
 * Created by Sergio Del Castillo on 21/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ConfigProperty {
    private final SimpleStringProperty property = new SimpleStringProperty("");
    private final SimpleStringProperty content = new SimpleStringProperty("");

    public ConfigProperty() {

    }

    public ConfigProperty(String key, List<Integer> list) {
        this(key, listToString(list));
    }

    private static String listToString(List<Integer> list) {
        StringBuilder builder = new StringBuilder();
        for (Integer i : list) {
            builder.append(i).append(',');
        }
        return builder.toString();
    }

    public ConfigProperty(String key, String content) {
        this.property.set(key);
        this.content.set(content);
    }

    public ConfigProperty(String key, int content) {
        this(key, String.valueOf(content));
    }

    public String getKey() {
        return property.get();
    }


    public String getContent() {
        return content.get();
    }

    public void setKey(String key) {
        this.property.set(key);
    }

    public void setContent(String content) {
        //this.content.set(content);
        this.content.set(content);
    }
}
