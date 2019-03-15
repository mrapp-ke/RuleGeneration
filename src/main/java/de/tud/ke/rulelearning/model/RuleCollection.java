package de.tud.ke.rulelearning.model;

import weka.core.Instance;

import java.io.Serializable;
import java.util.Collection;

public interface RuleCollection extends Collection<Rule>, Serializable {

    RuleCollection getCoveringRules(Instance instance);

}
