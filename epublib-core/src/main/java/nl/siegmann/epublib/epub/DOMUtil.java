package nl.siegmann.epublib.epub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.siegmann.epublib.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class provides utility methods for parsing XML.
 */
final class DOMUtil {
    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DOMUtil() {
        super();
    }

    /**
     * Get the first occurrence of an element of a given namespace and element
     * name that is a descendant of a specific parent element.
     *
     * @param parentElement the parent element to get the descendant of
     * @param namespace the namespace of the element to get
     * @param element the name of the element to get
     * @return the first occurrence of the element
     */
    public static Element getFirstElement(final Element parentElement, final String namespace, final String element) {
        NodeList elements = parentElement.getElementsByTagNameNS(namespace, element);
        return elements.getLength() > 0 ? (Element) elements.item(0) : null;
    }

    /**
     * Get the text of all elements of a given namespace and element name that
     * are descendants of a specific parent element.
     *
     * @param parentElement the parent element to get the descendants of
     * @param namespace the namespace of the elements to get the text of
     * @param element the name of the elements to get the text of
     * @return the text
     */
    public static List<String> getElementsTextContent(final Element parentElement, final String namespace, final String element) {
        List<String> text = Collections.emptyList();
        NodeList elements = parentElement.getElementsByTagNameNS(namespace, element);
        if (elements.getLength() > 0) {
            text = new ArrayList<>(elements.getLength());
            for (int i = 0; i < elements.getLength(); ++i) {
                text.add(getElementTextContent((Element) elements.item(i)));
            }
        }
        return text;
    }

    /**
     * Get the content of all text nodes that are descendants of a specific
     * node.
     *
     * The reason for this more complicated procedure instead of just returning
     * the data of the firstChild is that when the text is Chinese characters
     * then on Android each Character is represented in the DOM as an individual
     * Text node.
     *
     * @param node the node to get the content for
     * @return the text content
     */
    public static String getElementTextContent(final Element node) {
        String text = "";
        if (node != null) {
            StringBuilder buf = new StringBuilder();
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node child = childNodes.item(i);
                if (child != null && child.getNodeType() == Node.TEXT_NODE) {
                    buf.append(((Text) child).getData());
                }
            }
            text = buf.toString().trim();
        }
        return text;
    }

    /**
     * First tries to get the attribute value by doing an getAttributeNS on the element, if that gets an empty element it does a getAttribute without namespace.
     *
     * @param element
     * @param namespace
     * @param attribute
     * @return
     */
    public static String getAttributeValue(Element element, String namespace, String attribute) {
        String result = element.getAttributeNS(namespace, attribute);
        if (StringUtil.isEmpty(result)) {
            result = element.getAttribute(attribute);
        }
        return result;
    }

    /**
     * Finds in the current document the first element with the given namespace and elementName and with the given findAttributeName and findAttributeValue.
     * It then returns the value of the given resultAttributeName.
     *
     * @param document
     * @param namespace
     * @param elementName
     * @param findAttributeName
     * @param findAttributeValue
     * @param resultAttributeName
     * @return
     */
    public static String getAttributeValue(Document document, String namespace, String elementName, String findAttributeName, String findAttributeValue, String resultAttributeName) {
        NodeList metaTags = document.getElementsByTagNameNS(namespace, elementName);
        for(int i = 0; i < metaTags.getLength(); i++) {
            Element metaElement = (Element) metaTags.item(i);
            if(findAttributeValue.equalsIgnoreCase(metaElement.getAttribute(findAttributeName))
                && StringUtil.isNotBlank(metaElement.getAttribute(resultAttributeName))) {
                return metaElement.getAttribute(resultAttributeName);
            }
        }
        return null;
    }
}
