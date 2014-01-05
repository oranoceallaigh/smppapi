package com.adenki.smpp.util;

/**
 * Some string utility methods.
 * @version $Id:$
 * @since 1.0.0
 */
public final class StringUtil {

    private StringUtil() {
    }

    /**
     * Return a Java String with any non-ASCII characters escaped using "\\u"
     * notation.
     * @param s The string to escape.
     * @return A new String with non-ASCII characters escaped.
     */
    public static String escapeJava(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                b.append(c);
            } else {
                b.append(String.format("\\u%04x", (int) c));
            }
        }
        return b.toString();
    }

    /**
     * Return a Java String with any non-ASCII characters escaped using "\\u"
     * notation.
     * @param array An array of bytes to convert.
     * @return A new String with non-ASCII characters escaped.
     */
    public static String escapeJava(byte[] array) {
        StringBuilder b = new StringBuilder(array.length);
        for (byte el : array) {
            int i = (int) el & 0xff;
            if (Character.isLetterOrDigit(i)) {
                b.append((char) i);
            } else {
                b.append(String.format("\\u%04x", i));
            }
        }
        return b.toString();
    }
}
