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
 * Java SMPP API author: oran.kelly@ireland.com
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */
package ie.omk.smpp.message;

/** Structure to hold the flags for a message.
  * @author Oran Kelly
  * @see SmppConnection#submitMessage
  * @see SmppConnection#replaceMessage
  */
public class MsgFlags
{
    /** Message priority */
    public boolean priority = false;

    /** Registered delivery */
    public boolean registered = false;

    /** Replace message if already present */
    public boolean replace_if_present = false;

    /** Esm class */
    public int esm_class = 0;

    /** GSM protocol Id */
    public int protocol = 0;

    /** GSM data encoding */
    public int data_coding = 0;

    /** Default message number to send */
    public int default_msg = 0;
}
