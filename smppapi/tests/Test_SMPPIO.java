
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import ie.omk.smpp.util.SMPPIO;

public class Test_SMPPIO
{
    static byte[][] oneBytes = {
	{ (byte)0x4c },
	{ (byte)0xb6 }
    };

    static byte[][] twoBytes = {
	{ (byte)0x34, (byte)0x34 },
	{ (byte)0x63, (byte)0xe1 },
	{ (byte)0xd5, (byte)0x29 },
	{ (byte)0xcc, (byte)0xa8 }
    };

    static byte[][] threeBytes = {
	{ (byte)0x34, (byte)0x34, (byte)0x34 },
	{ (byte)0x63, (byte)0xe1, (byte)0x81 },
	{ (byte)0x29, (byte)0xd5, (byte)0x11 },
	{ (byte)0x29, (byte)0xa8, (byte)0xb8 },
	{ (byte)0xcc, (byte)0x12, (byte)0x00 },
	{ (byte)0xcc, (byte)0x44, (byte)0xa8 },
	{ (byte)0xcc, (byte)0xa8, (byte)0x02 },
	{ (byte)0xcc, (byte)0xa8, (byte)0xff }
    };

    static byte[][] fourBytes = {
	{ (byte)0x23, (byte)0x23, (byte)0x24, (byte)0x24 },
	{ (byte)0xef, (byte)0x00, (byte)0x0, (byte)0x0 },
	{ (byte)0xde, (byte)0xad, (byte)0xbe, (byte)0xef }
    };

    public static void main(String[] args)
    {
	try {
	    int len = 1;
	    System.out.println("One byte conversion:");
	    for (int i = 0; i < oneBytes.length; i++) {
		int x = SMPPIO.bytesToInt(oneBytes[i], 0, len);
		System.out.print(Integer.toHexString(x) + ", ");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		SMPPIO.writeInt(x, len, bout);
		System.out.println("toBytes: " + TestUtils.showBytes(bout.toByteArray()));
	    }
	    ++len;

	    System.out.println("\nTwo byte conversion:");
	    for (int i = 0; i < twoBytes.length; i++) {
		int x = SMPPIO.bytesToInt(twoBytes[i], 0, len);
		System.out.print(Integer.toHexString(x) + ", ");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		SMPPIO.writeInt(x, len, bout);
		System.out.println("toBytes: " + TestUtils.showBytes(bout.toByteArray()));
	    }
	    ++len;

	    System.out.println("\nThree byte conversion:");
	    for (int i = 0; i < threeBytes.length; i++) {
		int x = SMPPIO.bytesToInt(threeBytes[i], 0, len);
		System.out.print(Integer.toHexString(x) + ", ");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		SMPPIO.writeInt(x, len, bout);
		System.out.println("toBytes: " + TestUtils.showBytes(bout.toByteArray()));
	    }
	    ++len;

	    System.out.println("\nFour byte conversion:");
	    for (int i = 0; i < fourBytes.length; i++) {
		int x = SMPPIO.bytesToInt(fourBytes[i], 0, len);
		System.out.print(Integer.toHexString(x) + ", ");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		SMPPIO.writeInt(x, len, bout);
		System.out.println("toBytes: " + TestUtils.showBytes(bout.toByteArray()));
	    }
	    ++len;
	} catch (Exception x) {
	    x.printStackTrace(System.err);
	}
    }

}
