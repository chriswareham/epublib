package nl.siegmann.epublib.browsersupport;

/**
 * Implemented by classes that want to be notified whenever a user navigates to
 * another position in a book.
 *
 * @author paul
 */
public interface NavigationEventListener {
    /**
     * Called whenever a user navigates to another position in a book.
     *
     * @param event the event describing the navigation action
     */
    void navigationPerformed(NavigationEvent event);
}
