package de.tud.ke.rulelearning.learner;

import de.tud.ke.rulelearning.experiments.BaseConfiguration;
import de.tud.ke.rulelearning.model.DataSet;
import de.tud.ke.rulelearning.model.MultiplePredictionStats;
import de.tud.ke.rulelearning.model.PredictionStats;
import mulan.classifier.MultiLabelLearner;
import mulan.classifier.MultiLabelLearnerBase;
import mulan.classifier.MultiLabelOutput;
import mulan.data.MultiLabelInstances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instance;

import java.nio.file.Path;

public abstract class AbstractMultiLabelLearner<ConfigType extends BaseConfiguration, ModelType, StatsType> extends
        MultiLabelLearnerBase {

    public interface Callback<ModelType, StatsType> {

        void onModelBuilt(DataSet trainingData, int fold,
                          AbstractMultiLabelLearner<?, ModelType, StatsType> learner, ModelType model,
                          StatsType stats);

        void onModelFinalized(DataSet trainingData, int fold,
                              AbstractMultiLabelLearner<?, ModelType, StatsType> learner, ModelType model,
                              StatsType stats);

    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMultiLabelLearner.class);

    private final String name;

    private final ConfigType configuration;

    private final MultiplePredictionStats predictionStats;

    private Callback<ModelType, StatsType> callback = null;

    private int currentFold = 0;

    private DataSet trainingDataSet = null;

    private StatsType modelStats = null;

    private ModelType model;

    protected ModelType loadModel(final Path saveFilePath) {
        return null;
    }

    protected void saveModel(final Path saveFilePath, final ModelType ruleSet) {

    }

    public AbstractMultiLabelLearner(final String name, final ConfigType configuration,
                                     final MultiplePredictionStats predictionStats) {
        this.name = name;
        this.configuration = configuration;
        this.predictionStats = predictionStats;
    }

    protected abstract ModelType buildModel(final DataSet trainingDataSet) throws
            Exception;

    protected ModelType postProcessModel(final DataSet trainingDataSet, final ModelType model) {
        return model;
    }

    protected StatsType createModelStats(final DataSet trainingDataSet, final ModelType model) {
        return null;
    }

    protected ModelType finalizeModel(final DataSet trainingDataSet, final ModelType model)
            throws Exception {
        return model;
    }

    protected abstract PredictionStats makePrediction(final DataSet trainingDataSet,
                                                      final ModelType model, final Instance instance) throws Exception;

    protected abstract AbstractMultiLabelLearner<ConfigType, ModelType, StatsType> copy();

    public void setCallback(final Callback<ModelType, StatsType> callback) {
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public String getModelName() {
        return getName();
    }

    public ConfigType getConfiguration() {
        return configuration;
    }

    public int getCurrentFold() {
        return currentFold;
    }

    public MultiplePredictionStats getPredictionStats() {
        return predictionStats;
    }

    public StatsType getModelStats() {
        return modelStats;
    }

    public ModelType getModel() {
        return model;
    }

    protected DataSet createTrainingDataSet(final MultiLabelInstances multiLabelInstances) {
        return new DataSet(multiLabelInstances);
    }

    @Override
    protected void buildInternal(final MultiLabelInstances multiLabelInstances) throws Exception {
        this.trainingDataSet = createTrainingDataSet(multiLabelInstances);
        Path modelDirPath = configuration.getModelDirPath();
        boolean loadedFromSaveFile = true;

        if (modelDirPath != null) {
            model = loadModel(modelDirPath);
        }

        if (model == null) {
            loadedFromSaveFile = false;
            LOG.info("Training model using learner {}...", getClass().getSimpleName());
            this.model = buildModel(trainingDataSet);
            LOG.info("Post-processing model...");
            this.model = postProcessModel(trainingDataSet, model);
        }

        this.modelStats = createModelStats(trainingDataSet, model);

        if (callback != null) {
            callback.onModelBuilt(trainingDataSet, currentFold, this, model, modelStats);
        }

        if (modelDirPath != null && !loadedFromSaveFile) {
            saveModel(modelDirPath, model);
        }

        LOG.info("\nSuccessfully built model");

        LOG.info("Finalizing model...");
        this.model = finalizeModel(trainingDataSet, model);
        this.modelStats = createModelStats(trainingDataSet, model);

        if (callback != null) {
            callback.onModelFinalized(trainingDataSet, currentFold, this, model, modelStats);
        }
    }

    @Override
    protected MultiLabelOutput makePredictionInternal(final Instance instance) throws Exception {
        PredictionStats predictionStats = makePrediction(trainingDataSet, model, instance);
        this.predictionStats.addPredictionStats(predictionStats);
        return predictionStats.getPrediction();
    }

    @Override
    public MultiLabelLearner makeCopy() {
        currentFold++;
        AbstractMultiLabelLearner<ConfigType, ModelType, StatsType> copy = copy();
        copy.currentFold = currentFold;
        copy.callback = callback;
        return copy;
    }

}
