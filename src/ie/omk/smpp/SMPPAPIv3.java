/*
 * Java implementation of the SMPP v3.3 API
 * Copyright (C) 1998 - 2000 by Oran Kelly
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
 */
package ie.omk.smpp;

import ie.omk.smpp.message.*;
/*
 * Define the functions required in an SMPP implementation
 */
interface SMPPAPIv3
{
	public final int INTERFACE_VERSION = 0x00000003;

	SMPPResponse bind_receiver(SMPPRequest request);
	SMPPResponse bind_transmitter(SMPPRequest request);
	SMPPResponse unbind(SMPPRequest request);
	SMPPResponse submit_sm(SMPPRequest request);
	SMPPResponse submit_multi(SMPPRequest request);
	SMPPResponse deliver_sm(SMPPRequest request);
	SMPPResponse query_sm(SMPPRequest request);
	SMPPResponse query_last_msgs(SMPPRequest request);
	SMPPResponse query_msg_details(SMPPRequest request);
	SMPPResponse cancel_sm(SMPPRequest request);
	SMPPResponse replace_sm(SMPPRequest request);
	SMPPResponse enquire_link(SMPPRequest request);
	SMPPResponse pararetrieve(SMPPRequest request);
}

