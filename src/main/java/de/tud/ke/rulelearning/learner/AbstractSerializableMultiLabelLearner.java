package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.BaseConfiguration;
import de.tud.ke.rulelearning.model.MultiplePredictionStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractSerializableMultiLabelLearner<ConfigType extends BaseConfiguration, ModelType extends Serializable, StatsType>
        extends AbstractMultiLabelLearner<ConfigType, ModelType, StatsType> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSerializableMultiLabelLearner.class);

    public AbstractSerializableMultiLabelLearner(final String name, final ConfigType configuration,
                                                 final MultiplePredictionStats predictionStats) {
        super(name, configuration, predictionStats);
    }

    protected <T extends Serializable> T loadFromFile(final String path) throws Exception {
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(path))) {
            //noinspection unchecked
            return (T) stream.readObject();
        }
    }

    protected void saveToFile(final String path, final Serializable object) throws Exception {
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(path))) {
            stream.writeObject(object);
        }
    }

    protected String getModelSaveFilePath(final Path saveFilePath) {
        String directory = saveFilePath.toAbsolutePath().toString();
        String fileName = getModelName();

        if (getConfiguration().isCrossValidationUsed()) {
            fileName = fileName + "_fold-" + getCurrentFold() + "-" + getConfiguration().getCrossValidationFolds();
        }

        return Paths.get(directory, fileName + ".model").toAbsolutePath().toString();
    }

    @Override
    protected ModelType loadModel(final Path saveFilePath) {
        String filePath = getModelSaveFilePath(saveFilePath);
        LOG.info("Loading model from file {}...", filePath);
        ModelType model = null;

        try {
            model = loadFromFile(filePath);
        } catch (Exception e) {
            LOG.error("Failed to load model from file {}", filePath);
        }

        return model;
    }

    @Override
    protected void saveModel(final Path saveFilePath, final ModelType model) {
        String filePath = getModelSaveFilePath(saveFilePath);

        try {
            saveToFile(filePath, model);
            LOG.info("Successfully saved model to file {}", filePath);
        } catch (Exception e) {
            LOG.error("Failed to save model to file {}", filePath, e);
        }
    }

}
