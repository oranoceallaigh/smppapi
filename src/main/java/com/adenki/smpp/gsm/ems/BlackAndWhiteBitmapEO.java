package com.adenki.smpp.gsm.ems;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.adenki.smpp.util.ImageUtils;

/**
 * A black and white bitmap, as defined by
 * 3GPP TS 23.040 Annex E.
 * @version $Id$
 */
public class BlackAndWhiteBitmapEO extends ExtendedObject {

    public BlackAndWhiteBitmapEO(int referenceNum, BufferedImage image) {
        super(2, referenceNum);
        setData(createData(image));
    }
    
    private byte[] createData(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int bytesWidth = (width / 8) + 1;
        if (width % 8 == 0) {
            bytesWidth--;
        }
        ByteBuffer buffer = ByteBuffer.allocate((height * bytesWidth) + 2);
        buffer.put((byte) width);
        buffer.put((byte) height);
        ImageUtils.imageToBwBitmap(image, buffer);
        assert buffer.remaining() == 0;
        return buffer.array();
    }
}
