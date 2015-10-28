package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xmlpull.v1.XmlSerializer;

/**
 * This class provides a writer for EPUB 3.0 OPF package documents.
 */
public final class PackageDocumentWriter implements PackageDocumentBase {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PackageDocumentWriter.class);

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PackageDocumentWriter() {
        super();
    }

    /**
     * Write an EPUB 3.0 OPF package document.
     *
     * @param book the book to write the package document for
     * @param serializer the XML serialiser to write the package document to
     * @throws IOException if an I/O error occurs
     */
    public static void write(final XmlSerializer serializer, final Book book) throws IOException {
        Identifier bookId = book.getMetadata().getBookIdIdentifier();

        serializer.startDocument(Constants.CHARACTER_ENCODING, false);

        serializer.setPrefix(PREFIX_OPF, NAMESPACE_OPF);
        serializer.setPrefix(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);

        serializer.startTag(NAMESPACE_OPF, OPFElements.PACKAGE);
        serializer.attribute(PREFIX_EMPTY, OPFAttributes.VERSION, VERSION);
        serializer.attribute(PREFIX_EMPTY, OPFAttributes.UNIQUE_IDENTIFIER, bookId != null ? bookId.getId() : BOOK_ID);

        PackageDocumentMetadataWriter.writeMetaData(book, serializer);

        writeManifest(serializer, book);
        writeSpine(serializer, book);
        writeGuide(serializer, book);

        serializer.endTag(NAMESPACE_OPF, OPFElements.PACKAGE);

        serializer.endDocument();

        serializer.flush();
    }

    /**
     * Write a manifest.
     *
     * @param serializer the XML serialiser to write the manifest to
     * @param book the book to write the manifest for
     * @throws IOException if an I/O error occurs
     */
    private static void writeManifest(final XmlSerializer serializer, final Book book) throws IOException {
        serializer.startTag(NAMESPACE_OPF, OPFElements.MANIFEST);

        if (book.isNcx()) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.ITEM);
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.ID, book.getNcxId());
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.HREF, book.getNcxHref());
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.MEDIA_TYPE, book.getNcxMediaType());
            serializer.endTag(NAMESPACE_OPF, OPFElements.ITEM);
        }

        for (Resource resource: getResourcesSortedById(book)) {
            writeItem(serializer, book, resource);
        }

        serializer.endTag(NAMESPACE_OPF, OPFElements.MANIFEST);
    }

    /**
     * Write a resource as an item element.
     *
     * @param serializer the XML serialiser to write the item element to
     * @param book the book to write the resource for
     * @param resource the resource to write as an item element
     * @throws IOException if an I/O error occurs
     */
    private static void writeItem(final XmlSerializer serializer, final Book book, final Resource resource) throws IOException {
        if (!isValidResource(book, resource)) {
            return;
        }

        serializer.startTag(NAMESPACE_OPF, OPFElements.ITEM);
        serializer.attribute(PREFIX_EMPTY, OPFAttributes.ID, resource.getId());
        serializer.attribute(PREFIX_EMPTY, OPFAttributes.HREF, resource.getHref());
        serializer.attribute(PREFIX_EMPTY, OPFAttributes.MEDIA_TYPE, resource.getMediaType().getName());
        if (StringUtil.isNotBlank(resource.getProperties())) {
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.PROPERTIES, resource.getProperties());
        }
        serializer.endTag(NAMESPACE_OPF, OPFElements.ITEM);
    }

    /**
     * Write a spine.
     *
     * @param serializer the XML serialiser to write the spine to
     * @param book the book to write the spine for
     * @throws IOException if an I/O error occurs
     */
    private static void writeSpine(final XmlSerializer serializer, final Book book) throws IOException {
        serializer.startTag(NAMESPACE_OPF, OPFElements.SPINE);
        // REMOVED WRITING OF TABLE OF CONTENTS ATTRIBUTE
        serializer.attribute(PREFIX_EMPTY, OPFAttributes.TOC, book.getSpine().getTocResource().getId());

        // REMOVED WRITING OF COVER PAGE
        if (book.getCoverPage() != null // there is a cover page
            && book.getSpine().findFirstResourceById(book.getCoverPage().getId()) < 0) { // cover page is not already in the spine
            // write the cover html file
            serializer.startTag(NAMESPACE_OPF, OPFElements.ITEMREF);
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.IDREF, book.getCoverPage().getId());
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.LINEAR, "no");
            serializer.endTag(NAMESPACE_OPF, OPFElements.ITEMREF);
        }
        writeSpineItems(serializer, book.getSpine());

        serializer.endTag(NAMESPACE_OPF, OPFElements.SPINE);
    }

    private static void writeSpineItems(XmlSerializer serializer, Spine spine) throws IOException {
        for (SpineReference spineReference: spine.getSpineReferences()) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.ITEMREF);
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.IDREF, spineReference.getResourceId());
            if (!spineReference.isLinear()) {
                serializer.attribute(PREFIX_EMPTY, OPFAttributes.LINEAR, OPFValues.NO);
            }
            serializer.endTag(NAMESPACE_OPF, OPFElements.ITEMREF);
        }
    }

    private static void writeGuide(XmlSerializer serializer, Book book) throws IOException {
        if (!book.getGuide().isEmpty()) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.GUIDE);
            ensureCoverPageGuideReferenceWritten(book.getGuide(), serializer);
            for (GuideReference reference: book.getGuide().getReferences()) {
                writeGuideReference(reference, serializer);
            }
            serializer.endTag(NAMESPACE_OPF, OPFElements.GUIDE);
        }
    }

    private static void ensureCoverPageGuideReferenceWritten(Guide guide, XmlSerializer serializer) throws IOException {
        if (guide.getGuideReferencesByType(GuideReference.COVER).isEmpty()) {
            Resource coverPage = guide.getCoverPage();
            if (coverPage != null) {
                writeGuideReference(new GuideReference(guide.getCoverPage(), GuideReference.COVER, GuideReference.COVER), serializer);
            }
        }
    }

    private static void writeGuideReference(GuideReference reference, XmlSerializer serializer) throws IOException {
        if (reference != null) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.REFERENCE);
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.TYPE, reference.getType());
            serializer.attribute(PREFIX_EMPTY, OPFAttributes.HREF, reference.getCompleteHref());
            if (StringUtil.isNotBlank(reference.getTitle())) {
                serializer.attribute(PREFIX_EMPTY, OPFAttributes.TITLE, reference.getTitle());
            }
            serializer.endTag(NAMESPACE_OPF, OPFElements.REFERENCE);
        }
    }

    private static List<Resource> getResourcesSortedById(final Book book) {
        List<Resource> resources = new ArrayList<>(book.getResources().getAll());
        Collections.sort(resources, (Resource resource1, Resource resource2) -> resource1.getId().compareToIgnoreCase(resource2.getId()));
        return resources;
    }

    /**
     * Check whether a resource is valid. A valid resource must have a non-blank
     * ID, a non-blank link and a media type.
     *
     * @param book the book the resource is for
     * @param resource the resource to check
     * @return whether the resource is valid
     */
    private static boolean isValidResource(final Book book, final Resource resource) {
        if (resource == null || (resource.getMediaType() == MediatypeService.NCX && book.getSpine().getTocResource() != null)) {
            return false;
        }
        if (StringUtil.isBlank(resource.getId())) {
            LOGGER.error("resource id must not be blank (href: " + resource.getHref() + ", mediatype:" + resource.getMediaType() + ")");
            return false;
        }
        if (StringUtil.isBlank(resource.getHref())) {
            LOGGER.error("resource href must not be blank (id: " + resource.getId() + ", mediatype:" + resource.getMediaType() + ")");
            return false;
        }
        if (resource.getMediaType() == null) {
            LOGGER.error("resource media type must be specified (id: " + resource.getId() + ", href:" + resource.getHref() + ")");
            return false;
        }
        return true;
    }
}
