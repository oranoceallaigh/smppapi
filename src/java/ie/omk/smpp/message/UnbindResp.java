package ie.omk.smpp.message;

/**
 * SMSC response to an Unbind request.
 * 
 * @version $Id$
 */
public class UnbindResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new UnbindResp.
     */
    public UnbindResp() {
        super(UNBIND_RESP);
    }

    /**
     * Create a new UnbindResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public UnbindResp(SMPPPacket request) {
        super(request);
    }
}
