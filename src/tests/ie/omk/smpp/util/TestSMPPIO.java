package ie.omk.smpp.util;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Test the SMPPIO functions.
 * 
 * @version $Id$
 */
public class TestSMPPIO extends TestCase {

    private final long[] lowLongs = {
            0x11L,
            0x2211L,
            0x332211L,
            0x44332211L,
            0x5544332211L,
            0x665544332211L,
            0x77665544332211L,
            0x8877665544332211L,
    };

    private final long[] highLongs = {
            0xe1L,
            0xe2e1L,
            0xe3e2e1L,
            0xe4e3e2e1L,
            0xe5e4e3e2e1L,
            0xe6e5e4e3e2e1L,
            0xe7e6e5e4e3e2e1L,
            0xe8e7e6e5e4e3e2e1L,
    };

    private final int[] lowInts = {
            0x11,
            0x2211,
            0x332211,
            0x44332211,
    };
    private final int[] highInts = {
            0xe1,
            0xe2e1,
            0xe3e2e1,
            0xe4e3e2e1,
    };

    private final byte[][] lowBytes = {
            {0x11}, {0x22, 0x11}, {0x33, 0x22, 0x11},
            {0x44, 0x33, 0x22, 0x11}, {0x55, 0x44, 0x33, 0x22, 0x11},
            {0x66, 0x55, 0x44, 0x33, 0x22, 0x11},
            {0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11},
            {(byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11},
    };

    private final byte[][] highBytes = {
            {(byte) 0xe1},
            {(byte) 0xe2, (byte) 0xe1},
            {(byte) 0xe3, (byte) 0xe2, (byte) 0xe1},
            {(byte) 0xe4, (byte) 0xe3, (byte) 0xe2, (byte) 0xe1},
            {(byte) 0xe5, (byte) 0xe4, (byte) 0xe3, (byte) 0xe2, (byte) 0xe1},
            {(byte) 0xe6, (byte) 0xe5, (byte) 0xe4, (byte) 0xe3, (byte) 0xe2, (byte) 0xe1},
            {(byte) 0xe7, (byte) 0xe6, (byte) 0xe5, (byte) 0xe4, (byte) 0xe3, (byte) 0xe2, (byte) 0xe1},
            {(byte) 0xe8, (byte) 0xe7, (byte) 0xe6, (byte) 0xe5, (byte) 0xe4, (byte) 0xe3, (byte) 0xe2, (byte) 0xe1},
    };

    public TestSMPPIO(String n) {
        super(n);
    }

    public void testIntToBytes() {
        byte[] b;
        for (int i = 0; i < lowInts.length; i++) {
            b = new byte[i + 1];
            SMPPIO.intToBytes(lowInts[i], i + 1, b, 0);
            assertTrue("intToBytes, lowInts " + i,
                    Arrays.equals(lowBytes[i], b));
        }
        for (int i = 0; i < highInts.length; i++) {
            b = new byte[i + 1];
            SMPPIO.intToBytes(highInts[i], i + 1, b, 0);
            assertTrue("intToBytes, highInts " + i,
                    Arrays.equals(highBytes[i], b));
        }
    }

    public void testBytesToInt() {
        for (int i = 0; i < lowInts.length; i++) {
            assertEquals("bytesToInt, lowInts " + i,
                    lowInts[i], SMPPIO.bytesToInt(lowBytes[i], 0, i + 1));
        }
        for (int i = 0; i < highInts.length; i++) {
            assertEquals("bytesToInt, highInts " + i,
                    highInts[i], SMPPIO.bytesToInt(highBytes[i], 0, i + 1));
        }
    }

    public void testLongToBytes() {
        byte[] b;
        for (int i = 0; i < lowLongs.length; i++) {
            b = new byte[i + 1];
            SMPPIO.longToBytes(lowLongs[i], i + 1, b, 0);
            assertTrue("longToBytes, lowLongs " + i,
                    Arrays.equals(lowBytes[i], b));
        }
        for (int i = 0; i < highLongs.length; i++) {
            b = new byte[i + 1];
            SMPPIO.longToBytes(highLongs[i], i + 1, b, 0);
            assertTrue("longToBytes, highLongs " + i,
                    Arrays.equals(highBytes[i], b));
        }
    }

    public void testBytesToLong() {
        for (int i = 0; i < lowLongs.length; i++) {
            assertEquals("bytesToLong, lowLongs " + i,
                    lowLongs[i], SMPPIO.bytesToLong(lowBytes[i], 0, i + 1));
        }
        for (int i = 0; i < highLongs.length; i++) {
            assertEquals("bytesToLong, lowLongs " + i,
                    highLongs[i], SMPPIO.bytesToLong(highBytes[i], 0, i + 1));
        }
    }
}
