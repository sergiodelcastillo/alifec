package alifec.simulation.simulation;

import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;

import java.util.ArrayDeque;
import java.util.Collections;

/**
 * Created by Sergio Del Castillo on 30/06/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class EnergyHistoryHolder {
    private static final int HISTORY_MAX = 50;


    private final double width;
    private final double heightMax;
    private final int border;
    private double maxEnergy;
    private double[] colonyX = new double[HISTORY_MAX + 1];
    private ArrayDeque<Double> colony1 = new ArrayDeque<>();
    private ArrayDeque<Double> colony2 = new ArrayDeque<>();

    public EnergyHistoryHolder(int width, double heightMax, int border) {
        this.width = width;
        this.heightMax = heightMax;
        this.border = border;
        initializeColonyX();
    }

    private void initializeColonyX() {
        for (int i = 0; i <= HISTORY_MAX; i++) {
            colonyX[i] = border + i * ((width - 2 * border) / HISTORY_MAX);
        }
    }

    public void clear() {
        colony1.clear();
        colony2.clear();
    }

    public void add(Environment environment) {
        add(environment.getFirstOpponent(), colony1);
        add(environment.getSecondOpponent(), colony2);
        updateMax();
    }

    private void updateMax() {
        maxEnergy = Math.max(Collections.max(colony1), Collections.max(colony2));
    }

    public double[] colonyX() {
        return colonyX;
    }

    public double[] colony1() {
        return colony1.stream().mapToDouble(this::relative).toArray();
    }

    public double[] colony2() {
        return colony2.stream().mapToDouble(this::relative).toArray();
    }

    public int size() {
        return colony1.size();
    }

    private double relative(double value) {
        int max = (int) (heightMax - 2 * border);

        return border + (max - value * max / maxEnergy);
    }

    private void add(Colony opponent, ArrayDeque<Double> array) {
        if (array.size() > HISTORY_MAX) {
            array.pop();
        }
        array.add((double) opponent.getEnergy());
    }
}
