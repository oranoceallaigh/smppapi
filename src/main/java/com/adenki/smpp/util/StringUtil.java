package com.adenki.smpp.util;

/**
 * Some string utility methods.
 * @version $Id:$
 */
public final class StringUtil {

    private StringUtil() {
    }

    /**
     * Return a Java String with any non-ASCII characters escaped using "\u"
     * notation.
     * @param s The string to escape.
     * @return A new String with non-ASCII characters escaped.
     */
    public static final String escapeJava(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                b.append(c);
            } else {
                b.append(String.format("\\u%04x", (int) c));
            }
        }
    }
}
