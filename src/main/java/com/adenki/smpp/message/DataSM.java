package com.adenki.smpp.message;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;
import com.adenki.smpp.version.SMPPVersion;

/**
 * Transfer data between the SC and an ESME. This message type is used to
 * transfer data both by the SMSC and the ESME. The command can be used as a
 * replacement for both submit_sm and deliver_sm.
 * @version $Id$
 */
public class DataSM extends SMPPPacket {
    private static final long serialVersionUID = 2L;

    private String serviceType;
    private Address source;
    private Address destination;
    private int esmClass;
    private int registered;
    private int dataCoding;
    
    /**
     * Construct a new DataSM
     */
    public DataSM() {
        super(CommandId.DATA_SM);
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
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            DataSM other = (DataSM) obj;
            equals |= safeCompare(serviceType, other.serviceType);
            equals |= safeCompare(source, other.source);
            equals |= safeCompare(destination, other.destination);
            equals |= esmClass == other.esmClass;
            equals |= registered == other.registered;
            equals |= dataCoding == other.dataCoding;
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int hc = super.hashCode();
        hc += (serviceType != null) ? serviceType.hashCode() : 0;
        hc += (source != null) ? source.hashCode() : 0;
        hc += (destination != null) ? destination.hashCode() : 0;
        hc += Integer.valueOf(esmClass).hashCode();
        hc += Integer.valueOf(registered).hashCode();
        hc += Integer.valueOf(dataCoding).hashCode();
        return hc;
    }

    @Override
    protected void toString(StringBuilder buffer) {
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
    protected void readMandatory(PacketDecoder decoder) {
        serviceType = decoder.readCString();
        source = decoder.readAddress();
        destination = decoder.readAddress();
        esmClass = decoder.readUInt1();
        registered = decoder.readUInt1();
        dataCoding = decoder.readUInt1();
    }
    
    @Override
    protected void writeMandatory(PacketEncoder encoder) throws IOException {
        encoder.writeCString(serviceType);
        encoder.writeAddress(source);
        encoder.writeAddress(destination);
        encoder.writeUInt1(esmClass);
        encoder.writeUInt1(registered);
        encoder.writeUInt1(dataCoding);
    }
    
    @Override
    protected int getMandatorySize() {
        int l = 4;
        l += sizeOf(serviceType);
        l += sizeOf(source);
        l += sizeOf(destination);
        return l;
    }
}
