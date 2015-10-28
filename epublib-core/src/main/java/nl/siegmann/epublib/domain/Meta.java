package nl.siegmann.epublib.domain;

import java.io.Serializable;

/**
 * The meta element. The meta element provides a generic means of including
 * package metadata, allowing the expression of primary metadata about the
 * package or content and refinement of that metadata.
 *
 * A metadata item has an attribute <code>id</code>, which describes the meaning
 * of the item and must be unique within the document scope.
 *
 * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-meta-elem">meta</a>
 */
public class Meta implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The metadata item value.
     */
    private String value;
    /**
     * The property this metadata item relates to.
     */
    private final String property;
    /**
     * The expression or resource augmented by this element.
     */
    private final String refines;
    /**
     * The ID of this metadata item.
     */
    private final String id;
    /**
     * The source the value of the element is drawn from.
     */
    private final String scheme;

    /**
     * Construct an EPUB 3.0 metadata item.
     *
     * @param value the metadata item value
     * @param property the property this metadata item relates to
     */
    public Meta(final String value, final String property) {
        this(value, property, null, null, null);
    }

    /**
     * Construct an EPUB 3.0 metadata item.
     *
     * @param value the metadata item value
     * @param property the property this metadata item relates to
     * @param refines the expression or resource augmented by this element
     * @param id a reference ID that must be unique within the publication
     * @param scheme the source the value of the element is drawn from
     */
    public Meta(final String value, final String property, final String refines, final String id, final String scheme) {
        this.value = value;
        this.property = property;
        this.refines = refines;
        this.id = id;
        this.scheme = scheme;
    }

    /**
     * Get the metadata item value.
     *
     * @return the metadata item value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the property this metadata item relates to.
     *
     * @return the property this metadata item relates to
     */
    public String getProperty() {
        return property;
    }

    /**
     * Get the expression or resource augmented by this element.
     *
     * @return the expression or resource augmented by this element
     */
    public String getRefines() {
        return refines;
    }

    /**
     * Get the ID of this metadata item.
     *
     * @return the ID of this metadata item
     */
    public String getId() {
        return id;
    }

    /**
     * Get the source the value of the element is drawn from.
     *
     * @return the source the value of the element is drawn from
     */
    public String getScheme() {
        return scheme;
    }
}
