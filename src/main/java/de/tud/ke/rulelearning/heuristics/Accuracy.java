package de.tud.ke.rulelearning.heuristics;

public class Accuracy extends Heuristic {

    @Override
    public double evaluateConfusionMatrix(final ConfusionMatrix confusionMatrix) {
        double examples = confusionMatrix.getNumberOfExamples();
        return examples != 0 ? confusionMatrix.getNumberOfCorrectlyClassified() / examples : 0;
    }

    @Override
    public String toString() {
        return "accuracy";
    }

}
