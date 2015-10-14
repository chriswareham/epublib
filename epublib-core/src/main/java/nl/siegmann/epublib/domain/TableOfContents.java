package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The table of contents of the book.
 * The TableOfContents is a tree structure at the root it is a list of TOCReferences, each if which may have as children another list of TOCReferences.
 *
 * The table of contents is used by epub as a quick index to chapters and sections within chapters.
 * It may contain duplicate entries, may decide to point not to certain chapters, etc.
 *
 * See the spine for the complete list of sections in the order in which they should be read.
 *
 * @author paul
 * @see nl.siegmann.epublib.domain.Spine
 */
public class TableOfContents implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_PATH_SEPARATOR = "/";

    private List<TOCReference> tocReferences;

    public TableOfContents() {
        this(new ArrayList<>());
    }

    public TableOfContents(final List<TOCReference> tocReferences) {
        this.tocReferences = tocReferences;
    }

    public List<TOCReference> getTocReferences() {
        return tocReferences;
    }

    public void setTocReferences(final List<TOCReference> tocReferences) {
        this.tocReferences = tocReferences;
    }

    /**
     * Calls addTOCReferenceAtLocation after splitting the path using the DEFAULT_PATH_SEPARATOR.
     *
     * @return the new TOCReference
     */
    public TOCReference addSection(final Resource resource, final String path) {
        return addSection(resource, path, DEFAULT_PATH_SEPARATOR);
    }

    /**
     * Calls addTOCReferenceAtLocation after splitting the path using the given pathSeparator.
     *
     * @param resource the resource to add
     * @param path the location to add the resource at
     * @param pathSeparator the path separator for the location
     * @return the new TOCReference
     */
    public TOCReference addSection(final Resource resource, final String path, final String pathSeparator) {
        return addSection(resource, path.split(pathSeparator));
    }

    /**
     * Adds the given Resources to the TableOfContents at the location specified by the pathElements.
     *
     * Example:
     * Calling this method with a Resource and new String[] {"chapter1", "paragraph1"} will result in the following:
     * <ul>
     * <li>a TOCReference with the title "chapter1" at the root level.<br/>
     * If this TOCReference did not yet exist it will have been created and does not point to any resource</li>
     * <li>A TOCReference that has the title "paragraph1". This TOCReference will be the child of TOCReference "chapter1" and
     * will point to the given Resource</li>
     * </ul>
     *
     * @param resource the resource to add
     * @param pathElements the location to add the resource at
     * @return the new TOCReference
     */
    public TOCReference addSection(final Resource resource, final String[] pathElements) {
        if (pathElements == null || pathElements.length == 0) {
            return null;
        }
        TOCReference result = null;
        List<TOCReference> currentTocReferences = this.tocReferences;
        for (int i = 0; i < pathElements.length; i++) {
            String currentTitle = pathElements[i];
            result = findTocReferenceByTitle(currentTitle, currentTocReferences);
            if (result == null) {
                result = new TOCReference(currentTitle, null);
                currentTocReferences.add(result);
            }
            currentTocReferences = result.getChildren();
        }
        result.setResource(resource);
        return result;
    }

    /**
     * Adds the given Resources to the TableOfContents at the location specified by the pathElements.
     *
     * Example:
     * Calling this method with a Resource and new int[] {0, 0} will result in the following:
     * <ul>
     * <li>a TOCReference at the root level.<br/>
     * If this TOCReference did not yet exist it will have been created with a title of "" and does not point to any resource</li>
     * <li>A TOCReference that points to the given resource and is a child of the previously created TOCReference.<br/>
     * If this TOCReference didn't exist yet it will be created and have a title of ""</li>
     * </ul>
     *
     * @param resource the resource to add
     * @param pathElements the location to add the resource at
     * @param sectionTitlePrefix the section title prefix
     * @param sectionNumberSeparator the section number separator
     * @return the new TOCReference
     */
    public TOCReference addSection(final Resource resource, final int[] pathElements, final String sectionTitlePrefix, final String sectionNumberSeparator) {
        if (pathElements == null || pathElements.length == 0) {
            return null;
        }
        TOCReference result = null;
        List<TOCReference> currentTocReferences = this.tocReferences;
        for (int i = 0; i < pathElements.length; ++i) {
            int currentIndex = pathElements[i];
            if (currentIndex > 0 && currentIndex < (currentTocReferences.size() - 1)) {
                result = currentTocReferences.get(currentIndex);
            } else {
                result = null;
            }
            if (result == null) {
                paddTOCReferences(currentTocReferences, pathElements, i, sectionTitlePrefix, sectionNumberSeparator);
                result = currentTocReferences.get(currentIndex);
            }
            currentTocReferences = result.getChildren();
        }
        result.setResource(resource);
        return result;
    }

    public TOCReference addTOCReference(final TOCReference tocReference) {
        if (tocReferences == null) {
            tocReferences = new ArrayList<>();
        }
        tocReferences.add(tocReference);
        return tocReference;
    }

    /**
     * All unique references (unique by href) in the order in which they are referenced to in the table of contents.
     *
     * @return All unique references (unique by href) in the order in which they are referenced to in the table of contents.
     */
    public List<Resource> getAllUniqueResources() {
        Set<String> uniqueHrefs = new HashSet<>();
        List<Resource> result = new ArrayList<>();
        getAllUniqueResources(uniqueHrefs, result, tocReferences);
        return result;
    }

    /**
     * The total number of references in this table of contents.
     *
     * @return The total number of references in this table of contents.
     */
    public int size() {
        return getTotalSize(tocReferences);
    }

    /**
     * The maximum depth of the reference tree.
     *
     * @return The maximum depth of the reference tree
     */
    public int calculateDepth() {
        return calculateDepth(tocReferences, 0);
    }

    private void paddTOCReferences(final List<TOCReference> currentTocReferences, final int[] pathElements, final int pathPos, final String sectionPrefix, final String sectionNumberSeparator) {
        for (int i = currentTocReferences.size(); i <= pathElements[pathPos]; ++i) {
            String sectionTitle = createSectionTitle(pathElements, pathPos, i, sectionPrefix, sectionNumberSeparator);
            currentTocReferences.add(new TOCReference(sectionTitle, null));
        }
    }

    private String createSectionTitle(final int[] pathElements, final int pathPos, final int lastPos, final String sectionPrefix, final String sectionNumberSeparator) {
        StringBuilder title = new StringBuilder(sectionPrefix);
        for (int i = 0; i < pathPos; ++i) {
            if (i > 0) {
                title.append(sectionNumberSeparator);
            }
            title.append(pathElements[i] + 1);
        }
        if (pathPos > 0) {
            title.append(sectionNumberSeparator);
        }
        title.append(lastPos + 1);
        return title.toString();
    }

    private static int calculateDepth(final List<TOCReference> tocReferences, final int currentDepth) {
        int maxChildDepth = 0;
        for (TOCReference tocReference: tocReferences) {
            int childDepth = calculateDepth(tocReference.getChildren(), 1);
            if (childDepth > maxChildDepth) {
                maxChildDepth = childDepth;
            }
        }
        return currentDepth + maxChildDepth;
    }

    /**
     * Finds the first TOCReference in the given list that has the same title as the given Title.
     *
     * @param title the title to find
     * @param tocReferences the references it find the title in
     * @return null if not found.
     */
    private static TOCReference findTocReferenceByTitle(final String title, final List<TOCReference> tocReferences) {
        for (TOCReference tocReference: tocReferences) {
            if (title.equals(tocReference.getTitle())) {
                return tocReference;
            }
        }
        return null;
    }

    private static void getAllUniqueResources(final Set<String> uniqueHrefs, final List<Resource> result, final List<TOCReference> tocReferences) {
        for (TOCReference tocReference: tocReferences) {
            Resource resource = tocReference.getResource();
            if (resource != null && !uniqueHrefs.contains(resource.getHref())) {
                uniqueHrefs.add(resource.getHref());
                result.add(resource);
            }
            getAllUniqueResources(uniqueHrefs, result, tocReference.getChildren());
        }
    }

    private static int getTotalSize(final Collection<TOCReference> tocReferences) {
        int result = tocReferences.size();
        for (TOCReference tocReference: tocReferences) {
            result += getTotalSize(tocReference.getChildren());
        }
        return result;
    }
}
