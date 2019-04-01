package de.tud.ke.rulelearning.experiments;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Configuration implements BaseConfiguration {

    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> {

        protected final BaseConfiguration configuration;

        public AbstractBuilder(final BaseConfiguration configuration) {
            this.configuration = configuration;
        }

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public Path getArffFilePath() {
            return configuration.getArffFilePath();
        }

        public T setArffFile(final String path, final String... subDirs) {
            return setArffFile(path != null ? Paths.get(path, subDirs) : null);
        }

        public T setArffFile(final Path path) {
            configuration.setArffFilePath(path);
            return self();
        }

        public Path getXmlLabelsDefFilePath() {
            return configuration.getXmlLabelsDefFilePath();
        }

        public T setXmlLabelsDefFile(final String path, final String... subDirs) {
            return setXmlLabelsDefFile(path != null ? Paths.get(path, subDirs) : null);
        }

        public T setXmlLabelsDefFile(final Path path) {
            configuration.setXmlLabelsDefFilePath(path);
            return self();
        }

        public Path getTestArffFilePath() {
            return configuration.getTestArffFilePath();
        }

        public T setTestArffFile(final Path path) {
            configuration.setTestArffFilePath(path);
            return self();
        }

        public T setTestArffFile(final String path, final String... subDirs) {
            return setTestArffFile(path != null ? Paths.get(path, subDirs) : null);
        }

        public Path getOutputDirPath() {
            return configuration.getOutputDirPath();
        }

        public T setOutputDir(final Path path) {
            configuration.setOutputDirPath(path);
            return self();
        }

        public T setOutputDir(final String path) {
            return setOutputDir(path != null ? Paths.get(path) : null);
        }

        public Path getModelDirPath() {
            return configuration.getModelDirPath();
        }

        public T setModelDir(final Path path) {
            configuration.setModelDirPath(path);
            return self();
        }

        public T setModelDir(final String path, final String... subDirs) {
            return setModelDir(path != null ? Paths.get(path, subDirs) : null);
        }

        public boolean isCrossValidationUsed() {
            return configuration.isCrossValidationUsed();
        }

        public T setUseCrossValidation(final boolean useCrossValidation) {
            configuration.setUseCrossValidation(useCrossValidation);
            return self();
        }

        public int getCrossValidationFolds() {
            return configuration.getCrossValidationFolds();
        }

        public T setCrossValidationFolds(final int folds) {
            configuration.setCrossValidationFolds(folds);
            return self();
        }

        public boolean isPredictionCsvFileSaved() {
            return configuration.isPredictionCsvFileSaved();
        }

        public T setPredictionCsvFileSaved(final boolean savePredictionCsvFile) {
            configuration.setPredictionCsvFileSaved(savePredictionCsvFile);
            return self();
        }

    }

    public static class Builder extends AbstractBuilder<Builder> {

        public Builder() {
            super(new Configuration());
        }

        public BaseConfiguration build() {
            return configuration;
        }

    }

    private static final long serialVersionUID = 1L;

    private Path arffFilePath;

    private Path xmlLabelsDefFilePath;

    private Path testArffFilePath;

    private Path outputDirPath;

    private Path modelDirPath;

    private boolean useCrossValidation;

    private int crossValidationFolds;

    private boolean savePredictionCsvFile = false;

    @Override
    public Path getArffFilePath() {
        return arffFilePath;
    }

    @Override
    public void setArffFilePath(final Path path) {
        this.arffFilePath = path;
    }

    @Override
    public Path getXmlLabelsDefFilePath() {
        return xmlLabelsDefFilePath;
    }

    @Override
    public void setXmlLabelsDefFilePath(final Path path) {
        this.xmlLabelsDefFilePath = path;
    }

    @Override
    public Path getTestArffFilePath() {
        return testArffFilePath;
    }

    @Override
    public void setTestArffFilePath(final Path path) {
        this.testArffFilePath = path;
    }

    @Override
    public Path getOutputDirPath() {
        return outputDirPath;
    }

    @Override
    public void setOutputDirPath(final Path path) {
        this.outputDirPath = path;
    }

    @Override
    public Path getModelDirPath() {
        return modelDirPath;
    }

    @Override
    public void setModelDirPath(final Path path) {
        this.modelDirPath = path;
    }

    @Override
    public boolean isCrossValidationUsed() {
        return useCrossValidation;
    }

    @Override
    public void setUseCrossValidation(final boolean useCrossValidation) {
        this.useCrossValidation = useCrossValidation;
    }

    @Override
    public int getCrossValidationFolds() {
        return crossValidationFolds;
    }

    @Override
    public void setCrossValidationFolds(final int folds) {
        this.crossValidationFolds = folds;
    }

    @Override
    public boolean isPredictionCsvFileSaved() {
        return savePredictionCsvFile;
    }

    @Override
    public void setPredictionCsvFileSaved(final boolean savePredictionCsvFile) {
        this.savePredictionCsvFile = savePredictionCsvFile;
    }

    @Override
    public String toString() {
        return "-arff " + arffFilePath + "\n" +
                "-xml " + xmlLabelsDefFilePath + "\n" +
                "-test-arff " + testArffFilePath + "\n" +
                "-output-dir " + outputDirPath + "\n" +
                "-model-file " + modelDirPath + "\n" +
                "-cross-validation " + useCrossValidation + "\n" +
                "-folds " + crossValidationFolds + "\n" +
                "-save-prediction-csv-file " + savePredictionCsvFile + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(arffFilePath, that.arffFilePath) &&
                Objects.equals(xmlLabelsDefFilePath, that.xmlLabelsDefFilePath) &&
                Objects.equals(testArffFilePath, that.testArffFilePath) &&
                Objects.equals(outputDirPath, that.outputDirPath) &&
                Objects.equals(modelDirPath, that.modelDirPath) &&
                Objects.equals(useCrossValidation, that.useCrossValidation) &&
                Objects.equals(crossValidationFolds, that.crossValidationFolds) &&
                Objects.equals(savePredictionCsvFile, that.savePredictionCsvFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arffFilePath, xmlLabelsDefFilePath, testArffFilePath, outputDirPath, modelDirPath,
                useCrossValidation, crossValidationFolds, savePredictionCsvFile);
    }

}
