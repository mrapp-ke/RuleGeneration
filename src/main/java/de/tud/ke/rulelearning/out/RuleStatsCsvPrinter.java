package de.tud.ke.rulelearning.out;

import de.tud.ke.rulelearning.model.RuleStats;
import mulan.data.MultiLabelInstances;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.file.Path;

public class RuleStatsCsvPrinter extends CsvPrinter<RuleStatsCsvPrinter.RuleStatsResult> {

    public static class RuleStatsResult {

        private final String approach;

        private final RuleStats ruleStats;

        public RuleStatsResult(final String approach, final RuleStats ruleStats) {
            this.approach = approach;
            this.ruleStats = ruleStats;
        }

    }

    public class RuleStatsWriter extends CsvWriter<RuleStatsResult> {

        RuleStatsWriter(final CSVPrinter csvPrinter) {
            super(csvPrinter);
        }

        @Override
        protected void writeLine(final CSVPrinter csvPrinter, final RuleStatsResult data) throws
                IOException {
            csvPrinter.print(data.approach);
            csvPrinter.print(data.ruleStats.getRuleCount());
            csvPrinter.print(data.ruleStats.getMinBodySize());
            csvPrinter.print(data.ruleStats.getMaxBodySize());
            csvPrinter.print(data.ruleStats.getAvgBodySize());
            csvPrinter.print(data.ruleStats.getMinHeadSize());
            csvPrinter.print(data.ruleStats.getMaxHeadSize());
            csvPrinter.print(data.ruleStats.getAvgHeadSize());
            csvPrinter.print(data.ruleStats.getPositivePredictionRatio());
            csvPrinter.print(data.ruleStats.getInstanceCoveringStats().getTotalCount());
            csvPrinter.print(data.ruleStats.getInstanceCoveringStats().getCoveredCount());
            csvPrinter.print(data.ruleStats.getInstanceCoveringStats().getCoveredRatio());

            for (int labelIndex : dataSet.getLabelIndices()) {
                csvPrinter.print(data.ruleStats.getLabelCoveringStats(labelIndex).getTotalCount());
                csvPrinter.print(data.ruleStats.getLabelCoveringStats(labelIndex).getCoveredCount());
                csvPrinter.print(data.ruleStats.getLabelCoveringStats(labelIndex).getCoveredRatio());
                csvPrinter.print(data.ruleStats.getLabelPredictionStats(labelIndex).getTotalCount());
                csvPrinter.print(data.ruleStats.getLabelConditionStats(labelIndex).getCoveredRatio());
                csvPrinter.print(data.ruleStats.getLabelPredictionStats(labelIndex).getCoveredRatio());
            }
        }

    }

    private static final String[] HEADER = {
            "Approach", "# rules", "Min. body size", "Max. body size", "Avg. body size",
            "Min. head size", "Max. head size", "Avg. head size", "% pos. predictions",
            "# instances", "# covered instances", "% covered instances"
    };

    private final MultiLabelInstances dataSet;

    public RuleStatsCsvPrinter(final String path, final String fileName,
                               final MultiLabelInstances dataSet) {
        super(path, fileName);
        this.dataSet = dataSet;
    }

    public RuleStatsCsvPrinter(final Path outputFilePath, final MultiLabelInstances dataSet) {
        super(outputFilePath);
        this.dataSet = dataSet;
    }

    @Override
    protected CsvWriter<RuleStatsResult> createWriter(final CSVPrinter csvPrinter) {
        return new RuleStatsWriter(csvPrinter);
    }

    @Override
    protected CSVFormat createFormat() {
        String[] labelNames = dataSet.getLabelNames();
        String[] header = new String[HEADER.length + labelNames.length * 6];
        int i = 0;

        while (i < HEADER.length) {
            header[i] = HEADER[i];
            i++;
        }

        for (String labelName : labelNames) {
            header[i] = "# occurrences " + labelName;
            i++;
            header[i] = "# covered occurrences " + labelName;
            i++;
            header[i] = "% covered occurrences " + labelName;
            i++;
            header[i] = "# predictions " + labelName;
            i++;
            header[i] = "Avg. prediction body size " + labelName;
            i++;
            header[i] = "Avg. prediction head size " + labelName;
            i++;
        }

        return CSVFormat.DEFAULT.withHeader(header);
    }

}
