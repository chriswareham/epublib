package nl.siegmann.epublib.epub;

/**
 * Functionality shared by the PackageDocumentReader and the PackageDocumentWriter
 *
 * @author paul
 */
public interface PackageDocumentBase {
    String BOOK_ID = "BookId";
    String NAMESPACE_OPF = "http://www.idpf.org/2007/opf";
    String NAMESPACE_DUBLIN_CORE = "http://purl.org/dc/elements/1.1/";
    String PREFIX_DUBLIN_CORE = "dc";
    String PREFIX_OPF = "opf";
    String PREFIX_EMPTY = "";
    String DATE_FORMAT = "yyyy-MM-dd";

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

    public interface DCAttributes {
        String SCHEME = "scheme";
        String ID = "id";
    }

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
    }

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
    }

    public interface OPFValues {
        String META_COVER = "cover";
        String REFERENCE_COVER = "cover";
        String NO = "no";
        String GENERATOR = "generator";
    }
}
