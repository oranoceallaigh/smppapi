package com.adenki.smpp.gsm.ems;

import java.nio.ByteBuffer;

import com.adenki.smpp.gsm.AbstractHeaderElement;

/**
 * An extended object.
 * @version $Id$
 */
public class ExtendedObject extends AbstractHeaderElement {

    private int referenceNum;
    private int objectFormat;
    private int objectPosition;
    private int ptr;
    private byte[] data = new byte[0];
    private boolean mayForward = true;
    private boolean userPrompt;

    public ExtendedObject(int objectFormat, int referenceNum) {
        this.objectFormat = objectFormat;
        this.referenceNum = referenceNum;
    }

    public ExtendedObject(int objectFormat, int referenceNum, byte[] data) {
        this(objectFormat, referenceNum);
        setData(data);
    }
    
    public int getLength() {
        return data.length;
    }

    @Override
    public void reset() {
        super.reset();
        ptr = 0;
    }
    
    @Override
    public boolean isComplete() {
        return ptr == data.length;
    }
    
    @Override
    public boolean write(int segmentNum, ByteBuffer buffer) {
        // If we're writing the header, we need at least enough octets
        // for the IEI, IEI-Length and 7 octets of the extended object header.
        // Otherwise, we need at least enough octets for the IEI, IEI-Length
        // and 1 octet of actual data.
        if (ptr == 0 && buffer.remaining() < 9) {
            return false;
        } else if (ptr > 0 && buffer.remaining() < 3) {
            return false;
        }
        // See how many bytes we can write out from the data..
        int dataSize = Math.min(buffer.remaining() - 2, data.length - ptr);
        buffer.put((byte) 0x14);
        buffer.put((byte) dataSize);
        buffer.put(data, ptr, dataSize);
        ptr += dataSize;
        return true;
    }

    /**
     * Determine if this extended object can be forwarded by SMS.
     * @return <tt>true</tt> if the object may be forwarded, <tt>false</tt>
     * otherwise.
     */
    public boolean isMayForward() {
        return mayForward;
    }

    /**
     * Set if this object may be forwarded by SMS.
     * @param mayForward <tt>true</tt> if the object may be forwarded,
     * <tt>false</tt> if not.
     */
    public void setMayForward(boolean mayForward) {
        this.mayForward = mayForward;
    }

    /**
     * Determine if this object should be handled as a user prompt.
     * @return <tt>true</tt> if this object should be handled as a user
     * prompt, <tt>false</tt> if it should be handled normally.
     */
    public boolean isUserPrompt() {
        return userPrompt;
    }

    /**
     * Set if this object should be handled as a user prompt.
     * @param userPrompt <tt>true</tt> if this object should be handled as
     * a user prompt, <tt>false</tt> if it should be handled normally.
     */
    public void setUserPrompt(boolean userPrompt) {
        this.userPrompt = userPrompt;
    }
    
    /**
     * Get the object format of this extended object.
     * @return An integer representing the object's format.
     */
    public int getObjectFormat() {
        return objectFormat;
    }

    /**
     * Get the position in the SMS where this object will be displayed.
     * @return The position in the SMS where this object will be displayed.
     */
    public int getObjectPosition() {
        return objectPosition;
    }

    /**
     * Get the data of this extended object. This will include the 7-octet
     * extended object header.
     * @return This object's data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set this extended object's data. This should not include the
     * 7-octet header, as this will be generated.
     * @param data This extended object's data.
     */
    public void setData(byte[] data) {
        if (data == null) {
            throw new NullPointerException("data cannot be null");
        }
        this.data = new byte[data.length + 7];
        System.arraycopy(data, 0, this.data, 7, data.length);
        initHeader(data.length);
        reset();
    }

    /**
     * Set the position in the SMS where this object will be displayed.
     * @param objectPosition The position in the SMS where this object
     * will be displayed.
     */
    public void setObjectPosition(int objectPosition) {
        this.objectPosition = objectPosition;
    }

    private int getControlData() {
        int control = 0;
        if (!mayForward) {
            control |= 1;
        }
        if (userPrompt) {
            control |= 2;
        }
        return control;
    }
    
    private void initHeader(int dataLength) {
        data[0] = (byte) referenceNum;
        data[1] = (byte) (dataLength >> 8);
        data[2] = (byte) (dataLength & 0xff);
        data[3] = (byte) getControlData();
        data[4] = (byte) objectFormat;
        data[5] = (byte) (objectPosition >> 8);
        data[6] = (byte) (objectPosition & 0xff);
    }
}
