package gr.alx;

import com.fasterxml.jackson.databind.ObjectMapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by alx on 10/7/2016.
 */
public class PackageReleaseManager {


    private PackageReader packageReader;

    public PackageReleaseManager() {
        this.packageReader = new PackageReader(new ObjectMapper());
    }

    public void doManualVersion(String version) throws IOException {
        List<Path> paths = packageReader.getAllPackagePaths();
    }

    public void updateVersionInPackage(Path path, String version) {
        throw new NotImplementedException();
    }
}
