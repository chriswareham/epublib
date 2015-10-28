package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Link;
import nl.siegmann.epublib.domain.Meta;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Title;
import nl.siegmann.epublib.util.StringUtil;

import org.xmlpull.v1.XmlSerializer;

/**
 * This class provides a writer for EPUB 3.0 OPF package document metadata.
 */
public final class PackageDocumentMetadataWriter implements PackageDocumentBase {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PackageDocumentMetadataWriter() {
        super();
    }

    /**
     * Write EPUB 3.0 OPF package document metadata.
     *
     * @param book the book to write the package document metadata for
     * @param serializer the XML serialiser to write the metadata to
     * @throws IOException if an I/O error occurs
     */
    public static void writeMetaData(final Book book, final XmlSerializer serializer) throws IOException  {
        serializer.startTag(NAMESPACE_OPF, OPFElements.METADATA);
        serializer.setPrefix(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
        serializer.setPrefix(PREFIX_OPF, NAMESPACE_OPF);

        writeIdentifiers(book.getMetadata().getIdentifiers(), serializer);
        writeTitles(book.getMetadata().getTitles(), serializer);

        writeSimpleMetadata(book.getMetadata().getSubjects(), DCElements.SUBJECT, serializer);
        writeSimpleMetadata(book.getMetadata().getDescriptions(), DCElements.DESCRIPTION, serializer);
        writeSimpleMetadata(book.getMetadata().getPublishers(), DCElements.PUBLISHER, serializer);
        writeSimpleMetadata(book.getMetadata().getTypes(), DCElements.TYPE, serializer);
        writeSimpleMetadata(book.getMetadata().getRights(), DCElements.RIGHTS, serializer);

        writeMetadata(book.getMetadata().getMetadata(), serializer);
        writeLinks(book.getMetadata().getLinks(), serializer);

        writeAuthors(book.getMetadata().getAuthors(), serializer);
        writeContributors(book.getMetadata().getContributors(), serializer);
        writeDates(book.getMetadata().getDates(), serializer);
        writeLanguage(book.getMetadata().getLanguage(), serializer);
        writeOtherProperties(book.getMetadata().getOtherProperties(), serializer);
        writeCoverimage(book.getCoverImage(), serializer);

        serializer.endTag(NAMESPACE_OPF, OPFElements.METADATA);
    }

    private static void writeIdentifiers(final List<Identifier> identifiers, final XmlSerializer serializer) throws IOException  {
        for (Identifier identifier: identifiers) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);
            if (identifier.getId() != null) {
                serializer.attribute(PREFIX_EMPTY, DCAttributes.ID, identifier.getId());
            }
            if (identifier.getScheme() != null) {
                serializer.attribute(NAMESPACE_OPF, DCAttributes.SCHEME, identifier.getScheme());
            }
            serializer.text(identifier.getValue());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);
        }
    }

    private static void writeTitles(final List<Title> titles, final XmlSerializer serializer) throws IOException  {
        for (Title title : titles) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.TITLE);
            if (title.getId() != null) {
                serializer.attribute(PREFIX_EMPTY, DCAttributes.ID, title.getId());
            }
            serializer.text(title.getText());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.TITLE);
        }
    }

    private static void writeSimpleMetadata(final List<String> values, final String element, final XmlSerializer serializer) throws IOException {
        for (String value : values) {
            if (StringUtil.isNotBlank(value)) {
                serializer.startTag(NAMESPACE_DUBLIN_CORE, element);
                serializer.text(value);
                serializer.endTag(NAMESPACE_DUBLIN_CORE, element);
            }
        }
    }

    private static void writeMetadata(final List<Meta> metadata, final XmlSerializer serializer) throws IOException {
        for (Meta item : metadata) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.META);
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.PROPERTY, item.getProperty());
            serializer.text(item.getValue());
            serializer.endTag(NAMESPACE_OPF, OPFElements.META);
        }
    }

    private static void writeLinks(final List<Link> links, final XmlSerializer serializer) throws IOException {
        for (Link link : links) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.LINK);
            if (link.getRel() != null) {
                serializer.attribute(PREFIX_EMPTY, OPFAttributes.REL, link.getRel());
            }
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.HREF, link.getHref());
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.MEDIA_TYPE, link.getMediaType());
            serializer.endTag(NAMESPACE_OPF, OPFElements.LINK);
        }
    }

    private static void writeAuthors(final List<Author> authors, final XmlSerializer serializer) throws IOException {
        for (Author author : authors) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.CREATOR);
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.ROLE, author.getRelator().getCode());
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.FILE_AS, author.getLastname() + ", " + author.getFirstname());
            serializer.text(author.getFirstname() + " " + author.getLastname());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.CREATOR);
        }
    }

    private static void writeContributors(final List<Author> contributors, final XmlSerializer serializer) throws IOException {
        for (Author contributor : contributors) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.CONTRIBUTOR);
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.ROLE, contributor.getRelator().getCode());
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.FILE_AS, contributor.getLastname() + ", " + contributor.getFirstname());
            serializer.text(contributor.getFirstname() + " " + contributor.getLastname());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.CONTRIBUTOR);
        }
    }

    private static void writeDates(final List<Date> dates, final XmlSerializer serializer) throws IOException {
        for (Date date : dates) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.DATE);
            if (date.getEvent() != null) {
                serializer.attribute(NAMESPACE_OPF, OPFAttributes.EVENT, date.getEvent().toString());
            }
            serializer.text(date.getValue());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.DATE);
        }
    }

    private static void writeLanguage(final String language, final XmlSerializer serializer) throws IOException {
        if (StringUtil.isNotBlank(language)) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.LANGUAGE);
            serializer.text(language);
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.LANGUAGE);
        }
    }

    private static void writeOtherProperties(final Map<QName, String> otherProperties, final XmlSerializer serializer) throws IOException {
        if (otherProperties != null) {
            for (QName qName : otherProperties.keySet()) {
                serializer.startTag(qName.getNamespaceURI(), qName.getLocalPart());
                serializer.text(otherProperties.get(qName));
                serializer.endTag(qName.getNamespaceURI(), qName.getLocalPart());
            }
        }
    }

    private static void writeCoverimage(final Resource coverImage, final XmlSerializer serializer) throws IOException {
        if (coverImage != null) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.META);
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.NAME, OPFValues.META_COVER);
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.CONTENT, coverImage.getId());
            serializer.endTag(NAMESPACE_OPF, OPFElements.META);
        }
    }
}
