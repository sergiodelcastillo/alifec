package alifec.simulation.sumulation.test;

import java.util.*;

/**
 * Created by Sergio Del Castillo on 09/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Test {
    public static void main(String[] args) {
        List<String> source = new ArrayList<>();
        source.add("bicho vs random in inclinedPlane");
        source.add("bicho vs random in lattice");
        source.add("bicho vs random in rings");
        source.add("Advanced vs random in rings");
        source.add("bicho vs advanced in TwoGaussians");

        Queue<String> queue = new LinkedList<>(source);
        while (!queue.isEmpty()){
            System.out.println(queue.poll());
        }

    }
}
