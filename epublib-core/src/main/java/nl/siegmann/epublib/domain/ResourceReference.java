package nl.siegmann.epublib.domain;

import java.io.Serializable;

public class ResourceReference implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    private Resource resource;

    public ResourceReference(final Resource resource) {
        this.resource = resource;
    }

    public final Resource getResource() {
        return resource;
    }

    /**
     * Besides setting the resource it also sets the fragmentId to null.
     *
     * @param resource the resource
     */
    public void setResource(final Resource resource) {
        this.resource = resource;
    }

    /**
     * The id of the reference referred to.
     *
     * null of the reference is null or has a null id itself.
     *
     * @return The id of the reference referred to.
     */
    public String getResourceId() {
        return resource != null ? resource.getId() : null;
    }
}
