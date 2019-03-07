## README

This project allows to generate a large number of (single-label head) rules for multi-label classification from random forests. The program consists of two steps:

- Generate the rules
- Create a theory by selecting a subset of rules according to a simple covering algorithm and a heuristic

The generated rules are written to disk and loaded from there in subsequent runs of the program in order to avoid generating the rules all over again.

Additionally, various statistics about the generated rules and the selected theory, as well as the evaluation results on the test data, are written to CSV files.

Currently the following main classes are provided:

- `MainCoveringMEstimate`:  Uses different configurations of the m-Estimate for selecting the rules that are part of the final theory
- `MainCoveringFMeasure`: Uses different configurations of the F-Measure for selecting the rules that are part of the final theory

Various command line parameters must be passed to these main classes in order to run the program:

| Parameter          | Optional? | Default    | Description                                                                                                                                                   |
|--------------------|-----------|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| arff               | No        | null       | The path of file that should be used as the training set (or cross validation set).                                                                           |
| test-arff          | Yes       | null       | The path of the file that should be used as the test set (if no cross validation is used).                                                                    |
| xml                | No        | null       | The path of the XML file that contains information about the labels in the given arff file.                                                                   |
| output-dir         | Yes       | null       |  The path of the directory where the CSV output files should be stored.                                                                                       |
| model-dir          | Yes       | null       | The path of the directory where the generated rules should be stored.                                                                                         |
| cross-validation   | Yes       | false      | "true" if cross validation should be used, "false" otherwise.                                                                                                 |
| folds              | Yes       | 10         | The number of cross validation folds to be used.                                                                                                              |
| save-rule-csv-file | Yes       | false      | "true" if the rules of the selected theory should be written to a CSV file, "false" otherwise                                                                 |
| min-rules          | Yes       | 300000     | The number of rules to be generated at least.                                                                                                                 |
| covering           | Yes       | null       | The name of the covering algorithm to be used. Must be "label-wise" or "label-wise-no-revalidation".                                                          |
| covering-heuristic | Yes       | f1-measure | The heuristic to be used by the covering algorithm. Must be "precision", "recall", "accuracy", "fx-measure", "x-estimate" (x must be replaced with a number). |
| stopping-criterion | Yes       | null       | The stopping criterion to be used by the covering algorithm. Must be "coverage".                                                                              |

The program is also available as a jar-file. It can for example be executed from the command line as follows:

```
java -Xmx8G -cp rule-generation.jar de.tud.ke.rulelearning.MainCoveringMEstimate -arff data/yeast.arff -xml data/yeast.xml -output-dir results/yeast/m-estimate/ -model-dir yeast/models/ -cross-validation true -folds 10 -min-rules 300000 -covering label-wise -stopping-criterion coverage
```
