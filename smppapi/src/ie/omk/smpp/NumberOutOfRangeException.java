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

/** NumberOutOfRangeException
  * @author Oran Kelly
  * @version 1.0
  */
public class NumberOutOfRangeException
    extends ie.omk.smpp.SMPPException
{
    private int min = 0;
    private int max = 0;

    public NumberOutOfRangeException()
    {
    }

    /** Construct a new NumberOutOfRangeException with specified message.
      */
    public NumberOutOfRangeException(String s)
    {
	super(s);
    }

    /** Construct a new NumberOutOfRangeException.
      * @param min The minimum value allowed.
      * @param max The maximum value allowed.
      */
    public NumberOutOfRangeException(int min, int max)
    {
	this.min = min;
	this.max = max;
    }

    /** Get the minimum value allowed.
      * This value refers to the argument to which ever method raised this
      * exception.
      */
    public int getMin()
    {
	return (this.min);
    }

    /** Get the maximum value allowed.
      * This value refers to the argument to which ever method raised this
      * exception.
      */
    public int getMax()
    {
	return (this.max);
    }
}
