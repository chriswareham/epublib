package nl.siegmann.epublib.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import nl.siegmann.epublib.util.IOUtil;

/**
 * The link element. The link element is used to associate resources with a
 * Publication, such as metadata records.
 *
 * A link has an attribute <code>id</code>, which must be unique within the
 * document scope.
 *
 * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-link-elem">link</a>
 */
public class Link implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The absolute or relative IRI reference (RFC3987) of the resource.
     */
    private final String href;
    /**
     * The space-separated list of property values.
     */
    private final String rel;
    /**
     * The ID of this link.
     */
    private final String id;
    /**
     * The expression or resource augmented by this element.
     */
    private final String refines;
    /**
     * The media type (RFC2046) that specifies the type and format of the resource.
     */
    private final String mediaType;
    /**
     * The data of the resource.
     */
    private final byte[] data;

    /**
     * Construct a link element.
     *
     * @param href the absolute or relative IRI reference of the resource
     * @param rel the space-separated list of property values
     * @throws IOException if an I/O error occurs
     */
    public Link(final String href, final String rel) throws IOException {
        this(href, rel, null, null, null, null);
    }

    /**
     * Construct a link element.
     *
     * @param href the absolute or relative IRI reference of the resource
     * @param rel the space-separated list of property values
     * @param mediaType the media type that specifies the type and format of the resource
     * @param inputStream the input stream to read the data of the resource from
     * @throws IOException if an I/O error occurs
     */
    public Link(final String href, final String rel, final String mediaType, final InputStream inputStream) throws IOException {
        this(href, rel, null, null, mediaType, inputStream);
    }

    /**
     * Construct a link element.
     *
     * @param href the absolute or relative IRI reference of the resource
     * @param rel the space-separated list of property values
     * @param id a reference ID that must be unique within the publication
     * @param refines the expression or resource augmented by this element
     * @param mediaType the media type that specifies the type and format of the resource
     * @param inputStream the input stream to read the data of the resource from
     * @throws IOException if an I/O error occurs
     */
    public Link(final String href, final String rel, final String id, final String refines, final String mediaType, final InputStream inputStream) throws IOException {
        this.href = href;
        this.rel = rel;
        this.id = id;
        this.refines = refines;
        this.mediaType = mediaType;
        this.data = inputStream != null ? IOUtil.toByteArray(inputStream) : new byte[0];
    }

    /**
     * Get the absolute or relative IRI reference (RFC3987) of the resource.
     *
     * @return the absolute or relative IRI reference (RFC3987) of the resource
     */
    public String getHref() {
        return href;
    }

    /**
     * Get the space-separated list of property values.
     *
     * @return the space-separated list of property values
     */
    public String getRel() {
        return rel;
    }

    /**
     * Get the ID of this link.
     *
     * @return the ID of this link
     */
    public String getId() {
        return id;
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
     * Get the media type (RFC2046) that specifies the type and format of the resource.
     *
     * @return the media type (RFC2046) that specifies the type and format of the resource
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Get the data of the resource.
     *
     * @return the data of the resource
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }
}
