package de.tud.ke.rulelearning.experiments;

import java.io.Serializable;
import java.nio.file.Path;

public interface BaseConfiguration extends Serializable {

    Path getArffFilePath();

    void setArffFilePath(Path path);

    Path getXmlLabelsDefFilePath();

    void setXmlLabelsDefFilePath(Path path);

    Path getTestArffFilePath();

    void setTestArffFilePath(Path path);

    Path getOutputDirPath();

    void setOutputDirPath(Path path);

    Path getModelDirPath();

    void setModelDirPath(Path path);

    boolean isCrossValidationUsed();

    void setUseCrossValidation(boolean useCrossValidation);

    int getCrossValidationFolds();

    void setCrossValidationFolds(int folds);

    boolean isPredictionCsvFileSaved();

    void setPredictionCsvFileSaved(boolean savePredictionCsvFile);

    boolean isMinorityClassPredicted();

    void setMinorityClassPredicted(boolean predictMinorityClass);

}
