package nl.siegmann.epublib.browsersupport;

import java.util.EventObject;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.StringUtil;

/**
 * This class describes a navigation event.
 *
 * @author paul
 */
public class NavigationEvent extends EventObject {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    private Resource oldResource;

    private int oldSpinePos;

    private Navigator navigator;

    private Book oldBook;

    private int oldSectionPos;

    private String oldFragmentId;

    public NavigationEvent(final Object source) {
        super(source);
    }

    public NavigationEvent(final Object source, final Navigator navigator) {
        super(source);
        this.navigator = navigator;
        this.oldBook = navigator.getBook();
        this.oldFragmentId = navigator.getCurrentFragmentId();
        this.oldSectionPos = navigator.getCurrentSectionPos();
        this.oldResource = navigator.getCurrentResource();
        this.oldSpinePos = navigator.getCurrentSpinePos();
    }

    /**
     * The previous position within the section.
     *
     * @return The previous position within the section.
     */
    public int getOldSectionPos() {
        return oldSectionPos;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public String getOldFragmentId() {
        return oldFragmentId;
    }

    // package
    void setOldFragmentId(final String oldFragmentId) {
        this.oldFragmentId = oldFragmentId;
    }

    public Book getOldBook() {
        return oldBook;
    }

    // package
    void setOldPagePos(final int oldPagePos) {
        this.oldSectionPos = oldPagePos;
    }

    public int getCurrentSectionPos() {
        return navigator.getCurrentSectionPos();
    }

    public int getOldSpinePos() {
        return oldSpinePos;
    }

    public int getCurrentSpinePos() {
        return navigator.getCurrentSpinePos();
    }

    public String getCurrentFragmentId() {
        return navigator.getCurrentFragmentId();
    }

    public boolean isBookChanged() {
        if (oldBook == null) {
            return true;
        }
        return oldBook != navigator.getBook();
    }

    public boolean isSpinePosChanged() {
        return oldSpinePos != navigator.getCurrentSpinePos();
    }

    public boolean isFragmentChanged() {
        return StringUtil.equals(oldFragmentId, navigator.getCurrentFragmentId());
    }

    public Resource getOldResource() {
        return oldResource;
    }

    public Resource getCurrentResource() {
        return navigator.getCurrentResource();
    }

    public void setOldResource(final Resource oldResource) {
        this.oldResource = oldResource;
    }

    public void setOldSpinePos(final int oldSpinePos) {
        this.oldSpinePos = oldSpinePos;
    }

    public void setNavigator(final Navigator navigator) {
        this.navigator = navigator;
    }

    public void setOldBook(final Book oldBook) {
        this.oldBook = oldBook;
    }

    public Book getCurrentBook() {
        return navigator.getBook();
    }

    public boolean isResourceChanged() {
        return oldResource != navigator.getCurrentResource();
    }

    public boolean isSectionPosChanged() {
        return oldSectionPos != navigator.getCurrentSectionPos();
    }

    @Override
    public String toString() {
        return StringUtil.toString(
                "oldSectionPos", oldSectionPos,
                "oldResource", oldResource,
                "oldBook", oldBook,
                "oldFragmentId", oldFragmentId,
                "oldSpinePos", oldSpinePos,
                "currentPagePos", navigator.getCurrentSectionPos(),
                "currentResource", navigator.getCurrentResource(),
                "currentBook", navigator.getBook(),
                "currentFragmentId", navigator.getCurrentFragmentId(),
                "currentSpinePos", navigator.getCurrentSpinePos()
        );
    }
}
