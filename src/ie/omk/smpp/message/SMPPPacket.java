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
package ie.omk.smpp.message;

import java.io.*;
import java.net.SocketException;
import java.util.*;
import java.text.*;
import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;

/** This is the abstract class that all SMPP messages are inherited from
 *  @author Oran Kelly
 *  @version 1.0
 */
public abstract class SMPPPacket
{
    /** Command Id: Negative Acknowledgement */
    public static final int ESME_NACK 		= 0x80000000;
    /** Command Id: Bind Receiver */
    public static final int ESME_BNDRCV 	= 0x00000001;
    /** Command Id: Bind Receiver Response */
    public static final int ESME_BNDRCV_RESP 	= 0x80000001;
    /** Command Id: Bind transmitter */
    public static final int ESME_BNDTRN 	= 0x00000002;
    /** Command Id: Bind transmitter response */
    public static final int ESME_BNDTRN_RESP 	= 0x80000002;
    /** Command Id: Unbind */
    public static final int ESME_UBD		= 0x00000006;
    /** Command Id: Unbind response */
    public static final int ESME_UBD_RESP	= 0x80000006;
    /** Command Id: Submit int message */
    public static final int ESME_SUB_SM		= 0x00000004;
    /** Command Id: Submit int message response */
    public static final int ESME_SUB_SM_RESP	= 0x80000004;
    /** Command Id: Submit multiple int messages */
    public static final int ESME_SUB_MULTI	= 0x00000021;
    /** Command Id: Submit multi response */
    public static final int ESME_SUB_MULTI_RESP	= 0x80000021;
    /** Command Id: Deliver Short message */
    public static final int SMSC_DELIVER_SM	= 0x00000005;
    /** Command Id: Deliver int message response */
    public static final int SMSC_DELIVER_SM_RESP= 0x80000005;
    /** Command Id: Query int message */
    public static final int ESME_QUERY_SM	= 0x00000003;
    /** Command Id: Query int message response */
    public static final int ESME_QUERY_SM_RESP	= 0x80000003;
    /** Command Id: Query last messages */
    public static final int ESME_QUERY_LAST_MSGS= 0x00000023;
    /** Command Id: Query last messages response */
    public static final int ESME_QUERY_LAST_MSGS_RESP = 0x80000023;
    /** Command Id: Query message details */
    public static final int ESME_QUERY_MSG_DETAILS = 0x00000024;
    /** Command Id: Query message details response */
    public static final int ESME_QUERY_MSG_DETAILS_RESP	= 0x80000024;
    /** Command Id: Cancel int message */
    public static final int ESME_CANCEL_SM	= 0x00000008;
    /** Command Id: Cancel int message response */
    public static final int ESME_CANCEL_SM_RESP	= 0x80000008;
    /** Command Id: Replace int message */
    public static final int ESME_REPLACE_SM	= 0x00000007;
    /** Command Id: replace int message response */
    public static final int ESME_REPLACE_SM_RESP= 0x80000007;
    /** Command Id: Enquire Link */
    public static final int ESME_QRYLINK	= 0x00000015;
    /** Command Id: Enquire link respinse */
    public static final int ESME_QRYLINK_RESP	= 0x80000015;
    /** Command Id: Parameter retrieve */
    public static final int ESME_PARAM_RETRIEVE	= 0x00000022;
    /** Command Id: Paramater retrieve response */
    public static final int ESME_PARAM_RETRIEVE_RESP = 0x80000022;


    /** Message state at Smsc: En route */
    public static final int SM_STATE_EN_ROUTE		= 1;
    /** Message state at Smsc: Delivered (final) */
    public static final int SM_STATE_DELIVERED		= 2;
    /** Message state at Smsc: Expired (final) */
    public static final int SM_STATE_EXPIRED		= 3;
    /** Message state at Smsc: Deleted (final) */
    public static final int SM_STATE_DELETED		= 4;
    /** Message state at Smsc: Undeliverable (final) */
    public static final int SM_STATE_UNDELIVERABLE	= 5;
    /** Message state at Smsc: Accepted */
    public static final int SM_STATE_ACCEPTED		= 6;
    /** Message state at Smsc: Invalid message (final) */
    public static final int SM_STATE_INVALID		= 7;

    /** Esm class: Mobile Terminated; Normal delivery, no address swapping */
    public static final int SMC_MT			= 1;
    /** Esm class: Mobile originated */
    public static final int SMC_MO			= 2;
    /** Esm class: Mobile Originated / Terminated */
    public static final int SMC_MOMT			= 3;
    /** Esm class: Delivery receipt, no address swapping */
    public static final int SMC_RECEIPT						= 4;
    /** Esm class: Predefined message */
    public static final int SMC_DEFMSG			= 8;
    /** Esm class: Normal delivery , address swapping on */
    public static final int SMC_LOOPBACK_RECEIPT	= 16;
    /** Esm class: Delivery receipt, address swapping on */
    public static final int SMC_RECEIPT_SWAP		= 20;
    /** Esm class: Store message, do not send to Kernel */
    public static final int SMC_STORE			= 32;
    /** Esm class: Store message and send to kernel */
    public static final int SMC_STORE_FORWARD		= 36;
    /** Esm class: Distribution submission */
    public static final int SMC_DLIST			= 64;
    /** Esm class: Multiple recipient submission */
    public static final int SMC_MULTI			= 128;
    /** Esm class: Distribution list and multiple recipient submission */
    public static final int SMC_CAS_DL			= 256;
    /** Esm class: Escalated message FFU */
    public static final int SMC_ESCALATED		= 512;
    /** Esm class: Submit with replace message */
    public static final int SMC_SUBMIT_REPLACE		= 1024;
    /** Esm class: Memory capacity error */
    public static final int SMC_MCE			= 2048;

    /** Esme error code: No error */
    public static final int ESME_ROK			= 0;


    // XXX You're probably taking these out!
    /*
     * ERROR BASES FOR VARIOUS LIBRARIES
     */
    static final int 
	SMPP_ERR_BASE					= 20000,
	SMPPSMSC_ERR_BASE				= 20100,
	COM_ERR_BASE					= 20500,
	TAP_ERR_BASE					= 20600,
	UCP_ERR_BASE					= 20700,
	SM_ERR_BASE					= 20800,

