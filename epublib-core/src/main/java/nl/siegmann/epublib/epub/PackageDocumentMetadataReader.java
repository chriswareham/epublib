package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Date;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Reads the package document metadata.
 *
 * In its own separate class because the PackageDocumentReader became a bit large and unwieldy.
 *
 * @author paul
 *
 */
public class PackageDocumentMetadataReader implements PackageDocumentBase {

    private static final Logger log = LoggerFactory.getLogger(PackageDocumentMetadataReader.class);

    public static Metadata readMetadata(Document packageDocument) {
        Metadata result = new Metadata();
        Element metadataElement = DOMUtil.getFirstElementByTagNameNS(packageDocument.getDocumentElement(), NAMESPACE_OPF, OPFElements.METADATA);
        if(metadataElement == null) {
            log.error("Package does not contain element " + OPFElements.METADATA);
            return result;
        }
        result.setTitles(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.TITLE));
        result.setPublishers(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.PUBLISHER));
        result.setDescriptions(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.DESCRIPTION));
        result.setRights(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.RIGHTS));
        result.setTypes(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.TYPE));
        result.setSubjects(DOMUtil.getElementsTextChild(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.SUBJECT));
        result.setIdentifiers(readIdentifiers(metadataElement));
        result.setAuthors(readCreators(metadataElement));
        result.setContributors(readContributors(metadataElement));
        result.setDates(readDates(metadataElement));
        result.setOtherProperties(readOtherProperties(metadataElement));
        result.setMetaAttributes(readMetaProperties(metadataElement));
        Element languageTag = DOMUtil.getFirstElementByTagNameNS(metadataElement, NAMESPACE_DUBLIN_CORE, DCElements.LANGUAGE);
        if (languageTag != null) {
            result.setLanguage(DOMUtil.getTextChildrenContent(languageTag));
        }

        return result;
    }

    /**
     * consumes meta tags that have a property attribute as defined in the standard. For example:
     * &lt;meta property="rendition:layout"&gt;pre-paginated&lt;/meta&gt;
     * @param metadataElement
     * @return
     */
    private static Map<QName, String> readOtherProperties(Element metadataElement) {
        Map<QName, String> result = new HashMap<QName, String>();

        NodeList metaTags = metadataElement.getElementsByTagNameNS(NAMESPACE_OPF, OPFElements.META);
        for (int i = 0; i < metaTags.getLength(); i++) {
            Node metaNode = metaTags.item(i);
            Node property = metaNode.getAttributes().getNamedItem(OPFAttributes.PROPERTY);
            if (property != null) {
                String name = property.getNodeValue();
                String value = metaNode.getTextContent();
                result.put(new QName(name), value);
            }
        }

        return result;
    }

    /**
     * consumes meta tags that have a property attribute as defined in the standard. For example:
     * &lt;meta property="rendition:layout"&gt;pre-paginated&lt;/meta&gt;
     * @param metadataElement
     * @return
     */
    private static Map<String, String> readMetaProperties(Element metadataElement) {
        Map<String, String> result = new HashMap<String, String>();

        NodeList metaTags = metadataElement.getElementsByTagName(OPFElements.META);
        for (int i = 0; i < metaTags.getLength(); i++) {
            Element metaElement = (Element) metaTags.item(i);
            String name = metaElement.getAttribute(OPFAttributes.NAME);
            String value = metaElement.getAttribute(OPFAttributes.CONTENT);
            result.put(name,  value);
        }

        return result;
    }

    private static String getBookIdId(Document document) {
        Element packageElement = DOMUtil.getFirstElementByTagNameNS(document.getDocumentElement(), NAMESPACE_OPF, OPFElements.PACKAGE);
        if(packageElement == null) {
            return null;
        }
        String result = packageElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.UNIQUE_IDENTIFIER);
        return result;
    }

    private static List<Author> readCreators(Element metadataElement) {
        return readAuthors(DCElements.CREATOR, metadataElement);
    }

    private static List<Author> readContributors(Element metadataElement) {
        return readAuthors(DCElements.CONTRIBUTOR, metadataElement);
    }

    private static List<Author> readAuthors(String authorTag, Element metadataElement) {
        NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, authorTag);
        List<Author> result = new ArrayList<Author>(elements.getLength());
        for(int i = 0; i < elements.getLength(); i++) {
            Element authorElement = (Element) elements.item(i);
            Author author = createAuthor(authorElement);
            if (author != null) {
                result.add(author);
            }
        }
        return result;

    }

    private static List<Date> readDates(Element metadataElement) {
        NodeList elements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCElements.DATE);
        List<Date> result = new ArrayList<Date>(elements.getLength());
        for(int i = 0; i < elements.getLength(); i++) {
            Element dateElement = (Element) elements.item(i);
            Date date;
            try {
                date = new Date(DOMUtil.getTextChildrenContent(dateElement), dateElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.EVENT));
                result.add(date);
            } catch(IllegalArgumentException e) {
                log.error(e.getMessage());
            }
        }
        return result;

    }

    private static Author createAuthor(Element authorElement) {
        String authorString = DOMUtil.getTextChildrenContent(authorElement);
        if (StringUtil.isBlank(authorString)) {
            return null;
        }
        int spacePos = authorString.lastIndexOf(' ');
        Author result;
        if(spacePos < 0) {
            result = new Author(authorString);
        } else {
            result = new Author(authorString.substring(0, spacePos), authorString.substring(spacePos + 1));
        }
        result.setRole(authorElement.getAttributeNS(NAMESPACE_OPF, OPFAttributes.ROLE));
        return result;
    }

    private static List<Identifier> readIdentifiers(Element metadataElement) {
        NodeList identifierElements = metadataElement.getElementsByTagNameNS(NAMESPACE_DUBLIN_CORE, DCElements.IDENTIFIER);
        if(identifierElements.getLength() == 0) {
            log.error("Package does not contain element " + DCElements.IDENTIFIER);
            return new ArrayList<Identifier>();
        }
        String bookIdId = getBookIdId(metadataElement.getOwnerDocument());
        List<Identifier> result = new ArrayList<Identifier>(identifierElements.getLength());
        for(int i = 0; i < identifierElements.getLength(); i++) {
            Element identifierElement = (Element) identifierElements.item(i);
            String schemeName = identifierElement.getAttributeNS(NAMESPACE_OPF, DCAttributes.SCHEME);
            String identifierValue = DOMUtil.getTextChildrenContent(identifierElement);
            if (StringUtil.isBlank(identifierValue)) {
                continue;
            }
            Identifier identifier = new Identifier(schemeName, identifierValue);
            if(identifierElement.getAttribute("id").equals(bookIdId) ) {
                identifier.setBookId(true);
            }
            result.add(identifier);
        }
        return result;
    }

    private PackageDocumentMetadataReader() {
    }
}
