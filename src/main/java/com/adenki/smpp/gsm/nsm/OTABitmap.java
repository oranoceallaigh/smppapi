package com.adenki.smpp.gsm.nsm;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import com.adenki.smpp.util.ImageUtils;

/**
 * Class implementing the OTA Bitmap from the Nokia Smart
 * Messaging specification. Only supports black-and-white images.
 * @version $Id$
 */
public class OTABitmap {

    private byte[] imageData;
    private boolean compression;
    private boolean palette;
    private boolean largeImage;
    private int numImages;
    
    public OTABitmap(byte[] imageData, int width, int height) {
        this.imageData = imageData;
        if (width > 255 || height > 255) {
            largeImage = true;
        }
    }
    
    public OTABitmap(BufferedImage image) {
        this.imageData = createData(image);
        if (image.getWidth() > 255 || image.getHeight() > 255) {
            largeImage = true;
        }
    }

    public byte[] getData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public boolean isCompression() {
        return compression;
    }

    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    public boolean isPalette() {
        return palette;
    }

    public void setPalette(boolean palette) {
        this.palette = palette;
    }

    public boolean isLargeImage() {
        return largeImage;
    }

    public void setLargeImage(boolean largeImage) {
        this.largeImage = largeImage;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numIcons) {
        this.numImages = numIcons;
    }

    private int getInfoField() {
        int info = numImages & 0x4;
        if (largeImage) {
            info &= 0x10;
        }
        if (palette) {
            info &= 0x20;
        }
        if (compression) {
            info &= 0x40;
        }
        return info;
    }
    
    private byte[] createData(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int bytesWidth = (width / 8) + 1;
        if (width % 8 == 0) {
            bytesWidth--;
        }
        int headerSize = 4;
        if (largeImage) {
            headerSize += 2;
        }
        ByteBuffer buffer = ByteBuffer.allocate((height * bytesWidth) + headerSize);
        buffer.put((byte) getInfoField());
        if (largeImage) {
            buffer.putShort((short) width);
            buffer.putShort((short) height);
        } else {
            buffer.put((byte) width);
            buffer.put((byte) height);
        }
        // Depth..number of colours.
        buffer.put((byte) 1);
        ImageUtils.imageToBwBitmap(image, buffer);
        assert buffer.remaining() == 0;
        return buffer.array();
    }
}
