package controller.java.nutrients.function;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Nov 24, 2010
 * Time: 7:26:31 PM
 */
public interface Function {
    public float apply(int x, int y);

    public String getName();

    int getId();
}
