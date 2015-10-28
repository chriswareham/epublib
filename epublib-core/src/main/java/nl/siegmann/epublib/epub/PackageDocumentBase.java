package nl.siegmann.epublib.epub;

/**
 * This interface describes constants for EPUB 3.0 Package Documents.
 */
public interface PackageDocumentBase {
    /**
     * The EPUB version.
     */
    String VERSION = "3.0";
    /**
     * The Open Package Format namespace.
     */
    String NAMESPACE_OPF = "http://www.idpf.org/2007/opf";
    /**
     * The Dublin Core namespace.
     */
    String NAMESPACE_DUBLIN_CORE = "http://purl.org/dc/elements/1.1/";
    /**
     * The Open Package Format prefix.
     */
    String PREFIX_OPF = "opf";
    /**
     * The Dublin Core prefix.
     */
    String PREFIX_DUBLIN_CORE = "dc";
    /**
     * The empty prefix.
     */
    String PREFIX_EMPTY = "";
    /**
     * The <code>id</code> of the primary identifier of an EPUB Publication.
     */
    String BOOK_ID = "BookId";
    /**
     * The format for Package Document dates.
     */
    String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * The Dublin Core elements.
     */
    public interface DCElements {
        String TITLE = "title";
        String CREATOR = "creator";
        String SUBJECT = "subject";
        String DESCRIPTION = "description";
        String PUBLISHER = "publisher";
        String CONTRIBUTOR = "contributor";
        String DATE = "date";
        String TYPE = "type";
        String FORMAT = "format";
        String IDENTIFIER = "identifier";
        String SOURCE = "source";
        String LANGUAGE = "language";
        String RELATION = "relation";
        String COVERAGE = "coverage";
        String RIGHTS = "rights";
    }

    /**
     * The Dublin Core attributes.
     */
    public interface DCAttributes {
        String SCHEME = "scheme";
        String ID = "id";
    }

    /**
     * The Open Packaging Format elements.
     */
    public interface OPFElements {
        String METADATA = "metadata";
        String META = "meta";
        String MANIFEST = "manifest";
        String PACKAGE = "package";
        String ITEMREF = "itemref";
        String SPINE = "spine";
        String REFERENCE = "reference";
        String GUIDE = "guide";
        String ITEM = "item";
        String LINK = "link";
    }

    /**
     * The Open Packaging Format attributes.
     */
    public interface OPFAttributes {
        String UNIQUE_IDENTIFIER = "unique-identifier";
        String IDREF = "idref";
        String NAME = "name";
        String CONTENT = "content";
        String TYPE = "type";
        String HREF = "href";
        String LINEAR = "linear";
        String EVENT = "event";
        String ROLE = "role";
        String FILE_AS = "file-as";
        String ID = "id";
        String MEDIA_TYPE = "media-type";
        String TITLE = "title";
        String TOC = "toc";
        String VERSION = "version";
        String SCHEME = "scheme";
        String PROPERTY = "property";
        String PROPERTIES = "properties";
        String REL = "rel";
    }

    /**
     * The Open Packaging Format values.
     */
    public interface OPFValues {
        String META_COVER = "cover";
        String REFERENCE_COVER = "cover";
        String NO = "no";
        String GENERATOR = "generator";
    }
}
