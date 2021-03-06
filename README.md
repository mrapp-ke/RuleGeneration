## README

This project allows to generate a large number of (single-label head) rules for multi-label classification from random forests. The program consists of two steps:

- Generate the rules
- Create a theory by selecting a subset of rules according to a simple covering algorithm and a heuristic

The generated rules are written to disk and loaded from there in subsequent runs of the program in order to avoid generating the rules all over again.

Additionally, various statistics about the generated rules and the selected theory, as well as the evaluation results on the test data, are written to CSV files.

Currently the following main classes are provided:

- `MainCoveringMEstimate`:  Uses different configurations of the m-Estimate for selecting the rules that are part of the final theory
- `MainCoveringFMeasure`: Uses different configurations of the F-Measure for selecting the rules that are part of the final theory
- `MainStoppingCriterionMEstimate`: Uses different thresholds to filter the rules that have been selected by an experiment using the main class `MainCoveringMEstimate`.
- `MainStoppingCriterionFMeasure`: Uses different thresholds to filter the rules that have been selected by an `experiment using the main class `MainCoveringFMeasure`.
- `MainMEstimate`: Creates a single theory by selecting rules according to a the m-Estimate (as `MainCoveringMEstimate` does) and filtering them according to a threshold (as `MainStoppingCriterionMEstimate` does). Depending on the data set, the m-Estimate's beta parameter and the threshold are automatically set to values that have been identified to work best by using the main class `MainMEstimateParameterTuning` on validation sets.
- `MainMEstimateParameterTuning`: Tests different predefined parameter settings.
- `MainCVDatasetGenerator`: Converts a given data set into training-test-splits for performing a n-fold cross validation

Various command line parameters must be passed to these main classes in order to run the program:

| Parameter                | Optional? | Default    | Description                                                                                                                                                   |
|--------------------------|-----------|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| arff                     | No        | null       | The path of file that should be used as the training set (or cross validation set).                                                                           |
| test-arff                | Yes       | null       | The path of the file that should be used as the test set (if no cross validation is used).                                                                    |
| xml                      | No        | null       | The path of the XML file that contains information about the labels in the given arff file.                                                                   |
| output-dir               | Yes       | null       |  The path of the directory where the CSV output files should be stored.                                                                                       |
| model-dir                | Yes       | null       | The path of the directory where the generated rules should be stored.                                                                                         |
| cross-validation         | Yes       | false      | "true" if cross validation should be used, "false" otherwise.                                                                                                 |
| folds                    | Yes       | 10         | The number of cross validation folds to be used.                                                                                                              |
| save-prediction-csv-file | Yes       | false      | "true" if the predictions for the individual test instances should be written to a CSV file, "false" otherwise                                                |
| save-rule-csv-file       | Yes       | false      | "true" if the rules of the selected theory should be written to a CSV file, "false" otherwise                                                                 |
| min-rules                | Yes       | 300000     | The number of rules to be generated at least.                                                                                                                 |
| covering                 | Yes       | null       | The name of the covering algorithm to be used. Must be "label-wise" or "label-wise-no-revalidation".                                                          |
| covering-heuristic       | Yes       | f1-measure | The heuristic to be used by the covering algorithm. Must be "precision", "recall", "accuracy", "fx-measure", "x-estimate" (x must be replaced with a number). |
| predict-minority-class   | Yes       | true       | "true" if rules should predict the minority class of a label, "false" if they should always predict the presence of a label.                                  |

Additionally, the following command line parameters are available when using the main class `MainStoppingCriterionMEstimate`, `MainStoppingCriterionFMeasure`, `MainMEstimate` or `MainFMeasure`:

| Parameter                    | Optional? | Default | Description                                                                                                          |
|------------------------------|-----------|---------|----------------------------------------------------------------------------------------------------------------------|
| stopping-criterion-heuristic | Yes       | null    | The heuristic to be used to filter rules. If not specified, the value of the parameter "covering-heuristic" is used. |
| stopping-criterion-threshold | Yes       | 1       | The threshold to be used to filter rules.                                                                            |

The program is also available as a jar-file. It can for example be executed from the command line by specifying the main class and parameters to be used as follows:

```
java -Xmx8G -cp rule-generation.jar de.tud.ke.rulelearning.MainCoveringMEstimate -arff data/yeast.arff -xml data/yeast.xml -output-dir results/yeast/m-estimate/ -model-dir yeast/models/ -cross-validation true -folds 10 -min-rules 300000 -covering label-wise -stopping-criterion coverage
```
