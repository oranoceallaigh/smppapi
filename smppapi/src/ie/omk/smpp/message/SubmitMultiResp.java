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
import ie.omk.smpp.ErrorAddress;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Submit to multiple destinations response. Relevant inherited fields from
 * SMPPPacket: <br>
 * <ul>
 * messageId
 * </ul>
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public class SubmitMultiResp extends ie.omk.smpp.message.SMPPResponse {
    /** Table of unsuccessful destinations */
    private List unsuccessfulTable = null;

    /**
     * Construct a new Unbind.
     */
    public SubmitMultiResp() {
        super(SUBMIT_MULTI_RESP);
        unsuccessfulTable = Collections.synchronizedList(new ArrayList());
    }

    /**
     * Construct a new Unbind with specified sequence number.
     * 
     * @param seqNum
     *            The sequence number to use
     * @deprecated
     */
    public SubmitMultiResp(int seqNum) {
        super(SUBMIT_MULTI_RESP, seqNum);
        unsuccessfulTable = Collections.synchronizedList(new ArrayList());
    }

    /**
     * Create a new SubmitMultiResp packet in response to a BindReceiver. This
     * constructor will set the sequence number to it's expected value.
     * 
     * @param r
     *            The Request packet the response is to
     */
    public SubmitMultiResp(SubmitMulti r) {
        super(r);
    }

    /** Get the number of destinations the message was not delivered to. */
    public int getUnsuccessfulCount() {
        return (unsuccessfulTable.size());
    }

    /**
     * Add a destination address to the table of unsuccessful destinations.
     * 
     * @param ea
     *            ErrorAddress object representing the failed destination
     * @return The current count of unsuccessful destinations (including the new
     *         one)
     */
    public int add(ErrorAddress ea) {
        unsuccessfulTable.add(ea);
        return (unsuccessfulTable.size());
    }

    /**
     * Remove an address from the table of unsuccessful destinations.
     * 
     * @param a
     *            the address to remove.
     * @return the size of the table after removal.
     */
    public int remove(Address a) {
        synchronized (unsuccessfulTable) {
            int i = unsuccessfulTable.indexOf(a);
            if (i > -1)
                unsuccessfulTable.remove(i);

            return (unsuccessfulTable.size());
        }
    }

    /**
     * Get an iterator to iterate over the set of addresses in the unsuccessful
     * destination table.
     */
    public java.util.ListIterator tableIterator() {
        return (unsuccessfulTable.listIterator());
    }

    /**
     * Return the number of bytes this packet would be encoded as to an
     * OutputStream.
     * 
     * @return the number of bytes this packet would encode as.
     */
    public int getBodyLength() {
        int loop;

        int size = ((messageId != null) ? messageId.length() : 0);

        synchronized (unsuccessfulTable) {
            Iterator i = unsuccessfulTable.iterator();
            while (i.hasNext())
                size += ((ErrorAddress) i.next()).getLength();
        }

        // 1 1-byte integer, 1 c-string
        return (size + 1 + 1);
    }

    /**
     * Write a byte representation of this packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to write to
     * @throws java.io.IOException
     *             If an error occurs writing to the output stream.
     */
    protected void encodeBody(OutputStream out) throws java.io.IOException {
        int loop, size = 0;

        synchronized (unsuccessfulTable) {
            size = unsuccessfulTable.size();
            SMPPIO.writeCString(getMessageId(), out);
            SMPPIO.writeInt(size, 1, out);

            Iterator i = unsuccessfulTable.iterator();
            while (i.hasNext())
                ((ErrorAddress) i.next()).writeTo(out);
        }
    }

    public void readBodyFrom(byte[] body, int offset)
            throws SMPPProtocolException {
        messageId = SMPPIO.readCString(body, offset);
        offset += messageId.length() + 1;

        int unsuccessfulCount = SMPPIO.bytesToInt(body, offset++, 1);

        if (unsuccessfulCount < 1)
            return;

        for (int loop = 0; loop < unsuccessfulCount; loop++) {
            ErrorAddress a = new ErrorAddress();
            a.readFrom(body, offset);
            offset += a.getLength();
            unsuccessfulTable.add(a);
        }
    }

    /**
     * Convert this packet to a String. Not to be interpreted programmatically,
     * it's just dead handy for debugging!
     */
    public String toString() {
        return new String("submit_multi_resp");
    }
}