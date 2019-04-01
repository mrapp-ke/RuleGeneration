package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.heuristics.TieBreaker;

public interface Measurable {

    class Comparator<T extends Measurable> implements java.util.Comparator<T> {

        private final TieBreaker<T> tieBreaker;

        public Comparator() {
            this(null);
        }

        public Comparator(final TieBreaker<T> tieBreaker) {
            this.tieBreaker = tieBreaker;
        }

        @Override
        public int compare(T o1, T o2) {
            double h1 = o1.getHeuristicValue();
            double h2 = o2.getHeuristicValue();
            int comp = Double.compare(h1, h2);

            return comp != 0 || tieBreaker == null ? comp : tieBreaker.compare(o1, o2);
        }
    }


    double getHeuristicValue();

    void setHeuristicValue(double heuristicValue);

    ConfusionMatrix getConfusionMatrix();

    void setConfusionMatrix(ConfusionMatrix confusionMatrix);

}
