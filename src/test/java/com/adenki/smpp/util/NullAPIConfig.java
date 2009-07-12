package com.adenki.smpp.util;

/**
 * An object that implements {@link APIConfig} but does nothing.
 * @version $Id$
 */
public class NullAPIConfig extends AbstractAPIConfig implements APIConfig {

    public String getProperty(String property) throws PropertyNotFoundException {
        throw new PropertyNotFoundException();
    }

    public void initialise() {
    }

    public boolean reloadAPIConfig() {
        return false;
    }
}
