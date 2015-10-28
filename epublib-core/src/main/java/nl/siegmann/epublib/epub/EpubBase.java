package nl.siegmann.epublib.epub;

/**
 * This interface describes constants for EPUB 3.0 Publications.
 */
public interface EpubBase {
    /**
     * The MIME type filename.
     */
    String FILE_NAME_MIMETYPE = "mimetype";
    /**
     * The metadata information directory.
     */
    String DIR_NAME_META_INF = "META-INF";
    /**
     * The Open eBook Publication Structure directory for resources.
     */
    String DIR_NAME_OEBPS = "OEBPS";
    /**
     * The Open Packaging Forma package document filename.
     */
    String FILE_NAME_CONTENT_OPF = "content.opf";
    /**
     * The Open Container Format container filename.
     */
    String FILE_NAME_CONTAINER_XML = "container.xml";
    /**
     * The path separator.
     */
    String PATH_SEPARATOR = "/";
}
