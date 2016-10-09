package gr.alx.release;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.alx.release.bower.BowerReader;
import gr.alx.release.bower.BowerWriter;
import gr.alx.release.packagejson.PackageReader;
import gr.alx.release.packagejson.PackageWriter;
import gr.alx.release.pom.PomReader;
import gr.alx.release.pom.PomWriter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Paths;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by alx on 10/6/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReleaseManagerIT {

    @Spy
    ReleaseManager cut;

    Reader pomReader = new PomReader();
    Writer pomWriter = new PomWriter();
    FileHandler pomHandler = new FileHandler(pomReader, pomWriter);

    Reader packageReader = new PackageReader(new ObjectMapper());
    Writer packageWriter = new PackageWriter();
    FileHandler packageHandler = new FileHandler(packageReader, packageWriter);

    Reader bowerReader = new BowerReader(new ObjectMapper());
    Writer bowerWriter = new BowerWriter();
    FileHandler bowerHandler = new FileHandler(bowerReader, bowerWriter);

    private String oldPomVersion;
    private String oldPackageVersion;
    private String oldBowerVersion;

    @Before
    public void setUp() throws IOException, XmlPullParserException {
        FileRepresentation pomModel = pomReader.readFile(Paths.get("pom.xml"));
        oldPomVersion = pomModel.getVersion();

        FileRepresentation packageModel = packageReader.readFile(Paths.get("package.json"));
        oldPackageVersion = packageModel.getVersion();

        FileRepresentation bowerModel = bowerReader.readFile(Paths.get("bower.json"));
        oldBowerVersion = bowerModel.getVersion();
    }

    @After
    public void tearDown() throws IOException {
        cut.doManualVersion(oldPomVersion);
    }

    @Test
    public void shouldReleaseAutomaticVersion() throws IOException {

        cut.doRelease("bump minor");

        verify(cut).doAutomaticVersion("minor");
        verify(cut, times(4)).updateVersionInFile(anyObject(), eq("0.1.1-SNAPSHOT"), anyObject());
    }

    @Test
    public void shouldNotReleaseAutomaticVersionWithWrongAction() throws IOException {

        cut.doRelease("bump wrong");

        verify(cut).doAutomaticVersion("wrong");
        verify(cut).printInConsole("Allowed bump types are: " + ReleaseManager.allowedBumpTypes.toString());
    }

    @Test
    public void shouldReleaseManualVersion() throws IOException {

        cut.doRelease("release 0.1.1-SNAPSHOT");

        verify(cut, times(1)).doManualVersion(eq("0.1.1-SNAPSHOT"));
        verify(cut, times(10)).updateVersionInFile(anyObject(), eq("0.1.1-SNAPSHOT"), anyObject());
    }

    @Test
    public void shouldNotReleaseWrongVersion() throws IOException {

        cut.doRelease("release 0.1.1.SNAPSHOT");

        verify(cut, never()).updateVersionInFile(anyObject(), anyString(), anyObject());
        verify(cut).printInConsole(ReleaseManager.INVALID_VERSION_FORMAT);
    }

    @Test
    public void shouldNotReleaseWithWrongAction() throws IOException {

        cut.doRelease("wrong 0.1.1.SNAPSHOT");

        verify(cut).printInConsole(ReleaseManager.ALLOWED_ACTIONS_MESSAGE);
    }


    @Test
    public void shouldNotUpdateWrongPath() {

        String path = "wrong";

        cut.updateVersionInFile(Paths.get(path), "1.1.1", pomHandler);

        verify(cut).printInConsole("An error occurred during processing of the file: " + path);
    }
}
