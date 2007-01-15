package ie.omk.smpp.message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SMSC response to a QueryLastMsgs request.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class QueryLastMsgsResp extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    /** The table of messages returned */
    private List<String> messageTable = new ArrayList<String>();

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.getListInstance(ParamDescriptor.CSTRING, 0));
    }
    
    /**
     * Construct a new QueryLastMsgsResp.
     */
    public QueryLastMsgsResp() {
        super(QUERY_LAST_MSGS_RESP);
    }

    /**
     * Create a new QueryLastMsgsResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public QueryLastMsgsResp(SMPPPacket request) {
        super(request);
    }

    /**
     * Add a message Id to the response packet.
     * 
     * @param id
     *            The message Id to add to the packet.
     * @return The current number of message Ids (including the new one).
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the id is invalid.
     */
    public int addMessageId(String id) {
        if (!version.validateMessageId(id)) {
            throw new InvalidParameterValueException("Invalid message ID", id);
        }
        messageTable.add(id);
        return messageTable.size();
    }

    /** Get the number of message Ids. */
    public int getMsgCount() {
        return messageTable.size();
    }

    /**
     * Get a String array of the message Ids.
     * 
     * @return A String array of all the message Ids.
     */
    public String[] getMessageIds() {
        String[] ids;
        int loop = 0;

        synchronized (messageTable) {
            if (messageTable.size() == 0) {
                return null;
            }

            ids = new String[messageTable.size()];
            Iterator i = messageTable.iterator();
            while (i.hasNext()) {
                ids[loop++] = (String) i.next();
            }
        }

        return ids;
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("query_last_msgs_resp");
    }

    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                Integer.valueOf(messageTable.size()),
                messageTable,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        // Copy the message ID list into a type safe collection.
        List list = (List) params.get(1);
        messageTable = new ArrayList<String>(list.size());
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            messageTable.add((String) iter.next());
        }
    }
}
