package gr.alx;

import org.apache.maven.model.Model;
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
    MavenReleaseManager cut;

    PomReader pr = new PomReader();
    private String oldVersion;

    @Before
    public void setUp() {
        Model model = pr.readPomFile(Paths.get("pom.xml"));
        oldVersion = model.getVersion();
    }

    @After
    public void tearDown() throws IOException {
        cut.doManualVersion(oldVersion);
    }

    @Test
    public void shouldReleaseAutomaticVersion() throws IOException {

        cut.doRelease("bump minor");

        verify(cut).doAutomaticVersion("minor");
        verify(cut, times(4)).updateVersionInPom(anyObject(), eq("0.1.1-SNAPSHOT"));
    }

    @Test
    public void shouldReleaseManualVersion() throws IOException {

        cut.doRelease("release 0.1.1-SNAPSHOT");

        verify(cut).doManualVersion("0.1.1-SNAPSHOT");
        verify(cut, times(4)).updateVersionInPom(anyObject(), eq("0.1.1-SNAPSHOT"));
    }

    @Test
    public void shouldNotReleaseWrongVersion() throws IOException {

        cut.doRelease("release 0.1.1.SNAPSHOT");

        verify(cut, never()).updateVersionInPom(anyObject(), anyString());
    }
}
