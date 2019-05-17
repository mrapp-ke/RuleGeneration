package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.covering.Covering;

import java.util.Objects;

public class StoppingCriterionConfiguration extends RuleGenerationConfiguration {

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> extends
            RuleGenerationConfiguration.AbstractBuilder<T> {

        private Provider<Heuristic> stoppingCriterionHeuristic = null;

        private Provider<Double> stoppingCriterionThreshold = Provider.singleton(1d);

        public AbstractBuilder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
        }

        public Provider<Heuristic> getStoppingCriterionHeuristic() {
            return stoppingCriterionHeuristic;
        }

        public T setStoppingCriterionHeuristic(final Provider<Heuristic> stoppingCriterionHeuristic) {
            this.stoppingCriterionHeuristic = stoppingCriterionHeuristic;
            return self();
        }

        public T setStoppingCriterionThreshold(final Provider<Double> stoppingCriterionThreshold) {
            this.stoppingCriterionThreshold = stoppingCriterionThreshold;
            return self();
        }

        public Provider<Double> getStoppingCriterionThreshold() {
            return stoppingCriterionThreshold;
        }

    }

    public static class Builder extends AbstractBuilder<Builder> {


        public Builder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
        }

        public StoppingCriterionConfiguration build() {
            return new StoppingCriterionConfiguration(getBaseConfiguration(), isRuleCsvFileSaved(),
                    getMinRules(), getCovering(), getCoveringHeuristic(), getStoppingCriterionHeuristic(),
                    getStoppingCriterionThreshold());
        }

    }

    private static final long serialVersionUID = 1L;

    private final Provider<Heuristic> stoppingCriterionHeuristic;

    private final Provider<Double> stoppingCriterionThreshold;

    private StoppingCriterionConfiguration(final BaseConfiguration baseConfiguration, final boolean saveRuleCsvFile,
                                           final int minRules, final Covering.Type covering,
                                           final Provider<Heuristic> coveringHeuristic,
                                           final Provider<Heuristic> stoppingCriterionHeuristic,
                                           final Provider<Double> stoppingCriterionThreshold) {
        super(baseConfiguration, saveRuleCsvFile, minRules, covering, coveringHeuristic);
        this.stoppingCriterionHeuristic = stoppingCriterionHeuristic;
        this.stoppingCriterionThreshold = stoppingCriterionThreshold;
    }

    public Provider<Heuristic> getStoppingCriterionHeuristic() {
        return stoppingCriterionHeuristic;
    }

    public Provider<Double> getStoppingCriterionThreshold() {
        return stoppingCriterionThreshold;
    }

    @Override
    public String toString() {
        return super.toString() +
                "-stopping-criterion-heuristic " + stoppingCriterionHeuristic + "\n" +
                "-stopping-criterion-threshold " + stoppingCriterionThreshold + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stoppingCriterionThreshold, stoppingCriterionHeuristic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StoppingCriterionConfiguration that = (StoppingCriterionConfiguration) o;
        return that.stoppingCriterionThreshold == stoppingCriterionThreshold &&
                Objects.equals(that.getStoppingCriterionHeuristic(), getStoppingCriterionHeuristic());
    }

}
