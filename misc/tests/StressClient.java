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
package tests;

import ie.omk.smpp.Connection;
import ie.omk.smpp.event.ConnectionObserver;
import ie.omk.smpp.event.ReceiverExitEvent;
import ie.omk.smpp.event.SMPPEvent;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.DeliverSM;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.Unbind;
import ie.omk.smpp.message.UnbindResp;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * Example class to submit a message to a SMSC. This class simply binds to the
 * server, submits a message and then unbinds.
 */
public class StressClient implements ConnectionObserver {
    private static int msgCount = 0;

    // Start time (once successfully bound).
    private long start = 0;

    // End time (either send an unbind or an unbind received).
    private long end = 0;

    // Set to true to display each message received.
    private boolean showMsgs = false;

    // This is called when the connection receives a packet from the SMSC.
    public void update(Connection r, SMPPEvent ev) {
        switch (ev.getType()) {
        case SMPPEvent.RECEIVER_EXIT:
            receiverExit(r, (ReceiverExitEvent) ev);
            break;
        }
    }

    public void packetReceived(Connection myConnection, SMPPPacket pak) {
        switch (pak.getCommandId()) {

        // Bind transmitter response. Check it's status for success...
        case SMPPPacket.BIND_RECEIVER_RESP:
            if (pak.getCommandStatus() != 0) {
                System.out.println("Error binding to the SMSC. Error = "
                        + pak.getCommandStatus());
            } else {
                this.start = System.currentTimeMillis();
                System.out.println("Successfully bound. Waiting for message"
                        + " delivery..");
                System.out.println("(Each dot printed is 500 deliver_sm's!)");
            }
            break;

        // Submit message response...
        case SMPPPacket.DELIVER_SM:
            if (pak.getCommandStatus() != 0) {
                System.out.println("Deliver SM with an error! "
                        + pak.getCommandStatus());

            } else {
                ++msgCount;
                if (showMsgs) {
                    System.out.println(Integer.toString(pak.getSequenceNum())
                            + ": \"" + ((DeliverSM) pak).getMessageText()
                            + "\"");
                } else if ((msgCount % 500) == 0) {
                    System.out.print("."); // Give some feedback
                }
            }
            break;

        // Unbind request received from server..
        case SMPPPacket.UNBIND:
            this.end = System.currentTimeMillis();
            System.out.println("\nSMSC has requested unbind! Responding..");
            try {
                UnbindResp ubr = new UnbindResp((Unbind) pak);
                myConnection.sendResponse(ubr);
            } catch (IOException x) {
                x.printStackTrace(System.err);
            } finally {
                endReport();
            }
            break;

        // Unbind response..
        case SMPPPacket.UNBIND_RESP:
            this.end = System.currentTimeMillis();
            System.out.println("\nUnbound.");
            endReport();
            break;

        default:
            System.out.println("\nUnexpected packet received! Id = "
                    + Integer.toHexString(pak.getCommandId()));
        }
    }

    private void receiverExit(Connection myConnection, ReceiverExitEvent ev) {
        if (ev.getReason() != ReceiverExitEvent.EXCEPTION) {
            System.out.println("Receiver thread has exited normally.");
        } else {
            Throwable t = ev.getException();
            System.out.println("Receiver thread died due to exception:");
            t.printStackTrace(System.out);
            endReport();
        }
    }

    // Print out a report
    private void endReport() {
        System.out.println("deliver_sm's received: " + msgCount);
        System.out.println("Start time: " + new Date(start).toString());
        System.out.println("End time: " + new Date(end).toString());
        System.out.println("Elapsed: " + (end - start) + " milliseconds.");
    }

    public static void main(String[] clargs) {
        try {
            java.util.HashMap myArgs = ParseArgs.parse(clargs);
            int port = Integer.parseInt((String) myArgs.get(ParseArgs.PORT));

            StressClient ex = new StressClient();

            // Create a Connection object (we won't bind just yet..)
            Connection myConnection = new Connection((String) myArgs
                    .get(ParseArgs.HOSTNAME), port, true);

            // Need to add myself to the list of listeners for this connection
            myConnection.addObserver(ex);

            // Automatically respond to ENQUIRE_LINK requests from the SMSC
            myConnection.autoAckLink(true);
            myConnection.autoAckMessages(true);

            // Bind to the SMSC as a receiver
            BindResp resp = myConnection.bind(Connection.RECEIVER,
                    (String) myArgs.get(ParseArgs.SYSTEM_ID), (String) myArgs
                            .get(ParseArgs.PASSWORD), (String) myArgs
                            .get(ParseArgs.SYSTEM_TYPE), Integer
                            .parseInt((String) myArgs
                                    .get(ParseArgs.ADDRESS_TON)), Integer
                            .parseInt((String) myArgs
                                    .get(ParseArgs.ADDRESS_NPI)),
                    (String) myArgs.get(ParseArgs.ADDRESS_RANGE));

            System.out.println("Hit a key to issue an unbind..");
            System.in.read();

            if (myConnection.getState() == Connection.BOUND) {
                System.out.println("Sending unbind request..");
                myConnection.unbind();
            }

            Thread.sleep(2000);

            myConnection.closeLink();
        } catch (IOException x) {
            x.printStackTrace(System.err);
        } catch (InterruptedException x) {
        }
    }

}

// This is a cut-and-paste of the examples class.

class ParseArgs {

    public static final Object HOSTNAME = new String("hostname");

    public static final Object PORT = new String("port");

    public static final Object SYSTEM_ID = new String("sysid");

    public static final Object SYSTEM_TYPE = new String("systype");

    public static final Object PASSWORD = new String("password");

    public static final Object ADDRESS_TON = new String("ton");

    public static final Object ADDRESS_NPI = new String("npi");

    public static final Object ADDRESS_RANGE = new String("ar");

    private ParseArgs() {
    }

    public static final HashMap parse(String[] args) {
        HashMap a = new HashMap();

        try {
            String s;
            int i = args[0].indexOf(':');
            if (i >= 0) {
                s = args[0].substring(0, i);
                a.put(HOSTNAME, s);

                s = args[0].substring(i + 1);
                a.put(PORT, s);
            } else {
                a.put(HOSTNAME, args[0]);
            }

            a.put(SYSTEM_ID, args[1]);
            a.put(PASSWORD, args[2]);
            a.put(SYSTEM_TYPE, args[3]);

            int p1 = args[4].indexOf(':');
            int p2 = args[4].indexOf(':', p1 + 1);

            if (p1 > -1 && p2 > -1) {
                a.put(ADDRESS_TON, args[4].substring(0, p1));
                a.put(ADDRESS_NPI, args[4].substring(p1 + 1, p2));
                a.put(ADDRESS_RANGE, args[4].substring(p2 + 1));
            }
        } catch (ArrayIndexOutOfBoundsException x) {
        }

        return (a);
    }
}