package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URL;

import org.testng.annotations.Test;

import com.adenki.smpp.encoding.ASCIIEncoding;

@Test
public class PropertiesAPIConfigTest {

    public void testPropertiesAreLoadedFromDefaultLocation() throws Exception {
        PropertiesAPIConfig config = new PropertiesAPIConfig();
        config.initialise();
        // The following properties are set in
        // src/main/resources/smppapi.properties.
        assertTrue(config.getBoolean(APIConfig.LINK_AUTO_FLUSH));
        assertEquals(config.getInt(APIConfig.LINK_TIMEOUT), 120000);
        assertEquals(config.getInt(APIConfig.TOO_MANY_IO_EXCEPTIONS), 3);
        assertEquals(config.getInt(APIConfig.BIND_TIMEOUT), 180000);
    }
    
    public void testPropertiesAreLoadedFromSpecifiedURL() throws Exception {
        URL url = getClass().getResource("/propertiesapiconfigtest.properties");
        assertNotNull(url);
        PropertiesAPIConfig config = new PropertiesAPIConfig(url);
        config.initialise();
        assertTrue(config.getBoolean(APIConfig.LAX_VERSIONS));
        assertEquals(config.getInt(APIConfig.LINK_TIMEOUT), 90);
        assertEquals(config.getInt(APIConfig.BIND_TIMEOUT), 91);
        assertEquals(config.getProperty(APIConfig.DEFAULT_ALPHABET), ASCIIEncoding.class.getName());
    }
    
    public void testReloadConfigCorrectlyReloadsFromURL() throws Exception {
        URL url = getClass().getResource("/propertiesapiconfigtest.properties");
        assertNotNull(url);
        PropertiesAPIConfig config = new PropertiesAPIConfig(url);
        config.initialise();
        assertFalse(config.isSet(APIConfig.EVENT_DISPATCHER_CLASS));
        config.setProperty(APIConfig.EVENT_DISPATCHER_CLASS, "RandomClass");
        config.setProperty(APIConfig.LINK_TIMEOUT, "78");
        assertTrue(config.isSet(APIConfig.EVENT_DISPATCHER_CLASS));
        assertEquals(config.getInt(APIConfig.LINK_TIMEOUT), 78);
        config.reloadAPIConfig();
        assertFalse(config.isSet(APIConfig.EVENT_DISPATCHER_CLASS));
        assertEquals(config.getInt(APIConfig.LINK_TIMEOUT), 90);
    }
    
    public void testReconfigureReloadsPropertiesFromNewLocation() throws Exception {
        PropertiesAPIConfig config = new PropertiesAPIConfig();
        config.initialise();
        URL url = getClass().getResource("/propertiesapiconfigtest.properties");
        assertNotNull(url);
        assertEquals(config.getInt(APIConfig.LINK_TIMEOUT), 120000);
        config.reconfigure(url);
        assertEquals(config.getInt(APIConfig.LINK_TIMEOUT), 90);
    }
}
