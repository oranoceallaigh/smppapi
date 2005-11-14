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
package ie.omk.smpp;

import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;

/**
 * An address that message submission was unsuccessfully submitted to. This
 * class is used in the SubmitMultiResp packet type to return a list of SME
 * addresses that message submission failed for along with an error code for
 * each address indicating the reason for the failure.
 * 
 * @author Oran Kelly &lt;orank@users.sf.net&gt;
 */
public class ErrorAddress extends Address {
    /**
     * The error code showing why this address failed.
     */
    private int error = 0;

    /**
     * Create a new ErrorAddress object.
     */
    public ErrorAddress() {
    }

    /**
     * Create a new ErrorAddress object.
     * 
     * @param ton
     *            The Type Of Number.
     * @param npi
     *            The Numbering Plan Indicator.
     * @param addr
     *            The address.
     */
    public ErrorAddress(int ton, int npi, String addr) {
        super(ton, npi, addr);
    }

    /**
     * Create a new ErrorAddress object.
     * 
     * @param ton
     *            The Type Of Number.
     * @param npi
     *            The Numbering Plan Indicator.
     * @param addr
     *            The address.
     * @param error
     *            The error code indicating why message submission failed.
     */
    public ErrorAddress(int ton, int npi, String addr, int error) {
        super(ton, npi, addr);
        this.error = error;
    }

    /**
     * Get the error code associated with this ErrorAddress.
     */
    public int getError() {
        return (error);
    }

    /**
     * Set the error code associated with this ErrorAddress.
     */
    public void setError(int error) {
        this.error = error;
    }

    public int getLength() {
        return (super.getLength() + 4);
    }

    public void writeTo(OutputStream out) throws java.io.IOException {
        super.writeTo(out);
        SMPPIO.writeInt(error, 4, out);
    }

    public void readFrom(byte[] ea, int offset) {
        super.readFrom(ea, offset);
        offset += super.getLength();

        error = SMPPIO.bytesToInt(ea, offset, 4);
    }

    /**
     * Test driver function. This method checks that serialization and
     * deserialization of instances of this class result in byte arrays and new
     * packets of consistently the same size. It does the same as the NullTest
     * and FullTest packet test classes.
     */
    /*
     * public static final void main(String[] args) { try {
     * System.out.println("Null test:"); ErrorAddress a = new ErrorAddress();
     * java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
     * a.writeTo(os); byte[] b = os.toByteArray();
     * 
     * if (b.length == a.getLength()) System.out.println("\tpass 1."); else
     * System.out.println("\tfail 1.");
     * 
     * ErrorAddress a1 = new ErrorAddress(); a1.readFrom(b, 0);
     * 
     * if (b.length == a1.getLength()) System.out.println("\tpass 2."); else
     * System.out.println("\tfail 2."); } catch (Exception x) {
     * System.out.println("\texception:"); x.printStackTrace(System.out); }
     * 
     * try { System.out.println("\nFilled test:"); ErrorAddress a = new
     * ErrorAddress(2, 2, "4745879345", 5016); java.io.ByteArrayOutputStream os =
     * new java.io.ByteArrayOutputStream(); a.writeTo(os); byte[] b =
     * os.toByteArray();
     * 
     * if (b.length == a.getLength()) System.out.println("\tpass 1."); else
     * System.out.println("\tfail 1.");
     * 
     * ErrorAddress a1 = new ErrorAddress(); a1.readFrom(b, 0);
     * 
     * if (b.length == a1.getLength()) System.out.println("\tpass 2."); else
     * System.out.println("\tfail 2."); } catch (Exception x) {
     * System.out.println("\texception:"); x.printStackTrace(System.out); } }
     */
}