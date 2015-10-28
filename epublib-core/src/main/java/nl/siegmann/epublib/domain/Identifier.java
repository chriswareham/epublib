package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import nl.siegmann.epublib.util.StringUtil;

/**
 * The DCMES identifier element. The DCMES identifier element contains a single
 * identifier associated with the EPUB Publication, such as a UUID, DOI, ISBN or ISSN.
 *
 * An identifier has an attribute <code>id</code>, which describes the meaning
 * of the identifier and must be unique within the document scope. One of the
 * values of this attribute is referenced by the <code>unique-identifier</code>
 * attribute of the Package Document's root <code>metadata</code> element to
 * specify the primary identifier of the EPUB Publication.
 *
 * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-opf-dcidentifier">dc:identifier</a>
 */
public class Identifier implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    public interface Scheme {
        String UUID = "UUID";
        String ISBN = "ISBN";
        String URL = "URL";
        String URI = "URI";
    }

    /**
     * The ID of this identifier.
     */
    private final String id;
    /**
     * The identifier scheme (UUID, ISBN, etc).
     */
    private String scheme;
    /**
     * The identifier value (the actual UUID, ISBN, etc).
     */
    private String value;
    /**
     * Whether the identifier is the book identifier.
     */
    private boolean bookId;

    /**
     * Construct an identifier using the UUID schema and a random value.
     */
    public Identifier() {
        this(Scheme.UUID, UUID.randomUUID().toString(), null);
    }

    /**
     * Construct an identifier.
     *
     * @param scheme the identifier scheme (UUID, ISBN, etc)
     * @param value the identifier value (the actual UUID, ISBN, etc)
     */
    public Identifier(String scheme, String value) {
        this(scheme, value, null);
    }

    /**
     * Construct an identifier.
     *
     * @param scheme the identifier scheme (UUID, ISBN, etc)
     * @param value the identifier value (the actual UUID, ISBN, etc)
     * @param id a reference ID that must be unique within the publication
     */
    public Identifier(final String scheme, final String value, final String id) {
        this.scheme = scheme;
        this.value = value;
        this.id = id;
    }

    /**
     * The first identifier for which the bookId is true is made the bookId
     * identifier. If no identifier has bookId == true then the first bookId
     * identifier is written as the primary.
     *
     * @param identifiers
     * @return The first identifier for which the bookId is true is made the bookId identifier.
     */
    public static Identifier getBookIdIdentifier(List<Identifier> identifiers) {
        if(identifiers == null || identifiers.isEmpty()) {
            return null;
        }

        Identifier result = null;
        for(Identifier identifier: identifiers) {
            if(identifier.isBookId()) {
                result = identifier;
                break;
            }
        }

        if(result == null) {
            result = identifiers.get(0);
        }

        return result;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the ID of this identifier. This is a reference ID that must be unique
     * within the publication and can be used to refer to the identifier (in the
     * <code>unique-identifier</code> attribute of the Package Document's root
     * <code>metadata</code> element for example.
     *
     * @return the ID of this identifier
     */
    public String getId() {
        return id;
    }

    /**
     * This bookId property allows the book creator to add multiple ids and tell
     * the epubwriter which one to write out as the bookId.
     *
     * The Dublin Core metadata spec allows multiple identifiers for a Book.
     * The epub spec requires exactly one identifier to be marked as the book id.
     *
     * @return whether this is the unique book id.
     */
    public boolean isBookId() {
        return bookId;
    }

    public void setBookId(boolean bookId) {
        this.bookId = bookId;
    }

    /**
     * Return a string representation of this object.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return StringUtil.isNotBlank(scheme) ? scheme + ":" + value : value;
    }

    /**
     * Return whether some other object is equal to this object.
     *
     * @param obj the object with which to compare
     * @return whether the object is equal to this object
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Identifier)) {
            return false;
        }
        Identifier i = (Identifier) obj;
        return StringUtil.equals(scheme, i.scheme)
            && StringUtil.equals(value, i.value);
    }

    /**
     * Return a hash code value for this object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return StringUtil.defaultIfNull(scheme).hashCode() ^ StringUtil.defaultIfNull(value).hashCode();
    }
}
