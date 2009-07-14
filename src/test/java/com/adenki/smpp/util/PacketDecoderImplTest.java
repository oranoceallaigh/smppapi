package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.EOFException;
import java.nio.ByteBuffer;

import org.testng.annotations.Test;

import com.adenki.smpp.Address;
import com.adenki.smpp.ErrorAddress;

@Test
public class PacketDecoderImplTest {

    @Test(expectedExceptions = {EOFException.class})
    public void testReadUInt1ThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.flip();
        new PacketDecoderImpl(buffer).readUInt1();
    }
    
    public void testReadUInt1RetrievesCorrectNumberInsideSignedByteRange() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x6e);
        buffer.flip();
        assertEquals(new PacketDecoderImpl(buffer).readUInt1(), 0x6e);
    }

    public void testReadUInt1RetrievesCorrectNumberOutsideSignedByteRange() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0xf4);
        buffer.flip();
        assertEquals(new PacketDecoderImpl(buffer).readUInt1(), 0xf4);
    }

    @Test(expectedExceptions = {EOFException.class})
    public void testReadUInt2ThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x1);
        buffer.flip();
        new PacketDecoderImpl(buffer).readUInt2();
    }
    
    public void testReadUInt2RetrievesCorrectNumberInsideSignedShortRange() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x13);
        buffer.put((byte) 0xff);
        buffer.flip();
        assertEquals(new PacketDecoderImpl(buffer).readUInt2(), 0x13ff);
    }

    public void testReadUInt2RetrievesCorrectNumberOutsideSignedShortRange() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0xfe);
        buffer.put((byte) 0x11);
        buffer.flip();
        assertEquals(new PacketDecoderImpl(buffer).readUInt2(), 0xfe11);
    }

    @Test(expectedExceptions = {EOFException.class})
    public void testReadUInt4ThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x1);
        buffer.put((byte) 0x2);
        buffer.put((byte) 0x3);
        buffer.flip();
        new PacketDecoderImpl(buffer).readUInt4();
    }

    public void testReadUInt4RetrievesCorrectNumberInsideSignedIntRange() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x33);
        buffer.put((byte) 0xff);
        buffer.put((byte) 0xab);
        buffer.put((byte) 0xb6);
        buffer.flip();
        assertEquals(new PacketDecoderImpl(buffer).readUInt4(), 0x33ffabb6L);
    }

    public void testReadUInt4RetrievesCorrectNumberOutsideSignedIntRange() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0xe9);
        buffer.put((byte) 0x25);
        buffer.put((byte) 0xc9);
        buffer.put((byte) 0xd4);
        buffer.flip();
        assertEquals(new PacketDecoderImpl(buffer).readUInt4(), 0xe925c9d4L);
    }

    @Test(expectedExceptions = {EOFException.class})
    public void testReadInt8ThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x1);
        buffer.put((byte) 0x2);
        buffer.put((byte) 0x3);
        buffer.put((byte) 0x4);
        buffer.put((byte) 0x5);
        buffer.put((byte) 0x6);
        buffer.put((byte) 0x7);
        buffer.flip();
        new PacketDecoderImpl(buffer).readInt8();
    }
    
    public void testReadInt8RetrievesCorrectLongNumber() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x54);
        buffer.put((byte) 0x31);
        buffer.put((byte) 0x10);
        buffer.put((byte) 0xfe);
        buffer.put((byte) 0xdc);
        buffer.put((byte) 0xba);
        buffer.put((byte) 0x98);
        buffer.put((byte) 0x76);
        buffer.flip();
        assertEquals(new PacketDecoderImpl(buffer).readInt8(), 0x543110fedcba9876L);
    }

    @Test(expectedExceptions = {EOFException.class})
    public void testReadBytesThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x1);
        buffer.put((byte) 0x2);
        buffer.put((byte) 0x3);
        buffer.put((byte) 0x4);
        buffer.put((byte) 0x5);
        buffer.put((byte) 0x6);
        buffer.put((byte) 0x7);
        buffer.flip();
        new PacketDecoderImpl(buffer).readBytes(9);
    }
    
    public void testReadBytesReturnsSpecifiedNumberOfBytes() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x10);
        buffer.put((byte) 0x20);
        buffer.put((byte) 0x30);
        buffer.put((byte) 0x40);
        buffer.put((byte) 0x50);
        buffer.put((byte) 0x60);
        buffer.put((byte) 0x70);
        buffer.flip();
        byte[] bytes = new PacketDecoderImpl(buffer).readBytes(4);
        assertNotNull(bytes);
        assertEquals(bytes.length, 4);
        assertEquals(bytes[0], 0x10);
        assertEquals(bytes[1], 0x20);
        assertEquals(bytes[2], 0x30);
        assertEquals(bytes[3], 0x40);
        assertEquals(buffer.remaining(), 3);
    }

    @Test(expectedExceptions = {EOFException.class})
    public void testReadByteThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.flip();
        new PacketDecoderImpl(buffer).readByte();
    }
    
    public void testReadSingleByte() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x10);
        buffer.put((byte) 0x20);
        buffer.flip();
        byte aByte = new PacketDecoderImpl(buffer).readByte();
        assertEquals(aByte, 0x10);
        assertEquals(buffer.remaining(), 1);
    }

    @Test(expectedExceptions = {EOFException.class})
    public void testReadCStringThrowsExceptionWhenNullByteNotPresent() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x41);
        buffer.put((byte) 0x42);
        buffer.put((byte) 0x43);
        buffer.flip();
        new PacketDecoderImpl(buffer).readCString();
    }
    
    public void testReadCStringReturnsParsedString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x41);
        buffer.put((byte) 0x42);
        buffer.put((byte) 0x43);
        buffer.put((byte) 0);
        buffer.flip();
        String s = new PacketDecoderImpl(buffer).readCString();
        assertEquals(s, "ABC");
        assertEquals(buffer.remaining(), 0);
    }

    @Test(expectedExceptions = {EOFException.class})
    public void testReadStringThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x41);
        buffer.put((byte) 0x42);
        buffer.put((byte) 0x43);
        buffer.put((byte) 0x44);
        buffer.put((byte) 0x45);
        buffer.flip();
        new PacketDecoderImpl(buffer).readString(6);
    }

    public void testReadStringReturnsParsedString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x41);
        buffer.put((byte) 0x42);
        buffer.put((byte) 0x43);
        buffer.put((byte) 0x44);
        buffer.put((byte) 0x45);
        buffer.flip();
        String s = new PacketDecoderImpl(buffer).readString(5);
        assertEquals(s, "ABCDE");
        assertEquals(buffer.remaining(), 0);
    }
    
    @Test(expectedExceptions = {EOFException.class})
    public void testReadAddressThrowsExceptionWhenAddressNotAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // There's no nul-terminator for the address so parsing should fail.
        buffer.put((byte) 0x41);
        buffer.put((byte) 0x42);
        buffer.put((byte) 0x43);
        buffer.flip();
        new PacketDecoderImpl(buffer).readAddress();
    }
    
    public void testReadAddressReturnsParsedAddress() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x02);
        buffer.put((byte) 0x31);
        buffer.put((byte) 0x32);
        buffer.put((byte) 0x33);
        buffer.put((byte) 0x34);
        buffer.put((byte) 0x35);
        buffer.put((byte) 0);
        buffer.flip();
        Address address = new PacketDecoderImpl(buffer).readAddress();
        assertNotNull(address);
        assertEquals(address.getTON(), 1);
        assertEquals(address.getNPI(), 2);
        assertEquals(address.getAddress(), "12345");
        assertEquals(buffer.remaining(), 0);
    }
    
    @Test(expectedExceptions = {EOFException.class})
    public void testReadErrorAddressThrowsExceptionWhenInsufficientBytesAvailable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // The following is missing a complete error code
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x02);
        buffer.put((byte) 0x31);
        buffer.put((byte) 0x32);
        buffer.put((byte) 0x33);
        buffer.put((byte) 0x34);
        buffer.put((byte) 0x35);
        buffer.put((byte) 0);
        buffer.put((byte) 0x0a);
        buffer.put((byte) 0x0b);
        buffer.put((byte) 0x0c);
        buffer.flip();
        new PacketDecoderImpl(buffer).readErrorAddress();
    }
    
    public void testReadErrorAddressReturnsParsedErrorAddress() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put((byte) 0x01);
        buffer.put((byte) 0x02);
        buffer.put((byte) 0x31);
        buffer.put((byte) 0x32);
        buffer.put((byte) 0x33);
        buffer.put((byte) 0x34);
        buffer.put((byte) 0x35);
        buffer.put((byte) 0);
        buffer.put((byte) 0x0a);
        buffer.put((byte) 0x0b);
        buffer.put((byte) 0x0c);
        buffer.put((byte) 0x0d);
        buffer.flip();
        ErrorAddress errorAddr = new PacketDecoderImpl(buffer).readErrorAddress();
        assertNotNull(errorAddr);
        assertEquals(errorAddr.getTON(), 1);
        assertEquals(errorAddr.getNPI(), 2);
        assertEquals(errorAddr.getAddress(), "12345");
        assertEquals(errorAddr.getError(), 0xa0b0c0dL);
    }
    
    @Test(expectedExceptions = {EOFException.class})
    public void testReadDateThrowsExceptionWhenCStringIsNotParsable() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        // Missing the nul-terminator so parsing should fail.
        buffer.put("090714152133400+".getBytes("US-ASCII"));
        buffer.flip();
        new PacketDecoderImpl(buffer).readDate();
    }
    
    public void testReadDateReturnsCorrectlyParsedDate() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.put("090714152133400+".getBytes("US-ASCII"));
        buffer.put((byte) 0);
        buffer.flip();
        SMPPDate date = new PacketDecoderImpl(buffer).readDate();
        assertNotNull(date);
        assertEquals(date.getYear(), 2009);
        assertEquals(date.getMonth(), 7);
        assertEquals(date.getDay(), 14);
        assertEquals(date.getHour(), 15);
        assertEquals(date.getMinute(), 21);
        assertEquals(date.getSecond(), 33);
        assertEquals(date.getTenth(), 4);
        assertEquals(date.getUtcOffset(), 0);
        assertEquals(date.getSign(), '+');
    }
}
