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

package ie.omk.smpp.util;

/** Class representing a message encoded in binary format.
 * This class uses a data coding value of 4 (00000100b), in accordance with GSM
 * 03.38.
 */
public class BinaryEncoding
    extends ie.omk.smpp.util.MessageEncoding
{
    private static final int DCS = 4;

    private static final BinaryEncoding instance = new BinaryEncoding();

    private BinaryEncoding() {
	super (DCS);
    }

    /** Get the singleton instance of BinaryEncoding.
     */
    public static BinaryEncoding getInstance() {
	return (instance);
    }
}
