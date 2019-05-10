package de.tud.ke.rulelearning.out;

import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.measure.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class EvaluationCsvPrinter extends CsvPrinter<EvaluationCsvPrinter.EvaluationResult> {

    public static class EvaluationResult {

        private final String approach;

        private final Evaluation evaluation;

        public EvaluationResult(final String approach, final Evaluation evaluation) {
            this.approach = approach;
            this.evaluation = evaluation;
        }

    }

    public class EvaluationWriter extends CsvWriter<EvaluationResult> {

        private void printHammingLoss(final CSVPrinter csvPrinter,
                                      final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(HammingLoss.class);
            printMeasure(csvPrinter, measure);
            printValue(csvPrinter, measure != null ? 1d - measure.getValue() : null);
        }

        private void printSubsetAccuracy(final CSVPrinter csvPrinter,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(SubsetAccuracy.class);
            printMeasure(csvPrinter, measure);
        }

        private void printExampleBasedPrecision(final CSVPrinter csvPrinter,
                                                final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(ExampleBasedPrecision.class);
            printMeasure(csvPrinter, measure);
        }

        private void printExampleBasedRecall(final CSVPrinter csvPrinter,
                                             final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(ExampleBasedRecall.class);
            printMeasure(csvPrinter, measure);
        }

        private void printExampleBasedF1(final CSVPrinter csvPrinter,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(ExampleBasedFMeasure.class);
            printMeasure(csvPrinter, measure);
        }

        private void printMicroPrecision(final CSVPrinter csvPrinter,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(MicroPrecision.class);
            printMeasure(csvPrinter, measure);
        }

        private void printMicroRecall(final CSVPrinter csvPrinter,
                                      final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(MicroRecall.class);
            printMeasure(csvPrinter, measure);
        }

        private void printMicroF1(final CSVPrinter csvPrinter,
                                  final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(MicroFMeasure.class);
            printMeasure(csvPrinter, measure);
        }

        private void printMacroPrecision(final CSVPrinter csvPrinter,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            printMacroMeasure(csvPrinter, measures, MacroPrecision.class);
        }

        private void printMacroRecall(final CSVPrinter csvPrinter,
                                      final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            printMacroMeasure(csvPrinter, measures, MacroRecall.class);
        }

        private void printMacroF1(final CSVPrinter csvPrinter,
                                  final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            printMacroMeasure(csvPrinter, measures, MacroFMeasure.class);
        }

        private void printMacroMeasure(final CSVPrinter csvPrinter,
                                       final Map<Class<? extends Measure>, Measure> measures,
                                       final Class<? extends MacroAverageMeasure> clazz) throws
                IOException {
            Measure measure = measures.get(clazz);
            printMeasure(csvPrinter, measure);

            for (int i = 0; i < dataSet.getNumLabels(); i++) {
                Double value = measure != null ? ((MacroAverageMeasure) measure).getValue(i) : null;
                printValue(csvPrinter, value);
            }
        }

        private void printMeasure(final CSVPrinter csvPrinter, final Measure measure) throws
                IOException {
            printValue(csvPrinter, measure != null ? measure.getValue() : null);
        }

        private void printValue(final CSVPrinter csvPrinter, final Double value) throws
                IOException {
            csvPrinter.print(value != null ? value : "n/a");
        }

        EvaluationWriter(final CSVPrinter csvPrinter) {
            super(csvPrinter);
        }

        @Override
        protected void writeLine(final CSVPrinter csvPrinter, final EvaluationResult data) throws
                IOException {
            Map<Class<? extends Measure>, Measure> measures = data.evaluation.getMeasures().stream()
                    .collect(HashMap::new, (map, measure) -> map.put(measure.getClass(), measure),
                            HashMap::putAll);
            csvPrinter.print(data.approach);
            printHammingLoss(csvPrinter, measures);
            printSubsetAccuracy(csvPrinter, measures);
            printExampleBasedPrecision(csvPrinter, measures);
            printExampleBasedRecall(csvPrinter, measures);
            printExampleBasedF1(csvPrinter, measures);
            printMicroPrecision(csvPrinter, measures);
            printMicroRecall(csvPrinter, measures);
            printMicroF1(csvPrinter, measures);
            printMacroPrecision(csvPrinter, measures);
            printMacroRecall(csvPrinter, measures);
            printMacroF1(csvPrinter, measures);
        }

    }

    private static final String[] HEADER = {
            "Approach", "Hamm. Loss", "Hamm. Acc.", "Subs. Acc.", "Ex.-based Prec.",
            "Ex.-based Rec.", "Ex.-based F1", "Mi. Prec", "Mi. Rec", "Mi. F1"
    };

    private final MultiLabelInstances dataSet;

    public EvaluationCsvPrinter(final String path, final String fileName,
                                final MultiLabelInstances dataSet) {
        super(path, fileName);
        this.dataSet = dataSet;
    }

    public EvaluationCsvPrinter(final Path outputFilePath, final MultiLabelInstances dataSet) {
        super(outputFilePath);
        this.dataSet = dataSet;
    }

    @Override
    protected CsvWriter<EvaluationResult> createWriter(final CSVPrinter csvPrinter) {
        return new EvaluationWriter(csvPrinter);
    }

    @Override
    protected CSVFormat createFormat() {
        String[] labelNames = dataSet.getLabelNames();
        String[] header = new String[HEADER.length + 3 * (labelNames.length + 1)];
        int i = 0;

        while (i < HEADER.length) {
            header[i] = HEADER[i];
            i++;
        }

        header[i] = "Ma. Prec.";
        i++;

        for (String labelName : labelNames) {
            header[i] = "Ma. Prec. " + labelName;
            i++;
        }

        header[i] = "Ma. Rec.";
        i++;

        for (String labelName : labelNames) {
            header[i] = "Ma. Rec. " + labelName;
            i++;
        }

        header[i] = "Ma. F1";
        i++;

        for (String labelName : labelNames) {
            header[i] = "Ma. F1 " + labelName;
            i++;
        }

        return CSVFormat.DEFAULT.withHeader(header);
    }

}