	X25_ERR_BASE					= 21000;

    /*
     *	Description:	GSM defines
     */
    static final String[] GsmStrings = {
	"No error",
	"PNack: Unknown Customer",
	"PNack: Not provisioned",
	"TNack: Call is barred",
	"PNack: CUG Rejected",
	"TNack: Sms not supported by MS",
	"TNack: Error in receiveing MS",
	"TNack: Facility not supported",
	"TNack: Memory capacity exceeded",
	"TNack: Absent Subscriber",
	"TNack: Absent Subscriber detached",
	"TNack: MS did not respond to 2 pages",
	"TNack: Subscriber roamed to new PLMN",
	"TNack: System failure"
    };

	static final int
	    GSM_ERR_NONE		= 0,
	    GSM_ERR_P_UNKNOWN		= 1,	// Customer unknown
	    GSM_ERR_P_PROVISION		= 11,	// Not provisioned
	    GSM_ERR_T_BARRED		= 13,	// Call barred
	    GSM_ERR_P_CUG		= 15,	// CUG reject
	    GSM_ERR_T_MSSUPPORT		= 19,	// SMS not supported by MS
	    GSM_ERR_T_MSERROR		= 20,	// error in receiving MS
	    GSM_ERR_T_SUPPORT		= 21,	// facility not supported
	    GSM_ERR_T_MEMCAP		= 22,	// memory capacity exceeded
	    GSM_ERR_T_ABSENT		= 29,	// absent subscriber
	    GSM_ERR_T_ABSENT_DETACHED	= 30,	// absent subscriber detached
	    GSM_ERR_T_ABSENT_PAGEFAIL	= 31,	// MS didn't respond to 2 pages
	    GSM_ERR_T_SUPPORT_ROAMING	= 32,	// Subscriber roamed to new PLMN
	    GSM_ERR_T_SYSTEM		= 36,	// system failure

	    GSM_TON_UNKNOWN		= 0,
	    GSM_TON_INTERNATIONAL	= 1,
	    GSM_TON_NATIONAL		= 2,
	    GSM_TON_NETWORK		= 3,
	    GSM_TON_SUBSCRIBER		= 4,
	    GSM_TON_ALPHANUMERIC	= 5,
	    GSM_TON_ABBREVIATED		= 6,
	    GSM_TON_RESERVED_EXTN	= 7,

	    GSM_NPI_UNKNOWN		= 0,
	    GSM_NPI_E164		= 1,
	    GSM_NPI_ISDN		= GSM_NPI_E164,
	    GSM_NPI_X121		= 3,
	    GSM_NPI_TELEX		= 4,
	    GSM_NPI_NATIONAL		= 8,
	    GSM_NPI_PRIVATE		= 9,
	    GSM_NPI_ERMES		= 10,
	    GSM_NPI_RESERVED_EXTN	= 15,

	    ERR_COM_INVALID_ADDRESS		= COM_ERR_BASE+1,
	    ERR_COM_REMOTE_CLOSE		= COM_ERR_BASE+2,
	    ERR_COM_NO_CONNECTION		= COM_ERR_BASE+3,
	    ENOC				= ERR_COM_NO_CONNECTION,
	    ERR_COM_TIMEOUT			= COM_ERR_BASE+4,
	    ERR_COM_CONNECT_NOT_SUPPORTED	= COM_ERR_BASE+5,
	    ERR_COM_ACCEPT_NOT_SUPPORTED	= COM_ERR_BASE+6,
	    ERR_COM_LISTEN_NOT_SUPPORTED	= COM_ERR_BASE+7,
	    ERR_COM_READ_NOT_SUPPORTED		= COM_ERR_BASE+8,
	    ERR_COM_WRITE_NOT_SUPPORTED		= COM_ERR_BASE+9,
	    ERR_COM_CLOSE_NOT_SUPPORTED		= COM_ERR_BASE+10,
	    ERR_COM_QUERY_NOT_SUPPORTED		= COM_ERR_BASE+11,
	    ERR_COM_UNKNOWN_PROTOCOL		= COM_ERR_BASE+12,


	    ERR_SM_NOMEM			= SM_ERR_BASE+1,
	    ERR_SM_INVALID_ARG			= SM_ERR_BASE+2,
	    ERR_SM_NO_PROTOCOL			= SM_ERR_BASE+3,
	    ERR_SM_NOT_CONNECTED		= SM_ERR_BASE+4,
	    ERR_SM_TIMEOUT			= SM_ERR_BASE+5,
	    ERR_SM_NOT_SUPPORTED		= SM_ERR_BASE+6,

	    ERR_SM_CONF_ADDRESS			= SM_ERR_BASE + 7,
	    ERR_SM_CONF_SYSTEM_ID		= SM_ERR_BASE + 8,
	    ERR_SM_CONF_SYSTEM_TYPE		= SM_ERR_BASE + 9,
	    ERR_SM_CONF_PASSWORD		= SM_ERR_BASE + 10,
	    ERR_SM_CONF_PREFIX			= SM_ERR_BASE + 11,
	    ERR_SM_CONF_BIND_ADDRESS		= SM_ERR_BASE+ 12,
	    ERR_SM_CONF_SYNTAX			= SM_ERR_BASE + 13,

	    ERR_SM_CONNECTION_CLOSED		= ERR_COM_REMOTE_CLOSE,

	    ERR_SMPP_NOT_SUPPORTED		= ERR_SM_NOT_SUPPORTED,

