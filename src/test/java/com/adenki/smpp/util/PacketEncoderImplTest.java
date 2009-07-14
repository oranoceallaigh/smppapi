package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;

@Test
public class PacketEncoderImplTest {
    public void testWriteUInt1OutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeUInt1(0xf9);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0xf9);
    }

    public void testWriteUInt2OutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeUInt2(0xe592);
        buffer.flip();
        assertEquals(buffer.remaining(), 2);
        assertEquals(buffer.get(), (byte) 0xe5);
        assertEquals(buffer.get(), (byte) 0x92);
    }

    public void testWriteUInt4OutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeUInt4(0xddefe592L);
        buffer.flip();
        assertEquals(buffer.remaining(), 4);
        assertEquals(buffer.get(), (byte) 0xdd);
        assertEquals(buffer.get(), (byte) 0xef);
        assertEquals(buffer.get(), (byte) 0xe5);
        assertEquals(buffer.get(), (byte) 0x92);
    }

    public void testWriteInt8OutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeInt8(0x6821beefddefe592L);
        buffer.flip();
        assertEquals(buffer.remaining(), 8);
        assertEquals(buffer.get(), (byte) 0x68);
        assertEquals(buffer.get(), (byte) 0x21);
        assertEquals(buffer.get(), (byte) 0xbe);
        assertEquals(buffer.get(), (byte) 0xef);
        assertEquals(buffer.get(), (byte) 0xdd);
        assertEquals(buffer.get(), (byte) 0xef);
        assertEquals(buffer.get(), (byte) 0xe5);
        assertEquals(buffer.get(), (byte) 0x92);
    }

    public void testWriteCStringOutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeCString("ABCDE");
        buffer.flip();
        assertEquals(buffer.remaining(), 6);
        assertEquals(buffer.get(), (byte) 0x41);
        assertEquals(buffer.get(), (byte) 0x42);
        assertEquals(buffer.get(), (byte) 0x43);
        assertEquals(buffer.get(), (byte) 0x44);
        assertEquals(buffer.get(), (byte) 0x45);
        assertEquals(buffer.get(), (byte) 0);
    }
    
    public void testWriteNullCStringOutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeCString(null);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0);
    }

    public void testWriteStringOutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeString("ABCDEFGH", 6);
        buffer.flip();
        assertEquals(buffer.remaining(), 6);
        assertEquals(buffer.get(), (byte) 0x41);
        assertEquals(buffer.get(), (byte) 0x42);
        assertEquals(buffer.get(), (byte) 0x43);
        assertEquals(buffer.get(), (byte) 0x44);
        assertEquals(buffer.get(), (byte) 0x45);
        assertEquals(buffer.get(), (byte) 0x46);
    }
    
    public void testWriteNullStringWithZeroLengthSpecifiedWorks() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeString(null, 0);
        buffer.flip();
        assertEquals(buffer.remaining(), 0);
    }

    @Test(expectedExceptions = {IndexOutOfBoundsException.class})
    public void testWriteNullStringWithNonZeroLengthSpecifiedThrowsException() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeString(null, 4);
    }
    
    @Test(expectedExceptions = {IndexOutOfBoundsException.class})
    public void testWriteStringWithLengthArgumentGreaterThanStringLengthThrowsException() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        new PacketEncoderImpl(buffer).writeString("ABCDEFGH", 9);
    }
    
    public void testWriteDateOutputsCorrectBytes() throws Exception {
        TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");
        Calendar cal = new GregorianCalendar(tz);
        cal.set(Calendar.YEAR, 2009);
        cal.set(Calendar.MONTH, 6);
        cal.set(Calendar.DAY_OF_MONTH, 14);
        cal.set(Calendar.HOUR_OF_DAY, 16);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.SECOND, 19);
        cal.set(Calendar.MILLISECOND, 400);
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeDate(new AbsoluteSMPPDate(cal));
        buffer.flip();
        assertEquals(buffer.remaining(), 17);
        byte[] bytes = new byte[16];
        buffer.get(bytes);
        if (tz.inDaylightTime(new Date())) {
            assertEquals(new String(bytes, "US-ASCII"), "090714160119408+");
        } else {
            assertEquals(new String(bytes, "US-ASCII"), "090714160119404+");
        }
    }
    
    public void testWriteNullDateOutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeDate(null);
        buffer.flip();
        assertEquals(buffer.remaining(), 1);
        assertEquals(buffer.get(), (byte) 0);
    }
    
    public void testWriteAddressOutputsCorrectBytes() throws Exception {
        Address address = new Address(4, 9, "1234567");
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeAddress(address);
        buffer.flip();
        assertEquals(buffer.remaining(), 10);
        assertEquals(buffer.get(), (byte) 0x04);
        assertEquals(buffer.get(), (byte) 0x09);
        assertEquals(buffer.get(), (byte) 0x31);
        assertEquals(buffer.get(), (byte) 0x32);
        assertEquals(buffer.get(), (byte) 0x33);
        assertEquals(buffer.get(), (byte) 0x34);
        assertEquals(buffer.get(), (byte) 0x35);
        assertEquals(buffer.get(), (byte) 0x36);
        assertEquals(buffer.get(), (byte) 0x37);
        assertEquals(buffer.get(), (byte) 0);
    }
    
    public void testWriteNullAddressOutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeAddress(null);
        buffer.flip();
        assertEquals(buffer.remaining(), 3);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
    }
    
    public void testWriteErrorAddressOutputsCorrectBytes() throws Exception {
        ErrorAddress address = new ErrorAddress(4, 9, "1234567");
        address.setError(0x8723);
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeAddress(address);
        buffer.flip();
        assertEquals(buffer.remaining(), 14);
        assertEquals(buffer.get(), (byte) 0x04);
        assertEquals(buffer.get(), (byte) 0x09);
        assertEquals(buffer.get(), (byte) 0x31);
        assertEquals(buffer.get(), (byte) 0x32);
        assertEquals(buffer.get(), (byte) 0x33);
        assertEquals(buffer.get(), (byte) 0x34);
        assertEquals(buffer.get(), (byte) 0x35);
        assertEquals(buffer.get(), (byte) 0x36);
        assertEquals(buffer.get(), (byte) 0x37);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0x87);
        assertEquals(buffer.get(), (byte) 0x23);
    }
    
    public void testWriteNullErrorAddressOutputsCorrectBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeErrorAddress(null);
        buffer.flip();
        assertEquals(buffer.remaining(), 7);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
        assertEquals(buffer.get(), (byte) 0);
    }
    
    public void testWriteFullByteArrayWritesAllBytes() throws Exception {
        byte[] bytes = new byte[] { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeBytes(bytes);
        buffer.flip();
        assertEquals(buffer.remaining(), 5);
        assertEquals(buffer.get(), (byte) 1);
        assertEquals(buffer.get(), (byte) 2);
        assertEquals(buffer.get(), (byte) 3);
        assertEquals(buffer.get(), (byte) 4);
        assertEquals(buffer.get(), (byte) 5);
    }
    
    public void testWriteNullByteArrayWorks() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeBytes(null);
        buffer.flip();
        assertEquals(buffer.remaining(), 0);
    }
    
    public void testWriteBytesWithOffsetAndLengthOnlyWritesSpecifiedBytes() throws Exception {
        byte[] bytes = new byte[] { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeBytes(bytes, 1, 3);
        buffer.flip();
        assertEquals(buffer.remaining(), 3);
        assertEquals(buffer.get(), (byte) 2);
        assertEquals(buffer.get(), (byte) 3);
        assertEquals(buffer.get(), (byte) 4);
    }
    
    @Test(expectedExceptions = {IndexOutOfBoundsException.class})
    public void testWriteByteArrayWithLengthArgumentLongerThanByteArrayLengthThrowsException() throws Exception {
        byte[] bytes = new byte[] { 1, 2, 3, 4, 5 };
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeBytes(bytes, 1, 6);
    }
    
    public void testWriteNullByteArrayWithZeroLengthSpecifiedWorks() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeBytes(null, 0, 0);
        buffer.flip();
        assertEquals(buffer.remaining(), 0);
    }

    @Test(expectedExceptions = {IndexOutOfBoundsException.class})
    public void testWriteNullByteArrayWithNonZeroLengthSpecifiedThrowsException() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        new PacketEncoderImpl(buffer).writeBytes(null, 0, 3);
    }
}
