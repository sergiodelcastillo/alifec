package alifec.simulation.util;

import alifec.simulation.view.CompetitorView;

import java.util.Comparator;

/**
 * Created by Sergio Del Castillo on 18/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompetitorViewComparator  implements Comparator<CompetitorView>{
    @Override
    public int compare(CompetitorView o1, CompetitorView o2) {
        return Float.compare(o2.getModel().getAccumulated(), o1.getModel().getAccumulated());
    }
}
