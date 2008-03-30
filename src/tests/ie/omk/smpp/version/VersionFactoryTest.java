package ie.omk.smpp.version;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;
import ie.omk.smpp.util.APIConfig;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class VersionFactoryTest {

    private APIConfig cfg;
    
    @BeforeTest
    public void setUp() throws Exception {
        cfg = APIConfig.getInstance();
    }
    
    public void testGetDefaultVersionNoConfig() {
        cfg.remove(APIConfig.DEFAULT_VERSION);
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_5_0);
    }
    
    public void testGetDefaultVersionStrictWithValidConfig() {
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x33");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_3);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x34");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_4);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x50");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_5_0);
        
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
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_3);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x34");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_4);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x50");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_5_0);

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
        assertSame(VersionFactory.getVersion(0x33), SMPPVersion.VERSION_3_3);
        assertSame(VersionFactory.getVersion(0x34), SMPPVersion.VERSION_3_4);
        assertSame(VersionFactory.getVersion(0x50), SMPPVersion.VERSION_5_0);
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
            assertSame(VersionFactory.getVersion(i), SMPPVersion.VERSION_3_3);
        }
    }
}
