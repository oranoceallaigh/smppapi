package com.adenki.smpp.util;

import java.text.ParseException;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;
import com.adenki.smpp.message.SMPPProtocolException;

/**
 * Implementation of the packet decoder.
 * @version $Id$
 */
public class PacketDecoderImpl implements PacketDecoder {

    private static final SMPPDateFormat DATE_FORMAT = new SMPPDateFormat();
    private byte[] bytes;
    private int pos;
    
    public PacketDecoderImpl() {
    }

    public PacketDecoderImpl(byte[] bytes) {
        this.bytes = bytes;
    }

    public PacketDecoderImpl(byte[] bytes, int parsePosition) {
        this.bytes = bytes;
        this.pos = parsePosition;
    }
    
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getParsePosition() {
        return pos;
    }

    public void setParsePosition(int pos) {
        this.pos = pos;
    }

    public int getAvailableBytes() {
        return bytes.length - pos;
    }
    
    public byte readByte() {
        int index = pos;
        pos++;
        return bytes[index];
    }

    public String readCString() {
        String str = SMPPIO.readCString(bytes, pos);
        pos += str.length() + 1;
        return str;
    }

    public String readString(int length) {
        String str = SMPPIO.readString(bytes, pos, length);
        pos += str.length();
        return str;
    }

    public int readUInt1() {
        int value = SMPPIO.readUInt1(bytes, pos);
        pos++;
        return value;
    }

    public int readUInt2() {
        int value = SMPPIO.readUInt2(bytes, pos);
        pos += 2;
        return value;
    }

    public long readUInt4() {
        long value = SMPPIO.readUInt4(bytes, pos);
        pos += 4;
        return value;
    }

    public long readInt8() {
        long value = SMPPIO.readInt8(bytes, pos);
        pos += 8;
        return value;
    }
    
    public Address readAddress() {
        Address address = new Address();
        address.readFrom(this);
        return address;
    }
    
    public ErrorAddress readErrorAddress() {
        ErrorAddress errorAddress = new ErrorAddress();
        errorAddress.readFrom(this);
        return errorAddress;
    }
    
    public SMPPDate readDate() {
        SMPPDate date = null;
        String str = null;
        try {
            str = readCString();
            if (str.length() > 0) {
                date = (SMPPDate) DATE_FORMAT.parseObject(str);
            }
        } catch (ParseException x) {
            throw new SMPPProtocolException("Cannot parse date value: " + str, x);
        }
        return date;
    }
    
    public byte[] readBytes(int length) {
        int startIndex = pos;
        if (startIndex + length > bytes.length) {
            throw new ArrayIndexOutOfBoundsException(startIndex + length);
        }
        byte[] copy = new byte[length];
        System.arraycopy(bytes, startIndex, copy, 0, length);
        pos += length;
        return copy;
    }
}
