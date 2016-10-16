package gr.alx.release.manager;

import gr.alx.release.types.Reader;
import gr.alx.release.types.Writer;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
class FileHandler {

    private final Reader reader;
    private final Writer writer;

    public FileHandler(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public Reader getReader() {
        return reader;
    }

    public Writer getWriter() {
        return writer;
    }
}
