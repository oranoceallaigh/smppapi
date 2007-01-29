package ie.omk.smpp.message;

/**
 * ESME or SMSC response to an EnquireLink request. Used to positivly
 * acknowledge that this entity is still alive and capable of submitting, or
 * responding to, SMPP messages.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class EnquireLinkResp extends SMPPPacket {
    /**
     * Construct a new EnquireLinkResp.
     */
    public EnquireLinkResp() {
        super(ENQUIRE_LINK_RESP);
    }

    /**
     * Create a new BindReceiverResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public EnquireLinkResp(SMPPPacket request) {
        super(request);
    }
}

