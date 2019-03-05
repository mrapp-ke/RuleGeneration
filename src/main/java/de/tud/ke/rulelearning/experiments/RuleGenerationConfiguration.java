package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.learner.covering.StoppingCriterion;
import de.tud.ke.rulelearning.learner.evaluation.DefaultAggregation;
import de.tud.ke.rulelearning.learner.evaluation.MicroAveraging;
import de.tud.ke.rulelearning.learner.evaluation.MultiLabelEvaluation;
import de.tud.ke.rulelearning.learner.evaluation.PartialPredictionStrategy;

import java.util.Objects;

public class RuleGenerationConfiguration extends BaseConfigurationProxy {

    public static class Builder extends Configuration.AbstractBuilder<Builder> {

        private final BaseConfiguration baseConfiguration;

        private double minPerformance = 0.0;

        private int minRules = 300000;

        private Covering.Type covering = null;

        private MultiLabelEvaluation coveringEvaluation = new MultiLabelEvaluation(new FMeasure(),
                new PartialPredictionStrategy(), new MicroAveraging(new DefaultAggregation()));

        private StoppingCriterion.Type stoppingCriterion = null;


        public Builder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
            this.baseConfiguration = baseConfiguration;
        }

        public Builder setMinPerformance(final double minPerformance) {
            this.minPerformance = minPerformance;
            return this;
        }

        public double getMinPerformance() {
            return minPerformance;
        }

        public Builder setMinRules(final int minRules) {
            this.minRules = minRules;
            return this;
        }

        public int getMinRules() {
            return minRules;
        }

        public Covering.Type getCovering() {
            return covering;
        }

        public Builder setCovering(final Covering.Type covering) {
            this.covering = covering;
            return this;
        }

        public MultiLabelEvaluation getCoveringEvaluation() {
            return coveringEvaluation;
        }

        public Builder setCoveringEvaluation(final MultiLabelEvaluation coveringEvaluation) {
            this.coveringEvaluation = coveringEvaluation;
            return this;
        }

        public StoppingCriterion.Type getStoppingCriterion() {
            return stoppingCriterion;
        }

        public Builder setStoppingCriterion(final StoppingCriterion.Type stoppingCriterion) {
            this.stoppingCriterion = stoppingCriterion;
            return this;
        }

        public RuleGenerationConfiguration build() {
            return new RuleGenerationConfiguration(baseConfiguration, minPerformance, minRules, covering,
                    coveringEvaluation, stoppingCriterion);
        }

    }

    private final double minPerformance;

    private final int minRules;

    private final Covering.Type covering;

    private final MultiLabelEvaluation coveringEvaluation;

    private final StoppingCriterion.Type stoppingCriterion;

    private RuleGenerationConfiguration(final BaseConfiguration baseConfiguration, final double minPerformance,
                                        final int minRules, final Covering.Type covering,
                                        final MultiLabelEvaluation coveringEvaluation,
                                        final StoppingCriterion.Type stoppingCriterion) {
        super(baseConfiguration);
        this.minPerformance = minPerformance;
        this.minRules = minRules;
        this.covering = covering;
        this.coveringEvaluation = coveringEvaluation;
        this.stoppingCriterion = stoppingCriterion;
    }

    public double getMinPerformance() {
        return minPerformance;
    }

    public int getMinRules() {
        return minRules;
    }

    public Covering.Type getCovering() {
        return covering;
    }

    public MultiLabelEvaluation getCoveringEvaluation() {
        return coveringEvaluation;
    }

    public StoppingCriterion.Type getStoppingCriterion() {
        return stoppingCriterion;
    }

    @Override
    public String toString() {
        return getBaseConfiguration().toString() +
                "-min-performance " + minPerformance + "\n" +
                "-min-rules " + minRules + "\n" +
                "-covering " + (covering != null ? covering.getValue() : null) + "\n" +
                "-covering-heuristic " + getCoveringEvaluation().getEvaluationStrategy() + "_" +
                getCoveringEvaluation().getHeuristic() + "_" + getCoveringEvaluation().getAveragingStrategy() + "\n" +
                "-stopping-criterion " + getStoppingCriterion() + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minPerformance, minRules, covering, coveringEvaluation,
                stoppingCriterion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RuleGenerationConfiguration that = (RuleGenerationConfiguration) o;
        return Double.compare(that.minPerformance, minPerformance) == 0 &&
                that.minRules == minRules &&
                covering == that.covering &&
                Objects.equals(that.getCoveringEvaluation(), getCoveringEvaluation()) &&
                stoppingCriterion == that.stoppingCriterion;
    }

}
