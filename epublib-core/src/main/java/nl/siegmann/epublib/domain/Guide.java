package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The guide is a selection of special pages of the book.
 * Examples of these are the cover, list of illustrations, etc.
 *
 * It is an optional part of an epub, and support for the various types of references varies by reader.
 *
 * The only part of this that is heavily used is the cover page.
 */
public class Guide implements Serializable {

    public static final String DEFAULT_COVER_TITLE = GuideReference.COVER;
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -6256645339915751189L;

    private static final int COVERPAGE_NOT_FOUND = -1;

    private static final int COVERPAGE_UNINITIALISED = -2;

    private final List<GuideReference> references = new ArrayList<>();

    private int coverPageIndex = COVERPAGE_NOT_FOUND;

    public boolean isEmpty() {
        return references.isEmpty();
    }

    public List<GuideReference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public void setReferences(final List<GuideReference> references) {
        this.references.clear();
        if (references != null) {
            this.references.addAll(references);
        }
        uncheckCoverPage();
    }

    public GuideReference getCoverReference() {
        checkCoverPage();
        if (coverPageIndex >= 0) {
            return references.get(coverPageIndex);
        }
        return null;
    }

    public int setCoverReference(GuideReference guideReference) {
        if (coverPageIndex >= 0) {
            references.set(coverPageIndex, guideReference);
        } else {
            references.add(0, guideReference);
            coverPageIndex = 0;
        }
        return coverPageIndex;
    }

    /**
     * The coverpage of the book.
     *
     * @return The coverpage of the book.
     */
    public Resource getCoverPage() {
        GuideReference guideReference = getCoverReference();
        if (guideReference == null) {
            return null;
        }
        return guideReference.getResource();
    }

    public void setCoverPage(Resource coverPage) {
        GuideReference coverpageGuideReference = new GuideReference(coverPage, GuideReference.COVER, DEFAULT_COVER_TITLE);
        setCoverReference(coverpageGuideReference);
    }

    public ResourceReference addReference(GuideReference reference) {
        this.references.add(reference);
        uncheckCoverPage();
        return reference;
    }

    /**
     * A list of all GuideReferences that have the given referenceTypeName (ignoring case).
     *
     * @param referenceTypeName
     * @return A list of all GuideReferences that have the given referenceTypeName (ignoring case).
     */
    public List<GuideReference> getGuideReferencesByType(String referenceTypeName) {
        List<GuideReference> result = new ArrayList<>();
        for (GuideReference guideReference: references) {
            if (referenceTypeName.equalsIgnoreCase(guideReference.getType())) {
                result.add(guideReference);
            }
        }
        return result;
    }

    private void checkCoverPage() {
        if (coverPageIndex == COVERPAGE_UNINITIALISED) {
            int result = COVERPAGE_NOT_FOUND;
            for (int i = 0; i < references.size(); i++) {
                GuideReference guideReference = references.get(i);
                if (guideReference.getType().equals(GuideReference.COVER)) {
                    result = i;
                    break;
                }
            }
            coverPageIndex = result;
        }
    }

    private void uncheckCoverPage() {
        coverPageIndex = COVERPAGE_UNINITIALISED;
    }
}
