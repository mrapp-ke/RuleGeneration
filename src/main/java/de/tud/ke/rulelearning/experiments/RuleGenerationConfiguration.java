package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.heuristics.FMeasure;
import de.tud.ke.rulelearning.heuristics.Heuristic;
import de.tud.ke.rulelearning.learner.covering.Covering;

import java.util.Objects;

public class RuleGenerationConfiguration extends RuleLearnerConfiguration {

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> extends
            RuleLearnerConfiguration.AbstractBuilder<T> {

        private int minRules = 300000;

        private Covering.Type covering = null;

        private Heuristic coveringHeuristic = new FMeasure();

        public AbstractBuilder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
        }

        public T setMinRules(final int minRules) {
            this.minRules = minRules;
            return self();
        }

        public int getMinRules() {
            return minRules;
        }

        public Covering.Type getCovering() {
            return covering;
        }

        public T setCovering(final Covering.Type covering) {
            this.covering = covering;
            return self();
        }

        public Heuristic getCoveringHeuristic() {
            return coveringHeuristic;
        }

        public T setCoveringHeuristic(final Heuristic coveringHeuristic) {
            this.coveringHeuristic = coveringHeuristic;
            return self();
        }

    }


    public static class Builder extends AbstractBuilder<Builder> {

        public Builder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
        }

        public RuleGenerationConfiguration build() {
            return new RuleGenerationConfiguration(getBaseConfiguration(), isRuleCsvFileSaved(),
                    getMinRules(), getCovering(), getCoveringHeuristic());
        }

    }

    private static final long serialVersionUID = 1L;

    private final int minRules;

    private final Covering.Type covering;

    private final Heuristic coveringHeuristic;

    protected RuleGenerationConfiguration(final BaseConfiguration baseConfiguration, final boolean saveRuleCsvFile,
                                          final int minRules, final Covering.Type covering,
                                          final Heuristic coveringHeuristic) {
        super(baseConfiguration, saveRuleCsvFile);
        this.minRules = minRules;
        this.covering = covering;
        this.coveringHeuristic = coveringHeuristic;
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

    @Override
    public String toString() {
        return super.toString() +
                "-min-rules " + minRules + "\n" +
                "-covering " + (covering != null ? covering.getValue() : null) + "\n" +
                "-covering-heuristic " + coveringHeuristic + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minRules, covering, coveringHeuristic);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RuleGenerationConfiguration that = (RuleGenerationConfiguration) o;
        return that.minRules == minRules &&
                covering == that.covering &&
                Objects.equals(that.getCoveringHeuristic(), getCoveringHeuristic());
    }

}
