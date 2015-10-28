package nl.siegmann.epublib.domain;

/**
 * This enumeration describes text direction.
 *
 * @author Chris Wareham
 */
public enum TextDirection {
    /**
     * Text direction unspecified.
     */
    UNSPECIFIED(""),
    /**
     * Text direction left to right.
     */
    LEFT_TO_RIGHT("ltr"),
    /**
     * Text direction right to left.
     */
    RIGHT_TO_LEFT("rtl");

    /**
     * The text direction value.
     */
    private final String value;

    /**
     * Construct a text direction value.
     *
     * @param value the text direction value
     */
    TextDirection(final String value) {
        this.value = value;
    }

    /**
     * Get the text direction value as a string.
     *
     * @return the text direction value as a string
     */
    @Override
    public String toString() {
        return value;
    }
}
