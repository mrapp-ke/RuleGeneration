package de.tud.ke.rulelearning.util;

import de.mrapp.util.TextUtil;
import weka.core.Utils;

public final class ConfigUtil {

    private ConfigUtil() {

    }

    public static String getMandatoryArgument(final String argumentName, final String[] arguments) {
        String value = getOptionalArgument(argumentName, arguments);

        if (TextUtil.INSTANCE.isEmpty(value)) {
            throw new IllegalArgumentException("Missing mandatory argument \"" + argumentName + "\"");
        }

        return value;
    }

    public static String getOptionalArgument(final String argumentName, final String[] arguments) {
        return getOptionalArgument(argumentName, arguments, null);
    }

    public static String getOptionalArgument(final String argumentName, final String[] arguments,
                                             final String defaultValue) {
        try {
            String value = Utils.getOption(argumentName, arguments);
            return value.isEmpty() ? defaultValue : value;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve argument \"" + argumentName + "\"");
        }
    }

    public static boolean getBooleanArgument(final String argumentName, final String[] arguments,
                                             final boolean defaultValue) {
        String value = getOptionalArgument(argumentName, arguments);
        return value != null ? Boolean.valueOf(value) : defaultValue;
    }

    public static int getIntArgument(final String argumentName, final String[] arguments, final int defaultValue) {
        String value = getOptionalArgument(argumentName, arguments);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    public static double getDoubleArgument(final String argumentName, final String[] arguments, final double defaultValue) {
        String value = getOptionalArgument(argumentName, arguments);
        return value != null ? Double.valueOf(value) : defaultValue;
    }

}
