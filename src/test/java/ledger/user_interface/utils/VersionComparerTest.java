package ledger.user_interface.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionComparerTest {

    @Test
    public void isVersionGreater() throws Exception {
        assertFalse(VersionComparer.isVersionGreater("0.1.0", "1.0.0"));
        assertFalse(VersionComparer.isVersionGreater("1.0.0", "1.0.0"));
        assertFalse(VersionComparer.isVersionGreater("0.0.0", "0.0.0"));

        assertTrue(VersionComparer.isVersionGreater("1.0.0", "0.1.0"));
        assertTrue(VersionComparer.isVersionGreater("1.0.0", "0.0.1"));
    }

}