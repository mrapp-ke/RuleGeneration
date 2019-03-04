package de.tud.ke.rulelearning.util;

public final class TextUtil {

    private TextUtil() {

    }

    public static boolean isEmpty(final CharSequence text) {
        return text == null || text.toString().isEmpty();
    }

    public static boolean isNotEmpty(final CharSequence text) {
        return !isEmpty(text);
    }

}
