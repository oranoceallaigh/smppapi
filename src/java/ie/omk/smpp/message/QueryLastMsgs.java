package ie.omk.smpp.message;

import ie.omk.smpp.Address;

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

    /**
     * Set the number of messages to look up. The minimum number of messages to
     * query is 1 and the maximum is 100.
     * 
     * @param n
     *            The message count (1 &lt;= n &lt;= 100)
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the count is set outside the valid range.
     */
    public void setMsgCount(int n) throws InvalidParameterValueException {
        if (n > 0 && n <= 100) {
            this.msgCount = n;
        } else {
            throw new InvalidParameterValueException(
                    "Message count must be between 1 and 100", n);
        }
    }

    /** Get the number of messages being requested. */
    public int getMsgCount() {
        return msgCount;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("query_last_msgs");
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
