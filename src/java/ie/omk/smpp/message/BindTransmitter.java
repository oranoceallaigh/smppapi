package ie.omk.smpp.message;

/**
 * Bind to the SMSC as a transmitter.
 * 
 * @version $Id$
 */
public class BindTransmitter extends Bind {
    private static final long serialVersionUID = 1L;
    /**
     * Construct a new BindTransmitter.
     */
    public BindTransmitter() {
        super(BIND_TRANSMITTER);
    }
}

