/*
 * Java SMPP API Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net Java SMPP API Homepage:
 * http://smppapi.sourceforge.net/ $Id: TestConnection.java,v 1.2 2004/07/25
 * 12:08:01 orank Exp $
 */
package ie.omk.smpp;

import ie.omk.smpp.message.BindReceiverResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.net.StreamLink;
import ie.omk.smpp.util.Latin1Encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.TestCase;

public class TestConnection extends TestCase {

    private static final String TEST_MESSAGE = "Test M�ssage @�";

    public TestConnection(String name) {
        super(name);
    }

    /**
     * Test the functionality of the per-connection default alphabet.
     */
    public void testDefaultAlphabet() {
        try {
            // Set up some packets for the connection to read!
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            BindReceiverResp br = new BindReceiverResp();
            br.setSequenceNum(1);
            br.setSystemId("testSMSC");
            br.writeTo(bos);

            DeliverSM dsm = new DeliverSM();
            dsm.setSequenceNum(1);
            dsm.setMessage(Latin1Encoding.getInstance().encodeString(
                    TEST_MESSAGE));
            dsm.writeTo(bos);

            UnbindResp ur = new UnbindResp();
            ur.setSequenceNum(2);
            ur.writeTo(bos);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos
                    .toByteArray());
            bos = new ByteArrayOutputStream();

            StreamLink link = new StreamLink(bis, bos);
            Connection conn = new Connection(link);
            conn.setDefaultAlphabet(Latin1Encoding.getInstance());

            SubmitSM sm = (SubmitSM) conn.newInstance(SMPPPacket.SUBMIT_SM);
            sm.setMessageText(TEST_MESSAGE);
            try {
                byte[] expected = TEST_MESSAGE.getBytes("iso8859-1");
                assertTrue(Arrays.equals(expected, sm.getMessage()));
            } catch (UnsupportedEncodingException x) {
                // bullshit! Latin1 is a Java spec requirement
                x.printStackTrace(System.err);
                fail();
            }

            SMPPPacket p;
            p = conn.bind(Connection.RECEIVER, "test", "test", "test", 0, 0,
                    "6712345");
            if (p.getCommandId() != SMPPPacket.BIND_RECEIVER_RESP) {
                // but that's what I just put there!
                fail("Something wrong with the test class - unexpected packet");
            }

            p = conn.readNextPacket();
            if (p.getCommandId() != SMPPPacket.DELIVER_SM) {
                fail("Something wrong with the test class - unexpected packet");
            } else {
                try {
                    byte[] expected = TEST_MESSAGE.getBytes("iso8859-1");
                    assertTrue(Arrays.equals(expected, p.getMessage()));
                } catch (UnsupportedEncodingException x) {
                    x.printStackTrace(System.err);
                    fail();
                }
            }

            p = conn.unbind();
            if (p.getCommandId() != SMPPPacket.UNBIND_RESP) {
                fail("Something wrong with the test class - unexpected packet");
            }
        } catch (Exception x) {
            x.printStackTrace(System.err);
            fail();
        }
    }

    private void showBytes(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            System.out.print(" " + Integer.toHexString((int) b[i] & 0xff));
            if ((i % 30) == 0)
                System.out.print("\n");
        }
        System.out.print("\n");
    }
}