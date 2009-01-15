package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.testng.annotations.Test;

@Test
public class SMPPIOTest {

    public void testWriteByte() throws Exception {
        testWriteByte(0, allZero(1));
        testWriteByte(127, new byte[] {0x7f});
        testWriteByte(255, allFs(1));
    }
    
    public void testWriteShort() throws Exception {
        testWriteShort(0, allZero(2));
        testWriteShort(32768, new byte[] { (byte) 0x80, 0});
        testWriteShort(65535, allFs(2));
    }
    
    public void testWriteInt() throws Exception {
        testWriteInt(0, allZero(4));
        testWriteInt(20401123, new byte[] {0x1, 0x37, 0x4b, (byte) 0xe3});
        byte[] expected = allFs(4);
        expected[0] = 0x7f;
        testWriteInt(Integer.MAX_VALUE, expected);
    }

    public void testWriteLongInt() throws Exception {
        testWriteLongInt(0, allZero(4));
        testWriteLongInt(20401123, new byte[] {0x1, 0x37, 0x4b, (byte) 0xe3});
        testWriteLongInt(4294967295L, allFs(4));
    }
    
    public void testWriteLong() throws Exception {
        testWriteLong(0L, allZero(8));
        byte[] expected = allFs(8);
        expected[0] = 0x7f;
        testWriteLong(Long.MAX_VALUE, expected);
    }
    
    public void testBytesToByte() {
        testBytesToByte(allZero(1), 0);
        testBytesToByte(new byte[] {0x7f}, 127);
        testBytesToByte(allFs(2), 255);
    }
    
    public void testBytesToShort() {
        testBytesToShort(allZero(2), 0);
        testBytesToShort(new byte[] {(byte) 0xb2, (byte) 0x99}, 45721);
        testBytesToShort(allFs(2), 65535);
    }
    
    public void testBytesToInt() {
        testBytesToInt(allZero(4), 0);
        testBytesToInt(new byte[] {0x78, (byte) 0x88, (byte) 0x88, (byte) 0x88},
                2022213768);
        byte[] allF = allFs(4);
        allF[0] = 0x7f;
        testBytesToInt(allF, Integer.MAX_VALUE);
    }

    public void testBytesToLongInt() {
        testBytesToLongInt(allZero(4), 0L);
        testBytesToLongInt(
                new byte[] {0x78, (byte) 0x88, (byte) 0x88, (byte) 0x88},
                2022213768L);
        testBytesToLongInt(allFs(4), 4294967295L);
    }

    public void testBytesToLong() {
        testBytesToLong(allZero(8), 0L);
        byte[] allF = allFs(8);
        allF[0] = 0x7f;
        testBytesToLong(allF, Long.MAX_VALUE);
    }
    
    private void testWriteByte(int value, byte[] expected) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMPPIO.writeByte(value, out);
        byte[] array = out.toByteArray();
        compareArrays(expected, array);
    }
    
    private void testWriteShort(int value, byte[] expected) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMPPIO.writeShort(value, out);
        byte[] array = out.toByteArray();
        compareArrays(expected, array);
    }
    
    private void testWriteInt(int value, byte[] expected) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMPPIO.writeInt(value, out);
        byte[] array = out.toByteArray();
        compareArrays(expected, array);
    }
    
    private void testWriteLongInt(long value, byte[] expected) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMPPIO.writeLongInt(value, out);
        byte[] array = out.toByteArray();
        compareArrays(expected, array);
    }
    
    private void testWriteLong(long value, byte[] expected) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SMPPIO.writeLong(value, out);
        byte[] array = out.toByteArray();
        compareArrays(expected, array);
    }

    private void testBytesToByte(byte[] array, int expected) {
        int actual = SMPPIO.readUInt1(array, 0);
        assertEquals(actual, expected);
    }
    
    private void testBytesToShort(byte[] array, int expected) {
        int actual = SMPPIO.readUInt2(array, 0);
        assertEquals(actual, expected);
    }
    
    private void testBytesToInt(byte[] array, int expected) {
        int actual = SMPPIO.readInt4(array, 0);
        assertEquals(actual, expected);
    }
    
    private void testBytesToLongInt(byte[] array, long expected) {
        long actual = SMPPIO.readUInt4(array, 0);
        assertEquals(actual, expected);
    }
    
    private void testBytesToLong(byte[] array, long expected) {
        long actual = SMPPIO.readInt8(array, 0);
        assertEquals(actual, expected);
    }
    
    private void compareArrays(byte[] expected, byte[] actual) {
        assertEquals(actual.length, expected.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(actual[i], expected[i]);
        }
    }

    private byte[] allZero(int size) {
        byte[] array = new byte[size];
        Arrays.fill(array, (byte) 0);
        return array;
    }
    
    private byte[] allFs(int size) {
        byte[] array = new byte[size];
        Arrays.fill(array, (byte) 0xff);
        return array;
    }
}