	    ERR_SMPP_INVALID_ERRCODE		= SMPP_ERR_BASE + 2,
	    ERR_SMPP_HEADER_LEN			= SMPP_ERR_BASE + 3,
	    ERR_SMPP_HEADER_ERROR		= SMPP_ERR_BASE + 4,
	    ERR_SMPP_NOMEM			= SMPP_ERR_BASE + 5,
	    ERR_SMPP_SEQ_CHECK			= SMPP_ERR_BASE + 6,
	    ERR_SMPP_GENERIC_NAK		= SMPP_ERR_BASE + 7,
	    ERR_SMPP_PROTOCOL_ERROR		= SMPP_ERR_BASE + 8,
	    ERR_SMPP_INVALID_ARG		= SMPP_ERR_BASE + 9,
	    ERR_SMPP_ALREADY_BOUND		= SMPP_ERR_BASE + 10,
	    ERR_SMPP_NOT_BOUND			= SMPP_ERR_BASE + 11,
	    ERR_SMPP_SERVICE_NSUPPORTED		= SMPP_ERR_BASE + 12,
	    ERR_SMPP_SEND_ON_RECVBIND		= SMPP_ERR_BASE + 13,
	    ERR_SMPP_RECV_ON_TRANSBIND		= SMPP_ERR_BASE + 14,
	    ERR_SMPP_UNKNOWN_CMD_RECEIVED	= SMPP_ERR_BASE + 15,
	    ERR_SMPP_NOTHING_ARRIVED		= SMPP_ERR_BASE + 16,
	    ERR_SMPP_RBUFFER_TOO_SMALL		= SMPP_ERR_BASE + 17,
	    ERR_SMPP_RECEIVE_TIMEOUT		= SMPP_ERR_BASE + 18,
	    ERR_SMPP_RESPONSE_INVALID		= SMPP_ERR_BASE + 19,


	    SMSCBASE				= SMPPSMSC_ERR_BASE;
	/*
	   ERR_SMSC_SMPP_ROK			= SMSCBASE+ESME_ROK,
	   ERR_SMSC_SMPP_RINVMSGLEN		= SMSCBASE+ESME_RINVMSGLEN,
	   ERR_SMSC_SMPP_RINVCMDLEN		= SMSCBASE+ESME_RINVCMDLEN,
	   ERR_SMSC_SMPP_RINVCMDID		= SMSCBASE+ESME_RINVCMDID,
	   ERR_SMSC_SMPP_RINVBNDSTS		= SMSCBASE+ESME_RINVBNDSTS,
	   ERR_SMSC_SMPP_RALYBND		= SMSCBASE+ESME_RALYBND,
	   ERR_SMSC_SMPP_RINVPRTFLG		= SMSCBASE+ESME_RINVPRTFLG,
	   ERR_SMSC_SMPP_RINVREGDLVFLG		= SMSCBASE+ESME_RINVREGDLVFLG,
	   ERR_SMSC_SMPP_RSYSERR		= SMSCBASE+ESME_RSYSERR,
	   ERR_SMSC_SMPP_RINVPAR		= SMSCBASE+ESME_RINVPAR,
	   ERR_SMSC_SMPP_RINVSRCADR		= SMSCBASE+ESME_RINVSRCADR,
	   ERR_SMSC_SMPP_RINVDSTADR		= SMSCBASE+ESME_RINVDSTADR,
	   ERR_SMSC_SMPP_RINVMSGID		= SMSCBASE+ESME_RINVMSGID,
	   ERR_SMSC_SMPP_RINVPASWD		= SMSCBASE+ESME_RINVPASWD,
	   ERR_SMSC_SMPP_RINVPASWDLEN		= SMSCBASE+ESME_RINVPASWDLEN,
	   ERR_SMSC_SMPP_RINVSYSIDSRV		= SMSCBASE+ESME_RINVSYSIDSRV,
	   ERR_SMSC_SMPP_RCNTCANMSG		= SMSCBASE+ESME_RCNTCANMSG,
	   ERR_SMSC_SMPP_RINVDATFMT		= SMSCBASE+ESME_RINVDATFMT,
	   ERR_SMSC_SMPP_RCNTREPMSG		= SMSCBASE+ESME_RCNTREPMSG,
	   ERR_SMSC_SMPP_RMSGQFUL		= SMSCBASE+ESME_RMSGQFUL,

	   ERR_SMSC_SMPP_RINVSERTYP		= SMSCBASE+ESME_RINVSERTYP,
	   ERR_SMSC_SMPP_RSERNOTSUP		= SMSCBASE+ESME_RSERNOTSUP,
	   ERR_SMSC_SMPP_RINVMIDSER		= SMSCBASE+ESME_RINVMIDSER,
	   ERR_SMSC_SMPP_RINVREPADDR		= SMSCBASE+ESME_RINVREPADDR,
	   ERR_SMSC_SMPP_RCNTADDCUST		= SMSCBASE+ESME_RCNTADDCUST,
	   ERR_SMSC_SMPP_RCNTDELCUST		= SMSCBASE+ESME_RCNTDELCUST,
	   ERR_SMSC_SMPP_RCNTMODCUST		= SMSCBASE+ESME_RCNTMODCUST,
	   ERR_SMSC_SMPP_RCNTQRYCUST		= SMSCBASE+ESME_RCNTQRYCUST,
	   ERR_SMSC_SMPP_RINVCUSTID		= SMSCBASE+ESME_RINVCUSTID,
	   ERR_SMSC_SMPP_RINVCUSTIDLEN		= SMSCBASE+ESME_RINVCUSTIDLEN,
	   ERR_SMSC_SMPP_RINVCUSTNAMLEN		= SMSCBASE+ESME_RINVCUSTNAMLEN,
	   ERR_SMSC_SMPP_RINVCUSTADRLEN		= SMSCBASE+ESME_RINVCUSTADRLEN,
	   ERR_SMSC_SMPP_RINVADRLEN		= SMSCBASE+ESME_RINVADRLEN,
	   ERR_SMSC_SMPP_RCUSTEXIST		= SMSCBASE+ESME_RCUSTEXIST,
	   ERR_SMSC_SMPP_RCUSTNOTEXIST		= SMSCBASE+ESME_RCUSTNOTEXIST,
	   ERR_SMSC_SMPP_RINVCUGTR		= SMSCBASE+ESME_RINVCUGTR,

	   ERR_SMSC_SMPP_RCNTADDDL		= SMSCBASE+ESME_RCNTADDDL,
	   ERR_SMSC_SMPP_RCNTMODDL		= SMSCBASE+ESME_RCNTMODDL,
	   ERR_SMSC_SMPP_RCNTDELDL		= SMSCBASE+ESME_RCNTDELDL,
	   ERR_SMSC_SMPP_RCNTVIEWDL		= SMSCBASE+ESME_RCNTVIEWDL,
	   ERR_SMSC_SMPP_RCNTLISTDL		= SMSCBASE+ESME_RCNTLISTDL,
	   ERR_SMSC_SMPP_RCNTRETRPARAM		= SMSCBASE+ESME_RCNTRETRPARAM,
	   ERR_SMSC_SMPP_RINVPARAMLEN		= SMSCBASE+ESME_RINVPARAMLEN,
	   ERR_SMSC_SMPP_RINVNUMDESTS		= SMSCBASE+ESME_RINVNUMDESTS,
	   ERR_SMSC_SMPP_RINVDESTNAMELEN	= SMSCBASE+ESME_RINVDESTNAMELEN,
	   ERR_SMSC_SMPP_RINVDESCMEMBLEN	= SMSCBASE+ESME_RINVDESCMEMBLEN,
	   ERR_SMSC_SMPP_RCNTADDMEMBER		= SMSCBASE+ESME_RCNTADDMEMBER,
	   ERR_SMSC_SMPP_RCNTDELMEMBER		= SMSCBASE+ESME_RCNTDELMEMBER,
	   ERR_SMSC_SMPP_RINVMEMBERTYPE		= SMSCBASE+ESME_RINVMEMBERTYPE,
	   ERR_SMSC_SMPP_RINVMODOPT		= SMSCBASE+ESME_RINVMODOPT,
	   ERR_SMSC_SMPP_RINVDESTFLAG		= SMSCBASE+ESME_RINVDESTFLAG,
	   ERR_SMSC_SMPP_RINVSUBREP		= SMSCBASE+ESME_RINVSUBREP,
	   ERR_SMSC_SMPP_RINVESMCLASS		= SMSCBASE+ESME_RINVESMCLASS,
	   ERR_SMSC_SMPP_RCNTSUBDL		= SMSCBASE+ESME_RCNTSUBDL,
	   ERR_SMSC_SMPP_RCNTSUBMULRECP		= SMSCBASE+ESME_RCNTSUBMULRECP,
	   ERR_SMSC_SMPP_RINVSRCADDRLEN		= SMSCBASE+ESME_RINVSRCADDRLEN,
	   ERR_SMSC_SMPP_RINVDSTADDRLEN		= SMSCBASE+ESME_RINVDSTADDRLEN,
	   ERR_SMSC_SMPP_RINVSRCTON		= SMSCBASE+ESME_RINVSRCTON,
	   ERR_SMSC_SMPP_RINVSRCNPI		= SMSCBASE+ESME_RINVSRCNPI,
	   ERR_SMSC_SMPP_RINVDSTTON		= SMSCBASE+ESME_RINVDSTTON,
	   ERR_SMSC_SMPP_RINVDSTNPI		= SMSCBASE+ESME_RINVDSTNPI,
	   ERR_SMSC_SMPP_RINVESMTYPE		= SMSCBASE+ESME_RINVESMTYPE,
	   ERR_SMSC_SMPP_RINVSYSTYP		= SMSCBASE+ESME_RINVSYSTYP,
	   ERR_SMSC_SMPP_RINVREPFLAG		= SMSCBASE+ESME_RINVREPFLAG,
	   ERR_SMSC_SMPP_RINVNUMMSGS		= SMSCBASE+ESME_RINVNUMMSGS,

	   ERR_SMSC_SMPP_RLIMITEXCEED		= SMSCBASE+ESME_RLIMITEXCEED,
	   ERR_SMSC_SMPP_RTXNOTALLOWD		= SMSCBASE+ESME_RTXNOTALLOWD,
	   ERR_SMSC_SMPP_RTHROTTLEXCD		= SMSCBASE+ESME_RTHROTTLEXCD,
	   ERR_SMSC_SMPP_RPROVNOTALLWD		= SMSCBASE+ESME_RPROVNOTALLWD,
	   ERR_SMSC_SMPP_RTXEXCEED		= SMSCBASE+ESME_RTXEXCEED,

	   ERR_SMSC_SMPP_RINVSCHED		= SMSCBASE+ESME_RINVSCHED,
	   ERR_SMSC_SMPP_RINVEXPIRY		= SMSCBASE+ESME_RINVEXPIRY,
	   ERR_SMSC_SMPP_RPREDEFMSGNOTFN	= SMSCBASE+ESME_RPREDEFMSGNOTFND,
	   ERR_SMSC_SMPP_RINVTON		= SMSCBASE+ESME_RINVTON,
	   ERR_SMSC_SMPP_RINVNPI		= SMSCBASE+ESME_RINVNPI,
	   ERR_SMSC_SMPP_RINVADDR		= SMSCBASE+ESME_RINVADDR;*/

