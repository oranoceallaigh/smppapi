package com.adenki.smpp.version;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

import com.adenki.smpp.util.APIConfig;
import com.adenki.smpp.util.APIConfigFactory;
import com.adenki.smpp.util.PropertiesAPIConfig;

@Test
public class VersionFactoryTest {

    public void testGetDefaultVersionNoConfig() {
        PropertiesAPIConfig cfg = new PropertiesAPIConfig();
        cfg.initialise();
        cfg.remove(APIConfig.DEFAULT_VERSION);
        APIConfigFactory.setCachedConfig(cfg);
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_5_0);
        APIConfigFactory.reset();
    }

    @Test(expectedExceptions = {VersionException.class})
    public void testGetDefaultVersionStrictWithValidConfig() {
        PropertiesAPIConfig cfg = new PropertiesAPIConfig();
        cfg.initialise();
        APIConfigFactory.setCachedConfig(cfg);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x33");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_3);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x34");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_4);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x50");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_5_0);
        
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x1a");
        try {
            VersionFactory.getDefaultVersion();
        } finally {
            APIConfigFactory.reset();
        }
    }

    @Test(expectedExceptions = {VersionException.class})
    public void testGetDefaultVersionLaxWithValidConfig() {
        PropertiesAPIConfig cfg = new PropertiesAPIConfig();
        cfg.initialise();
        APIConfigFactory.setCachedConfig(cfg);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x33");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_3);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x34");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_3_4);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0x50");
        assertSame(VersionFactory.getDefaultVersion(), SMPPVersion.VERSION_5_0);
        cfg.setProperty(APIConfig.DEFAULT_VERSION, "0xab");
        try {
            VersionFactory.getDefaultVersion();
        } finally {
            APIConfigFactory.reset();
        }
    }
    
    public void testGetVersionStrict() throws Exception {
        PropertiesAPIConfig cfg = new PropertiesAPIConfig();
        cfg.initialise();
        APIConfigFactory.setCachedConfig(cfg);
        cfg.setProperty(APIConfig.LAX_VERSIONS, "false");
        assertSame(VersionFactory.getVersion(0x33), SMPPVersion.VERSION_3_3);
        assertSame(VersionFactory.getVersion(0x34), SMPPVersion.VERSION_3_4);
        assertSame(VersionFactory.getVersion(0x50), SMPPVersion.VERSION_5_0);
        final int[] falseVersions = { 0, 0x13, 0x2c, 0x32, 0x40, 0x80 };
        for (int i = 0; i < falseVersions.length; i++) {
            try {
                VersionFactory.getVersion(falseVersions[i]);
                fail("Successfully got a version from value "
                        + falseVersions[i]);
            } catch (VersionException x) {
                // This is expected.
            }
        }
        APIConfigFactory.reset();
    }

    public void testGetVersionLax() throws Exception {
        PropertiesAPIConfig cfg = new PropertiesAPIConfig();
        cfg.initialise();
        APIConfigFactory.setCachedConfig(cfg);
        cfg.setProperty(APIConfig.LAX_VERSIONS, "true");
        for (int i = 0; i <= 0x33; i++) {
            assertSame(VersionFactory.getVersion(i), SMPPVersion.VERSION_3_3);
        }
        APIConfigFactory.reset();
    }
}
