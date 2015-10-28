package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import nl.siegmann.epublib.util.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

/**
 * This class provides a reader for the container contents of an EPUB 3.0
 * Publication.
 */
public class EpubReader implements EpubBase {
    /**
     * The book post-processor.
     */
    private BookProcessor bookProcessor;

    /**
     * Create a reader for the container contents of an EPUB 3.0 Publication.
     */
    public EpubReader() {
        this(BookProcessor.IDENTITY_BOOKPROCESSOR);
    }

    /**
     * Create a reader for the container contents of an EPUB 3.0 Publication.
     *
     * @param bookProcessor the book post-processor
     */
    public EpubReader(final BookProcessor bookProcessor) {
        this.bookProcessor = bookProcessor;
    }

    /**
     * Get the book post-processor.
     *
     * @return the book post-processor
     */
    public BookProcessor getBookProcessor() {
        return bookProcessor;
    }

    /**
     * Set the book post-processor.
     *
     * @param bookProcessor the book post-processor
     */
    public void setBookProcessor(final BookProcessor bookProcessor) {
        this.bookProcessor = bookProcessor;
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param in the input stream to read the container contents from
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final InputStream in) throws IOException {
        return read(in, Constants.CHARACTER_ENCODING);
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param in the input stream to read the container contents from
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final ZipInputStream in) throws IOException {
        return read(in, Constants.CHARACTER_ENCODING);
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param file the file to read the container contents from
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final ZipFile file) throws IOException {
        return read(file, Constants.CHARACTER_ENCODING);
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param in the input stream to read the container contents from
     * @param encoding the encoding for XHTML resources in the container contents
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final InputStream in, final String encoding) throws IOException {
        return read(ResourcesLoader.loadResources(new ZipInputStream(in), encoding));
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param in the input stream to read the container contents from
     * @param encoding the encoding for XHTML resources in the container contents
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final ZipInputStream in, final String encoding) throws IOException {
        return read(ResourcesLoader.loadResources(in, encoding));
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param file the file to read the container contents from
     * @param encoding the encoding for XHTML resources in the container contents
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final ZipFile file, final String encoding) throws IOException {
        return read(ResourcesLoader.loadResources(file, encoding));
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication, without loading
     * resource data.
     *
     * @param file the file to read the container contents from
     * @param encoding the encoding for XHTML resources in the container contents
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book readLazy(final ZipFile file, final String encoding) throws IOException {
        return readLazy(file, encoding, Arrays.asList(MediatypeService.MEDIA_TYPES));
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication, without loading
     * resource data.
     *
     * @param file the file to read the container contents from
     * @param encoding the encoding for XHTML resources in the container contents
     * @param lazyLoadedTypes the media types of the resources to not load the data for
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book readLazy(final ZipFile file, final String encoding, final List<MediaType> lazyLoadedTypes) throws IOException {
        return read(ResourcesLoader.loadResources(file, encoding, lazyLoadedTypes));
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param resources the resources for the publication
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final Resources resources) throws IOException {
        return read(resources, new Book());
    }

    /**
     * Read the container contents of an EPUB 3.0 Publication.
     *
     * @param resources the resources for the publication
     * @param book the book to populate with the container contents
     * @return the book describing the container contents
     * @throws IOException if an I/O error occurs
     */
    public Book read(final Resources resources, final Book book) throws IOException {
        resources.remove(FILE_NAME_MIMETYPE);
        String packageResourceHref = getPackageResourceHref(resources);
        Resource packageResource = processPackageResource(packageResourceHref, book, resources);
        book.setOpfResource(packageResource);
        Resource ncxResource = processNcxResource(book);
        book.setNcxResource(ncxResource);
        return postProcessBook(book);
    }

    /**
     * Post-process a book.
     *
     * @param book the book to post-process
     * @return the post-processed book
     */
    private Book postProcessBook(final Book book) {
        return bookProcessor != null ? bookProcessor.processBook(book) : book;
    }

    private Resource processNcxResource(final Book book) {
        return NCXDocument.read(book, this);
    }

    private String getPackageResourceHref(final Resources resources) throws IOException {
        try {
            String defaultResult = DIR_NAME_OEBPS + PATH_SEPARATOR + FILE_NAME_CONTENT_OPF;
            String result = defaultResult;
            Resource containerResource = resources.remove(DIR_NAME_META_INF + PATH_SEPARATOR + FILE_NAME_CONTAINER_XML);
            if(containerResource == null) {
                return result;
            }
            Document document = ResourceUtil.getAsDocument(containerResource);
            Element rootFileElement = (Element) ((Element) document.getDocumentElement().getElementsByTagName("rootfiles").item(0)).getElementsByTagName("rootfile").item(0);
            result = rootFileElement.getAttribute("full-path");
            if(StringUtil.isBlank(result)) {
                result = defaultResult;
            }
            return result;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Failed to read package resource href", e);
        }
    }

    /**
     * Process a package resource.
     *
     * @param packageResourceHref the package resource link
     * @param book the container contents
     * @param resources the resources
     * @return the package resource
     * @throws IOException if an I/O error occurs
     */
    public Resource processPackageResource(final String packageResourceHref, final Book book, final Resources resources) throws IOException {
        Resource packageResource = resources.remove(packageResourceHref);
        PackageDocumentReader.read(packageResource, book, resources);
        return packageResource;
    }
}
