package de.tud.ke.rulelearning.heuristics;

public final class HeuristicFactory {

    private HeuristicFactory() {

    }

    public static Heuristic create(final String name) {
        if (name != null) {
            switch (name) {
                case "precision":
                    return new Precision();
                case "recall":
                    return new Recall();
                case "accuracy":
                    return new Accuracy();
                default:
                    if (name.startsWith("f") && name.endsWith("measure")) {
                        double beta = Double.valueOf(name.substring("f".length(), name.length() - "measure".length()));
                        return new FMeasure(beta);
                    } else if (name.endsWith("estimate")) {
                        double m = Double.valueOf(name.substring(0, name.length() - "estimate".length()));
                        return new MEstimate(m);
                    } else {
                        throw new IllegalArgumentException("Invalid heuristic: " + name);
                    }
            }
        }

        return null;
    }

}
