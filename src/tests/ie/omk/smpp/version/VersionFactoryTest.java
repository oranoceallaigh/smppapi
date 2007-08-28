package ie.omk.smpp.version;

import ie.omk.smpp.util.APIConfig;
import junit.framework.TestCase;

public class VersionFactoryTest extends TestCase {

    private APIConfig cfg;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cfg = APIConfig.getInstance();
    }
    
    public void testGetDefaultVersionNoConfig() {
        cfg.remove(APIConfig.DEFAULT_VERSION);
        assertSame(SMPPVersion.VERSION_5_0, VersionFactory.getDefaultVersion());
    }
    
    public void testGetDefaultVersionStrictWithValidConfig() {
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x33");
        assertSame(SMPPVersion.VERSION_3_3, VersionFactory.getDefaultVersion());
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x34");
        assertSame(SMPPVersion.VERSION_3_4, VersionFactory.getDefaultVersion());
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x50");
        assertSame(SMPPVersion.VERSION_5_0, VersionFactory.getDefaultVersion());
        
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x1a");
        try {
            VersionFactory.getDefaultVersion();
            fail("Successfully read a default version with a value of 0x1a.");
        } catch (VersionException x) {
            // pass
        }
    }
    
    public void testGetDefaultVersionLaxWithValidConfig() {
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x33");
        assertSame(SMPPVersion.VERSION_3_3, VersionFactory.getDefaultVersion());
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x34");
        assertSame(SMPPVersion.VERSION_3_4, VersionFactory.getDefaultVersion());
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x50");
        assertSame(SMPPVersion.VERSION_5_0, VersionFactory.getDefaultVersion());

        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0xab");
        try {
            VersionFactory.getDefaultVersion();
            fail("Successfully read a default version with a value of 0xab.");
        } catch (VersionException x) {
            // pass
        }
    }
    
    public void testGetVersionStrict() throws Exception {
        cfg.setProperty(APIConfig.LAX_VERSIONS, "false");
        assertSame(SMPPVersion.VERSION_3_3, VersionFactory.getVersion(0x33));
        assertSame(SMPPVersion.VERSION_3_4, VersionFactory.getVersion(0x34));
        assertSame(SMPPVersion.VERSION_5_0, VersionFactory.getVersion(0x50));
        int[] false_versions = { 0, 0x13, 0x2c, 0x32, 0x40, 0x80 };
        for (int i = 0; i < false_versions.length; i++) {
            try {
                VersionFactory.getVersion(false_versions[i]);
                fail("Got a version with interface_version of "
                        + false_versions[i]);
            } catch (VersionException x) {
                // pass
            }
        }
    }

    public void testGetVersionLax() throws Exception {
        cfg.setProperty(APIConfig.LAX_VERSIONS, "true");
        for (int i = 0; i <= 0x33; i++) {
            assertSame(SMPPVersion.VERSION_3_3, VersionFactory.getVersion(i));
        }
    }
}
