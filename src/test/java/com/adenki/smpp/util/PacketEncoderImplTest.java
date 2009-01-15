package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;

@Test
public class PacketEncoderImplTest {

    public void testWriteCStringWritesNulByte() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeCString(null);
        assertEquals(out.toByteArray()[0], (byte) 0);
    }
    
    public void testWriteCStringWritesAsciiAndNul() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeCString("ABC");
        byte[] array = out.toByteArray();
        assertEquals(array.length, 4);
        assertEquals(array[0], (byte) 0x41);
        assertEquals(array[1], (byte) 0x42);
        assertEquals(array[2], (byte) 0x43);
        assertEquals(array[3], (byte) 0);
    }
    
    public void testWriteStringWritesNothingOnEmptyString() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeString("", 0);
        assertEquals(out.toByteArray().length, 0);
    }
    
    public void testWriteStringWritesAscii() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeString("CD!", 3);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 3);
        assertEquals(array[0], (byte) 0x43);
        assertEquals(array[1], (byte) 0x44);
        assertEquals(array[2], (byte) 0x21);
    }

    public void testWriteStringWritesSubstring() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeString("EFGHIJ", 4);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 4);
        assertEquals(array[0], (byte) 0x45);
        assertEquals(array[1], (byte) 0x46);
        assertEquals(array[2], (byte) 0x47);
        assertEquals(array[3], (byte) 0x48);
    }
    
    public void testWriteStringExceptionsWhenLengthIsInvalid() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PacketEncoderImpl encoder = new PacketEncoderImpl(out);
            encoder.writeString("Three", 6);
            fail("should have thrown StringIndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException x) {
            // success
        }
    }
    
    public void testWriteUInt1Succeeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeUInt1(34);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 1);
        assertEquals(array[0], (byte) 34);
    }
    
    public void testWriteUInt2Succeeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeUInt2(0xfabe);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 2);
        assertEquals(array[0], (byte) 0xfa);
        assertEquals(array[1], (byte) 0xbe);
    }

    public void testWriteUInt4Succeeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeUInt4(0xffeffabeL);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 4);
        assertEquals(array[0], (byte) 0xff);
        assertEquals(array[1], (byte) 0xef);
        assertEquals(array[2], (byte) 0xfa);
        assertEquals(array[3], (byte) 0xbe);
    }
    
    public void testWriteInt4Succeeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeInt4((int) 0xddccbbaa);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 4);
        assertEquals(array[0], (byte) 0xdd);
        assertEquals(array[1], (byte) 0xcc);
        assertEquals(array[2], (byte) 0xbb);
        assertEquals(array[3], (byte) 0xaa);
    }
    
    public void testWriteInt8Succeeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeInt8(0xeeddccbbaa998877L);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 8);
        assertEquals(array[0], (byte) 0xee);
        assertEquals(array[1], (byte) 0xdd);
        assertEquals(array[2], (byte) 0xcc);
        assertEquals(array[3], (byte) 0xbb);
        assertEquals(array[4], (byte) 0xaa);
        assertEquals(array[5], (byte) 0x99);
        assertEquals(array[6], (byte) 0x88);
        assertEquals(array[7], (byte) 0x77);
    }
    
    public void testWriteAddressSucceeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        Address address = new Address(2, 3, "123");
        encoder.writeAddress(address);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 6);
        assertEquals(array[0], (byte) 2);
        assertEquals(array[1], (byte) 3);
        assertEquals(array[2], (byte) 0x31);
        assertEquals(array[3], (byte) 0x32);
        assertEquals(array[4], (byte) 0x33);
        assertEquals(array[5], (byte) 0);
    }

    public void testWriteNullAddressSucceeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeAddress(null);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 3);
        assertEquals(array[0], (byte) 0);
        assertEquals(array[1], (byte) 0);
        assertEquals(array[2], (byte) 0);
    }
    
    public void testWriteDateSucceeds() throws Exception {
        Calendar calendar = Calendar.getInstance();
        SMPPDate date = SMPPDate.getAbsoluteInstance(calendar);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeDate(date);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 17);
        // Not worried about the actual characters encoded - date tests
        // will cover that.
        assertEquals(array[16], (byte) 0);
    }
    
    public void testWriteNullDateSucceeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeDate(null);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 1);
        assertEquals(array[0], (byte) 0);
    }
    
    public void testWriteByteArraySucceeds() throws Exception {
        byte[] bytes = new byte[] { 0, 1, 2, 3, (byte) 0xef, (byte) 0x9a };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeBytes(bytes, 0, bytes.length);
        byte[] array = out.toByteArray();
        assertEquals(array.length, bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            assertEquals(array[i], bytes[i]);
        }
    }
    
    public void testWriteByteSubArraySucceeds() throws Exception {
        byte[] bytes = new byte[] { 0, 1, 2, 3, (byte) 0xef, (byte) 0x9a };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeBytes(bytes, 3, 3);
        byte[] array = out.toByteArray();
        assertEquals(array.length, 3);
        for (int i = 0; i < 3; i++) {
            assertEquals(array[i], bytes[3 + i]);
        }
    }

    public void testWriteZeroLengthByteArraySucceeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeBytes(new byte[0], 0, 0);
        assertEquals(out.toByteArray().length, 0);
    }
    
    public void testWriteNullByteArraySucceeds() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PacketEncoderImpl encoder = new PacketEncoderImpl(out);
        encoder.writeBytes(null, 0, 0);
        assertEquals(out.toByteArray().length, 0);
    }
    
    public void testWriteBytesExceptionsWhenNotEnoughBytes() throws Exception {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PacketEncoderImpl encoder = new PacketEncoderImpl(out);
            encoder.writeBytes(new byte[] { 1, 2, 3 }, 1, 3);
            fail("should have thrown ArrayIndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException x) {
            // success
        }
    }
}
