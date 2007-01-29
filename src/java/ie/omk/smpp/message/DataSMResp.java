package ie.omk.smpp.message;

import ie.omk.smpp.version.SMPPVersion;

import java.util.List;


/**
 * Response to a data_sm.
 * @version $Id$
 */
public class DataSMResp extends SMPPPacket {
    private String messageId;
    
    /**
     * Construct a new DataSMResp.
     */
    public DataSMResp() {
        super(DATA_SM_RESP);
    }

    /**
     * Create a new DataSMResp packet in response to a DataSM. This constructor
     * will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public DataSMResp(SMPPPacket request) {
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
