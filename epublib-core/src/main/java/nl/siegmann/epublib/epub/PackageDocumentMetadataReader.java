package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nl.siegmann.epublib.domain.Title;

/**
 * This class provides a reader for EPUB 3.0 OPF package document metadata.
 */
public class PackageDocumentMetadataReader implements PackageDocumentBase {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PackageDocumentMetadataReader() {
        super();
    }

    /**
     * Read EPUB 3.0 OPF package document metadata.
     *
     * @param packageDocument the package document to read the metadata from
     * @return the package document metadata
     */
    public static Metadata readMetadata(final Document packageDocument) {
        Metadata metadata = new Metadata();

        Element metadataElement = DOMUtil.getFirstElement(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFElements.METADATA);
        if (metadataElement != null) {
            metadata.setTitles(readTitles(metadataElement));
            metadata.setPublishers(DOMUtil.getElementsTextContent(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.PUBLISHER));
            metadata.setDescriptions(DOMUtil.getElementsTextContent(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.DESCRIPTION));
            metadata.setRights(DOMUtil.getElementsTextContent(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.RIGHTS));
            metadata.setTypes(DOMUtil.getElementsTextContent(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.TYPE));
            metadata.setSubjects(DOMUtil.getElementsTextContent(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.SUBJECT));
            metadata.setIdentifiers(readIdentifiers(metadataElement));
            metadata.setAuthors(readCreators(metadataElement));
            metadata.setContributors(readContributors(metadataElement));
            metadata.setDates(readDates(metadataElement));
            metadata.setOtherProperties(readOtherProperties(metadataElement));
            metadata.setMetaAttributes(readMetaProperties(metadataElement));
        }

        Element languageTag = DOMUtil.getFirstElement(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.LANGUAGE);
        if (languageTag != null) {
            metadata.setLanguage(DOMUtil.getElementTextContent(languageTag));
        }

        return metadata;
    }

    private static List<Title> readTitles(final Element metadataElement) {
        List<Title> titles = new ArrayList<>();

        NodeList titleElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCElements.TITLE);
        for (int i = 0; i < titleElements.getLength(); ++i) {
            Element metaElement = (Element) titleElements.item(i);
            String id = metaElement.getAttribute(DCAttributes.ID);
            if (StringUtil.isNotBlank(id)) {
                titles.add(new Title(metaElement.getTextContent(), id));
            } else {
                titles.add(new Title(metaElement.getTextContent()));
            }
        }

