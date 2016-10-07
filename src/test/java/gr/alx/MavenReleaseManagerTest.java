package gr.alx;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by alx on 10/2/2016.
 */
@RunWith(JUnitParamsRunner.class)
public class MavenReleaseManagerTest {

    private MavenReleaseManager cut;

    @Before
    public void setUp() {
        cut = new MavenReleaseManager();
    }

    @Test
    @Parameters(method = "bumpUpParams")
    @TestCaseName("shouldBumpUpVersion from {0} to {1} when action is {2}")
    public void shouldBumpUpVersion(String oldVersion, String newVersion, String type) {
        assertThat(cut.bumpUpVersion(oldVersion, type)).isEqualTo(newVersion);
    }

    private Object bumpUpParams() {
        return new Object[]{
                new Object[]{"0.0.1-SNAPSHOT", "0.0.2-SNAPSHOT", "build"},
                new Object[]{"0.0.1-SNAPSHOT", "1.0.1-SNAPSHOT", "major"},
                new Object[]{"0.0.1-SNAPSHOT", "0.1.1-SNAPSHOT", "minor"},
                new Object[]{"0.0.1-SNAPSHOT", "0.0.1", "prod"},
                new Object[]{"0.0.1", "0.0.1-SNAPSHOT", "snapshot"}
        };
    }

    @Test
    @Parameters(method = "splitParams")
    @TestCaseName("shouldSplitVersion from {0} to {1}")
    public void shouldSplitVersion(String version, Version versionModel) {
        assertThat(cut.splitVersion(version))
                .isEqualToComparingFieldByFieldRecursively(versionModel);

    }

    private Object splitParams() {
        return new Object[]{
                new Object[]{"0.0.1-SNAPSHOT", new Version(0, 0, 1, true)},
                new Object[]{"0.0.1", new Version(0, 0, 1, false)},
                new Object[]{"10.10.110-SNAPSHOT", new Version(10, 10, 110, true)},
                new Object[]{"10.010.110-SNAPSHOT", new Version(10, 10, 110, true)}
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters({"1.0..1", "1.0-SNAPSHOT", "1.1.1.1.1"})
    public void shouldNotSplitVersion(String version) {
        cut.splitVersion(version);
    }

    @Test
    @Parameters({"1.0.1", "1.10.1", "1.0.100", "1.0.1-SNAPSHOT", "1.0.10-SNAPSHOT"})
    public void shouldValidateVersion(String version) {
        assertThat(cut.validVersion(version)).isTrue();

    }

    @Test
    @Parameters({"1.0..1", "1.1012.1", "0.1.0.100", "1.0.1.SNAPSHOT", "1.0-SNAPSHOT"})
    public void shouldNotValidateVersion(String version) {
        assertThat(cut.validVersion(version)).isFalse();

    }
}