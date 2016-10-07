package gr.alx;

import java.nio.file.Path;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
public interface Writer<T> {

    String writeNewVersion(Path path, String oldVersion, T model);
}
