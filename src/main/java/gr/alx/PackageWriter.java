package gr.alx;

import java.nio.file.Path;

/**
 * Created by TRIFYLLA on 7/10/2016.
 */
public class PackageWriter implements Writer<PackageJsonFileRepresentation> {
    @Override
    public String writeNewVersion(Path path, String oldVersion, PackageJsonFileRepresentation model) {
        return null;
    }
}
