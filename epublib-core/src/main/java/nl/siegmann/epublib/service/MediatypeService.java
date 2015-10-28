package nl.siegmann.epublib.service;

import java.util.HashMap;
import java.util.Map;

import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.util.StringUtil;

/**
 * Manages mediatypes that are used by epubs.
 *
 * @author paul
 */
public final class MediatypeService {

    public static final MediaType XHTML = new MediaType("application/xhtml+xml", ".xhtml", new String[] {".htm", ".html", ".xhtml"});

    public static final MediaType EPUB = new MediaType("application/epub+zip", ".epub");

    public static final MediaType NCX = new MediaType("application/x-dtbncx+xml", ".ncx");

    public static final MediaType JAVASCRIPT = new MediaType("text/javascript", ".js");

    public static final MediaType CSS = new MediaType("text/css", ".css");

    // images
    public static final MediaType JPG = new MediaType("image/jpeg", ".jpg", new String[] {".jpg", ".jpeg"});

    public static final MediaType PNG = new MediaType("image/png", ".png");

    public static final MediaType GIF = new MediaType("image/gif", ".gif");

    public static final MediaType SVG = new MediaType("image/svg+xml", ".svg");

    // fonts
    public static final MediaType TTF = new MediaType("application/x-truetype-font", ".ttf");

    public static final MediaType OPENTYPE = new MediaType("application/vnd.ms-opentype", ".otf");

    public static final MediaType WOFF = new MediaType("application/font-woff", ".woff");

    // audio
    public static final MediaType MP3 = new MediaType("audio/mpeg", ".mp3");

    public static final MediaType MP4 = new MediaType("audio/mp4", ".mp4");

    public static final MediaType OGG = new MediaType("audio/ogg", ".ogg");

    public static final MediaType SMIL = new MediaType("application/smil+xml", ".smil");

    public static final MediaType XPGT = new MediaType("application/adobe-page-template+xml", ".xpgt");

    public static final MediaType PLS = new MediaType("application/pls+xml", ".pls");

    public static final MediaType[] MEDIA_TYPES = new MediaType[] {
        XHTML, EPUB, JPG, PNG, GIF, CSS, SVG, TTF, NCX, XPGT, OPENTYPE, WOFF, SMIL, PLS, JAVASCRIPT, MP3, MP4, OGG
    };

    public static final Map<String, MediaType> MEDIA_TYPES_BY_NAME = new HashMap<>();

    static {
        for (MediaType mediaType : MEDIA_TYPES) {
            MEDIA_TYPES_BY_NAME.put(mediaType.getName(), mediaType);
        }
    }

    private MediatypeService() {
        super();
    }

    public static boolean isBitmapImage(final MediaType mediaType) {
        return mediaType == JPG || mediaType == PNG || mediaType == GIF;
    }

    /**
     * Gets the MediaType based on the file extension.
     * Null if no matching extension found.
     *
     * @param filename the name of the file to determine the MediaType of
     * @return the MediaType based on the file extension
     */
    public static MediaType getMediaTypeByFilename(final String filename) {
        for (MediaType mediatype : MEDIA_TYPES) {
            for (String extension: mediatype.getExtensions()) {
                if (StringUtil.endsWithIgnoreCase(filename, extension)) {
                    return mediatype;
                }
            }
        }
        return null;
    }

    public static MediaType getMediaTypeByName(final String name) {
        return MEDIA_TYPES_BY_NAME.get(name);
    }
}
