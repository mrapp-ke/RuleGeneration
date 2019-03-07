package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.covering.Covering;
import de.tud.ke.rulelearning.learner.covering.StoppingCriterion;

import java.util.Objects;

public class RuleGenerationConfiguration extends RuleLearnerConfiguration {

    public static class Builder extends RuleLearnerConfiguration.AbstractBuilder<Builder> {

        private final BaseConfiguration baseConfiguration;

        private int minRules = 300000;

        private Covering.Type covering = null;

        private Heuristic coveringHeuristic = new FMeasure();

        private StoppingCriterion.Type stoppingCriterion = null;


        public Builder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
            this.baseConfiguration = baseConfiguration;
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

        public Heuristic getCoveringHeuristic() {
            return coveringHeuristic;
        }

        public Builder setCoveringHeuristic(final Heuristic coveringHeuristic) {
            this.coveringHeuristic = coveringHeuristic;
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
            return new RuleGenerationConfiguration(baseConfiguration, isRuleCsvFileSaved(), minRules, covering,
                    coveringHeuristic, stoppingCriterion);
        }

    }

    private final int minRules;

    private final Covering.Type covering;

    private final Heuristic coveringHeuristic;

    private final StoppingCriterion.Type stoppingCriterion;

    private RuleGenerationConfiguration(final BaseConfiguration baseConfiguration, final boolean saveRuleCsvFile,
                                        final int minRules, final Covering.Type covering,
                                        final Heuristic coveringHeuristic,
                                        final StoppingCriterion.Type stoppingCriterion) {
        super(baseConfiguration, saveRuleCsvFile);
        this.minRules = minRules;
        this.covering = covering;
        this.coveringHeuristic = coveringHeuristic;
        this.stoppingCriterion = stoppingCriterion;
    }

    public int getMinRules() {
        return minRules;
    }

    public Covering.Type getCovering() {
        return covering;
    }

    public Heuristic getCoveringHeuristic() {
        return coveringHeuristic;
    }

    public StoppingCriterion.Type getStoppingCriterion() {
        return stoppingCriterion;
    }

    @Override
    public String toString() {
        return super.toString() +
                "-min-rules " + minRules + "\n" +
                "-covering " + (covering != null ? covering.getValue() : null) + "\n" +
                "-covering-heuristic " + coveringHeuristic + "\n" +
                "-stopping-criterion " + getStoppingCriterion() + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minRules, covering, coveringHeuristic, stoppingCriterion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RuleGenerationConfiguration that = (RuleGenerationConfiguration) o;
        return that.minRules == minRules &&
                covering == that.covering &&
                Objects.equals(that.getCoveringHeuristic(), getCoveringHeuristic()) &&
                stoppingCriterion == that.stoppingCriterion;
    }

}
