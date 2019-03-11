package de.tud.ke.rulelearning.learner.prediction;

import de.tud.ke.rulelearning.model.LabelStats;
import de.tud.ke.rulelearning.model.DataSet;
import mulan.classifier.MultiLabelOutput;
import weka.core.Instance;

public interface Predictor<ModelType> {

    MultiLabelOutput makePrediction(DataSet trainingDataSet, ModelType model,
                                    Instance instance, LabelStats labelStats);

}
