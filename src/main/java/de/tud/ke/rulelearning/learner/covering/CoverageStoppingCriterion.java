package de.tud.ke.rulelearning.learner.covering;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.model.Rule;

public class CoverageStoppingCriterion implements StoppingCriterion {

    @Override
    public boolean isSatisfied(final Rule rule, final int labelIndex) {
        ConfusionMatrix confusionMatrix = rule.getHead().getLabelWiseConfusionMatrix(labelIndex);
        return (confusionMatrix.getNumberOfTruePositives() <= confusionMatrix.getNumberOfFalsePositives());
    }

}
