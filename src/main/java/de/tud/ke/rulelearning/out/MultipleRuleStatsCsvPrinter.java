package de.tud.ke.rulelearning.out;

import de.tud.ke.rulelearning.model.MultipleRuleStats;
import mulan.data.MultiLabelInstances;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.file.Path;

public class MultipleRuleStatsCsvPrinter extends CsvPrinter<MultipleRuleStatsCsvPrinter.MultipleRuleStatsResult> {

    public static class MultipleRuleStatsResult {

        private final String approach;

        private final MultipleRuleStats ruleStats;

        public MultipleRuleStatsResult(final String approach, final MultipleRuleStats ruleStats) {
            this.approach = approach;
            this.ruleStats = ruleStats;
        }

    }

    public class RuleStatsWriter extends CsvWriter<MultipleRuleStatsResult> {

        RuleStatsWriter(final CSVPrinter csvPrinter) {
            super(csvPrinter);
        }

        @Override
        protected void writeLine(final CSVPrinter csvPrinter, final MultipleRuleStatsResult data) throws IOException {
            csvPrinter.print(data.approach);
            csvPrinter.print(data.ruleStats.getAvgRuleCount());
            csvPrinter.print(data.ruleStats.getAvgMinBodySize());
            csvPrinter.print(data.ruleStats.getAvgMaxBodySize());
            csvPrinter.print(data.ruleStats.getAvgBodySize());
            csvPrinter.print(data.ruleStats.getAvgMinHeadSize());
            csvPrinter.print(data.ruleStats.getAvgMaxHeadSize());
            csvPrinter.print(data.ruleStats.getAvgHeadSize());
            csvPrinter.print(data.ruleStats.getAvgPositivePredictionRatio());
            csvPrinter.print(data.ruleStats.getAvgInstanceCount());
            csvPrinter.print(data.ruleStats.getAvgCoveredInstanceCount());
            csvPrinter.print(data.ruleStats.getAvgCoveredInstanceRatio());

            for (int labelIndex : dataSet.getLabelIndices()) {
                csvPrinter.print(data.ruleStats.getAvgLabelOccurrences(labelIndex));
                csvPrinter.print(data.ruleStats.getAvgCoveredLabelOccurrences(labelIndex));
                csvPrinter.print(data.ruleStats.getAvgCoveredLabelRatio(labelIndex));
                csvPrinter.print(data.ruleStats.getAvgPredictions(labelIndex));
                csvPrinter.print(data.ruleStats.getAvgPredictionBodySize(labelIndex));
                csvPrinter.print(data.ruleStats.getAvgPredictionHeadSize(labelIndex));
            }
        }

    }

    private static final String[] HEADER = {
            "Approach", "Avg. # rules", "Avg. min. body size", "Avg. max. body size", "Avg. body size",
            "Avg. min. head size", "Avg. max. head size", "Avg. head size", "Avg. % pos. predictions",
            "Avg. # instances", "Avg. # covered instances", "Avg. % covered instances"
    };

    private final MultiLabelInstances dataSet;

    public MultipleRuleStatsCsvPrinter(final String path, final String fileName, final MultiLabelInstances dataSet) {
        super(path, fileName);
        this.dataSet = dataSet;
    }

    public MultipleRuleStatsCsvPrinter(final Path outputFilePath, final MultiLabelInstances dataSet) {
        super(outputFilePath);
        this.dataSet = dataSet;
    }

    @Override
    protected CsvWriter<MultipleRuleStatsResult> createWriter(final CSVPrinter csvPrinter) {
        return new RuleStatsWriter(csvPrinter);
    }

    @Override
    protected CSVFormat createFormat() {
        String[] labelNames = dataSet.getLabelNames();
        String[] header = new String[HEADER.length + 6 * labelNames.length];
        int i = 0;

        while (i < HEADER.length) {
            header[i] = HEADER[i];
            i++;
        }

        for (String labelName : labelNames) {
            header[i] = "Avg. # occurrences " + labelName;
            i++;
            header[i] = "Avg. # covered occurrences " + labelName;
            i++;
            header[i] = "Avg. % covered occurrences " + labelName;
            i++;
            header[i] = "Avg. # predictions " + labelName;
            i++;
            header[i] = "Avg. prediction body size " + labelName;
            i++;
            header[i] = "Avg. prediction head size " + labelName;
            i++;
        }

        return CSVFormat.DEFAULT.withHeader(header);
    }

}
