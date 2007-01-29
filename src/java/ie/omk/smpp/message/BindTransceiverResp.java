package ie.omk.smpp.message;

/**
 * SMSC response to a BindTransceiver request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class BindTransceiverResp extends BindResp {
    /**
     * Construct a new BindTransceiverResp.
     */
    public BindTransceiverResp() {
        super(BIND_TRANSCEIVER_RESP);
    }

    /**
     * Create a new BindTransceiverResp packet in response to a BindTransceiver.
     * This constructor will set the sequence number to that of the packet it is
     * in response to.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public BindTransceiverResp(SMPPPacket request) {
        super(request);
    }
}

