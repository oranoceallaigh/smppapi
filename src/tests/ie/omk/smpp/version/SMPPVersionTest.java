package ie.omk.smpp.version;

import ie.omk.smpp.util.APIConfig;
import junit.framework.TestCase;

public class SMPPVersionTest extends TestCase {
    public void testDefaultVersion() {
        assertEquals(SMPPVersion.V34, SMPPVersion.getDefaultVersion());
        assertSame(SMPPVersion.V34, SMPPVersion.getDefaultVersion());
    }
    
    public void testGetVersionStrict() throws Exception {
        APIConfig.getInstance().setProperty(APIConfig.LAX_VERSIONS, "false");
        assertSame(SMPPVersion.V33, SMPPVersion.getVersion(0x33));
        assertSame(SMPPVersion.V34, SMPPVersion.getVersion(0x34));
        int[] false_versions = { 0, 0x13, 0x2c, 0x32, 0x40, 0x80 };
        for (int i = 0; i < false_versions.length; i++) {
            try {
                SMPPVersion.getVersion(false_versions[i]);
                fail("Got a version with interface_version of "
                        + false_versions[i]);
            } catch (VersionException x) {
                // pass
            }
        }
    }

    public void testGetVersionLax() throws Exception {
        APIConfig.getInstance().setProperty(APIConfig.LAX_VERSIONS, "true");
        assertSame(SMPPVersion.V33, SMPPVersion.getVersion(0x33));
        assertSame(SMPPVersion.V34, SMPPVersion.getVersion(0x34));
        
        for (int i = 0; i <= 0x32; i++) {
            assertSame(SMPPVersion.V33, SMPPVersion.getVersion(i));
        }

        int[] false_versions = { 0x40, 0x80 };
        for (int i = 0; i < false_versions.length; i++) {
            try {
                SMPPVersion.getVersion(false_versions[i]);
                fail("Got a version with interface_version of "
                        + false_versions[i]);
            } catch (VersionException x) {
                // pass
            }
        }
    }
}
