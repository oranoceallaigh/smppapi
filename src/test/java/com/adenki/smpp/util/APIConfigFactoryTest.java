package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

@Test
public class APIConfigFactoryTest {

    public void testPropertiesAPIConfigIsTheDefault() {
        APIConfigFactory.reset();
        APIConfig config = APIConfigFactory.getConfig();
        assertNotNull(config);
        assertEquals(config.getClass(), PropertiesAPIConfig.class);
    }
    
    public void testGetConfigReturnsACachedClass() {
        APIConfigFactory.reset();
        APIConfig config1 = APIConfigFactory.getConfig();
        APIConfig config2 = APIConfigFactory.getConfig();
        assertNotNull(config1);
        assertNotNull(config2);
        assertSame(config1, config2);
    }
    
    public void testNewInstanceIsInstantiatedWhenCachingIsDisabled() {
        try {
            System.setProperty(APIConfigFactory.CACHE_CONFIG_PROP, "false");
            APIConfigFactory.reset();
            APIConfig config1 = APIConfigFactory.getConfig();
            APIConfig config2 = APIConfigFactory.getConfig();
            assertNotNull(config1);
            assertNotNull(config2);
            assertNotSame(config1, config2);
        } finally {
            System.clearProperty(APIConfigFactory.CACHE_CONFIG_PROP);
        }
    }
    
    public void testLoadConfigLoadsSpecifiedConfigClass() throws Exception {
        try {
            System.setProperty(
                    APIConfigFactory.CONFIG_CLASS_PROP,
                    "com.adenki.smpp.util.NullAPIConfig");
            APIConfigFactory.reset();
            APIConfig config = APIConfigFactory.loadConfig();
            assertNotNull(config);
            assertEquals(config.getClass(), NullAPIConfig.class);
        } finally {
            System.clearProperty(APIConfigFactory.CONFIG_CLASS_PROP);
        }
    }

    @Test(expectedExceptions = {InvalidConfigurationException.class})
    public void testLoadConfigThrowsExceptionWhenConfigClassDoesNotExist() {
        try {
            System.setProperty(
                    APIConfigFactory.CONFIG_CLASS_PROP,
                    "com.adenki.smpp.util.NonExistentAPIConfig");
            APIConfigFactory.reset();
            APIConfigFactory.loadConfig();
        } finally {
            System.clearProperty(APIConfigFactory.CONFIG_CLASS_PROP);
        }
    }
}
