package gr.alx.release;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
public class FileHandler {

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
