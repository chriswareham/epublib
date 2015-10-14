package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlSerializer;

/**
 * Writes the opf package document as defined by namespace http://www.idpf.org/2007/opf
 *
 * @author paul
 *
 */
public class PackageDocumentWriter implements PackageDocumentBase {

    private static final Logger log = LoggerFactory.getLogger(PackageDocumentWriter.class);

    public static void write(EpubWriter epubWriter, XmlSerializer serializer, Book book) throws IOException {
        try {
            serializer.startDocument(Constants.CHARACTER_ENCODING, false);
            serializer.setPrefix(PREFIX_OPF, NAMESPACE_OPF);
            serializer.setPrefix(PREFIX_DUBLIN_CORE, NAMESPACE_DUBLIN_CORE);
            serializer.startTag(NAMESPACE_OPF, OPFElements.PACKAGE);
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.VERSION, "2.0");
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.UNIQUE_IDENTIFIER, BOOK_ID);

            PackageDocumentMetadataWriter.writeMetaData(book, serializer);

            writeManifest(book, epubWriter, serializer);
            writeSpine(book, epubWriter, serializer);
            writeGuide(book, epubWriter, serializer);

            serializer.endTag(NAMESPACE_OPF, OPFElements.PACKAGE);
            serializer.endDocument();
            serializer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Writes the package's spine.
     *
     * @param book
     * @param epubWriter
     * @param serializer
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws XMLStreamException
     */
    private static void writeSpine(Book book, EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(NAMESPACE_OPF, OPFElements.SPINE);
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.TOC, book.getSpine().getTocResource().getId());

        if(book.getCoverPage() != null // there is a cover page
            &&    book.getSpine().findFirstResourceById(book.getCoverPage().getId()) < 0) { // cover page is not already in the spine
            // write the cover html file
            serializer.startTag(NAMESPACE_OPF, OPFElements.ITEMREF);
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.IDREF, book.getCoverPage().getId());
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.LINEAR, "no");
            serializer.endTag(NAMESPACE_OPF, OPFElements.ITEMREF);
        }
        writeSpineItems(book.getSpine(), serializer);
        serializer.endTag(NAMESPACE_OPF, OPFElements.SPINE);
    }

    private static void writeManifest(Book book, EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(NAMESPACE_OPF, OPFElements.MANIFEST);

        serializer.startTag(NAMESPACE_OPF, OPFElements.ITEM);
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.ID, epubWriter.getNcxId());
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.HREF, epubWriter.getNcxHref());
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.MEDIA_TYPE, epubWriter.getNcxMediaType());
        serializer.endTag(NAMESPACE_OPF, OPFElements.ITEM);

//        writeCoverResources(book, serializer);

        for(Resource resource: getAllResourcesSortById(book)) {
            writeItem(book, resource, serializer);
        }

        serializer.endTag(NAMESPACE_OPF, OPFElements.MANIFEST);
    }

    private static List<Resource> getAllResourcesSortById(Book book) {
        List<Resource> allResources = new ArrayList<Resource>(book.getResources().getAll());
        Collections.sort(allResources, new Comparator<Resource>() {
            @Override
            public int compare(Resource resource1, Resource resource2) {
                return resource1.getId().compareToIgnoreCase(resource2.getId());
            }
        });
        return allResources;
    }

    /**
     * Writes a resources as an item element
     * @param resource
     * @param serializer
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     * @throws XMLStreamException
     */
    private static void writeItem(Book book, Resource resource, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        if(resource == null ||
                (resource.getMediaType() == MediatypeService.NCX
                && book.getSpine().getTocResource() != null)) {
            return;
        }
        if(StringUtil.isBlank(resource.getId())) {
            log.error("resource id must not be empty (href: " + resource.getHref() + ", mediatype:" + resource.getMediaType() + ")");
            return;
        }
        if(StringUtil.isBlank(resource.getHref())) {
            log.error("resource href must not be empty (id: " + resource.getId() + ", mediatype:" + resource.getMediaType() + ")");
            return;
        }
        if(resource.getMediaType() == null) {
            log.error("resource mediatype must not be empty (id: " + resource.getId() + ", href:" + resource.getHref() + ")");
            return;
        }
        serializer.startTag(NAMESPACE_OPF, OPFElements.ITEM);
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.ID, resource.getId());
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.HREF, resource.getHref());
        serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.MEDIA_TYPE, resource.getMediaType().getName());
        serializer.endTag(NAMESPACE_OPF, OPFElements.ITEM);
    }

    /**
     * List all spine references
     * @throws IOException
     * @throws IllegalStateException
     * @throws IllegalArgumentException
     */
    private static void writeSpineItems(Spine spine, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        for (SpineReference spineReference: spine.getSpineReferences()) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.ITEMREF);
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.IDREF, spineReference.getResourceId());
            if (! spineReference.isLinear()) {
                serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.LINEAR, OPFValues.NO);
            }
            serializer.endTag(NAMESPACE_OPF, OPFElements.ITEMREF);
        }
    }

    private static void writeGuide(Book book, EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(NAMESPACE_OPF, OPFElements.GUIDE);
        ensureCoverPageGuideReferenceWritten(book.getGuide(), epubWriter, serializer);
        for (GuideReference reference: book.getGuide().getReferences()) {
            writeGuideReference(reference, serializer);
        }
        serializer.endTag(NAMESPACE_OPF, OPFElements.GUIDE);
    }

    private static void ensureCoverPageGuideReferenceWritten(Guide guide, EpubWriter epubWriter, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        if (guide.getGuideReferencesByType(GuideReference.COVER).isEmpty()) {
            Resource coverPage = guide.getCoverPage();
            if (coverPage != null) {
                writeGuideReference(new GuideReference(guide.getCoverPage(), GuideReference.COVER, GuideReference.COVER), serializer);
            }
        }
    }

    private static void writeGuideReference(GuideReference reference, XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        if (reference != null) {
            serializer.startTag(NAMESPACE_OPF, OPFElements.REFERENCE);
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.TYPE, reference.getType());
            serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.HREF, reference.getCompleteHref());
            if (StringUtil.isNotBlank(reference.getTitle())) {
                serializer.attribute(PackageDocumentBase.PREFIX_EMPTY, OPFAttributes.TITLE, reference.getTitle());
            }
            serializer.endTag(NAMESPACE_OPF, OPFElements.REFERENCE);
        }
    }

    private PackageDocumentWriter() {
    }
}
