package gr.alx;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by alx on 10/7/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PackageReleaseManagerIT {

    @Spy
    PackageReleaseManager cut;

    @Test
    public void shouldReleaseManualVersion() throws IOException {
        cut.doManualVersion("release 0.1.1-SNAPSHOT");

//        verify(cut, times(4)).updateVersionInPackage(anyObject(), eq("0.1.1"));
    }


}
