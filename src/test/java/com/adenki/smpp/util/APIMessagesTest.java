package com.adenki.smpp.util;

import static org.testng.Assert.assertNotNull;

import java.util.MissingResourceException;

import org.testng.annotations.Test;

public class APIMessagesTest {

    @Test
    public void testAPIMessagesWorksWithNoBundle() throws Exception {
        PropertiesAPIConfig cfg = new PropertiesAPIConfig();
        cfg.initialise();
        cfg.setProperty(APIMessages.BUNDLE_PROPERTY, "non_existent");
        APIConfigFactory.setCachedConfig(cfg);
        APIMessages messages = new APIMessages();
        assertNotNull(messages.getPacketStatus(8));
        // Ensure other tests are not affected by this one.
        APIConfigFactory.reset();
    }
    
    @Test
    public void testGetPacketStatusReturnsValidValue() throws Exception {
        APIMessages messages = new APIMessages();
        assertNotNull(messages.getPacketStatus(2));
    }
    
    @Test(expectedExceptions = MissingResourceException.class)
    public void testGetPacketStatusThrowsExceptionOnUnrecognizedStatus() throws Exception {
        APIMessages messages = new APIMessages();
        messages.getPacketStatus(Integer.MAX_VALUE);
    }
}
