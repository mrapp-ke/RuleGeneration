package de.tud.ke.rulelearning;

import de.tud.ke.rulelearning.util.ConfigUtil;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.nio.file.Paths;
import java.util.Random;

public class MainCVDatasetGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(MainCVDatasetGenerator.class);

    private static void writeTrainingInstances(final Instances instances, final String path, final String dataSetName,
                                               final int fold) {
        writeInstances(instances, path, dataSetName + "-train", fold);
    }

    private static void writeTestInstances(final Instances instances, final String path, final String dataSetName,
                                           final int fold) {
        writeInstances(instances, path, dataSetName + "-test", fold);
    }

    private static void writeInstances(final Instances instances, final String path, final String dataSetName,
                                       final int fold) {
        writeInstances(instances, path, dataSetName + "_fold_" + (fold + 1) + ".arff");
    }

    private static void writeInstances(final Instances instances, final String path, final String fileName) {
        String absolutePath = Paths.get(path, fileName).toAbsolutePath().toString();

        try {
            ConverterUtils.DataSink.write(absolutePath, instances);
            LOG.info("Successfully wrote data set to file \"" + absolutePath + "\"");
        } catch (Exception e) {
            LOG.error("Failed to save data set to file \"" + absolutePath + "\"", e);
        }
    }

    public static void main(String... args) throws InvalidDataFormatException {
        String arffFilePath = ConfigUtil.getMandatoryArgument("arff", args);
        String arffFileName = Paths.get(arffFilePath).getFileName().toString();
        String dataSetName = arffFileName.toLowerCase().endsWith(".arff") ? arffFileName
                .substring(0, arffFileName.length() - ".arff".length()) : arffFileName;
        String xmlLabelsDefFilePath = ConfigUtil.getOptionalArgument("xml", args, arffFilePath.replace(".arff", ".xml"));
        int folds = ConfigUtil.getIntArgument("folds", args, 10);
        String outputDirPath = ConfigUtil.getMandatoryArgument("output-dir", args);
        int seed = ConfigUtil.getIntArgument("seed", args, 1);

        MultiLabelInstances multiLabelInstances = new MultiLabelInstances(arffFilePath, xmlLabelsDefFilePath);
        Instances workingSet = new Instances(multiLabelInstances.getDataSet());
        workingSet.randomize(new Random((long) seed));

        for (int i = 0; i < folds; ++i) {
            LOG.info("Fold " + (i + 1) + "/" + folds);

            Instances train = workingSet.trainCV(folds, i);
            Instances test = workingSet.testCV(folds, i);

            writeTrainingInstances(train, outputDirPath, dataSetName, i);
            writeTestInstances(test, outputDirPath, dataSetName, i);
        }
    }

}
