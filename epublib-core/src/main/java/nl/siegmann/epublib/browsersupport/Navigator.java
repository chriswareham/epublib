package nl.siegmann.epublib.browsersupport;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

/**
 * A helper class for epub browser applications.
 *
 * It helps moving from one resource to the other, from one resource to the
 * other and keeping other elements of the application up-to-date by calling the
 * NavigationEventListeners.
 *
 * @author paul
 */
public class Navigator implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    private Book book;

    private Resource currentResource;

    private String currentFragmentId;

    private int currentSpinePos;

    private int currentPagePos;

    private final List<NavigationEventListener> eventListeners = new CopyOnWriteArrayList<>();

    public Navigator() {
        this(null);
    }

    public Navigator(final Book book) {
        this.book = book;
        if (book != null) {
            this.currentResource = book.getCoverPage();
        }
    }

    public Book getBook() {
        return book;
    }

    public int gotoFirstSpineSection(final Object source) {
        return gotoSpineSection(0, source);
    }

    public int gotoPreviousSpineSection(final Object source) {
        return gotoPreviousSpineSection(0, source);
    }

    public int gotoPreviousSpineSection(final int pagePos, final Object source) {
        if (currentSpinePos < 0) {
            return gotoSpineSection(0, pagePos, source);
        }
        return gotoSpineSection(currentSpinePos - 1, pagePos, source);
    }

    public boolean hasNextSpineSection() {
        return currentSpinePos < (book.getSpine().size() - 1);
    }

    public boolean hasPreviousSpineSection() {
        return currentSpinePos > 0;
    }

    public int gotoNextSpineSection(final Object source) {
        if (currentSpinePos < 0) {
            return gotoSpineSection(0, source);
        }
        return gotoSpineSection(currentSpinePos + 1, source);
    }

    public int gotoResource(final String resourceHref, final Object source) {
        return gotoResource(book.getResources().getByHref(resourceHref), source);
    }

    public int gotoResource(final Resource resource, final Object source) {
        return gotoResource(resource, 0, null, source);
    }

    public int gotoResource(final Resource resource, final String fragmentId, final Object source) {
        return gotoResource(resource, 0, fragmentId, source);
    }

    public int gotoResource(final Resource resource, final int pagePos, final Object source) {
        return gotoResource(resource, pagePos, null, source);
    }

    public int gotoResource(final Resource resource, final int pagePos, final String fragmentId, final Object source) {
        if (resource == null) {
            return -1;
        }

        currentResource = resource;
        currentSpinePos = book.getSpine().getResourceIndex(currentResource);
        currentPagePos = pagePos;
        currentFragmentId = fragmentId;

        handleEventListeners(new NavigationEvent(source, this));

        return currentSpinePos;
    }

    public int gotoResourceId(final String resourceId, final Object source) {
        return gotoSpineSection(book.getSpine().findFirstResourceById(resourceId), source);
    }

    public int gotoSpineSection(final int newSpinePos, final Object source) {
        return gotoSpineSection(newSpinePos, 0, source);
    }

    /**
     * Go to a specific section.
     * Illegal spine positions are silently ignored.
     *
     * @param newSpinePos the new spine position
     * @param newPagePos the new page position
     * @param source the source of the change of position
     * @return the current position within the spine
     */
    public int gotoSpineSection(final int newSpinePos, final int newPagePos, final Object source) {
        if (newSpinePos == currentSpinePos) {
            return currentSpinePos;
        }
        if (newSpinePos < 0 || newSpinePos >= book.getSpine().size()) {
            return currentSpinePos;
        }

        currentSpinePos = newSpinePos;
        currentPagePos = newPagePos;
        currentResource = book.getSpine().getResource(currentSpinePos);

        handleEventListeners(new NavigationEvent(source, this));

        return currentSpinePos;
    }

    public int gotoLastSpineSection(final Object source) {
        return gotoSpineSection(book.getSpine().size() - 1, source);
    }

    public void gotoBook(final Book newBook, final Object source) {
        book = newBook;
        currentFragmentId = null;
        currentPagePos = 0;
        currentResource = null;
        currentSpinePos = newBook.getSpine().getResourceIndex(currentResource);

        handleEventListeners(new NavigationEvent(source, this));
    }

    /**
     * The current position within the spine.
     *
     * @return something less than 0 if the current position is not within the spine.
     */
    public int getCurrentSpinePos() {
        return currentSpinePos;
    }

    public Resource getCurrentResource() {
        return currentResource;
    }

    /**
     * Sets the current index and resource without calling the event listeners.
     *
     * If you want the eventListeners called use gotoSection(index);
     *
     * @param newIndex the new index
     */
    public void setCurrentSpinePos(final int newIndex) {
        currentSpinePos = newIndex;
        currentResource = book.getSpine().getResource(newIndex);
    }

    /**
     * Sets the current index and resource without calling the eventlisteners.
     *
     * If you want the eventListeners called use gotoSection(index);
     *
     * @param newResource the new resource
     * @return the current index
     */
    public int setCurrentResource(final Resource newResource) {
        currentSpinePos = book.getSpine().getResourceIndex(newResource);
        currentResource = newResource;
        return currentSpinePos;
    }

    public String getCurrentFragmentId() {
        return currentFragmentId;
    }

    public int getCurrentSectionPos() {
        return currentPagePos;
    }

    public boolean addNavigationEventListener(final NavigationEventListener eventListener) {
        return eventListeners.add(eventListener);
    }

    public boolean removeNavigationEventListener(final NavigationEventListener eventListener) {
        return eventListeners.remove(eventListener);
    }

    private void handleEventListeners(final NavigationEvent event) {
        for (NavigationEventListener eventListener : eventListeners) {
            eventListener.navigationPerformed(event);
        }
    }
}
