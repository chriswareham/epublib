package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.util.StringUtil;

/**
 * This DCMES Optional Element is used to represent the name of a person,
 * organisation, etc.
 *
 * @author Paul Siegmann
 * @author Chris Wareham
 *
 * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-opf-dcmes-optional">The DCMES Optional Elements</a>
 */
public class Author extends AbstractDublinCoreElement {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    private Relator relator = Relator.AUTHOR;

    private String firstname;

    private String lastname;

    public Author(final String singleName) {
        this("", singleName);
    }

    public Author(final String firstname, final String lastname) {
        super(null, null, TextDirection.UNSPECIFIED);
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public Relator setRole(final String code) {
        Relator result = Relator.byCode(code);
        if (result == null) {
            result = Relator.AUTHOR;
        }
        relator = result;
        return result;
    }

    public Relator getRelator() {
        return relator;
    }

    public void setRelator(final Relator relator) {
        this.relator = relator;
    }

    @Override
    public String toString() {
        return lastname + ", " + firstname;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if(!(obj instanceof Author)) {
            return false;
        }
        Author author = (Author) obj;
        return StringUtil.equals(firstname, author.firstname)
            && StringUtil.equals(lastname, author.lastname);
    }

    @Override
    public int hashCode() {
        return StringUtil.hashCode(firstname, lastname);
    }
}
