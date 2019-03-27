package de.tud.ke.rulelearning.heuristics;

import de.tud.ke.rulelearning.model.Measurable;

import java.util.Comparator;

public interface TieBreaker<T extends Measurable> extends Comparator<T> {

}
