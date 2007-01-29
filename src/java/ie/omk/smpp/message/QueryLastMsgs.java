package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.ParamDescriptor;

import java.util.List;

/**
 * Query the last number of messages sent from a certain ESME. Relevant
 * inherited fields from SMPPPacket: <br>
 * <ul>
 * source <br>
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class QueryLastMsgs extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    /**
     * The source address for which to query messages. The last <code>
     * msgCount</code> messages originating from this source address will
     * be retrieved.
     */
    private Address source;

    /**
     * Number of messages to look up.
     */
    private int msgCount;

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.INTEGER1);
    }
    
    /**
     * Construct a new QueryLastMsgs.
     */
    public QueryLastMsgs() {
        super(QUERY_LAST_MSGS);
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    // TODO: msgCount must be between 1 and 100 inclusive for validation.
    
    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("source=").append(source)
        .append("msgCount=").append(msgCount);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                source,
                Integer.valueOf(msgCount),
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        source = (Address) params.get(0);
        msgCount = ((Number) params.get(1)).intValue();
    }
}
