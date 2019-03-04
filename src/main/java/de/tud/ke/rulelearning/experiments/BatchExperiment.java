package de.tud.ke.rulelearning.experiments;

import de.tud.ke.rulelearning.experiments.AbstractSingleExperiment.SharedData;
import de.tud.ke.rulelearning.out.CsvPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class BatchExperiment<ConfigType extends BaseConfiguration> extends AbstractExperiment<ConfigType> {

    private static final Logger LOG = LoggerFactory.getLogger(BatchExperiment.class);

    private final List<Experiment<?>> experiments = new ArrayList<>();

    private final SharedData sharedData;

    public BatchExperiment(final ConfigurationFactory<ConfigType> configFactory, final String[] args) {
        super(configFactory, args);
        this.sharedData = new SharedData();
    }

    public BatchExperiment(final ConfigType config) {
        super(config);
        this.sharedData = new SharedData();
    }

    public void addExperiment(final Function<SharedData, Experiment<?>> factory) {
        this.experiments.add(factory.apply(sharedData));
    }

    public List<Experiment<?>> getExperiments() {
        return Collections.unmodifiableList(experiments);
    }

    @Override
    public void run() {
        deleteOutputFiles();

        for (int i = 0; i < experiments.size(); i++) {
            Experiment<?> experiment = experiments.get(i);
            LOG.info("Running experiment {} out of {}", i + 1, experiments.size());
            experiment.run();
        }

        sharedData.evaluationCsvPrinters.values().forEach(CsvPrinter::close);
        sharedData.multipleEvaluationCsvPrinters.values().forEach(CsvPrinter::close);
        sharedData.ruleStatsCsvPrinters.values().forEach(CsvPrinter::close);
        sharedData.multipleRuleStatsCsvPrinters.values().forEach(CsvPrinter::close);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

}
