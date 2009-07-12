package com.adenki.smpp.util;

import static org.testng.Assert.assertNotNull;

import java.util.MissingResourceException;

import org.testng.annotations.Test;

@Test
public class APIMessagesTest {

    public void testAPIMessagesWorksWithNoBundle() throws Exception {
        try {
            PropertiesAPIConfig cfg = new PropertiesAPIConfig();
            cfg.initialise();
            cfg.setProperty(APIMessages.BUNDLE_PROPERTY, "non_existent");
            APIConfigFactory.setCachedConfig(cfg);
            APIMessages messages = new APIMessages();
            assertNotNull(messages.getPacketStatus(8));
        } finally {
            // Ensure other tests are not affected by this one.
            APIConfigFactory.reset();
        }
    }
    
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