    /************************** End definitions *****************************/


    protected int cmdLen = 0;
    protected int cmdId = 0;
    protected int cmdStatus = 0;
    protected int seqNo = 0;

    /** Almost all packets use one or more of these.
     * These attributes were all stuck in here for easier maintenance...
     * instead of altering 5 different packets, just alter it here!!
     * Special cases like SubmitMulti and QueryMsgDetailsResp hmaintain
     * their own destination tables.  Any packets that wish to use
     * these attribs should override the appropriate methods defined
     * below to be public and just call super.method()
     */
    protected SmeAddress	source;		// Source address
    protected SmeAddress	destination;	// Destination address
    protected MsgFlags		flags;		// Set of message type flags
    protected String		message;	// The text of a short message
    protected String		serviceType;	// Service type for this msg
    protected Date		deliveryTime;	// Scheduled delivery time
    protected Date		expiryTime;	// Scheduled expiry time
    protected Date		finalDate;	// Date of reaching final state
    protected int		messageId;	// Smsc allocated message Id
    protected int		messageStatus;	// Status of message
    protected int		errorCode;	// Error associated with message


    /** Create a new SMPPPacket with specified Id and sequence number.
     *  @param id Command Id value
     *  @param seqNo Command Sequence number
     *  @exception ie.omk.smpp.SMPPException If an invalid sequence number is used
     */
    public SMPPPacket(int id, int seqNo)
    {
	this.cmdId = id;
	if(seqNo >= 0x01 && seqNo <= 0x7FFFFFFF) {
	    this.seqNo = seqNo;
	} else {
	    Debug.d(this, "<init>", "Bad sequence no: "+seqNo, Debug.DBG_1);
	    throw new SMPPException("Sequence Number out of Bounds.");
	}

	// Flags should always be created (rest of code assumes it is.)
	flags = new MsgFlags();
    }

