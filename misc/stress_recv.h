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
 *
 * $Id$
 */

#ifndef _STRESS_RECV_H
#define	_STRESS_RECV_H


#define	UNBIND		0x06UL
#define	UNBIND_RESP	0x80000006UL
#define BIND_RECV	0x1UL
#define BIND_RECV_RESP	0x80000001UL
#define DELIVER_SM	0x05UL
#define DELIVER_SM_RESP	0x80000005UL

#define SMSC_ID		"recv_stresser"


/* Send 10,000 messages by default. */
#define	DEFAULT_MSG_COUNT	10000L

/* Default port to listen on */
#define	DEFAULT_LISTEN_PORT	5432


typedef struct _smpp_header
{
	uint32_t cmd_len;
	uint32_t cmd_id;
	uint32_t cmd_status;
	uint32_t seq_num;
} smpp_header;


typedef struct _bind_receiver
{
	uint32_t cmd_len;
	uint32_t cmd_id;
	uint32_t cmd_status;
	uint32_t seq_num;

	char sys_id[16];
	char password[9];
	char sys_type[13];
	char interface_version;
	char addr_ton;
	char addr_npi;
	char addr_range[41];
} bind_receiver;

typedef struct _bind_receiver_resp
{
	uint32_t cmd_len;
	uint32_t cmd_id;
	uint32_t cmd_status;
	uint32_t seq_num;

	char sys_id[16];
} bind_receiver_resp;


typedef struct _deliver_sm
{
	uint32_t cmd_len;
	uint32_t cmd_id;
	uint32_t cmd_status;
	uint32_t seq_num;

	char service_type[6];
	char source_ton;
	char source_npi;
	char source_addr[21];

	char dest_ton;
	char dest_npi;
	char dest_addr[21];

	char esm_class;
	char protocol_id;
	char priority;
	char delivery_time[17];
	char expiry_time[17];
	char registered;
	char replace_if_present;
	char data_coding;
	char default_msg_id;
	char sm_length;
	char message[160];
} deliver_sm;

#endif /* _STRESS_RECV_H */
