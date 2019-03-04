package de.tud.ke.rulelearning.experiments;

public interface Experiment<ConfigType extends BaseConfiguration> extends Runnable {

    ConfigType getConfiguration();

    String getName();

}