    /** Read an SMPPPacket header from an InputStream
     * @param in InputStream to read from
     * @exception IOException If EOS is reached or I/O error occurs before an SMPPPacket can be read
     * @see java.io.InputStream
     */
    public SMPPPacket(InputStream in)
    {
	// Flags should always be created (rest of code assumes it is.)
	flags = new MsgFlags();

	try {
	    int cmdLen = 0, cmdID = 0, cmdStatus = 0, seqNo = 0;

	    byte[] b = new byte[4];
	    if(in.read(b, 0, 4) == -1)
		throw new IOException();
	    cmdLen = bytesToInt(b[0], b[1], b[2], b[3]);

	    if(in.read(b, 0, 4) == -1)
		throw new IOException();
	    cmdID = bytesToInt(b[0], b[1], b[2], b[3]);

	    if(in.read(b, 0, 4) == -1)
		throw new IOException();
	    cmdStatus = bytesToInt(b[0], b[1], b[2], b[3]);

	    if(in.read(b, 0, 4) == -1)
		throw new IOException();
	    seqNo = bytesToInt(b[0], b[1], b[2], b[3]);

	    this.cmdLen = cmdLen;
	    this.cmdId = cmdID;
	    this.cmdStatus = cmdStatus;
	    this.seqNo = seqNo;
	} catch(IOException iox) {
	    if(Debug.dbg)
		Debug.d(this, "<init>", "IOException " + iox.getMessage(), Debug.DBG_1);
	    throw new SMPPException("Input stream does not contain an SMPP Packet.");
	}
    }

    /** Get the length of this SMPP packet
     * @return The size in bytes of this packet
     */
    public final int getCommandLen()
    {
	return size();
    }

    /** Get the Command Id of this SMPP packet
     * @return The Command Id of this packet
     */
    public final int getCommandId()
    {
	return cmdId;
    }

    /** Get the status of this packet
     * @return The error status of this packet (only relevent to Response packets)
     */
    public final int getCommandStatus()
    {
	return cmdStatus;
    }

    /** Get the sequence number of this packet
     * @return The sequence number of this SMPP packet
     */
    public final int getSeqNo()
    {
	return seqNo;
    }


    /* ************************************************************** */
    /*          Methods to set and read message attributes            */
    /* ************************************************************** */

    void setSource(SmeAddress s)
    {
	if(s != null) {
	    source = s;
	    if(Debug.dbg)
		Debug.d(this, "setSource", "Source set to "+s, Debug.DBG_4);
	} else if(Debug.dbg) {
	    Debug.d(this, "setSource", "Attempt to set source to null", Debug.DBG_3);
	}
    }
    SmeAddress getSource()
    {
	if(source != null)
	    return new SmeAddress(source.ton, source.npi, source.addr);
	else
	    return null;
    }

    void setDestination(SmeAddress s)
    {
	if(s != null) {
	    destination = s;
	    if(Debug.dbg)
		Debug.d(this, "setDestination", "Dest set to"+s, Debug.DBG_4);
	} else {
	    Debug.d(this, "setDestination", "Attempt to set destination to "
		    + "null", Debug.DBG_3);
	}
    }
    SmeAddress getDestination()
    {
	if(destination != null)
	    return new SmeAddress(destination.ton, destination.npi, destination.addr);
	else
	    return null;
    }

