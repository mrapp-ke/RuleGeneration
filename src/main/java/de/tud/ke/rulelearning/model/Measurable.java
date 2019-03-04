package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;

public interface Measurable {

    double getHeuristicValue();

    void setHeuristicValue(double heuristicValue);

    ConfusionMatrix getConfusionMatrix();

    void setConfusionMatrix(ConfusionMatrix confusionMatrix);


}
