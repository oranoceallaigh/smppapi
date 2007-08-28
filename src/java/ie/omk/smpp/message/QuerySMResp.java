package ie.omk.smpp.message;

import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * SMSC response to a QuerySM request. Contains the current state of a short
 * message at the SMSC.
 * 
 * @version $Id$
 */
public class QuerySMResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    private String messageId;
    private SMPPDate finalDate;
    private int messageState;
    private int errorCode;
    
    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1);
    }
    
    /**
     * Construct a new QuerySMResp.
     */
    public QuerySMResp() {
        super(QUERY_SM_RESP);
    }

    /**
     * Create a new QuerySMResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public QuerySMResp(SMPPPacket request) {
        super(request);
    }
    
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public SMPPDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(SMPPDate finalDate) {
        this.finalDate = finalDate;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getMessageState() {
        return messageState;
    }

    public void setMessageState(int messageState) {
        this.messageState = messageState;
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("messageId=").append(messageId)
        .append(",finalDate=").append(finalDate)
        .append(",messageState=").append(messageState)
        .append(",errorCode=").append(errorCode);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
        smppVersion.validateMessageState(messageState);
        smppVersion.validateErrorCode(errorCode);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }

    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                messageId,
                finalDate,
                Integer.valueOf(messageState),
                Integer.valueOf(errorCode),
        };
    }

    @Override
    protected void setMandatoryParameters(List<Object> params) {
        messageId = (String) params.get(0);
        finalDate = (SMPPDate) params.get(1);
        messageState = ((Number) params.get(2)).intValue();
        errorCode = ((Number) params.get(3)).intValue();
    }
}
