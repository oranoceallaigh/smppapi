/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
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
 */
package ie.omk.smpp;

/** InvalidAddressRangeException
  * @author Oran Kelly
  * @version 1.0
  */
public class InvalidAddressRangeException
    extends ie.omk.smpp.SMPPException
{
    private String addressRange = null;

    public InvalidAddressRangeException()
    {
    }

    /** Construct a new InvalidAddressRangeException with specified message.
      */
    public InvalidAddressRangeException(String s)
    {
	super(s);
    }

    /** Construct a new InvalidAddressRangeException.
      * @param s A detail message.
      * @param addressRange The addressRange that caused the exception.
      */
    public InvalidAddressRangeException(String s, String addressRange)
    {
	super(s);
	this.addressRange = addressRange;
    }

    /** Get the address range that caused this exception.
      */
    public String getAddressRange()
    {
	return (this.addressRange);
    }
}