        return titles;
    }

    /**
     * Read <code>meta</code> elements that have <code>property</code> attributes.
     *
     * @param metadataElement the metadata element to read the meta elements from
     * @return the meta element properties
     * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-meta-elem">meta</a>
     */
    private static Map<QName, String> readOtherProperties(Element metadataElement) {
        Map<QName, String> properties = new HashMap<>();

        NodeList metaElements = metadataElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFElements.META);
        for (int i = 0; i < metaElements.getLength(); ++i) {
            Node metaElement = metaElements.item(i);
            Node property = metaElement.getAttributes().getNamedItem(OPFAttributes.PROPERTY);
            if (property != null) {
                properties.put(new QName(property.getNodeValue()), metaElement.getTextContent());
            }
        }

        return properties;
    }

    /**
     * Read <code>meta</code> elements that have <code>property</code> attributes.
     *
     * @param metadataElement the metadata element to read the meta elements from
     * @return the meta element properties
     * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-meta-elem">meta</a>
     */
    private static Map<String, String> readMetaProperties(final Element metadataElement) {
        Map<String, String> properties = new HashMap<>();

        NodeList metaElements = metadataElement.getElementsByTagName(OPFElements.META);
        for (int i = 0; i < metaElements.getLength(); ++i) {
            Element metaElement = (Element) metaElements.item(i);
            String name = metaElement.getAttribute(OPFAttributes.NAME);
            String value = metaElement.getAttribute(OPFAttributes.CONTENT);
            properties.put(name,  value);
        }

        return properties;
    }

    /**
     * Get the book creators.
     *
     * @param metadataElement the metadata element to read the creators from
     * @return the book creators
     */
    private static List<Author> readCreators(final Element metadataElement) {
        return readAuthors(metadataElement, DCElements.CREATOR);
    }

    /**
     * Get the book contributors.
     *
     * @param metadataElement the metadata element to read the contributors from
     * @return the book contributors
     */
    private static List<Author> readContributors(final Element metadataElement) {
        return readAuthors(metadataElement, DCElements.CONTRIBUTOR);
    }

    /**
     * Get the book authors (creators or contributors).
     *
     * @param metadataElement the metadata element to read the authors from
     * @param authorElementName the name of the author element
     * @return the book authors
     */
    private static List<Author> readAuthors(final Element metadataElement, final String authorElementName) {
        NodeList authorElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, authorElementName);

        List<Author> authors = new ArrayList<>(authorElements.getLength());

        for (int i = 0; i < authorElements.getLength(); ++i) {
            Element authorElement = (Element) authorElements.item(i);
            Author author = readAuthor(authorElement);
            if (author != null) {
                authors.add(author);
            }
        }

        return authors;
    }

    /**
     * Get an author.
     *
     * @param authorElement the element to get an author for
     * @return the author or null if the element is empty
     */
    private static Author readAuthor(final Element authorElement) {
        Author author = null;
        String name = DOMUtil.getElementTextContent(authorElement);
        if (StringUtil.isNotBlank(name)) {
            int i = name.lastIndexOf(' ');
            author = i == -1 ? new Author(name) : new Author(name.substring(0, i), name.substring(i + 1));
            author.setRole(authorElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.ROLE));
        }
        return author;
    }

    /**
     * Get the book dates.
     *
     * @param metadataElement the metadata element to read the dates from
     * @return the book dates
     */
    private static List<Date> readDates(final Element metadataElement) {
        NodeList dateElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCElements.DATE);

        List<Date> dates = new ArrayList<>(dateElements.getLength());

        for (int i = 0; i < dateElements.getLength(); ++i) {
            Element dateElement = (Element) dateElements.item(i);
            String date = DOMUtil.getElementTextContent(dateElement);
            if (StringUtil.isNotBlank(date)) {
                dates.add(new Date(date, dateElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.EVENT)));
            }
        }

        return dates;
    }

    /**
     * Read <code>identifier</code> elements.
     *
     * @param metadataElement the metadata element to read the identifier elements from
     * @return the identifiers
     * @see <a href="http://www.idpf.org/epub/30/spec/epub30-publications.html#sec-opf-dcidentifier">identifier</a>
     */
    private static List<Identifier> readIdentifiers(final Element metadataElement) {
        NodeList identifierElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);

        if (identifierElements.getLength() == 0) {
            return Collections.emptyList();
        }

        List<Identifier> identifiers = new ArrayList<>(identifierElements.getLength());

        String bookId = getBookId(metadataElement.getOwnerDocument());

        for (int i = 0; i < identifierElements.getLength(); ++i) {
            Element identifierElement = (Element) identifierElements.item(i);
            String value = DOMUtil.getElementTextContent(identifierElement);
            if (StringUtil.isNotBlank(value)) {
                String id = identifierElement.getAttribute(DCAttributes.ID);
                String scheme = identifierElement.getAttributeNS(NAMESPACE_OPF, DCAttributes.SCHEME);
                Identifier identifier = new Identifier(scheme, value, id);
                identifier.setBookId(id.equals(bookId));
                identifiers.add(identifier);
            }
        }

        return identifiers;
    }

    /**
     * Get the book identifier. The EPUB 3 specification states that one
     * identifier can be explicitly marked as the book identifier, which is
     * referenced by the <code>unique-identifer</code> attribute of the
     * <code>package</code> element.
     *
     * @param document the document
     * @return the book identifier
     */
    private static String getBookId(final Document document) {
        String bookId = null;
        Element packageElement = DOMUtil.getFirstElement(document.getDocumentElement(), NAMESPACE_OPF, OPFElements.PACKAGE);
        if (packageElement != null) {
            bookId = packageElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.UNIQUE_IDENTIFIER);
        }
        return bookId;
    }
}
