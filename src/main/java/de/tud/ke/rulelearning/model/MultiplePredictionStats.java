package de.tud.ke.rulelearning.model;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiplePredictionStats implements Iterable<PredictionStats>, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<PredictionStats> predictionStats = new ArrayList<>();

    public void addPredictionStats(final PredictionStats stats) {
        predictionStats.add(stats);
    }

    public void clear() {
        predictionStats.clear();
    }

    @NotNull
    @Override
    public Iterator<PredictionStats> iterator() {
        return predictionStats.iterator();
    }

}
