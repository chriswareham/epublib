package nl.siegmann.epublib.domain;

import java.io.Serializable;

/**
 * The DCMES title element. The DCMES title element represents an instance of a
 * name given to the EPUB Publication.
 *
 * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#elemdef-opf-dctitle">dc:title</a>
 */
public class Title implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The content of this element.
     */
    private final String text;
    /**
     * The ID of this element, which must be unique within the document scope.
     */
    private final String id;
    /**
     * The language used in the content of this element.
     */
    private final String lang;
    /**
     * The base text direction of the content of this element.
     */
    private final TextDirection dir;

    /**
     * Construct a DCMES title element.
     *
     * @param text the content of this element
     */
    public Title(final String text) {
        this(text, null, null, TextDirection.UNSPECIFIED);
    }

    /**
     * Construct a DCMES title element.
     *
     * @param text the content of this element
     * @param id the ID of this element, which must be unique within the document scope
     */
    public Title(final String text, final String id) {
        this(text, id, null, TextDirection.UNSPECIFIED);
    }

    /**
     * Construct a DCMES title element.
     *
     * @param text the content of this element
     * @param id the ID of this element
     * @param lang the language used in the content of this element
     * @param dir the base text direction of the content of this element
     */
    public Title(final String text, final String id, final String lang, final TextDirection dir) {
        this.text = text;
        this.id = id;
        this.lang = lang;
        this.dir = dir;
    }

    /**
     * Get the content of this element.
     *
     * @return the content of this element
     */
    public String getText() {
        return text;
    }

    /**
     * Get the ID of this element.
     *
     * @return the ID of this element
     */
    public String getId() {
        return id;
    }

    /**
     * Get the language used in the content of this element.
     *
     * @return the language used in the content of this element
     */
    public String getLang() {
        return lang;
    }

    /**
     * Get the base text direction of the content of this element.
     *
     * @return the base text direction of the content of this element
     */
    public TextDirection getDir() {
        return dir;
    }
}
