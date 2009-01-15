package com.adenki.smpp.message;

/**
 * Bind to the SMSC as receiver. This message is used to bind to the SMSC as a
 * Receiver ESME.
 * 
 * @version $Id$
 */
public class BindReceiver extends com.adenki.smpp.message.Bind {
    private static final long serialVersionUID = 2L;
    /**
     * Constructs a new BindReceiver.
     */
    public BindReceiver() {
        super(CommandId.BIND_RECEIVER);
    }
}

