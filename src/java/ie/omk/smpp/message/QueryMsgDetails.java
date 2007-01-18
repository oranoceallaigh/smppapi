package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.ParamDescriptor;

import java.util.List;

/**
 * Query Message details. Get all information about an existing message at the
 * SMSC.
 * 
 * @author Oran Kelly
 * @version $Id: $
 */
public class QueryMsgDetails extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    /**
     * Original message ID of the required message.
     */
    private String messageId;
    /**
     * Source address of the message.
     */
    private Address source;
    /**
     * Length of the message text required.
     */
    private int smLength;

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.INTEGER1);
    }
    
    /**
     * Construct a new QueryMsgDetails.
     */
    public QueryMsgDetails() {
        super(QUERY_MSG_DETAILS);
    }
    
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    /**
     * Set the number of bytes of the original message required. Minimum request
     * length is 0, maximum is 160. If the length is outside these bounds, it
     * will be set to the min or max.
     * 
     * @param len
     *            The number of bytes required.
     */
    public void setSmLength(int len) {
        smLength = len;

        if (smLength < 0) {
            smLength = 0;
        }
        if (smLength > 160) {
            smLength = 160;
        }
    }

    /** Get the number of bytes of the original message being requested. */
    public int getSmLength() {
        return smLength;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("query_msg_details");
    }

    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                messageId,
                source,
                Integer.valueOf(smLength),
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        messageId = (String) params.get(0);
        source = (Address) params.get(1);
        smLength = ((Number) params.get(2)).intValue();
    }
}
