package de.tud.ke.rulelearning.out;

import mulan.core.MulanException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.MultipleEvaluation;
import mulan.evaluation.measure.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MultipleEvaluationCsvPrinter extends CsvPrinter<MultipleEvaluationCsvPrinter.MultipleEvaluationResult> {

    public static class MultipleEvaluationResult {

        private final String approach;

        private final MultipleEvaluation evaluation;

        public MultipleEvaluationResult(final String approach,
                                        final MultipleEvaluation evaluation) {
            this.approach = approach;
            this.evaluation = evaluation;
        }

    }

    public class MultipleEvaluationWriter extends CsvWriter<MultipleEvaluationResult> {

        private void printHammingLoss(final CSVPrinter csvPrinter,
                                      final MultipleEvaluation evaluation,
                                      final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(HammingLoss.class);
            Double mean = printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
            printValue(csvPrinter, mean != null ? 1d - mean : null);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printSubsetAccuracy(final CSVPrinter csvPrinter,
                                         final MultipleEvaluation evaluation,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(SubsetAccuracy.class);
            printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printExampleBasedPrecision(final CSVPrinter csvPrinter,
                                                final MultipleEvaluation evaluation,
                                                final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(ExampleBasedPrecision.class);
            printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printExampleBasedRecall(final CSVPrinter csvPrinter,
                                             final MultipleEvaluation evaluation,
                                             final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(ExampleBasedRecall.class);
            printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printExampleBasedF1(final CSVPrinter csvPrinter,
                                         final MultipleEvaluation evaluation,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(ExampleBasedFMeasure.class);
            printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printMicroPrecision(final CSVPrinter csvPrinter,
                                         final MultipleEvaluation evaluation,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(MicroPrecision.class);
            printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printMicroRecall(final CSVPrinter csvPrinter,
                                      final MultipleEvaluation evaluation,
                                      final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(MicroRecall.class);
            printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printMicroF1(final CSVPrinter csvPrinter, final MultipleEvaluation evaluation,
                                  final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            Measure measure = measures.get(MicroFMeasure.class);
            printMean(csvPrinter, evaluation, measure);
            printStd(csvPrinter, evaluation, measure);
        }

        private void printMacroPrecision(final CSVPrinter csvPrinter,
                                         final MultipleEvaluation evaluation,
                                         final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            printMacroMeasure(csvPrinter, evaluation, measures, MacroPrecision.class);
        }

        private void printMacroRecall(final CSVPrinter csvPrinter,
                                      final MultipleEvaluation evaluation,
                                      final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            printMacroMeasure(csvPrinter, evaluation, measures, MacroRecall.class);
        }

        private void printMacroF1(final CSVPrinter csvPrinter,
                                  final MultipleEvaluation evaluation,
                                  final Map<Class<? extends Measure>, Measure> measures) throws
                IOException {
            printMacroMeasure(csvPrinter, evaluation, measures, MacroFMeasure.class);
        }

        private void printMacroMeasure(final CSVPrinter csvPrinter,
                                       final MultipleEvaluation evaluation,
                                       final Map<Class<? extends Measure>, Measure> measures,
                                       final Class<? extends MacroAverageMeasure> clazz) throws
                IOException {
            try {
                Measure measure = measures.get(clazz);
                printMean(csvPrinter, evaluation, measure);
                printStd(csvPrinter, evaluation, measure);

                for (int i = 0; i < dataSet.getNumLabels(); i++) {
                    Double mean = measure != null ? evaluation.getMean(measure.getName(), i) : null;
                    printValue(csvPrinter, mean);
                    Double std = measure != null ? evaluation.getStd(measure.getName(), i) : null;
                    printValue(csvPrinter, std);
                }
            } catch (MulanException e) {
                throw new IOException(e);
            }
        }

        private Double printMean(final CSVPrinter csvPrinter, final MultipleEvaluation evaluation,
                                 final Measure measure) throws
                IOException {
            Double value = measure != null ? evaluation.getMean(measure.getName()) : null;
            printValue(csvPrinter, value);
            return value;
        }

        private void printStd(final CSVPrinter csvPrinter, final MultipleEvaluation evaluation,
                              final Measure measure) throws IOException {
            printValue(csvPrinter, measure != null ? evaluation.getStd(measure.getName()) : null);
        }

        private void printValue(final CSVPrinter csvPrinter, final Double value) throws
                IOException {
            csvPrinter.print(value != null ? value : "n/a");
        }

        MultipleEvaluationWriter(final CSVPrinter csvPrinter) {
            super(csvPrinter);
        }

        @Override
        protected void writeLine(final CSVPrinter csvPrinter,
                                 final MultipleEvaluationResult data) throws
                IOException {
            MultipleEvaluation evaluation = data.evaluation;
            Map<Class<? extends Measure>, Measure> measures = evaluation.getEvaluations().get(0)
                    .getMeasures().stream()
                    .collect(HashMap::new, (map, measure) -> map.put(measure.getClass(), measure),
                            HashMap::putAll);
            csvPrinter.print(data.approach);
            printHammingLoss(csvPrinter, evaluation, measures);
            printSubsetAccuracy(csvPrinter, evaluation, measures);
            printExampleBasedPrecision(csvPrinter, evaluation, measures);
            printExampleBasedRecall(csvPrinter, evaluation, measures);
            printExampleBasedF1(csvPrinter, evaluation, measures);
            printMicroPrecision(csvPrinter, evaluation, measures);
            printMicroRecall(csvPrinter, evaluation, measures);
            printMicroF1(csvPrinter, evaluation, measures);
            printMacroPrecision(csvPrinter, evaluation, measures);
            printMacroRecall(csvPrinter, evaluation, measures);
            printMacroF1(csvPrinter, evaluation, measures);
        }

    }

    private static final String[] HEADER = {
            "Approach",
            "Avg. Hamm. Loss", "Std.-dev. Hamm. Loss",
            "Avg. Hamm. Acc.", "Std.-dev. Hamm. Acc.",
            "Avg. Subs. Acc.", "Std.-dev. Subs. Acc.",
            "Avg. Ex.-based Prec.", "Std.-dev. Ex.-based Prec.",
            "Avg. Ex.-based Rec.", "Std.-dev. Ex.-based Rec",
            "Avg. Ex.-based F1", "Std.-dev. Ex.-based F1",
            "Avg. Mi. Prec", "Std.-dev. Mi. Prec.",
            "Avg. Mi. Rec", "Std.-dev. Mic. Rec.",
            "Avg. Mi. F1", "Std.-dev. Mi. F1"
    };

    private final MultiLabelInstances dataSet;

    public MultipleEvaluationCsvPrinter(final String path, final String fileName,
                                        final MultiLabelInstances dataSet) {
        super(path, fileName);
        this.dataSet = dataSet;
    }

    public MultipleEvaluationCsvPrinter(final Path outputFilePath,
                                        final MultiLabelInstances dataSet) {
        super(outputFilePath);
        this.dataSet = dataSet;
    }

    @Override
    protected CsvWriter<MultipleEvaluationResult> createWriter(final CSVPrinter csvPrinter) {
        return new MultipleEvaluationWriter(csvPrinter);
    }

    @Override
    protected CSVFormat createFormat() {
        String[] labelNames = dataSet.getLabelNames();
        String[] header = new String[HEADER.length + 3 * (labelNames.length * 2 + 2)];
        int i = 0;

        while (i < HEADER.length) {
            header[i] = HEADER[i];
            i++;
        }

        header[i] = "Ma. Prec.";
        i++;
        header[i] = "Std.-dev. Ma. Prec.";
        i++;

        for (String labelName : labelNames) {
            header[i] = "Ma. Prec. " + labelName;
            i++;
            header[i] = "Std.-dev. Ma. Prec. " + labelName;
            i++;
        }

        header[i] = "Ma. Rec.";
        i++;
        header[i] = "Std.-dev. Ma. Rec.";
        i++;

        for (String labelName : labelNames) {
            header[i] = "Ma. Rec. " + labelName;
            i++;
            header[i] = "Std.-dev. Ma. Rec. " + labelName;
            i++;
        }

        header[i] = "Ma. F1";
        i++;
        header[i] = "Std.-dev. Ma. F1";
        i++;

        for (String labelName : labelNames) {
            header[i] = "Ma. F1 " + labelName;
            i++;
            header[i] = "Std.-dev. Ma. F1 " + labelName;
            i++;
        }

        return CSVFormat.DEFAULT.withHeader(header);
    }

}
