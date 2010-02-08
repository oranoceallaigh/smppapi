package com.adenki.smpp.util;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.adenki.smpp.SMPPRuntimeException;

/**
 * Utility methods for working with images.
 * @version $Id$
 */
public final class ImageUtils {

    private ImageUtils() {
    }
    
    /**
     * Use a {@link BufferedImage} to create black and white bitmap data.
     * Bytes are output to the supplied <tt>buffer</tt> where each bit
     * represents a single pixel. 0 represents a white pixel, 1 represents
     * a black pixel. The leftmost pixel is in the most significant bit
     * of the byte..so bit 8 of byte 0 represents the pixel at (0, 0).
     * Bit 7 of byte 1 represents the pixel at (9, 0).
     * @param image The image to create bitmap data for.
     * @param buffer The buffer to output bitmap data to.
     * @throws SMPPRuntimeException If there is insufficient space in
     * the buffer to contain the bitmap data. 
     */
    public static void imageToBwBitmap(BufferedImage image, ByteBuffer buffer) {
        int width = image.getWidth();
        int height = image.getHeight();
        int bytesWidth = (width / 8) + 1;
        if (width % 8 == 0) {
            bytesWidth--;
        }
        if (buffer.remaining() < (bytesWidth * height)) {
            throw new SMPPRuntimeException(
                    "Insufficient space to for bitmap data");
        }
        int bit = 7;
        int currentByte = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int colour = image.getRGB(x, y);
                if ((colour & 0xffffff) != 0xffffff) {
                    currentByte |= 1 << bit;
                }
                bit--;
                if (bit < 0) {
                    buffer.put((byte) currentByte);
                    bit = 7;
                    currentByte = 0;
                }
            }
        }
    }
}
