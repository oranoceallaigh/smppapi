package com.adenki.smpp.gsm.ems;

import java.awt.image.BufferedImage;

/**
 * Implementation of a variable picture. See 3GPP TS 23.040 9.2.3.24.10.1.7.
 * @version $Id$
 */
public class LargePicture extends Picture {

    public LargePicture(BufferedImage image, int position) {
        super(0x10, image, position);
    }

    public LargePicture(BufferedImage image) {
        super(0x10, image);
    }

    @Override
    protected void checkImage(BufferedImage image) {
        if (image.getWidth() != 32 || image.getHeight() != 32) {
            throw new IllegalArgumentException("Image must be exactly 32x32");
        }
    }
}
