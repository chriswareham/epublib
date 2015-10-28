package nl.siegmann.epublib.domain;

import java.io.Serializable;

/**
 * A DCMES Optional Element. These elements all conform to a generalised
 * definition with the same attributes.
 *
 * @author Chris Wareham
 */
public class AbstractDublinCoreElement implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ID of this element.
     */
    private final String id;
    /**
     * The language of this element and its descendants.
     */
    private final String lang;
    /**
     * The base text direction of this element and its descendants.
     */
    private final TextDirection dir;

    /**
     * Construct a DCMES Optional Element.
     *
     * @param id the ID of this element
     * @param lang the language of this element and its descendants
     * @param dir the base text direction of this element and its descendants
     */
    public AbstractDublinCoreElement(final String id, final String lang, final TextDirection dir) {
        this.id = id;
        this.lang = lang;
        this.dir = dir;
    }
    
    /**
     * Get the ID of this element, which must be unique within the document
     * scope.
     *
     * @return the ID of this element
     */
    public String getId() {
        return id;
    }

    /**
     * The language used in the contents and attribute values of this element
     * and its descendants.
     *
     * @return the language of this element and its descendants
     */
    public String getLang() {
        return lang;
    }

    /**
     * The base text direction of the content and attribute values of this
     * element and its descendants.
     *
     * @return the base text direction of this element and its descendants
     */
    public TextDirection getDir() {
        return dir;
    }
}
