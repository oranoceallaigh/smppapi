package ie.omk.smpp.message;


/**
 * Submit a message to the SMSC for delivery to a single destination.
 * 
 * @author Oran Kelly
 * @version $Id: $
 */
public class SubmitSM extends SMPacket {
    
    public SubmitSM() {
        super(SMPPPacket.SUBMIT_SM);
    }
}

