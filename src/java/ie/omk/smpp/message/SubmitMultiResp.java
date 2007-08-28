package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.ErrorAddress;
import ie.omk.smpp.message.param.ListParamDescriptor;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.version.SMPPVersion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Submit to multiple destinations response.
 * 
 * @version $Id: $
 */
public class SubmitMultiResp extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    private String messageId;
    
    /** Table of unsuccessful destinations */
    private List<ErrorAddress> unsuccessfulTable = new ArrayList<ErrorAddress>();

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.INTEGER1)
        .add(new ListParamDescriptor(ParamDescriptor.ERROR_ADDRESS, 1));
    }
    
    /**
     * Construct a new Unbind.
     */
    public SubmitMultiResp() {
        super(SUBMIT_MULTI_RESP);
    }

    /**
     * Create a new SubmitMultiResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param request
     *            The Request packet the response is to
     */
    public SubmitMultiResp(SMPPPacket request) {
        super(request);
    }

    
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /** Get the number of destinations the message was not delivered to. */
    public int getUnsuccessfulCount() {
        return unsuccessfulTable.size();
    }

    /**
     * Add a destination address to the table of unsuccessful destinations.
     * 
     * @param ea
     *            ErrorAddress object representing the failed destination
     * @return The current count of unsuccessful destinations (including the new
     *         one)
     */
    public int add(ErrorAddress ea) {
        unsuccessfulTable.add(ea);
        return unsuccessfulTable.size();
    }

    /**
     * Remove an address from the table of unsuccessful destinations.
     * 
     * @param a
     *            the address to remove.
     * @return the size of the table after removal.
     */
    public int remove(Address a) {
        synchronized (unsuccessfulTable) {
            int i = unsuccessfulTable.indexOf(a);
            if (i > -1) {
                unsuccessfulTable.remove(i);
            }

            return unsuccessfulTable.size();
        }
    }

    /**
     * Get an iterator to iterate over the set of addresses in the unsuccessful
     * destination table.
     */
    public ListIterator<ErrorAddress> tableIterator() {
        return unsuccessfulTable.listIterator();
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("messageId=").append(messageId)
        .append(",unsuccessfulCount=").append(unsuccessfulTable.size())
        .append(",unsuccessful=").append(unsuccessfulTable);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
        smppVersion.validateNumUnsuccessful(unsuccessfulTable.size());
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }

    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                messageId,
                Integer.valueOf(unsuccessfulTable.size()),
                unsuccessfulTable,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        messageId = (String) params.get(0);
        // Index 1 intentionally skipped
        List list = (List) params.get(2);
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            unsuccessfulTable.add((ErrorAddress) iter.next());
        }
    }
}
