package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractExperiment<ConfigType extends BaseConfiguration> implements
        Experiment<ConfigType> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractExperiment.class);

    private final ConfigType configuration;

    private BaseConfiguration createBaseConfiguration(final String[] args) {
        String arffFilePath = ConfigUtil.getMandatoryArgument("arff", args);
        String xmlLabelsDefFilePath = ConfigUtil
                .getOptionalArgument("xml", args, arffFilePath.replace(".arff", ".xml"));
        String testArffFilePath = ConfigUtil.getOptionalArgument("test-arff", args);
        String outputDirPath = ConfigUtil.getOptionalArgument("output-dir", args);
        String modelDirPath = ConfigUtil.getOptionalArgument("model-dir", args);
        boolean useCrossValidation = ConfigUtil.getBooleanArgument("cross-validation", args, false);
        int crossValidationFolds = ConfigUtil.getIntArgument("folds", args, 10);
        boolean savePredictionCsvFile = ConfigUtil.getBooleanArgument("save-prediction-csv-file", args, false);
        return new Configuration.Builder()
                .setArffFile(arffFilePath)
                .setXmlLabelsDefFile(xmlLabelsDefFilePath)
                .setTestArffFile(testArffFilePath)
                .setOutputDir(outputDirPath)
                .setModelDir(modelDirPath)
                .setUseCrossValidation(useCrossValidation)
                .setCrossValidationFolds(crossValidationFolds)
                .setPredictionCsvFileSaved(savePredictionCsvFile)
                .build();
    }

    public AbstractExperiment(final ConfigurationFactory<ConfigType> configFactory, final String[] args) {
        BaseConfiguration baseConfiguration = createBaseConfiguration(args);
        this.configuration = configFactory.create(baseConfiguration, args);
    }

    public AbstractExperiment(final ConfigType configuration) {
        this.configuration = configuration;
    }

    protected final void deleteOutputFiles() {
        try {
            Path outputDirPath = getConfiguration().getOutputDirPath();

            if (outputDirPath != null) {
                LOG.debug("Deleting old output files...");
                DirectoryStream.Filter<Path> filter = path -> !Files.isDirectory(path);

                for (Path path : Files.newDirectoryStream(outputDirPath, filter)) {
                    Files.delete(path);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to delete old output files", e);
        }
    }

    @Override
    public final ConfigType getConfiguration() {
        return configuration;
    }

}
