package com.adenki.smpp.util;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.gsm.GSMError;
import com.adenki.smpp.message.MessageState;

/**
 * Look up descriptions of various SMPP codes.
 * @version $Id$
 * @since 0.4.0
 */
public class APIMessages {
    public static final String BUNDLE_PROPERTY = "smppapi.bundle";
    public static final String DEFAULT_BUNDLE_NAME = "smpp_messages";
    
    private static final Logger LOG = LoggerFactory.getLogger(APIMessages.class);
    private static final String PACKET_STATUS_PREFIX = "packet.status.";
    private static final String MESSAGE_STATE_PREFIX = "message.state.";
    private static final String GSM_ERROR_PREFIX = "gsm.errors.";
    
    private ResourceBundle bundle;
    
    public APIMessages() {
        loadBundle();
    }
    
    public String getPacketStatus(int statusCode) {
        StringBuilder resource = new StringBuilder(PACKET_STATUS_PREFIX);
        resource.append("0x");
        resource.append(Integer.toHexString(statusCode).toLowerCase());
        return bundle.getString(resource.toString());
    }

    public String getMessageState(MessageState state) {
        StringBuilder resource = new StringBuilder(MESSAGE_STATE_PREFIX);
        resource.append("0x");
        resource.append(Integer.toHexString(state.getValue()).toLowerCase());
        return bundle.getString(resource.toString());
    }

    public String getGSMError(GSMError error) {
        StringBuilder resource = new StringBuilder(GSM_ERROR_PREFIX);
        resource.append(error.name().toLowerCase());
        return bundle.getString(resource.toString());
    }

    private void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle(getBundleName());
        } catch (MissingResourceException x) {
            LOG.warn("Cannot load API messages.");
            bundle = getDummyBundle();
        }
    }

    private String getBundleName() {
        APIConfig cfg = APIConfigFactory.getConfig();
        return cfg.getProperty(BUNDLE_PROPERTY, DEFAULT_BUNDLE_NAME);
    }
    
    private ResourceBundle getDummyBundle() {
        return new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return key;
            }
            
            @Override
            public Enumeration<String> getKeys() {
                return new Vector<String>().elements();
            }
        };
    }
}
