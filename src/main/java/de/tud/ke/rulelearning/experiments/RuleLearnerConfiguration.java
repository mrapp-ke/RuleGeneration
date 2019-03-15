package de.tud.ke.rulelearning.experiments;

import java.util.Objects;

public class RuleLearnerConfiguration extends BaseConfigurationProxy {

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>>
            extends Configuration.AbstractBuilder<T> {

        private boolean saveRuleCsvFile = false;

        public AbstractBuilder(final BaseConfiguration configuration) {
            super(configuration);
        }

        public boolean isRuleCsvFileSaved() {
            return saveRuleCsvFile;
        }

        public T setRuleCsvFileSaved(final boolean saveRuleCsvFile) {
            this.saveRuleCsvFile = saveRuleCsvFile;
            return self();
        }

    }

    public static class Builder extends AbstractBuilder<Builder> {

        public Builder(final BaseConfiguration baseConfiguration) {
            super(baseConfiguration);
        }

        public RuleLearnerConfiguration build() {
            return new RuleLearnerConfiguration(configuration, isRuleCsvFileSaved());
        }

    }

    private final boolean saveRuleCsvFile;

    protected RuleLearnerConfiguration(final BaseConfiguration baseConfiguration, final boolean saveRuleCsvFile) {
        super(baseConfiguration);
        this.saveRuleCsvFile = saveRuleCsvFile;
    }

    public boolean isRuleCsvFileSaved() {
        return saveRuleCsvFile;
    }

    @Override
    public String toString() {
        return super.toString() +
                "-save-rule-csv-file " + saveRuleCsvFile + "\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), saveRuleCsvFile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RuleLearnerConfiguration that = (RuleLearnerConfiguration) o;
        return that.saveRuleCsvFile == saveRuleCsvFile;
    }

}
