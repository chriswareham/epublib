package nl.siegmann.epublib.epub;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Link;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.IOUtil;

import org.xmlpull.v1.XmlSerializer;

/**
 * This class provides a writer for the container contents of an EPUB 3.0
 * Publication.
 */
public class EpubWriter implements EpubBase {
    /**
     * The book pre-processor.
     */
    private BookProcessor bookProcessor;

    /**
     * Create a writer for the container contents of an EPUB 3.0 Publication.
     */
    public EpubWriter() {
        this(BookProcessor.IDENTITY_BOOKPROCESSOR);
    }

    /**
     * Create a writer for the container contents of an EPUB 3.0 Publication.
     *
     * @param bookProcessor the book pre-processor
     */
    public EpubWriter(final BookProcessor bookProcessor) {
        this.bookProcessor = bookProcessor;
    }

    /**
     * Get the book pre-processor.
     *
     * @return the book pre-processor
     */
    public BookProcessor getBookProcessor() {
        return bookProcessor;
    }

    /**
     * Set the book pre-processor.
     *
     * @param bookProcessor the book pre-processor
     */
    public void setBookProcessor(final BookProcessor bookProcessor) {
        this.bookProcessor = bookProcessor;
    }

    /**
     * Create an XML serialiser.
     *
     * @param out the output stream serialise XML to
     * @return an XML serialiser
     * @throws UnsupportedEncodingException if the character encoding is not supported
     */
    public XmlSerializer createXmlSerializer(final ZipOutputStream out) throws UnsupportedEncodingException {
        return EpubProcessorSupport.createXmlSerializer(out);
    }

    /**
     * Write the container contents of an EPUB 3.0 Publication. The stream is
     * closed on return from this method.
     *
     * @param book the book describing the container contents
     * @param out the output stream to write the container contents to
     */
    public void write(final Book book, final OutputStream out) {
        Book preProcessedBook = preProcessBook(book);
        try (ZipOutputStream resultStream = new ZipOutputStream(out)) {
            writeMimeType(resultStream);
            writeContainerXml(resultStream);
            initTOCResource(preProcessedBook); // XXX
            writeResources(preProcessedBook, resultStream);
            writeLinks(preProcessedBook, resultStream);
            writePackageDocument(preProcessedBook, resultStream);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write EPUB", exception);
        }
    }

    /**
     * Pre-process a book.
     *
     * @param book the book to pre-process
     * @return the pre-processed book
     */
    private Book preProcessBook(final Book book) {
        return bookProcessor != null ? bookProcessor.processBook(book) : book;
    }

    /**
     * Write a <code>mimetype</code> entry. This is stored as the first file in
     * the container, it should not be compressed or encrypted. Its contents
     * identify the container as an EPUB Publication.
     *
     * @param out the output stream to write content to
     * @throws IOException if an I/O error occurs
     */
    private void writeMimeType(final ZipOutputStream out) throws IOException {
        ZipEntry zipEntry = new ZipEntry(FILE_NAME_MIMETYPE);
        zipEntry.setMethod(ZipEntry.STORED);
        byte[] bytes = MediatypeService.EPUB.getName().getBytes();
        zipEntry.setSize(bytes.length);
        zipEntry.setCrc(calculateCrc(bytes));
        out.putNextEntry(zipEntry);
        out.write(bytes);
    }

    /**
     * Write a <code>META-INF/container.xml</code> entry. Its contents list the
     * root file of the EPUB Publication.
     *
     * @param out the output stream to write content to
     * @throws IOException if an I/O error occurs
     */
    private void writeContainerXml(final ZipOutputStream out) throws IOException {
        out.putNextEntry(new ZipEntry(DIR_NAME_META_INF + PATH_SEPARATOR + FILE_NAME_CONTAINER_XML));
        Writer writer = new OutputStreamWriter(out);
        writer.write("<?xml version=\"1.0\"?>\n");
        writer.write("<container version=\"1.0\" xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\">\n");
        writer.write("  <rootfiles>\n");
        writer.write("    <rootfile full-path=\"" + DIR_NAME_OEBPS +  PATH_SEPARATOR + FILE_NAME_CONTENT_OPF + "\" media-type=\"application/oebps-package+xml\"/>\n");
        writer.write("  </rootfiles>\n");
        writer.write("</container>\n");
        writer.flush();
    }

    private void initTOCResource(final Book book) throws IOException {
        Resource tocResource = NCXDocument.createNCXResource(book);
        Resource currentTocResource = book.getSpine().getTocResource();
        if (currentTocResource != null) {
            book.getResources().remove(currentTocResource.getHref());
        }
        book.getSpine().setTocResource(tocResource);
        book.getResources().add(tocResource);
    }

    /**
     * Write the resources that make up an EPUB Publication to the
     * <code>OEBPS</code> directory.
     *
     * @param book the book to write the resources for
     * @param out the output stream to write write content to
     * @throws IOException if an I/O error occurs
     */
    private void writeResources(final Book book, final ZipOutputStream out) throws IOException {
        for (Resource resource: book.getResources().getAll()) {
            out.putNextEntry(new ZipEntry(DIR_NAME_OEBPS + PATH_SEPARATOR + resource.getHref()));
            try (InputStream inputStream = resource.getInputStream()) {
                IOUtil.copy(inputStream, out);
            }
        }
    }

    /*
     * Write the linked resources that make up an EPUB Publication to the
     * <code>OEBPS</code> directory.
     *
     * @param book the book to write the linked resources for
     * @param out the output stream to write write content to
     * @throws IOException if an I/O error occurs
     */
    public void writeLinks(final Book book, final ZipOutputStream out) throws IOException {
        for (Link link : book.getMetadata().getLinks()) {
            out.putNextEntry(new ZipEntry(DIR_NAME_OEBPS + PATH_SEPARATOR + link.getHref()));
            try (InputStream inputStream = link.getInputStream()) {
                IOUtil.copy(inputStream, out);
            }
        }
    }

    /**
     * Write a <code>OEBPS/content.opf</code> entry. Its contents are metadata
     * about the resources that make up an EPUB Publication, including reading
     * order.
     *
     * @param book the book to write the package document for
     * @param out the output stream to write the package document to
     * @throws IOException if an I/O error occurs
     */
    public void writePackageDocument(final Book book, final ZipOutputStream out) throws IOException {
        out.putNextEntry(new ZipEntry(DIR_NAME_OEBPS +  PATH_SEPARATOR + FILE_NAME_CONTENT_OPF));
        XmlSerializer xmlSerializer = createXmlSerializer(out);
        PackageDocumentWriter.write(xmlSerializer, book);
        xmlSerializer.flush();
    }

    /**
     * Calculate the CRC for data.
     *
     * @param data the data to calculate the CRC for
     * @return the CRC for the data
     */
    private long calculateCrc(final byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }
}
