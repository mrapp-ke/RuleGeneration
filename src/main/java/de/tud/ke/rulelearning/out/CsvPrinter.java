package de.tud.ke.rulelearning.out;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class CsvPrinter<T> implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(CsvPrinter.class);

    public static abstract class CsvWriter<T> {

        private CSVPrinter csvPrinter;

        public CsvWriter(final CSVPrinter csvPrinter) {
            this.csvPrinter = csvPrinter;
        }

        protected abstract void writeLine(final CSVPrinter csvPrinter, final T data) throws IOException;

        public void write(final T data) {
            if (isClosed()) {
                throw new IllegalStateException("Writer already closed");
            }

            try {
                writeLine(csvPrinter, data);
                csvPrinter.println();
                csvPrinter.flush();
            } catch (Exception e) {
                LOG.error("Failed to write line to CSV file", e);
                close();
            }
        }

        public void flush() {
            if (isClosed()) {
                throw new IllegalStateException("Writer already closed");
            }

            try {
                csvPrinter.flush();
            } catch (Exception e) {
                LOG.error("Failed to flush", e);
                close();
            }
        }

        public boolean isClosed() {
            return csvPrinter == null;
        }

        public void close() {
            if (!isClosed()) {
                try {
                    csvPrinter.close();
                } catch (IOException e) {
                    LOG.error("Failed to close CSV printer", e);
                } finally {
                    csvPrinter = null;
                }
            }
        }

    }

    private final Path outputFilePath;

    private CsvWriter<T> writer;

    private boolean isWriterClosed() {
        return writer == null || writer.isClosed();
    }

    private CsvWriter<T> openWriter() throws IOException {
        Writer fileWriter = new BufferedWriter(new FileWriter(outputFilePath.toFile()));
        CSVFormat csvFormat = createFormat();
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, csvFormat);
        return createWriter(csvPrinter);
    }

    public CsvPrinter(final String path, final String fileName) {
        this(Paths.get(path, fileName));
    }

    public CsvPrinter(final Path outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    protected abstract CsvWriter<T> createWriter(final CSVPrinter csvPrinter);

    protected abstract CSVFormat createFormat();

    public void print(final T data) throws IOException {
        if (isWriterClosed()) {
            writer = openWriter();
        }

        writer.write(data);
    }

    public void flush() throws IOException {
        if (isWriterClosed()) {
            writer = openWriter();
        }

        writer.flush();
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }

}
