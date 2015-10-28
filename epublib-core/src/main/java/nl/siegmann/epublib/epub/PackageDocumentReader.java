package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;


/**
 * This class provides a reader for EPUB 3.0 OPF package documents.
 */
public class PackageDocumentReader implements PackageDocumentBase {
    /**
     * The identifiers for NCX items.
     */
    private static final String[] NCX_ITEM_IDS = new String[] {"toc", "ncx"};

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private PackageDocumentReader() {
        super();
    }

    /**
     * Read an EPUB 3.0 OPF package document.
     *
     * @param resource the package document resource
     * @param book the book to read the package document for
     * @param resources the resources
     * @throws IOException if an I/O error occurs
     */
    public static void read(final Resource resource, final Book book, final Resources resources) throws IOException {
        try {
            Document packageDocument = ResourceUtil.getAsDocument(resource);

            Resources localResources = fixHrefs(resource.getHref(), resources);

            readGuide(packageDocument, book, localResources);

            // Books sometimes use non-identifier ids. We map these here to legal ones.
            Map<String, String> idMapping = new HashMap<>();

            localResources = readManifest(packageDocument, localResources, idMapping);
            book.setResources(localResources);

            readCover(packageDocument, book);

            book.setMetadata(PackageDocumentMetadataReader.readMetadata(packageDocument));

            book.setSpine(readSpine(packageDocument, book.getResources(), idMapping));

            // If we did not find a cover page then we make the first page of the book the cover page.
            if (book.getCoverPage() == null && !book.getSpine().isEmpty()) {
                book.setCoverPage(book.getSpine().getResource(0));
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to read package document", e);
        }
    }

    /**
     * Reads the manifest containing the resource ids, hrefs and mediatypes.
     *
     * @param packageDocument
     * @param packageHref
     * @param epubReader
     * @param book
     * @param resourcesByHref
     * @return a Map with resources, with their id's as key.
     */
    private static Resources readManifest(Document packageDocument, Resources resources, Map<String, String> idMapping) {
        Element manifestElement = DOMUtil.getFirstElement(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFElements.MANIFEST);
        Resources result = new Resources();
        if(manifestElement == null) {
            // XXX package document doesn't contain manifest element
            return result;
        }
        NodeList itemElements = manifestElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFElements.ITEM);
        for(int i = 0; i < itemElements.getLength(); i++) {
            Element itemElement = (Element) itemElements.item(i);
            String id = DOMUtil.getAttributeValue(itemElement, NAMESPACE_OPF, OPFAttributes.ID);
            String href = DOMUtil.getAttributeValue(itemElement, NAMESPACE_OPF, OPFAttributes.HREF);
            try {
                href = URLDecoder.decode(href, Constants.CHARACTER_ENCODING);
            } catch (UnsupportedEncodingException e) {
                continue;
            }
            String mediaTypeName = DOMUtil.getAttributeValue(itemElement, NAMESPACE_OPF, OPFAttributes.MEDIA_TYPE);
            Resource resource = resources.remove(href);
            if(resource == null) {
                // resource not found
                continue;
            }
            resource.setId(id);
            MediaType mediaType = MediatypeService.getMediaTypeByName(mediaTypeName);
            if(mediaType != null) {
                resource.setMediaType(mediaType);
            }
            result.add(resource);
            idMapping.put(id, resource.getId());
        }
        return result;
    }

