package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.util.StringUtil;

import org.xmlpull.v1.XmlSerializer;

public class PackageDocumentMetadataWriter implements PackageDocumentBase {

    /**
     * Writes the book's metadata.
     *
     * @param book
     * @param serializer
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    public static void writeMetaData(Book book, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
        serializer.startTag(NAMESPACE_OPF, OPFElements.METADATA);
        serializer.setPrefix(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
        serializer.setPrefix(PREFIX_OPF, NAMESPACE_OPF);

        writeIdentifiers(book.getMetadata().getIdentifiers(), serializer);
        writeSimpleMetdataElements(DCElements.TITLE, book.getMetadata().getTitles(), serializer);
        writeSimpleMetdataElements(DCElements.SUBJECT, book.getMetadata().getSubjects(), serializer);
        writeSimpleMetdataElements(DCElements.DESCRIPTION, book.getMetadata().getDescriptions(), serializer);
        writeSimpleMetdataElements(DCElements.PUBLISHER, book.getMetadata().getPublishers(), serializer);
        writeSimpleMetdataElements(DCElements.TYPE, book.getMetadata().getTypes(), serializer);
        writeSimpleMetdataElements(DCElements.RIGHTS, book.getMetadata().getRights(), serializer);

        // write authors
        for(Author author: book.getMetadata().getAuthors()) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.CREATOR);
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.ROLE, author.getRelator().getCode());
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.FILE_AS, author.getLastname() + ", " + author.getFirstname());
            serializer.text(author.getFirstname() + " " + author.getLastname());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.CREATOR);
        }

        // write contributors
        for(Author author: book.getMetadata().getContributors()) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.CONTRIBUTOR);
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.ROLE, author.getRelator().getCode());
            serializer.attribute(NAMESPACE_OPF, OPFAttributes.FILE_AS, author.getLastname() + ", " + author.getFirstname());
            serializer.text(author.getFirstname() + " " + author.getLastname());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.CONTRIBUTOR);
        }

        // write dates
        for (Date date: book.getMetadata().getDates()) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.DATE);
            if (date.getEvent() != null) {
                serializer.attribute(NAMESPACE_OPF, OPFAttributes.EVENT, date.getEvent().toString());
            }
            serializer.text(date.getValue());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.DATE);
        }

        // write language
        if(StringUtil.isNotBlank(book.getMetadata().getLanguage())) {
            serializer.startTag(NAMESPACE_DUBLIN_CORE, "language");
            serializer.text(book.getMetadata().getLanguage());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, "language");
        }

        // write other properties
        if(book.getMetadata().getOtherProperties() != null) {
            for(Map.Entry<QName, String> mapEntry: book.getMetadata().getOtherProperties().entrySet()) {
                serializer.startTag(mapEntry.getKey().getNamespaceURI(), mapEntry.getKey().getLocalPart());
                serializer.text(mapEntry.getValue());
                serializer.endTag(mapEntry.getKey().getNamespaceURI(), mapEntry.getKey().getLocalPart());

            }
        }

        // write coverimage
        if(book.getCoverImage() != null) { // write the cover image
            serializer.startTag(NAMESPACE_OPF, OPFElements.META);
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.NAME, OPFValues.META_COVER);
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.CONTENT, book.getCoverImage().getId());
            serializer.endTag(NAMESPACE_OPF, OPFElements.META);
        }

        // write generator
        serializer.startTag(NAMESPACE_OPF, OPFElements.META);
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.NAME, OPFValues.GENERATOR);
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.CONTENT, Constants.EPUBLIB_GENERATOR_NAME);
        serializer.endTag(NAMESPACE_OPF, OPFElements.META);

        serializer.endTag(NAMESPACE_OPF, OPFElements.METADATA);
    }

    private static void writeSimpleMetdataElements(String tagName, List<String> values, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        for(String value: values) {
            if (StringUtil.isBlank(value)) {
                continue;
            }
            serializer.startTag(NAMESPACE_DUBLIN_CORE, tagName);
            serializer.text(value);
            serializer.endTag(NAMESPACE_DUBLIN_CORE, tagName);
        }
    }

    /**
     * Writes out the complete list of Identifiers to the package document.
     * The first identifier for which the bookId is true is made the bookId identifier.
     * If no identifier has bookId == true then the first bookId identifier is written as the primary.
     *
     * @param identifiers
     * @param serializer
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @
     */
    private static void writeIdentifiers(List<Identifier> identifiers, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException  {
        Identifier bookIdIdentifier = Identifier.getBookIdIdentifier(identifiers);
        if(bookIdIdentifier == null) {
            return;
        }

        serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, DCAttributes.ID, BOOK_ID);
        serializer.attribute(NAMESPACE_OPF, OPFAttributes.SCHEME, bookIdIdentifier.getScheme());
        serializer.text(bookIdIdentifier.getValue());
        serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);

        for(Identifier identifier: identifiers.subList(1, identifiers.size())) {
            if(identifier == bookIdIdentifier) {
                continue;
            }
            serializer.startTag(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);
            serializer.attribute(NAMESPACE_OPF, "scheme", identifier.getScheme());
            serializer.text(identifier.getValue());
            serializer.endTag(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);
        }
    }

    private PackageDocumentMetadataWriter() {
    }

}
