package com.adenki.smpp.util;

import java.io.IOException;
import java.text.ParseException;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;
import com.adenki.smpp.message.SMPPProtocolException;

/**
 * Abstract base class for {@link PacketDecoder} implementations.
 * @version $Id$
 */
public abstract class AbstractPacketDecoder implements PacketDecoder {
    private final SMPPDateFormat dateFormat = new SMPPDateFormat();
    
    public Address readAddress() throws IOException {
        Address address = new Address();
        address.readFrom(this);
        return address;
    }
    
    public ErrorAddress readErrorAddress() throws IOException {
        ErrorAddress errorAddress = new ErrorAddress();
        errorAddress.readFrom(this);
        return errorAddress;
    }
    
    public SMPPDate readDate() throws IOException {
        SMPPDate date = null;
        String str = null;
        try {
            str = readCString();
            if (str.length() > 0) {
                date = (SMPPDate) dateFormat.parseObject(str);
            }
        } catch (ParseException x) {
            throw new SMPPProtocolException("Cannot parse date value: " + str, x);
        }
        return date;
    }
}