    /**
     * Reads the book's guide.
     * Here some more attempts are made at finding the cover page.
     *
     * @param packageDocument
     * @param epubReader
     * @param book
     * @param resources
     */
    private static void readGuide(Document packageDocument, Book book, Resources resources) {
        Element guideElement = DOMUtil.getFirstElement(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFElements.GUIDE);
        if(guideElement == null) {
            return;
        }
        Guide guide = book.getGuide();
        NodeList guideReferences = guideElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFElements.REFERENCE);
        for (int i = 0; i < guideReferences.getLength(); i++) {
            Element referenceElement = (Element) guideReferences.item(i);
            String resourceHref = DOMUtil.getAttributeValue(referenceElement, NAMESPACE_OPF, OPFAttributes.HREF);
            if (StringUtil.isBlank(resourceHref)) {
                continue;
            }
            Resource resource = resources.getByHref(StringUtil.substringBefore(resourceHref, Constants.FRAGMENT_SEPARATOR_CHAR));
            if (resource == null) {
                continue;
            }
            String type = DOMUtil.getAttributeValue(referenceElement, NAMESPACE_OPF, OPFAttributes.TYPE);
            if (StringUtil.isBlank(type)) {
                continue;
            }
            String title = DOMUtil.getAttributeValue(referenceElement, NAMESPACE_OPF, OPFAttributes.TITLE);
            if (GuideReference.COVER.equalsIgnoreCase(type)) {
                continue; // cover is handled elsewhere
            }
            GuideReference reference = new GuideReference(resource, type, title, StringUtil.substringAfter(resourceHref, Constants.FRAGMENT_SEPARATOR_CHAR));
            guide.addReference(reference);
        }
    }

    /**
     * Strips off the package prefixes up to the href of the packageHref.
     *
     * Example:
     * If the packageHref is "OEBPS/content.opf" then a resource href like "OEBPS/foo/bar.html" will be turned into "foo/bar.html"
     *
     * @param packageHref
     * @param resourcesByHref
     * @return The stipped package href
     */
    private static Resources fixHrefs(String packageHref, Resources resourcesByHref) {
        int lastSlashPos = packageHref.lastIndexOf('/');
        if(lastSlashPos < 0) {
            return resourcesByHref;
        }
        Resources result = new Resources();
        for(Resource resource: resourcesByHref.getAll()) {
            if(StringUtil.isNotBlank(resource.getHref())
                    || resource.getHref().length() > lastSlashPos) {
                resource.setHref(resource.getHref().substring(lastSlashPos + 1));
            }
            result.add(resource);
        }
        return result;
    }

    /**
     * Reads the document's spine, containing all sections in reading order.
     *
     * @param packageDocument
     * @param epubReader
     * @param book
     * @param resourcesById
     * @return the document's spine, containing all sections in reading order.
     */
    private static Spine readSpine(Document packageDocument, Resources resources, Map<String, String> idMapping) {

        Element spineElement = DOMUtil.getFirstElement(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFElements.SPINE);
        if (spineElement == null) {
            return generateSpineFromResources(resources);
        }
        Spine result = new Spine();
        result.setTocResource(findTableOfContentsResource(spineElement, resources));
        NodeList spineNodes = packageDocument.getElementsByTagNameNS(NAMESPACE_OPF, OPFElements.ITEMREF);
        List<SpineReference> spineReferences = new ArrayList<SpineReference>(spineNodes.getLength());
        for(int i = 0; i < spineNodes.getLength(); i++) {
            Element spineItem = (Element) spineNodes.item(i);
            String itemref = DOMUtil.getAttributeValue(spineItem, NAMESPACE_OPF, OPFAttributes.IDREF);
            if(StringUtil.isBlank(itemref)) {
                continue;
            }
            String id = idMapping.get(itemref);
            if (id == null) {
                id = itemref;
            }
            Resource resource = resources.getByIdOrHref(id);
            if(resource == null) {
                continue;
            }

            SpineReference spineReference = new SpineReference(resource);
            if (OPFValues.NO.equalsIgnoreCase(DOMUtil.getAttributeValue(spineItem, NAMESPACE_OPF, OPFAttributes.LINEAR))) {
                spineReference.setLinear(false);
            }
            spineReferences.add(spineReference);
        }
        result.setSpineReferences(spineReferences);
        return result;
    }

    /**
     * Creates a spine out of all resources in the resources.
     * The generated spine consists of all XHTML pages in order of their href.
     *
     * @param resources
     * @return a spine created out of all resources in the resources.
     */
    private static Spine generateSpineFromResources(Resources resources) {
        Spine result = new Spine();
        List<String> resourceHrefs = new ArrayList<>();
        resourceHrefs.addAll(resources.getAllHrefs());
        Collections.sort(resourceHrefs, String.CASE_INSENSITIVE_ORDER);
        for (String resourceHref: resourceHrefs) {
            Resource resource = resources.getByHref(resourceHref);
            if (resource.getMediaType() == MediatypeService.NCX) {
                result.setTocResource(resource);
            } else if (resource.getMediaType() == MediatypeService.XHTML) {
                result.addSpineReference(new SpineReference(resource));
            }
        }
        return result;
    }

    /**
     * The spine tag should contain a 'toc' attribute with as value the resource id of the table of contents resource.
     *
     * Here we try several ways of finding this table of contents resource.
     * We try the given attribute value, some often-used ones and finally look through all resources for the first resource with the table of contents mimetype.
     *
     * @param spineElement
     * @param resourcesById
     * @return the Resource containing the table of contents
     */
    private static Resource findTableOfContentsResource(Element spineElement, Resources resources) {
        String tocResourceId = DOMUtil.getAttributeValue(spineElement, NAMESPACE_OPF, OPFAttributes.TOC);
        Resource tocResource = null;
        if (StringUtil.isNotBlank(tocResourceId)) {
            tocResource = resources.getByIdOrHref(tocResourceId);
        }

        if (tocResource != null) {
            return tocResource;
        }

        for (String ncxItemId : NCX_ITEM_IDS) {
            tocResource = resources.getByIdOrHref(ncxItemId);
            if (tocResource != null) {
                return tocResource;
            }
            tocResource = resources.getByIdOrHref(ncxItemId.toUpperCase());
            if (tocResource != null) {
                return tocResource;
            }
        }

        // get the first resource with the NCX mediatype
        tocResource = resources.findFirstResourceByMediaType(MediatypeService.NCX);

        return tocResource;
    }

    /**
     * Find all resources that have something to do with the coverpage and the cover image.
     * Search the meta tags and the guide references
     *
     * @param packageDocument
     * @return all resources that have something to do with the coverpage and the cover image.
     */
    // package
    static Set<String> findCoverHrefs(Document packageDocument) {

        Set<String> result = new HashSet<>();

        // try and find a meta tag with name = 'cover' and a non-blank id
        String coverResourceId = DOMUtil.getAttributeValue(packageDocument, NAMESPACE_OPF,
                                            OPFElements.META, OPFAttributes.NAME, OPFValues.META_COVER,
                                            OPFAttributes.CONTENT);

        if (StringUtil.isNotBlank(coverResourceId)) {
            String coverHref = DOMUtil.getAttributeValue(packageDocument, NAMESPACE_OPF,
                    OPFElements.ITEM, OPFAttributes.ID, coverResourceId,
                    OPFAttributes.HREF);
            if (StringUtil.isNotBlank(coverHref)) {
                result.add(coverHref);
            } else {
                result.add(coverResourceId); // maybe there was a cover href put in the cover id attribute
            }
        }
        // try and find a reference tag with type is 'cover' and reference is not blank
        String coverHref = DOMUtil.getAttributeValue(packageDocument, NAMESPACE_OPF,
                                            OPFElements.REFERENCE, OPFAttributes.TYPE, OPFValues.REFERENCE_COVER,
                                            OPFAttributes.HREF);
        if (StringUtil.isNotBlank(coverHref)) {
            result.add(coverHref);
        }
        return result;
    }

    /**
     * Finds the cover resource in the packageDocument and adds it to the book if found.
     * Keeps the cover resource in the resources map
     * @param packageDocument
     * @param book
     * @param resources
     */
    private static void readCover(Document packageDocument, Book book) {
        Collection<String> coverHrefs = findCoverHrefs(packageDocument);
        for (String coverHref: coverHrefs) {
            Resource resource = book.getResources().getByHref(coverHref);
            if (resource != null) {
                if (resource.getMediaType() == MediatypeService.XHTML) {
                    book.setCoverPage(resource);
                } else if (MediatypeService.isBitmapImage(resource.getMediaType())) {
                    book.setCoverImage(resource);
                }
            }
        }
    }
}
