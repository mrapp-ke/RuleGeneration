package de.tud.ke.rulelearning.model;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import mulan.classifier.MultiLabelOutput;
import mulan.evaluation.GroundTruth;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PredictionStats implements Iterable<PredictionStats.SingleLabelPredictionStats> {

    public class SingleLabelPredictionStats {

        private final boolean relevant;

        private final boolean predicted;

        private final Integer coveringRuleCount;

        private Integer positivePredictionCount;

        private Integer negativePredictionCount;

        SingleLabelPredictionStats(final PredictionStats predictionStats, final int i, final int labelIndex,
                                   final Collection<Rule> coveringRules) {
            this.relevant = predictionStats.getGroundTruth().getTrueLabels()[i];
            this.predicted = predictionStats.getPrediction().getBipartition()[i];
            this.coveringRuleCount = coveringRules != null ? coveringRules.size() : null;

            if (coveringRules != null) {
                positivePredictionCount = 0;
                negativePredictionCount = 0;

                for (Rule rule : coveringRules) {
                    Condition condition = rule.getHead().getCondition(labelIndex);

                    if (condition instanceof NominalCondition) {
                        if (((NominalCondition) condition).getValue().equals("1")) {
                            positivePredictionCount++;
                        } else {
                            negativePredictionCount++;
                        }
                    }
                }
            }
        }

        public boolean isRelevant() {
            return relevant;
        }

        public boolean isPredicted() {
            return predicted;
        }

        public boolean isPredictedCorrectly() {
            return relevant == predicted;
        }

        public boolean isTruePositive() {
            return isPredictedCorrectly() && relevant;
        }

        public boolean isTrueNegative() {
            return isPredictedCorrectly() && !relevant;
        }

        public boolean isFalsePositive() {
            return !isPredictedCorrectly() && predicted;
        }

        public boolean isFalseNegative() {
            return !isPredictedCorrectly() && !predicted;
        }

        public Integer getPositivePredictionCount() {
            return positivePredictionCount;
        }

        public Double getPositivePredictionRatio() {
            if (coveringRuleCount != null && positivePredictionCount != null) {
                return coveringRuleCount > 0 ? (double) positivePredictionCount / (double) coveringRuleCount : 0;
            }

            return null;
        }

        public Integer getNegativePredictionCount() {
            return negativePredictionCount;
        }

        public Double getNegativePredictionRatio() {
            if (coveringRuleCount != null && negativePredictionCount != null) {
                return coveringRuleCount > 0 ? (double) negativePredictionCount / (double) coveringRuleCount : 0;
            }

            return null;
        }

        public Integer getNoPredictionCount() {
            if (coveringRuleCount != null && positivePredictionCount != null && negativePredictionCount != null) {
                return coveringRuleCount - positivePredictionCount - negativePredictionCount;
            }

            return null;
        }

        public Double getNoPredictionRatio() {
            if (coveringRuleCount != null) {
                return coveringRuleCount > 0 ? (double) getNoPredictionCount() / (double) coveringRuleCount : 0;
            }

            return null;
        }

    }

    private final MultiLabelOutput prediction;

    private final GroundTruth groundTruth;

    private final Collection<Rule> coveringRules;

    private final List<SingleLabelPredictionStats> singleLabelPredictionStats = new LinkedList<>();

    private final ConfusionMatrix confusionMatrix;

    public PredictionStats(final DataSet dataSet,
                           final MultiLabelOutput prediction, final GroundTruth groundTruth,
                           final Collection<Rule> coveringRules) {
        this.prediction = prediction;
        this.groundTruth = groundTruth;
        this.coveringRules = coveringRules;
        this.confusionMatrix = new ConfusionMatrix();
        int[] labelIndices = dataSet.getDataSet().getLabelIndices();

        for (int i = 0; i < labelIndices.length; i++) {
            int labelIndex = labelIndices[i];
            SingleLabelPredictionStats stats = new SingleLabelPredictionStats(this, i, labelIndex, coveringRules);

            if (stats.isTruePositive()) {
                confusionMatrix.addTruePositives(1);
            } else if (stats.isFalsePositive()) {
                confusionMatrix.addFalsePositives(1);
            } else if (stats.isTrueNegative()) {
                confusionMatrix.addTrueNegatives(1);
            } else {
                confusionMatrix.addFalseNegatives(1);
            }

            singleLabelPredictionStats.add(stats);
        }
    }

    public MultiLabelOutput getPrediction() {
        return prediction;
    }

    public GroundTruth getGroundTruth() {
        return groundTruth;
    }

    public int getLabelCount() {
        return getGroundTruth().getTrueLabels().length;
    }

    public Integer getCoveringRuleCount() {
        return coveringRules != null ? coveringRules.size() : null;
    }

    public ConfusionMatrix getConfusionMatrix() {
        return confusionMatrix;
    }

    @NotNull
    @Override
    public Iterator<SingleLabelPredictionStats> iterator() {
        return singleLabelPredictionStats.iterator();
    }

}