    void setMessageFlags(MsgFlags f)
    {
	if(f != null) {
	    flags = f;
	    if(Debug.dbg)
		Debug.d(this, "setMessageFlags", "Message flags set to "+f, Debug.DBG_4);
	} else {
	    Debug.d(this, "setMessageFlags", "Attempt to set flags to null",
		    Debug.DBG_3);
	}
    }
    void setPriority(boolean b)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.priority = b;
	if(Debug.dbg)
	    Debug.d(this, "setPriority", b, Debug.DBG_4);
    }
    void setRegistered(boolean b)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.registered = b;
	if(Debug.dbg)
	    Debug.d(this, "setRegistered", b, Debug.DBG_4);
    }
    void setReplaceIfPresent(boolean b)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.replace_if_present = b;
	if(Debug.dbg)
	    Debug.d(this, "setReplaceIfPresent", b, Debug.DBG_4);
    }
    void setEsmClass(int c)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.esm_class = c;
	if(Debug.dbg)
	    Debug.d(this, "setEsmClass", c, Debug.DBG_4);
    }
    void setProtocolId(int id)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.protocol = id;
	if(Debug.dbg)
	    Debug.d(this, "setProtocol", id, Debug.DBG_4);
    }
    void setDataCoding(int dc)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.data_coding = dc;
	if(Debug.dbg)
	    Debug.d(this, "setDataCoding", dc, Debug.DBG_4);
    }
    void setDefaultMsg(int id)
    {
	if(flags == null)
	    flags = new MsgFlags();

	flags.default_msg = id;
	if(Debug.dbg)
	    Debug.d(this, "setDefaultMsg", id, Debug.DBG_4);
    }

    MsgFlags getMessageFlags()
    {
	return flags;
    }
    boolean isRegistered()
    {
	return flags.registered;
    }
    boolean isPriority()
    {
	return flags.priority;
    }
    boolean isReplaceIfPresent()
    {
	return flags.replace_if_present;
    }
    int getEsmClass()
    {
	return flags.esm_class;
    }
    int getProtocolId()
    {
	return flags.protocol;
    }
    int getDataCoding()
    {
	return flags.data_coding;
    }
    int getDefaultMsgId()
    {
	return flags.default_msg;
    }

    void setMessageText(String text)
    {
	if(text == null)
	{ message = null; return; }

	if(text.length() < 161) {
	    message = new String(text);
	    if(Debug.dbg)
		Debug.d(this, "setMessageText", text, Debug.DBG_4);
	} else {
	    Debug.d(this, "setMessageText", "Message too long", Debug.DBG_1);
	    throw new SMPPException("SM Text length must be < 160 characters");
	}
    }
    String getMessageText()
    {
	return (message == null) ? null : new String(message);
    }
    int getMessageLen()
    {
	return (message == null) ? 0 : message.length();
    }

    void setServiceType(String type)
    {
	if(type == null)
	{ serviceType = null; return; }

	if(type.length() < 6) {
	    serviceType = new String(type);
	    if(Debug.dbg)
		Debug.d(this, "setServiceType", type, Debug.DBG_4);
	} else {
	    Debug.d(this, "setServiceType", "Service type too long", Debug.DBG_1);
	    throw new SMPPException("Service type must be < 6 characters");
	}
    }
    String getServiceType()
    {
	return (serviceType == null) ? null : new String(serviceType);
    }

    void setDeliveryTime(Date d)
    {
	if(d == null)
	{ deliveryTime = null; return; }

	deliveryTime = d;
	if(Debug.dbg)
	    Debug.d(this, "setDeliveryTime", "Delivery time set to "+d, Debug.DBG_4);
    }
    Date getDeliveryTime()
    {
	return deliveryTime;
    }

    void setExpiryTime(Date d)
    {
	if(d == null)
	{ expiryTime = null; return; }

	expiryTime = d;
	if(Debug.dbg)
	    Debug.d(this, "setExpiryTime", "Expiry time set to "+d, Debug.DBG_4);
    }
    Date getExpiryTime()
    {
	return expiryTime;
    }

    void setFinalDate(Date d)
    {
	if(d == null)
	{ finalDate = null; return; }

	finalDate = d;
	if(Debug.dbg)
	    Debug.d(this, "setFinalDate", "Final date set to "+d, Debug.DBG_4);
    }
    Date getFinalDate()
    {
	return finalDate;
    }

    void setMessageId(int id)
    {
	if(id < 0)
	{ messageId = 0; return; }

	String sid = Integer.toHexString(id);
	if(sid.length() > 9) {
	    Debug.d(this, "setMessageId", "id invalid"+id, Debug.DBG_1);
	    throw new SMPPException("Message Id invalid: range "
		    + "[00000000h - FFFFFFFFh");
	}

	messageId = id;
	if(Debug.dbg)
	    Debug.d(this, "setMessageId", id, Debug.DBG_4);
    }
    int getMessageId()
    {
	return messageId;
    }

    void setMessageStatus(int st)
    {
	messageStatus = st;
	if(Debug.dbg)
	    Debug.d(this, "setMessageStatus", st, Debug.DBG_4);
    }
    int getMessageStatus()
    {
	return messageStatus;
    }

    void setErrorCode(int code)
    {
	errorCode = code; 
	if(Debug.dbg)
	    Debug.d(this, "setErrorCode", code, Debug.DBG_4);
    }
    int getErrorCode()
    {
	return errorCode;
    }

    public String toString()
    {
	return new String("header: " + cmdLen + ", "
		+ Integer.toHexString(cmdId) + ", "
		+ cmdStatus + ", "
		+ Integer.toHexString(seqNo));
    }

    /* ****************** Static helper methods ******************** */

    /** Convert 4 bytes into an integer, assuming MSB first order.
     * @param b1 Most significant byte
     * @param b2 ...
     * @param b3 ...
     * @param b4 Least significant byte
     * @return Integer representation of the bytes
     */
    public static final int bytesToInt(byte b1, byte b2, byte b3, byte b4)
    {
	int x = 0x00000000;

	x |= (((int)b1) << 24);
	x |= (((int)b2) << 16);
	x |= (((int)b3) << 8);
	x |= ((int)b4);
	return x;
    }

    /** Read in a NUL-terminated string from a byte array
     * @param b Byte array to read from
     * @param startPos Position to start from in the array
     * @return A String representation (with NUL byte removed)
     */
    public static final String bytesToCString(byte[] b, int startPos)
    {
	StringBuffer s = new StringBuffer();

	for(int loop=startPos; loop < b.length; loop++) {
	    if(b[loop] == (byte) 0x00)
		break;
	    else
		s.append((char)b[loop]);
	}

	return s.toString();
    }

    /** Read an Integer from an InputStream
     * @param in The InputStream to read from
     * @param len The number of bytes to form the integer from (usually either 1 or 4)
     * @return An integer representation of the <i>len</i> bytes read in
     * @exception java.io.IOException If EOS is reached before <i>len</i> bytes
     * @see java.io.InputStream
     */
    public static final int readInt(InputStream in, int len)
	throws IOException
    {
	int x = 0x00000000;
	int shiftwidth = 0;

	for(int loop=len-1; loop>=0; loop--) {
	    int b = in.read();
	    if(b == -1)
		throw new IOException("End of Input stream before integer read");

	    shiftwidth = 8 * loop;
	    x |= (b << shiftwidth);
	}

	return x;
    }

    /** Read in a NUL-terminated string from an InputStream
     * @param in The InputStream to read from
     * @return A String representation with the NUL byte removed.
     * @exception java.io.IOException If EOS is reached before a NUL byte
     * @see java.io.InputStream
     */
    public static final String readCString(InputStream in)
	throws IOException
    {
	StringBuffer s = new StringBuffer();

	int b = in.read();
	while(b != 0) {
	    if(b == -1)
		throw new IOException("End of Input Stream before NULL byte");

	    s.append((char)b);
	    b = in.read();
	}

	return s.toString();
    }

    /** Read in a string of specified length from an InputStream.
     * The String may contain NUL bytes.
     * @param in The InputStream to read from
     * @param len The number of bytes to read in from the InputStream
     * @return A String of length <i>len</i>
     * @exception java.io.IOException If EOS is reached before a NUL byte
     * @see java.io.InputStream
     */
    public static final String readString(InputStream in, int len)
	throws IOException
    {
	if(len < 1)
	    return null;

	StringBuffer s = new StringBuffer();

	int b;
	do {
	    b = in.read();
	    if(b == -1)
		throw new IOException("End of Input Stream before NULL byte");

	    s.append((char)b);
	} while(s.length() < len);

	return s.toString();
    }


    /** Read in a String of specified length from a byte array.
     * The string may contain NUL bytes
     * @param b The byte array to read from
     * @param startPos The position in the array to start from
     * @param Len The number of bytes to read in to the string
     * @return A String representation of length <i>Len</i>
     */
    public static final String bytesToString(byte[] b, int startPos, int Len)
    {
	StringBuffer s = new StringBuffer();

	for(int loop=startPos; loop < b.length; loop++) {
	    if(s.length() >= Len)
		break;
	    else
		s.append((char)b[loop]);
	}

	return s.toString();
    }

    /** Convert a 4-byte integer to a byte array in MSB first order
     * @param b The array to store the integer in
     * @param offset The position in the array to store the integer in
     * @param num The number to store
     */
    public static final void intToByteArray(byte b[], int offset, int num)
    {
	b[offset]   = (byte) ((num & 0xff000000) >> 24);
	b[offset+1] = (byte) ((num & 0x00ff0000) >> 16);
	b[offset+2] = (byte) ((num & 0x0000ff00) >> 8);
	b[offset+3] = (byte) (num & 0x00000ff);
    }


    /** Get the size (in bytes) of this SMPP Packet
     * @return The size in bytes of this packet
     */
    public int size()
    {
	// An SMPPPacket's size is standard at 16...4 4-byte integers
	return 16;
    }

    /** Write the byte representation of this SMPP packet to an OutputStream
     * @param out The OutputStream to use
     * @exception ie.omk.smpp.SMPPException If an error occurs trying to write the packet.
     * @see java.io.OutputStream
     */
    public void writeTo(OutputStream out)
    {
	try {
	    // Make sure the size is set proper
	    cmdLen = size();

	    writeInt(cmdLen, 4, out);
	    writeInt(cmdId, 4, out);
	    writeInt(cmdStatus, 4, out);
	    writeInt(seqNo, 4, out);
	    if(Debug.dbg)
		Debug.d(this, "writeTo", "Header written to "
			+ out.getClass().getName(), Debug.DBG_4);

	} catch(IOException x) {
	    Debug.d(this, "writeTo", "IOException "+x.getMessage(), Debug.DBG_1);
	    throw new SMPPException("Error writing SMPP header information "
		    + "to output stream");
	}
    }

    /** Write the byte representation of an integer to an OutputStream in MSB furst order
     * @param x The integer to write
     * @param len The number of bytes in this integer (usually either 1 or 4)
     * @param out The OutputStream to write the integer to
     * @exception java.io.IOException If an I/O error occurs.
     * @see java.io.OutputStream
     */
    public static void writeInt(int x, int len, OutputStream out)
	throws IOException
    {
	byte[] b = new byte[len];
	int mask = 0, sw = 0;			// AND mask, shiftwidth

	for(int loop = 0; loop < len; loop++)
	    b[loop] = (byte) 0x00;

	for(int loop = 0; loop < len; loop++) {
	    sw = ((len-1) - loop) * 8;
	    mask = 0x000000ff << sw;
	    b[loop] = (byte) ((x & mask) >>> sw);
	}

	out.write(b);
    }

    /** Write a String to an OutputStream followed by a NUL-byte
     * @param s The string to write
     * @param out The output stream to write to
     * @exception java.io.IOException If an I/O error occurs
     * @see java.io.OutputStream
     */
    public static void writeCString(String s, OutputStream out)
	throws IOException
    {
	if(s == null)
	    s = new String();
	writeString(new String(s + (char)0), out);
    }

    /** Write a String of specified length to an OutputStream
     * @param s The String to write
     * @param len The length of the String to write.  If this is longer than the length of the String, the whole String will be sent.
     * @param out The OutputStream to use
     * @exception java.io.IOException If an I/O error occurs
     * @see java.io.OutputStream
     */
    public static void writeString(String s, int len, OutputStream out)
	throws IOException
    {
	if(s == null)
	    return;

	if(len > s.length())
	    writeString(s, out);
	else
	    writeString(s.substring(0, len), out);
    }

    /** Write a String in it's entirety to an OutputStream
     * @param s The String to write
     * @param out The OutputStream to write to
     * @exception java.io.IOException If an I/O error occurs
     * @see java.io.OutputStream
     */
    public static void writeString(String s, OutputStream out)
	throws IOException
    {
	if(s == null)
	    return;
	byte[] b = s.getBytes();
	out.write(b);
    }

    /** Make a String representing a Date object.
     * Note that the SMPP Protocol defines a string that contains information
     * about the time difference between local time and UTC.  Since the
     * java.util.Date reflects UTC, the time difference will always be set
     * to 00+.
     * @param d The Date to put into a string.
     * @return The string representation as defined by the SMPP protocol.
     */
    public static String makeDateString(Date d)
    {
	if(d == null)
	    return null;

	SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmssS");
	String s = formatter.format(d);
	return new String(s + "00+");
    }

    /** Make a java.util.Date object from an Smpp time string.
     * Time strings returned from the SMSC are assumed to be in the
     * format "YYMMDDhhmmss" where
     * <ul>
     * <li> YY = Year (00 - 99)
     * <li> MM = Month (01 - 12)
     * <li> DD = Day (01 - 31)
     * <li> hh = Hour (00 - 23)
     * <li> mm = minute (00 - 59)
     * <li> ss = second (00 - 59)
     * </ul>
     * @param An SMSC time string of the above format.
     * @return A java.util.Date object representing the time and date given
     */
    public static Date makeDateFromString(String s)
    {
	if(s == null) return null;

	SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
	ParsePosition pos = new ParsePosition(0);
	return formatter.parse(s, pos);
    }


    /** Read an SMPPPacket from an InputStream.  The type of the packet is
     * determined from the Command Id value in the header and the appropriate
     * message class is created, read and returned.
     * @param in The InputStream to read the packet from
     * @return An SMPP message upcast to SMPPPacket.  The actual type can be determined from SMPPPacket.getCommandId()
     * @exception java.io.EOFException If the end of stream is reached
     * @exception java.io.IOException If an I/O error occurs
     * @see java.io.InputStream
     * @see ie.omk.smpp.message.SMPPPacket#getCommandId
     */
    public static SMPPPacket readPacket(InputStream in)
	throws SocketException, IOException
    {
	int omk = 0, cmdLen = 0, cmdId = 0, cmdSt = 0;
	byte b[];

	// Need the command len and command id first...
	b = new byte[16];
	omk = in.read(b, 0, 16);

	// If -1 bytes come back, it's a baaad thing.
	if(omk == -1)
	    throw new EOFException("End of Stream reached.  No data available");

	cmdLen = bytesToInt(b[0], b[1], b[2], b[3]);
	cmdId = bytesToInt(b[4], b[5], b[6], b[7]);
	cmdSt = bytesToInt(b[8], b[9], b[10], b[11]);

	Debug.d(SMPPPacket.class, "readPacket", "Header(len:"
		+ cmdLen
		+ ", id:"
		+ Integer.toHexString(cmdId)
		+ ", status:"
		+ cmdSt, Debug.DBG_2);

	SequenceInputStream bin = new SequenceInputStream(
		new ByteArrayInputStream(b), in);
	switch(cmdId) {
	    case ESME_NACK:
		Debug.d(SMPPPacket.class,
			"readPacket", "GenericNack", Debug.DBG_3);
		return new GenericNack(bin);

	    case ESME_BNDRCV:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindReceiver", Debug.DBG_3);
		return new BindReceiver(bin);

	    case ESME_BNDRCV_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindReceiverResp", Debug.DBG_3);
		return new BindReceiverResp(bin);

	    case ESME_BNDTRN:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindTransmitter", Debug.DBG_3);
		return new BindTransmitter(bin);

	    case ESME_BNDTRN_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "BindTransmitterResp", Debug.DBG_3);
		return new BindTransmitterResp(bin);

	    case ESME_UBD:
		Debug.d(SMPPPacket.class,
			"readPacket", "Unbind", Debug.DBG_3);
		return new Unbind(bin);

	    case ESME_UBD_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "UnbindResp", Debug.DBG_3);
		return new UnbindResp(bin);

	    case ESME_SUB_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitSM", Debug.DBG_3);
		return new SubmitSM(bin);

	    case ESME_SUB_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitSMResp", Debug.DBG_3);
		return new SubmitSMResp(bin);

	    case ESME_SUB_MULTI:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitMulti", Debug.DBG_3);
		return new SubmitMulti(bin);

	    case ESME_SUB_MULTI_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "SubmitMultiResp", Debug.DBG_3);
		return new SubmitMultiResp(bin);

	    case SMSC_DELIVER_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "DeliverSm", Debug.DBG_3);
		return new DeliverSM(bin);

	    case SMSC_DELIVER_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "DeliverSMResp", Debug.DBG_3);
		return new DeliverSMResp(bin);

	    case ESME_QUERY_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "QuerySM", Debug.DBG_3);
		return new QuerySM(bin);

	    case ESME_QUERY_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "QuerySMResp", Debug.DBG_3);
		return new QuerySMResp(bin);

	    case ESME_QUERY_LAST_MSGS:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryLastMsgs", Debug.DBG_3);
		return new QueryLastMsgs(bin);

	    case ESME_QUERY_LAST_MSGS_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryLastMsgsResp", Debug.DBG_3);
		return new QueryLastMsgsResp(bin);

	    case ESME_QUERY_MSG_DETAILS:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryMsgDetails", Debug.DBG_3);
		return new QueryMsgDetails(bin);

	    case ESME_QUERY_MSG_DETAILS_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "QueryMsgDetailsResp", Debug.DBG_3);
		return new QueryMsgDetailsResp(bin);

	    case ESME_CANCEL_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "CancelSM", Debug.DBG_3);
		return new CancelSM(bin);

	    case ESME_CANCEL_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "CancelSMResp", Debug.DBG_3);
		return new CancelSMResp(bin);

	    case ESME_REPLACE_SM:
		Debug.d(SMPPPacket.class,
			"readPacket", "ReplaceSM", Debug.DBG_3);
		return new ReplaceSM(bin);

	    case ESME_REPLACE_SM_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "ReplaceSMResp", Debug.DBG_3);
		return new ReplaceSMResp(bin);

	    case ESME_QRYLINK:
		Debug.d(SMPPPacket.class,
			"readPacket", "EnquireLink", Debug.DBG_3);
		return new EnquireLink(bin);

	    case ESME_QRYLINK_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "EnquireLinkResp", Debug.DBG_3);
		return new EnquireLinkResp(bin);

	    case ESME_PARAM_RETRIEVE:
		Debug.d(SMPPPacket.class,
			"readPacket", "ParamRetrieve", Debug.DBG_3);
		return new ParamRetrieve(bin);

	    case ESME_PARAM_RETRIEVE_RESP:
		Debug.d(SMPPPacket.class,
			"readPacket", "ParamRetrieveResp", Debug.DBG_3);
		return new ParamRetrieveResp(bin);

	    default:
		Debug.d(SMPPPacket.class,
			"readPacket", "Unknown Packet", Debug.DBG_3);
		throw new SMPPException("Unidentified Packet on input stream. "
			+ "id=" + String.valueOf(cmdId));

	}
    }


    public static final String getGsmErr(int code)
    {
	int ptr = 0;
	final int total = 14;

	// If this occurs, then its the programmer's fault..
	if (GsmStrings.length < total)
	    return null;

	// These gotta be in the right order, otherwise it'll return the
	// wrong string for the error code...
	// XXX Put these in a Hashtable.
	if (code == GSM_ERR_NONE)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_P_UNKNOWN)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_P_PROVISION)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_BARRED)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_P_CUG)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_MSSUPPORT)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_MSERROR)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_SUPPORT)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_MEMCAP)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_ABSENT)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_ABSENT_DETACHED)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_ABSENT_PAGEFAIL)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_SUPPORT_ROAMING)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;
	if (code == GSM_ERR_T_SYSTEM)
	    return new String(GsmStrings[ptr]);
	else
	    ptr++;

	return null;
    }
}
