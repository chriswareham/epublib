package nl.siegmann.epublib.domain;

import java.io.Serializable;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.util.StringUtil;

public class TitledResourceReference extends ResourceReference implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    private String fragmentId;

    private String title;

    public TitledResourceReference(final Resource resource) {
        this(resource, null);
    }

    public TitledResourceReference(final Resource resource, final String title) {
        this(resource, title, null);
    }

    public TitledResourceReference(final Resource resource, final String title, final String fragmentId) {
        super(resource);
        this.title = title;
        this.fragmentId = fragmentId;
    }

    public String getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(final String fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Sets the resource to the given resource and sets the fragmentId to null.
     *
     * @param resource the resource
     */
    @Override
    public void setResource(final Resource resource) {
        setResource(resource, null);
    }

    /**
     * Sets the resource and fragmentId to the given resource and fragmentId.
     *
     * @param resource the resource
     * @param fragmentId the fragmentId
     */
    public void setResource(final Resource resource, final String fragmentId) {
        super.setResource(resource);
        this.fragmentId = fragmentId;
    }

    /**
     * If the fragmentId is blank it returns the resource href, otherwise it returns the resource href + '#' + the fragmentId.
     *
     * @return If the fragmentId is blank it returns the resource href, otherwise it returns the resource href + '#' + the fragmentId.
     */
    public String getCompleteHref() {
        if (StringUtil.isBlank(fragmentId)) {
            return getResource().getHref();
        }
        return getResource().getHref() + Constants.FRAGMENT_SEPARATOR_CHAR + fragmentId;
    }
}
