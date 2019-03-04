package de.tud.ke.rulelearning.experiments;

import java.nio.file.Path;
import java.util.Objects;

public class BaseConfigurationProxy implements BaseConfiguration {

    private final BaseConfiguration baseConfiguration;

    public BaseConfigurationProxy(final BaseConfiguration baseConfiguration) {
        this.baseConfiguration = baseConfiguration;
    }

    protected BaseConfiguration getBaseConfiguration() {
        return baseConfiguration;
    }

    @Override
    public final Path getArffFilePath() {
        return baseConfiguration.getArffFilePath();
    }

    @Override
    public final void setArffFilePath(final Path path) {
        baseConfiguration.setArffFilePath(path);
    }

    @Override
    public final Path getXmlLabelsDefFilePath() {
        return baseConfiguration.getXmlLabelsDefFilePath();
    }

    @Override
    public final void setXmlLabelsDefFilePath(final Path path) {
        baseConfiguration.setXmlLabelsDefFilePath(path);
    }

    @Override
    public final Path getTestArffFilePath() {
        return baseConfiguration.getTestArffFilePath();
    }

    @Override
    public final void setTestArffFilePath(final Path path) {
        baseConfiguration.setTestArffFilePath(path);
    }

    @Override
    public final Path getOutputDirPath() {
        return baseConfiguration.getOutputDirPath();
    }

    @Override
    public final void setOutputDirPath(final Path path) {
        baseConfiguration.setOutputDirPath(path);
    }

    @Override
    public final Path getModelDirPath() {
        return baseConfiguration.getModelDirPath();
    }

    @Override
    public final void setModelDirPath(final Path path) {
        baseConfiguration.setModelDirPath(path);
    }

    @Override
    public final boolean isCrossValidationUsed() {
        return baseConfiguration.isCrossValidationUsed();
    }

    @Override
    public final void setUseCrossValidation(final boolean useCrossValidation) {
        baseConfiguration.setUseCrossValidation(useCrossValidation);
    }

    @Override
    public final int getCrossValidationFolds() {
        return baseConfiguration.getCrossValidationFolds();
    }

    @Override
    public final void setCrossValidationFolds(final int folds) {
        baseConfiguration.setCrossValidationFolds(folds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseConfiguration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseConfigurationProxy that = (BaseConfigurationProxy) o;
        return Objects.equals(baseConfiguration, that.baseConfiguration);
    }

}
