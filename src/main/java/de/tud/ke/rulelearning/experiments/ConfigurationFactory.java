package de.tud.ke.rulelearning.experiments;

public interface ConfigurationFactory<ConfigType extends BaseConfiguration> {

    ConfigType create(BaseConfiguration baseConfiguration, String[] args);

}
