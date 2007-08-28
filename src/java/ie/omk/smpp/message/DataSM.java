package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * Transfer data between the SC and an ESME. This message type is used to
 * transfer data both by the SMSC and the ESME. The command can be used as a
 * replacement for both submit_sm and deliver_sm.
 * @version $Id$
 */
public class DataSM extends SMPPPacket {
    private static final long serialVersionUID = 1L;
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();

    private String serviceType;
    private Address source;
    private Address destination;
    private int esmClass;
    private int registered;
    private int dataCoding;
    
    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1);
    }
    
    /**
     * Construct a new DataSM
     */
    public DataSM() {
        super(DATA_SM);
    }

    public int getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(int dataCoding) {
        this.dataCoding = dataCoding;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public int getEsmClass() {
        return esmClass;
    }

    public void setEsmClass(int esmClass) {
        this.esmClass = esmClass;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("serviceType=").append(serviceType)
        .append(",source=").append(source)
        .append(",destination=").append(destination)
        .append(",esmClass=").append(esmClass)
        .append(",registered=").append(registered)
        .append(",dataCoding=").append(dataCoding);
    }

    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateServiceType(serviceType);
        smppVersion.validateAddress(source);
        smppVersion.validateAddress(destination);
        smppVersion.validateEsmClass(esmClass);
        smppVersion.validateRegisteredDelivery(registered);
        smppVersion.validateDataCoding(dataCoding);
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                serviceType,
                source,
                destination,
                Integer.valueOf(esmClass),
                Integer.valueOf(registered),
                Integer.valueOf(dataCoding),
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        serviceType = (String) params.get(0);
        source = (Address) params.get(1);
        destination = (Address) params.get(2);
        esmClass = ((Number) params.get(3)).intValue();
        registered = ((Number) params.get(4)).intValue();
        dataCoding = ((Number) params.get(5)).intValue();
    }
}
