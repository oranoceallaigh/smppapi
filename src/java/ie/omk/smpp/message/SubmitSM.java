package ie.omk.smpp.message;


/**
 * Submit a message to the SMSC for delivery to a single destination.
 * 
 * @version $Id: $
 */
public class SubmitSM extends SMPacket {
    private static final long serialVersionUID = 1L;
    
    public SubmitSM() {
        super(SMPPPacket.SUBMIT_SM);
    }
}

