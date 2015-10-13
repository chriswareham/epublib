package nl.siegmann.epublib.epub;

/**
 * Functionality shared by the PackageDocumentReader and the PackageDocumentWriter
 *
 * @author paul
 */
public interface PackageDocumentBase {
    String BOOK_ID_ID = "BookId";
    String NAMESPACE_OPF = "http://www.idpf.org/2007/opf";
    String NAMESPACE_DUBLIN_CORE = "http://purl.org/dc/elements/1.1/";
    String PREFIX_DUBLIN_CORE = "dc";
    String PREFIX_OPF = "opf";
    String dateFormat = "yyyy-MM-dd";

    public interface DCElements {
        String title = "title";
        String creator = "creator";
        String subject = "subject";
        String description = "description";
        String publisher = "publisher";
        String contributor = "contributor";
        String date = "date";
        String type = "type";
        String format = "format";
        String identifier = "identifier";
        String source = "source";
        String language = "language";
        String relation = "relation";
        String coverage = "coverage";
        String rights = "rights";
    }

    public interface DCAttributes {
        String scheme = "scheme";
        String id = "id";
    }

    public interface OPFElements {
        String metadata = "metadata";
        String meta = "meta";
        String manifest = "manifest";
        String packageTag = "package";
        String itemref = "itemref";
        String spine = "spine";
        String reference = "reference";
        String guide = "guide";
        String item = "item";
    }

    public interface OPFAttributes {
        String uniqueIdentifier = "unique-identifier";
        String idref = "idref";
        String name = "name";
        String content = "content";
        String type = "type";
        String href = "href";
        String linear = "linear";
        String event = "event";
        String role = "role";
        String file_as = "file-as";
        String id = "id";
        String media_type = "media-type";
        String title = "title";
        String toc = "toc";
        String version = "version";
        String scheme = "scheme";
        String property = "property";
    }

    public interface OPFValues {
        String meta_cover = "cover";
        String reference_cover = "cover";
        String no = "no";
        String generator = "generator";
    }
}
