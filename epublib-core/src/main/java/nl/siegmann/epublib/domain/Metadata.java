package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.StringUtil;

/**
 * A Book's collection of Metadata.
 * In the future it should contain all Dublin Core attributes, for now it contains a set of often-used ones.
 *
 * @author paul
 *
 */
public class Metadata implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The default language.
     */
    public static final String DEFAULT_LANGUAGE = "en";

    private boolean autoGeneratedId = true;
    private List<Author> authors = new ArrayList<>();
    private List<Author> contributors = new ArrayList<>();
    private List<Date> dates = new ArrayList<>();
    private Map<QName, String> otherProperties = new HashMap<>();
    private List<String> rights = new ArrayList<>();
    private final List<Identifier> identifiers = new ArrayList<>();
    private List<String> subjects = new ArrayList<>();
    private String format = MediatypeService.EPUB.getName();
    private List<String> types = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<String> publishers = new ArrayList<>();
    private Map<String, String> metaAttributes = new HashMap<>();
    private String language = DEFAULT_LANGUAGE;
    /**
     * The titles of the publication.
     */
    private final List<Title> titles = new ArrayList<>();
    /**
     * The package metadata of the the publication.
     */
    private final List<Meta> metadata = new ArrayList<>();
    /**
     * The resource links of the publication.
     */
    private final List<Link> links = new ArrayList<>();

    public Metadata() {
        identifiers.add(new Identifier());
        autoGeneratedId = true;
    }

    public boolean isAutoGeneratedId() {
        return autoGeneratedId;
    }

    /**
     * Metadata properties not hard-coded like the author, title, etc.
     *
     * @return Metadata properties not hard-coded like the author, title, etc.
     */
    public Map<QName, String> getOtherProperties() {
        return otherProperties;
    }

    public void setOtherProperties(Map<QName, String> otherProperties) {
        this.otherProperties = otherProperties;
    }

    public Date addDate(Date date) {
        this.dates.add(date);
        return date;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }

    public Author addAuthor(Author author) {
        authors.add(author);
        return author;
    }

    public List<Author> getAuthors() {
        return authors;
    }
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Author addContributor(Author contributor) {
        contributors.add(contributor);
        return contributor;
    }

    public List<Author> getContributors() {
        return contributors;
    }

    public void setContributors(List<Author> contributors) {
        this.contributors = contributors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public void setRights(List<String> rights) {
        this.rights = rights;
    }

    public List<String> getRights() {
        return rights;
    }

    public String addPublisher(String publisher) {
        this.publishers.add(publisher);
        return publisher;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public String addDescription(String description) {
        this.descriptions.add(description);
        return description;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    /**
     * Get the book identifier. The EPUB 3 specification states that one
     * identifier can be explicitly marked as the book identifier, otherwise it
     * is assumed to be the first identifier.
     *
     * @return the book identifier
     */
    public Identifier getBookIdIdentifier() {
        for (Identifier identifier: identifiers) {
            if (identifier.isBookId()) {
                return identifier;
            }
        }
        return identifiers.isEmpty() ? null : identifiers.get(0);
    }

    /**
     * Get the identifiers of the publication.
     *
     * @return the identifiers of the publication
     */
    public List<Identifier> getIdentifiers() {
        return Collections.unmodifiableList(identifiers);
    }

    public void addIdentifier(Identifier identifier) {
        if (autoGeneratedId && !identifiers.isEmpty()) {
            identifiers.set(0, identifier);
        } else {
            identifiers.add(identifier);
        }
        autoGeneratedId = false;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers.clear();
        if (identifiers != null) {
            this.identifiers.addAll(identifiers);
        }
        autoGeneratedId = false;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public String addType(String type) {
        this.types.add(type);
        return type;
    }

    public List<String> getTypes() {
        return types;
    }
    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getMetaAttribute(String name) {
        return metaAttributes.get(name);
    }

    public void setMetaAttributes(Map<String, String> metaAttributes) {
        this.metaAttributes = metaAttributes;
    }

    /**
     * Gets the first non-blank title of the book.
     * Will return "" if no title found.
     *
     * @return the first non-blank title of the book.
     */
    public String getFirstTitle() {
        for (Title title: titles) {
            if (StringUtil.isNotBlank(title.getText())) {
                return title.getText();
            }
        }
        return "";
    }

    /**
     * Get the titles of the publication.
     *
     * @return the titles of the publication
     */
    public List<Title> getTitles() {
        return Collections.unmodifiableList(titles);
    }

    /**
     * Set the titles of the publication.
     *
     * @param titles the titles of the publication
     */
    public void setTitles(final List<Title> titles) {
        this.titles.clear();
        if (titles != null) {
            this.titles.addAll(titles);
        }
    }

    /**
     * Add a title.
     *
     * @param title the title to add
     */
    public void addTitle(final Title title) {
        titles.add(title);
    }

    /**
     * Get the package metadata of the the publication.
     *
     * @return the package metadata of the the publication
     */
    public List<Meta> getMetadata() {
        return Collections.unmodifiableList(metadata);
    }

    /**
     * Add a package metadata item.
     *
     * @param item the package metadata item to add
     */
    public void addItem(final Meta item) {
        metadata.add(item);
    }

    /**
     * Get the resource links of the publication.
     *
     * @return the resource links of the publication
     */
    public List<Link> getLinks() {
        return Collections.unmodifiableList(links);
    }

    /**
     * Add a resource link.
     *
     * @param link the resource lint to add
     */
    public void addLink(final Link link) {
        links.add(link);
    }
}
