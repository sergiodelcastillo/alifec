package alifec.core.simulation.nutrient.function;


public interface Function {
    float apply(int x, int y);

    String getName();

    int getId();
}
