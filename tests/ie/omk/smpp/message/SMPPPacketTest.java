/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 * $Id$
 */
package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.InvalidParameterValueException;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.AlphabetEncoding;
import ie.omk.smpp.util.BinaryEncoding;
import ie.omk.smpp.util.DefaultAlphabetEncoding;
import ie.omk.smpp.util.Latin1Encoding;
import ie.omk.smpp.util.MessageEncoding;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;


public class SMPPPacketTest extends TestCase {

    private PrivateEncoding myEnc = new PrivateEncoding();

    private PrivateVersion myVer = new PrivateVersion();


    public SMPPPacketTest(String n) {
	super (n);
    }

    /** Test that setting a value and then getting that value are the same.
     */
    public void testSetters() {
	try {
	    SMPPPacket p = new GenericNack();

	    // Set the version first..this version class doesn't fail anything
	    // for fields too long or invalid values or anything.
	    p.setVersion(myVer);
	    assertEquals(myVer, p.getVersion());

	    p.setAlphabet(myEnc);
	    assertEquals(myEnc, p.getMessageEncoding());

	    // XXX add test for setMessageEncoding

	    p.setDataCoding(0x9b);
	    assertEquals(0x9b, p.getDataCoding());

	    p.setDefaultMsg(20);
	    assertEquals(20, p.getDefaultMsg());

	    SMPPDate smppDate = new SMPPDate();
	    p.setDeliveryTime(smppDate);
	    assertEquals(smppDate, p.getDeliveryTime());

	    Address dAddr = new Address(1, 2, "12345678");
	    p.setDestination(dAddr);
	    assertEquals(dAddr, p.getDestination());

	    p.setErrorCode(78);
	    assertEquals(78, p.getErrorCode());

	    p.setEsmClass(3);
	    assertEquals(3, p.getEsmClass());

	    smppDate = new SMPPDate();
	    p.setExpiryTime(smppDate);
	    assertEquals(smppDate, p.getExpiryTime());

	    smppDate = new SMPPDate();
	    p.setFinalDate(smppDate);
	    assertEquals(smppDate, p.getFinalDate());

	    byte[] msg = new byte[] {
		1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14
	    };
	    p.setMessage(msg);
	    assertTrue(Arrays.equals(msg, p.getMessage()));

	    msg = new byte[] {
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24
	    };
	    byte[] subMsg = new byte[] {
		16, 17, 18, 19, 20, 21
	    };
	    p.setMessage(msg, 1, 6, BinaryEncoding.getInstance());
	    assertTrue(Arrays.equals(subMsg, p.getMessage()));

	    msg = new byte[] {
		31, 32, 33, 34, 35
	    };
	    p.setMessage(msg, BinaryEncoding.getInstance());
	    assertTrue(Arrays.equals(msg, p.getMessage()));
	    
	    p.setMessageId("45678");
	    assertEquals("45678", p.getMessageId());

	    p.setMessageStatus(6);
	    assertEquals(6, p.getMessageStatus());

	    String text = "Text i\u00f1t\u00e8rn\u00e4ti\u00f6nal";
	    p.setMessageText(text);
	    assertEquals(text, p.getMessageText());

	    p.setAlphabet(Latin1Encoding.getInstance());
	    p.setMessageText(text, Latin1Encoding.getInstance());
	    assertEquals(text, p.getMessageText());

	    p.setPriority(2);
	    assertEquals(2, p.getPriority());

	    p.setProtocolID(13);
	    assertEquals(13, p.getProtocolID());

	    p.setRegistered(4);
	    assertEquals(4, p.getRegistered());

	    p.setReplaceIfPresent(1);
	    assertEquals(1, p.getReplaceIfPresent());

	    p.setSequenceNum(56);
	    assertEquals(56, p.getSequenceNum());

	    p.setServiceType("stype");
	    assertEquals("stype", p.getServiceType());

	    Address sAddr = new Address(3, 4, "56789");
	    p.setSource(sAddr);
	    assertEquals(sAddr, p.getSource());

	    TLVTable tlv = new TLVTable();
	    p.setTLVTable(tlv);
	    assertTrue(tlv == p.getTLVTable());

	} catch (InvalidParameterValueException x) {
	    x.printStackTrace(System.err);
	    fail("InvalidParameterValueException");
	}
    }


