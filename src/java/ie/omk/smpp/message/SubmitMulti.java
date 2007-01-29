package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.BytesParamDescriptor;
import ie.omk.smpp.message.param.DestinationTableParamDescriptor;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * Submit a message to multiple destinations.
 * 
 * @author Oran Kelly
 * @version $Id: $
 */
public class SubmitMulti extends SMPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    /** Table of destinations */
    private DestinationTable destinationTable = new DestinationTable();

    static {
        List<ParamDescriptor> body = BODY_DESCRIPTOR.getBody();
        body.addAll(SMPacket.BODY_DESCRIPTOR.getBody());
        body.set(2, ParamDescriptor.INTEGER1);
        body.add(3, new DestinationTableParamDescriptor(2));
        body.set(14, new BytesParamDescriptor(13));
    }
    
    /**
     * Construct a new SubmitMulti.
     */
    public SubmitMulti() {
        super(SUBMIT_MULTI);
    }

    /**
     * Get a handle to the error destination table. Applications may add
     * destination addresses or distribution list names to the destination
     * table.
     */
    public DestinationTable getDestinationTable() {
        return destinationTable;
    }

    /**
     * Add an address to the destination table.
     * 
     * @param d
     *            The SME destination address
     * @return The current number of destination addresses (including the new
     *         one).
     * @see Address
     */
    public int addDestination(Address d) {
        synchronized (destinationTable) {
            destinationTable.add(d);
            return destinationTable.size();
        }
    }

    /**
     * Add a distribution list to the destination table.
     * 
     * @param d
     *            the distribution list name.
     * @return The current number of destination addresses (including the new
     */
    public int addDestination(String d) {
        synchronized (destinationTable) {
            destinationTable.add(d);
            return destinationTable.size();
        }
    }

    /**
     * Get the number of destinations in the destination table.
     */
    public int getNumDests() {
        return destinationTable.size();
    }

    public void setDestination(Address destination) {
        throw new UnsupportedOperationException("SubmitMulti does not support"
                + " the setDestination operation");
    }

    @Override
    protected void toString(StringBuffer buffer) {
        int length = 0;
        if (message != null) {
            length = message.length;
        }
        buffer.append("serviceType=").append(serviceType)
        .append(",source=").append(source)
        .append(",numberOfDests=").append(destinationTable.size())
        .append(",destinations=").append(destinationTable)
        .append(",esmClass=").append(esmClass)
        .append(",protocolID=").append(protocolID)
        .append(",priority=").append(priority)
        .append(",deliveryTime=").append(deliveryTime)
        .append(",expiryTime=").append(expiryTime)
        .append(",registered=").append(registered)
        .append(",replaceIfPresent=").append(replaceIfPresent)
        .append(",dataCoding=").append(dataCoding)
        .append(",defaultMsg=").append(defaultMsg)
        .append(",smLength=").append(length)
        .append(",message=").append(message);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        super.validateMandatory(smppVersion);
        smppVersion.validateNumberOfDests(destinationTable.size());
        for (Address address : destinationTable.getAddresses()) {
            smppVersion.validateAddress(address);
        }
        for (String distributionList : destinationTable.getDistributionLists()) {
            smppVersion.validateDistListName(distributionList);
        }
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }

    @Override
    protected Object[] getMandatoryParameters() {
        Object[] superObj = super.getMandatoryParameters();
        Object[] obj = new Object[15];
        obj[0] = superObj[0];
        obj[1] = superObj[1];
        obj[2] = Integer.valueOf(destinationTable.size());
        obj[3] = destinationTable;
        System.arraycopy(superObj, 3, obj, 4, 11);
        return obj;
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        serviceType = (String) params.get(0);
        source = (Address) params.get(1);
        // Intentionally skipping index 2
        destinationTable = (DestinationTable) params.get(3);
        esmClass = ((Number) params.get(4)).intValue();
        protocolID = ((Number) params.get(5)).intValue();
        priority = ((Number) params.get(6)).intValue();
        deliveryTime = (SMPPDate) params.get(7);
        expiryTime = (SMPPDate) params.get(8);
        registered = ((Number) params.get(9)).intValue();
        replaceIfPresent = ((Number) params.get(10)).intValue();
        dataCoding = ((Number) params.get(11)).intValue();
        defaultMsg = ((Number) params.get(12)).intValue();
        // Intentionally skipping param[13]
        message = (byte[]) params.get(14);
    }
}
