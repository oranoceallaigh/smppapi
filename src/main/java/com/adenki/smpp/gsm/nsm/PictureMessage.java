package com.adenki.smpp.gsm.nsm;

import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.gsm.PortAddressing16;
import com.adenki.smpp.gsm.UserData;
import com.adenki.smpp.gsm.UserDataImpl;

/**
 * Implementation of a Nokia Smart Messaging Picture Multipart Message.
 * @version $Id$
 */
public class PictureMessage {
    private OTABitmap bitmap;
    private String text;
    private boolean useUnicode;
    
    public PictureMessage() {
    }
    
    public PictureMessage(BufferedImage image, String text) {
        this(new OTABitmap(image), text);
    }
    
    public PictureMessage(OTABitmap bitmap, String text) {
        this.bitmap = bitmap;
        this.text = text;
    }

    public byte[][] createSegments() {
        UserData userData = new UserDataImpl();
        userData.addHeaderElement(new PortAddressing16(0, 0x158a));
        userData.setData(getData());
        return userData.toSegments();
    }
    
    public byte[] getData() {
        byte[] bitmapData = bitmap.getData();
        byte[] textData;
        try {
            String encoding = useUnicode ? "UTF-16" : "ISO-8859-1";
            textData = text.getBytes(encoding);
        } catch (UnsupportedEncodingException x) {
            throw new SMPPRuntimeException(x);
        }
        int size = 7 + bitmapData.length + textData.length;
        ByteBuffer buffer = ByteBuffer.allocate(size);
        // Version, an ASCII "0"
        buffer.put((byte) 0x30);
        // Output text first
        int textType = useUnicode ? 1 : 0;
        buffer.put((byte) textType);
        buffer.putShort((short) textData.length);
        buffer.put(textData);
        // Followed by the image
        buffer.put((byte) 2);
        buffer.putShort((short) bitmapData.length);
        buffer.put(bitmapData);
        return buffer.array();
    }
    
    public OTABitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(OTABitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isUseUnicode() {
        return useUnicode;
    }

    public void setUseUnicode(boolean useUnicode) {
        this.useUnicode = useUnicode;
    }
}