    /** Test the decoding of a packet. Test the decoding of a packet that
     * consists of a header, mandatory body parameters and optional parameters.
     */
    public void testFullDecode() {
	try {
	    SubmitSM sm = new SubmitSM();
	    sm.setSequenceNum(70);
	    sm.setSource(new Address(0, 0, "12345678"));
	    sm.setDestination(new Address(0, 0, "87654321"));
	    sm.setProtocolID(7);
	    sm.setMessageText("Test message text");
	    sm.setOptionalParameter(Tag.PAYLOAD_TYPE, new Integer(0));
	    //sm.setOptionalParameter(Tag.PRIVACY_INDICATOR, new Integer(2));
	    //sm.setOptionalParameter(Tag.ALERT_ON_MESSAGE_DELIVERY, null);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    sm.writeTo(out);

	    SubmitSM sm1 = new SubmitSM();
	    byte[] b1 = out.toByteArray();
	    sm1.readFrom(b1, 0);

	    out.reset();
	    sm1.writeTo(out);

	    byte[] b2 = out.toByteArray();

	    assertTrue(Arrays.equals(b1, b2));
	} catch (Exception x) {
	    x.printStackTrace(System.err);
	    fail("Failing due to exception.");
	}
    }
    
    public void testDefaultEncSetMessage() {
        try {
            SubmitSM sm = new SubmitSM();
            sm.setVersion(SMPPVersion.V33);
            StringBuffer text160 = new StringBuffer(160);

            for (int i = 0; i < 16; i++) {
                    text160.append("0123456789");
            }

            sm.setMessageText(text160.toString(), DefaultAlphabetEncoding.getInstance());

            sm = new SubmitSM();
            sm.setVersion(SMPPVersion.V34);
            sm.setMessageText(text160.toString(), DefaultAlphabetEncoding.getInstance());
        } catch (InvalidParameterValueException x) {
            fail("Message of length 160 was rejected with DefaultAlphabetEncoding");
        }
    }


    public void testDefaultEncTooLong() {

        SubmitSM sm = new SubmitSM();
        StringBuffer textLong = new StringBuffer(300);

        try {
            sm.setVersion(SMPPVersion.V33);

            for (int i = 0; i < 16; i++) {
                textLong.append("0123456789");
            }

            textLong.append("Message is now too long");

            sm.setMessageText(textLong.toString(), DefaultAlphabetEncoding.getInstance());
            fail("Message was too long [" + textLong.length() + "], but accepted by SMPPPacket");
        } catch (InvalidParameterValueException x) {
        }

        try {
            // 3.4 allows up to 254 bytes for the message payload
            sm = new SubmitSM();
            sm.setVersion(SMPPVersion.V34);
            textLong = new StringBuffer();

            for (int i = 0; i < 30; i++) {
                textLong.append("0123456789");
            }
            sm.setMessageText(textLong.toString(), DefaultAlphabetEncoding.getInstance());
            fail("Message was too long [" + textLong.length() + "], but accepted by SMPPPacket");
        } catch (InvalidParameterValueException x) {
        }
    }

    private class PrivateEncoding extends AlphabetEncoding {
	public PrivateEncoding() {
	    super (0x9a);
	}

	public String decodeString(byte[] b) {
	    return (new String(b));
	}

	public byte[] encodeString(String s) {
	    return (s.getBytes());
	}
    }

    private class PrivateVersion extends SMPPVersion {

	public PrivateVersion() {
	    super (0x9a, "TestVersion");
	}

	public int getMaxLength(int field) {
	    return (1000);
	}

	public boolean isSupported(int commandID) {
	    return (true);
	}

    public boolean isSupportOptionalParams() {
        return (true);
    }
    
	public boolean validateAddress(Address s) {
	    return (true);
	}

	public boolean validateEsmClass(int c) {
	    return (true);
	}

	public boolean validateProtocolID(int id) {
	    return (true);
	}

	public boolean validateDataCoding(int dc) {
	    return (true);
	}

	public boolean validateDefaultMsg(int id) {
	    return (true);
	}

	public boolean validateMessageText(String text, AlphabetEncoding alphabet) {
	    return (true);
	}

	public boolean validateMessage(byte[] message, MessageEncoding encoding) {
	    return (true);
	}

	public boolean validateServiceType(String type) {
	    return (true);
	}

	public boolean validateMessageId(String id) {
	    return (true);
	}

	public boolean validateMessageState(int state) {
	    return (true);
	}

	public boolean validateErrorCode(int code) {
	    return (true);
	}

	public boolean validatePriorityFlag(int flag) {
	    return (true);
	}

	public boolean validateRegisteredDelivery(int flag) {
	    return (true);
	}

	public boolean validateReplaceIfPresent(int flag) {
	    return (true);
	}

	public boolean validateNumberOfDests(int num) {
	    return (true);
	}

	public boolean validateNumUnsuccessful(int num) {
	    return (true);
	}

	public boolean validateDistListName(String name) {
	    return (true);
	}

	public boolean validateSystemId(String sysId) {
	    return (true);
	}

	public boolean validatePassword(String password) {
	    return (true);
	}

	public boolean validateSystemType(String sysType) {
	    return (true);
	}

	public boolean validateAddressRange(String addressRange) {
	    return (true);
	}

	public boolean validateParamName(String paramName) {
	    return (true);
	}

	public boolean validateParamValue(String paramValue) {
	    return (true);
	}
    }
}
