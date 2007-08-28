package ie.omk.smpp.message;


import ie.omk.smpp.version.SMPPVersion;

import java.util.List;


/**
 * ESME response to a Deliver message request.
 * 
 * @version $Id: $
 */
public class DeliverSMResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;

    private String messageId;
    
    /**
     * Construct a new DeliverSMResp.
     */
    public DeliverSMResp() {
        super(DELIVER_SM_RESP);
    }

    /**
     * Create a new DeliverSMResp packet in response to a DeliverSM. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public DeliverSMResp(SMPPPacket request) {
        super(request);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("messageId=").append(messageId);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BodyDescriptor.ONE_CSTRING;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                messageId,
        };
    }

    @Override
    protected void setMandatoryParameters(List<Object> params) {
        messageId = (String) params.get(0);
    }
}
