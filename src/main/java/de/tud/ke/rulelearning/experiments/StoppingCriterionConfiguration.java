package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.covering.Covering;

import java.util.Objects;

public class StoppingCriterionConfiguration extends RuleLearnerConfiguration {

    public static class Builder extends AbstractBuilder<Builder> {

        private final BaseConfiguration baseConfiguration;

        private Covering.Type covering = null;

        private Heuristic coveringHeuristic = new FMeasure();

        private double stoppingCriterionThreshold = 1;

        public Builder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
            this.baseConfiguration = baseConfiguration;
        }

        public Covering.Type getCovering() {
            return covering;
        }

        public Builder setCovering(final Covering.Type covering) {
            this.covering = covering;
            return this;
        }

        public Heuristic getCoveringHeuristic() {
            return coveringHeuristic;
        }

        public Builder setCoveringHeuristic(final Heuristic coveringHeuristic) {
            this.coveringHeuristic = coveringHeuristic;
            return this;
        }

        public Builder setStoppingCriterionThreshold(final double stoppingCriterionThreshold) {
            this.stoppingCriterionThreshold = stoppingCriterionThreshold;
            return this;
        }

        public double getStoppingCriterionThreshold() {
            return stoppingCriterionThreshold;
        }

        public StoppingCriterionConfiguration build() {
            return new StoppingCriterionConfiguration(baseConfiguration, isRuleCsvFileSaved(),
                    covering, coveringHeuristic, stoppingCriterionThreshold);
        }

    }

    private final Covering.Type covering;

    private final Heuristic coveringHeuristic;

    private final double stoppingCriterionThreshold;

    private StoppingCriterionConfiguration(final BaseConfiguration baseConfiguration, final boolean saveRuleCsvFile,
                                           final Covering.Type covering,
                                           final Heuristic coveringHeuristic,
                                           final double stoppingCriterionThreshold) {
        super(baseConfiguration, saveRuleCsvFile);
        this.covering = covering;
        this.coveringHeuristic = coveringHeuristic;
        this.stoppingCriterionThreshold = stoppingCriterionThreshold;
    }

    public Covering.Type getCovering() {
        return covering;
    }

    public Heuristic getCoveringHeuristic() {
        return coveringHeuristic;
    }

    public double getStoppingCriterionThreshold() {
        return stoppingCriterionThreshold;
    }

    @Override
    public String toString() {
        return super.toString() +
                "-covering " + (covering != null ? covering.getValue() : null) + "\n" +
                "-covering-heuristic " + coveringHeuristic + "\n" +
                "-stopping-criterion-threshold " + stoppingCriterionThreshold + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stoppingCriterionThreshold, covering, coveringHeuristic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StoppingCriterionConfiguration that = (StoppingCriterionConfiguration) o;
        return that.stoppingCriterionThreshold == stoppingCriterionThreshold &&
                covering == that.covering &&
                Objects.equals(that.getCoveringHeuristic(), getCoveringHeuristic());
    }

}
