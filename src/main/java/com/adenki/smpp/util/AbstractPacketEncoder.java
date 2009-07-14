package com.adenki.smpp.util;

import java.io.IOException;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;

/**
 * Abstract base class for {@link PacketEncoder} implementations.
 * @version $Id$
 */
public abstract class AbstractPacketEncoder implements PacketEncoder {
    private final SMPPDateFormat dateFormat = new SMPPDateFormat();
    
    public PacketEncoder writeCString(String value) throws IOException {
        if (value != null) {
            writeBytes(value.getBytes("US-ASCII"));
        }
        writeByte(0);
        return this;
    }

    public PacketEncoder writeString(String value, int length) throws IOException {
        if (value != null) {
            if (length > value.length()) {
                throw new IndexOutOfBoundsException(String.valueOf(length));
            }
            byte[] array = value.getBytes("US-ASCII");
            writeBytes(array, 0, length);
        } else {
            if (length > 0) {
                throw new IndexOutOfBoundsException(String.valueOf(length));
            }
        }
        return this;
    }

    public PacketEncoder writeAddress(Address address) throws IOException {
        if (address != null) {
            address.writeTo(this);
        } else {
            new Address().writeTo(this);
        }
        return this;
    }

    public PacketEncoder writeErrorAddress(ErrorAddress errorAddress) throws IOException {
        if (errorAddress != null) {
            errorAddress.writeTo(this);
        } else {
            new ErrorAddress().writeTo(this);
        }
        return this;
    }

    public PacketEncoder writeDate(SMPPDate date) throws IOException {
        String str = dateFormat.format(date);
        writeCString(str);
        return this;
    }
}
