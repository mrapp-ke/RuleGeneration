package de.tud.ke.rulelearning.out;

import de.tud.ke.rulelearning.model.PredictionStats;
import de.tud.ke.rulelearning.model.PredictionStats.SingleLabelPredictionStats;
import mulan.data.MultiLabelInstances;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.file.Path;

public class PredictionStatsCsvPrinter extends CsvPrinter<PredictionStats> {

    public class PredictionStatsWriter extends CsvWriter<PredictionStats> {

        private String getType(final SingleLabelPredictionStats singleLabelPredictionStats) {
            if (singleLabelPredictionStats.isTruePositive()) {
                return "TP";
            } else if (singleLabelPredictionStats.isFalsePositive()) {
                return "FP";
            } else if (singleLabelPredictionStats.isTrueNegative()) {
                return "TN";
            } else {
                return "FN";
            }
        }

        private String formatInteger(final Integer value) {
            return value != null ? value.toString() : "n/a";
        }

        private String formatDouble(final Double value) {
            return value != null ? value.toString() : "n/a";
        }

        PredictionStatsWriter(final CSVPrinter csvPrinter) {
            super(csvPrinter);
        }

        @Override
        protected void writeLine(final CSVPrinter csvPrinter, final PredictionStats data) throws
                IOException {
            csvPrinter.print(formatInteger(data.getCoveringRuleCount()));
            csvPrinter.print(data.getConfusionMatrix().getNumberOfTruePositives());
            csvPrinter.print(data.getConfusionMatrix().getNumberOfFalsePositives());
            csvPrinter.print(data.getConfusionMatrix().getNumberOfTrueNegatives());
            csvPrinter.print(data.getConfusionMatrix().getNumberOfFalseNegatives());

            for (SingleLabelPredictionStats singleLabelPredictionStats : data) {
                csvPrinter.print(singleLabelPredictionStats.isRelevant());
                csvPrinter.print(singleLabelPredictionStats.isPredicted());
                csvPrinter.print(singleLabelPredictionStats.isPredictedCorrectly());
                csvPrinter.print(getType(singleLabelPredictionStats));
                csvPrinter.print(formatInteger(singleLabelPredictionStats.getPositivePredictionCount()));
                csvPrinter.print(formatDouble(singleLabelPredictionStats.getPositivePredictionRatio()));
                csvPrinter.print(formatInteger(singleLabelPredictionStats.getNegativePredictionCount()));
                csvPrinter.print(formatDouble(singleLabelPredictionStats.getNegativePredictionRatio()));
                csvPrinter.print(formatInteger(singleLabelPredictionStats.getNoPredictionCount()));
                csvPrinter.print(formatDouble(singleLabelPredictionStats.getNoPredictionRatio()));
            }
        }

    }

    private static final String[] HEADER = new String[]{"# covering rules", "TP", "FP", "TN", "FN"};

    private final MultiLabelInstances dataSet;

    public PredictionStatsCsvPrinter(final String path, final String fileName,
                                     final MultiLabelInstances dataSet) {
        super(path, fileName);
        this.dataSet = dataSet;
    }

    public PredictionStatsCsvPrinter(final Path outputFilePath,
                                     final MultiLabelInstances dataSet) {
        super(outputFilePath);
        this.dataSet = dataSet;
    }

    @Override
    protected CsvWriter<PredictionStats> createWriter(final CSVPrinter csvPrinter) {
        return new PredictionStatsWriter(csvPrinter);
    }

    @Override
    protected CSVFormat createFormat() {
        String[] labelNames = dataSet.getLabelNames();
        String[] header = new String[HEADER.length + labelNames.length * 10];
        int i = 0;

        while (i < HEADER.length) {
            header[i] = HEADER[i];
            i++;
        }

        for (String labelName : labelNames) {
            header[i] = labelName + ": relevant";
            i++;
            header[i] = labelName + ": predicted";
            i++;
            header[i] = labelName + ": correct";
            i++;
            header[i] = labelName + ": type";
            i++;
            header[i] = labelName + ": # pos. predictions";
            i++;
            header[i] = labelName + ": % pos. predictions";
            i++;
            header[i] = labelName + ": # neg. predictions";
            i++;
            header[i] = labelName + ": % neg. predictions";
            i++;
            header[i] = labelName + ": # no predictions";
            i++;
            header[i] = labelName + ": % no predictions";
            i++;
        }

        return CSVFormat.DEFAULT.withHeader(header);
    }

}
