package nl.siegmann.epublib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Various String utility functions.
 *
 * Most of the functions herein are re-implementations of the ones in apache
 * commons StringUtils. The reason for re-implementing this is that the
 * functions are fairly simple and using my own implementation saves the
 * inclusion of a 200Kb jar file.
 *
 * @author Paul Siegmann
 */
public final class StringUtil {
    /**
     * Changes a path containing '..', '.' and empty dirs into a path that
     * doesn't. X/foo/../Y is changed into 'X/Y', etc. Does not handle invalid
     * paths like "../".
     *
     * @param path
     * @return the normalized path
     */
    public static String collapsePathDots(String path) {
        String[] stringParts = path.split("/");
        List<String> parts = new ArrayList<>(Arrays.asList(stringParts));
        for (int i = 0; i < parts.size() - 1; i++) {
            String currentDir = parts.get(i);
            if (currentDir.length() == 0 || currentDir.equals(".")) {
                parts.remove(i);
                i--;
            } else if (currentDir.equals("..")) {
                parts.remove(i - 1);
                parts.remove(i - 1);
                i -= 2;
            }
        }
        StringBuilder result = new StringBuilder();
        if (path.startsWith("/")) {
            result.append('/');
        }
        for (int i = 0; i < parts.size(); i++) {
            result.append(parts.get(i));
            if (i < (parts.size() - 1)) {
                result.append('/');
            }
        }
        return result.toString();
    }

    /**
     * Whether the String is not null, not zero-length and does not contain of
     * only whitespace.
     *
     * @param text
     * @return Whether the String is not null, not zero-length and does not contain of
     */
    public static boolean isNotBlank(String text) {
        return !isBlank(text);
    }

    /**
     * Whether the String is null, zero-length and does contain only whitespace.
     *
     * @return Whether the String is null, zero-length and does contain only whitespace.
     */
    public static boolean isBlank(String text) {
        if (isEmpty(text)) {
            return true;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether the given string is null or zero-length.
     *
     * @param text the input for this method
     * @return Whether the given string is null or zero-length.
     */
    public static boolean isEmpty(String text) {
        return (text == null) || (text.length() == 0);
    }

    /**
     * Whether the given source string ends with the given suffix, ignoring
     * case.
     *
     * @param source
     * @param suffix
     * @return Whether the given source string ends with the given suffix, ignoring case.
     */
    public static boolean endsWithIgnoreCase(String source, String suffix) {
        if (isEmpty(suffix)) {
            return true;
        }
        if (isEmpty(source)) {
            return false;
        }
        if (suffix.length() > source.length()) {
            return false;
        }
        return source.substring(source.length() - suffix.length())
                .toLowerCase().endsWith(suffix.toLowerCase());
    }

    /**
     * If the given text is null return "", the original text otherwise.
     *
     * @param text
     * @return If the given text is null "", the original text otherwise.
     */
    public static String defaultIfNull(String text) {
        return defaultIfNull(text, "");
    }

    /**
     * If the given text is null return "", the given defaultValue otherwise.
     *
     * @param text
     * @param defaultValue
     * @return If the given text is null "", the given defaultValue otherwise.
     */
    public static String defaultIfNull(String text, String defaultValue) {
        if (text == null) {
            return defaultValue;
        }
        return text;
    }

    /**
     * Gives the substring of the given text before the given separator.
     *
     * If the text does not contain the given separator then the given text is
     * returned.
     *
     * @param text
     * @param separator
     * @return  the substring of the given text before the given separator.
     */
    public static String substringBefore(String text, char separator) {
        if (isEmpty(text)) {
            return text;
        }
        int sepPos = text.indexOf(separator);
        if (sepPos < 0) {
            return text;
        }
        return text.substring(0, sepPos);
    }

    /**
     * Gives the substring of the given text before the last occurrence of the
     * given separator.
     *
     * If the text does not contain the given separator then the given text is
     * returned.
     *
     * @param text
     * @param separator
     * @return the substring of the given text before the last occurrence of the given separator.
     */
    public static String substringBeforeLast(String text, char separator) {
        if (isEmpty(text)) {
            return text;
        }
        int cPos = text.lastIndexOf(separator);
        if (cPos < 0) {
            return text;
        }
        return text.substring(0, cPos);
    }

    /**
     * Gives the substring of the given text after the last occurrence of the
     * given separator.
     *
     * If the text does not contain the given separator then "" is returned.
     *
     * @param text
     * @param separator
     * @return the substring of the given text after the last occurrence of the given separator.
     */
    public static String substringAfterLast(String text, char separator) {
        if (isEmpty(text)) {
            return text;
        }
        int cPos = text.lastIndexOf(separator);
        if (cPos < 0) {
            return "";
        }
        return text.substring(cPos + 1);
    }

    /**
     * Gives the substring of the given text after the given separator.
     *
     * If the text does not contain the given separator then "" is returned.
     *
     * @param text the input text
     * @param c the separator char
     * @return the substring of the given text after the given separator.
     */
    public static String substringAfter(String text, char c) {
        if (isEmpty(text)) {
            return text;
        }
        int cPos = text.indexOf(c);
        if (cPos < 0) {
            return "";
        }
        return text.substring(cPos + 1);
    }

    /**
     * Pretty print one or more name-value pairs of strings.
     *
     * @param strPairs the name-value pairs of strings
     * @return a string representation of the name-value pairs of strings
     */
    public static String toString(final Object... strPairs) {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        for (int i = 0; i < strPairs.length; i += 2) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(strPairs[i]);
            buf.append(": ");
            Object str = null;
            if ((i + 1) < strPairs.length) {
                str = strPairs[i + 1];
            }
            if (str == null) {
                buf.append("<null>");
            } else {
                buf.append('\'');
                buf.append(str);
                buf.append('\'');
            }
        }
        buf.append(']');
        return buf.toString();
    }

    /**
     * Compare two strings for equality.
     *
     * @param str1 the first string to compare
     * @param str2 the second string to compare
     * @return whether the strings are equal
     */
    public static boolean equals(final String str1, final String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    /**
     * Generate a hash code for one or more strings.
     *
     * @param strs the strings to generate a hash code for
     * @return the hash code for the strings
     */
    public static int hashCode(final String... strs) {
        int result = 31;
        for (String str : strs) {
            result ^= String.valueOf(str).hashCode();
        }
        return result;
    }

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private StringUtil() {
        super();
    }
}
