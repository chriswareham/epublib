package nl.siegmann.epublib.browsersupport;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

/**
 * A history of the user's locations with the epub.
 *
 * @author paul.siegmann
 *
 */
public class NavigationHistory implements NavigationEventListener {

    public static final int DEFAULT_MAX_HISTORY_SIZE = 1000;

    public static final long DEFAULT_HISTORY_WAIT_TIME = 1000L;

    private int maxHistorySize = DEFAULT_MAX_HISTORY_SIZE;

    private long historyWaitTime = DEFAULT_HISTORY_WAIT_TIME;

    private long lastUpdateTime;

    private List<Location> locations = new ArrayList<>();

    private final Navigator navigator;

    private int currentPos = -1;

    private int currentSize;

    public NavigationHistory(final Navigator navigator) {
        this.navigator = navigator;
        navigator.addNavigationEventListener(this);
        initBook(navigator.getBook());
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public void initBook(final Book book) {
        if (book == null) {
            return;
        }

        locations = new ArrayList<>();
        currentPos = -1;
        currentSize = 0;
        if (navigator.getCurrentResource() != null) {
            addLocation(navigator.getCurrentResource().getHref());
        }
    }

    /**
     * If the time between a navigation event is less than the historyWaitTime
     * then the new location is not added to the history.
     * When a user is rapidly viewing many pages using the slider we do not want
     * all of them to be added to the history.
     *
     * @return the time we wait before adding the page to the history
     */
    public long getHistoryWaitTime() {
        return historyWaitTime;
    }

    public void setHistoryWaitTime(final long historyWaitTime) {
        this.historyWaitTime = historyWaitTime;
    }

    public void addLocation(final Resource resource) {
        if (resource != null) {
            addLocation(resource.getHref());
        }
    }

    public void addLocation(final String href) {
        addLocation(new Location(href));
    }

    /**
     * Adds the location after the current position.
     * If the current position is not the end of the list then the elements
     * between the current element and the end of the list will be discarded.
     * Does nothing if the new location matches the current location.
     * <br/>
     * If this number of locations becomes larger then the historySize then the
     * first item(s) will be removed.
     *
     * @param location the location to add
     */
    public void addLocation(final Location location) {
        // do nothing if the new location matches the current location
        if (!(locations.isEmpty()) && location.getHref().equals(locations.get(currentPos).getHref())) {
            return;
        }
        currentPos++;
        if (currentPos != currentSize) {
            locations.set(currentPos, location);
        } else {
            locations.add(location);
            checkHistorySize();
        }
        currentSize = currentPos + 1;
    }

    /**
     * Removes all elements that are too much for the maxHistorySize out of the history.
     *
     */
    private void checkHistorySize() {
        while (locations.size() > maxHistorySize) {
            locations.remove(0);
            currentSize--;
            currentPos--;
        }
    }

    private String getLocationHref(final int pos) {
        if (pos < 0 || pos >= locations.size()) {
            return null;
        }
        return locations.get(currentPos).getHref();
    }

    /**
     * Moves the current positions delta positions.
     *
     * move(-1) to go one position back in history.<br/>
     * move(1) to go one position forward.<br/>
     *
     * @param delta
     *
     * @return Whether we actually moved. If the requested value is illegal it will return false, true otherwise.
     */
    public boolean move(final int delta) {
        if (((currentPos + delta) < 0) || ((currentPos + delta) >= currentSize)) {
            return false;
        }
        currentPos += delta;
        navigator.gotoResource(getLocationHref(currentPos), this);
        return true;
    }

    /**
     * If this is not the source of the navigationEvent then the addLocation
     * will be called with the href of the currentResource in the
     * navigationEvent.
     *
     * @param event the navigation event
     */
    @Override
    public void navigationPerformed(final NavigationEvent event) {
        if (this == event.getSource()) {
            return;
        }

        if (event.getCurrentResource() == null) {
            return;
        }

        if ((System.currentTimeMillis() - this.lastUpdateTime) > historyWaitTime) {
            // if the user scrolled rapidly through the pages then the last page will not be added to the history. We fix that here:
            addLocation(event.getOldResource());
            addLocation(event.getCurrentResource().getHref());
        }
        lastUpdateTime = System.currentTimeMillis();
    }

    public String getCurrentHref() {
        if (currentPos < 0 || currentPos >= locations.size()) {
            return null;
        }
        return locations.get(currentPos).getHref();
    }

    public void setMaxHistorySize(final int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    public int getMaxHistorySize() {
        return maxHistorySize;
    }

    public static class Location {

        private final String href;

        public Location(final String href) {
            this.href = href;
        }

        public String getHref() {
            return href;
        }
    }
}
