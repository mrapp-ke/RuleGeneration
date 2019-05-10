package de.tud.ke.rulelearning.out;

import de.tud.ke.rulelearning.heuristics.ConfusionMatrix;
import de.tud.ke.rulelearning.model.*;
import de.tud.ke.rulelearning.util.IteratorUtil;
import mulan.data.MultiLabelInstances;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleCsvPrinter extends CsvPrinter<Rule> {

    public class RuleWriter extends CsvWriter<Rule> {

        RuleWriter(final CSVPrinter csvPrinter) {
            super(csvPrinter);
        }

        private String formatCondition(final Condition condition) {
            if (condition instanceof NumericCondition) {
                NumericCondition numericCondition = (NumericCondition) condition;
                return numericCondition.getComparator().getSign() + " " + numericCondition.getValue();
            } else if (condition instanceof NominalCondition) {
                NominalCondition nominalCondition = (NominalCondition) condition;
                return "= " + nominalCondition.getValue();
            } else {
                return null;
            }
        }

        @Override
        protected void writeLine(final CSVPrinter csvPrinter, final Rule data) throws IOException {
            Body body = data.getBody();
            Head head = data.getHead();

            csvPrinter.print(body.size());
            csvPrinter.print(head.size());
            csvPrinter.print(data.getHeuristicValue());
            csvPrinter.print(data.getConfusionMatrix().getNumberOfTruePositives());
            csvPrinter.print(data.getConfusionMatrix().getNumberOfFalsePositives());
            csvPrinter.print(data.getConfusionMatrix().getNumberOfTrueNegatives());
            csvPrinter.print(data.getConfusionMatrix().getNumberOfFalseNegatives());

            Iterator<Attribute> attributeIterator = de.mrapp.util.IteratorUtil.INSTANCE.createFilteredIterator(
                    IteratorUtil.createForLoopIterator(dataSet.getDataSet(), Instances::numAttributes, Instances::attribute),
                    attribute -> !labelIndices.contains(attribute.index()));

            while (attributeIterator.hasNext()) {
                Attribute attribute = attributeIterator.next();
                Condition condition = body.getCondition(attribute.index());
                csvPrinter.print(formatCondition(condition));
            }

            for (int labelIndex : dataSet.getLabelIndices()) {
                Condition condition = head.getCondition(labelIndex);
                csvPrinter.print(formatCondition(condition));
                csvPrinter.print(head.getLabelWiseHeuristicValue(labelIndex));
                ConfusionMatrix labelWiseConfusionMatrix = head.getLabelWiseConfusionMatrix(labelIndex);
                csvPrinter.print(labelWiseConfusionMatrix != null ?
                        labelWiseConfusionMatrix.getNumberOfTruePositives() : 0d);
                csvPrinter.print(labelWiseConfusionMatrix != null ?
                        labelWiseConfusionMatrix.getNumberOfFalsePositives() : 0d);
                csvPrinter.print(labelWiseConfusionMatrix != null ?
                        labelWiseConfusionMatrix.getNumberOfTrueNegatives() : 0d);
                csvPrinter.print(labelWiseConfusionMatrix != null ?
                        labelWiseConfusionMatrix.getNumberOfFalseNegatives() : 0d);
            }

        }

    }

    private static final String[] HEADER = {
            "Body size", "Head size", "Score", "TP", "FP", "TN", "FN"
    };

    private final MultiLabelInstances dataSet;

    private final Set<Integer> labelIndices;

    public RuleCsvPrinter(final String path, final String fileName,
                          final MultiLabelInstances dataSet) {
        super(path, fileName);
        this.dataSet = dataSet;
        this.labelIndices = Arrays.stream(dataSet.getLabelIndices()).boxed().collect(Collectors.toSet());
    }

    public RuleCsvPrinter(final Path outputFilePath, final MultiLabelInstances dataSet) {
        super(outputFilePath);
        this.dataSet = dataSet;
        this.labelIndices = Arrays.stream(dataSet.getLabelIndices()).boxed().collect(Collectors.toSet());
    }

    @Override
    protected CsvWriter<Rule> createWriter(final CSVPrinter csvPrinter) {
        return new RuleWriter(csvPrinter);
    }

    @Override
    protected CSVFormat createFormat() {
        String[] labelNames = dataSet.getLabelNames();
        int featureCount = dataSet.getDataSet().numAttributes() - labelNames.length;
        String[] header = new String[HEADER.length + featureCount + labelNames.length * 6];
        int i = 0;

        while (i < HEADER.length) {
            header[i] = HEADER[i];
            i++;
        }

        Iterator<Attribute> attributeIterator = de.mrapp.util.IteratorUtil.INSTANCE.createFilteredIterator(
                IteratorUtil.createForLoopIterator(dataSet.getDataSet(), Instances::numAttributes, Instances::attribute),
                attribute -> !labelIndices.contains(attribute.index()));

        while (attributeIterator.hasNext()) {
            Attribute attribute = attributeIterator.next();
            header[i] = attribute.name();
            i++;
        }

        for (String labelName : labelNames) {
            header[i] = labelName;
            i++;
            header[i] = labelName + ": Score";
            i++;
            header[i] = labelName + ": TP";
            i++;
            header[i] = labelName + ": FP";
            i++;
            header[i] = labelName + ": TN";
            i++;
            header[i] = labelName + ": FN";
            i++;
        }

        return CSVFormat.DEFAULT.withHeader(header);
    }

}
