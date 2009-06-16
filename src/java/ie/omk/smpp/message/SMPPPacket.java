package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.tlv.TLVTable;
import ie.omk.smpp.message.tlv.Tag;
import ie.omk.smpp.util.AlphabetEncoding;
import ie.omk.smpp.util.BinaryEncoding;
import ie.omk.smpp.util.EncodingFactory;
import ie.omk.smpp.util.MessageEncoding;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.util.SMPPIO;
import ie.omk.smpp.version.SMPPVersion;

import java.io.OutputStream;
import java.util.Date;

/**
 * This is the abstract class that all SMPP messages are inherited from.
 * 
 * @author Oran Kelly
 * @version 1.0
 */
public abstract class SMPPPacket {
    /** Command Id: Negative Acknowledgement */
    public static final int GENERIC_NACK = 0x80000000;

    /** Command Id: Bind Receiver */
    public static final int BIND_RECEIVER = 0x00000001;

    /** Command Id: Bind Receiver Response */
    public static final int BIND_RECEIVER_RESP = 0x80000001;

    /** Command Id: Bind transmitter */
    public static final int BIND_TRANSMITTER = 0x00000002;

    /** Command Id: Bind transmitter response */
    public static final int BIND_TRANSMITTER_RESP = 0x80000002;

    /** Command Id: Query message */
    public static final int QUERY_SM = 0x00000003;

    /** Command Id: Query message response */
    public static final int QUERY_SM_RESP = 0x80000003;

    /** Command Id: Submit message */
    public static final int SUBMIT_SM = 0x00000004;

    /** Command Id: Submit message response */
    public static final int SUBMIT_SM_RESP = 0x80000004;

    /** Command Id: Deliver Short message */
    public static final int DELIVER_SM = 0x00000005;

    /** Command Id: Deliver message response */
    public static final int DELIVER_SM_RESP = 0x80000005;

    /** Command Id: Unbind */
    public static final int UNBIND = 0x00000006;

    /** Command Id: Unbind response */
    public static final int UNBIND_RESP = 0x80000006;

    /** Command Id: Replace message */
    public static final int REPLACE_SM = 0x00000007;

    /** Command Id: replace message response */
    public static final int REPLACE_SM_RESP = 0x80000007;

    /** Command Id: Cancel message */
    public static final int CANCEL_SM = 0x00000008;

    /** Command Id: Cancel message response */
    public static final int CANCEL_SM_RESP = 0x80000008;

    /** Command Id: Bind transceiver */
    public static final int BIND_TRANSCEIVER = 0x00000009;

    /** Command Id: Bind transceiever response. */
    public static final int BIND_TRANSCEIVER_RESP = 0x80000009;

    /** Command Id: Outbind. */
    public static final int OUTBIND = 0x0000000b;

    /** Command Id: Enquire Link */
    public static final int ENQUIRE_LINK = 0x00000015;

    /** Command Id: Enquire link respinse */
    public static final int ENQUIRE_LINK_RESP = 0x80000015;

    /** Command Id: Submit multiple messages */
    public static final int SUBMIT_MULTI = 0x00000021;

    /** Command Id: Submit multi response */
    public static final int SUBMIT_MULTI_RESP = 0x80000021;

    /** Command Id: Parameter retrieve */
    public static final int PARAM_RETRIEVE = 0x00000022;

    /** Command Id: Paramater retrieve response */
    public static final int PARAM_RETRIEVE_RESP = 0x80000022;

    /** Command Id: Query last messages */
    public static final int QUERY_LAST_MSGS = 0x00000023;

    /** Command Id: Query last messages response */
    public static final int QUERY_LAST_MSGS_RESP = 0x80000023;

    /** Command Id: Query message details */
    public static final int QUERY_MSG_DETAILS = 0x00000024;

    /** Command Id: Query message details response */
    public static final int QUERY_MSG_DETAILS_RESP = 0x80000024;

    /** Command Id: alert notification. */
    public static final int ALERT_NOTIFICATION = 0x00000102;

    /** Command Id: Data message. */
    public static final int DATA_SM = 0x00000103;

    /** Command Id: Data message response. */
    public static final int DATA_SM_RESP = 0x80000103;

    /** Message state at Smsc: En route */
    public static final int SM_STATE_EN_ROUTE = 1;

    /** Message state at Smsc: Delivered (final) */
    public static final int SM_STATE_DELIVERED = 2;

    /** Message state at Smsc: Expired (final) */
    public static final int SM_STATE_EXPIRED = 3;

    /** Message state at Smsc: Deleted (final) */
    public static final int SM_STATE_DELETED = 4;

    /** Message state at Smsc: Undeliverable (final) */
    public static final int SM_STATE_UNDELIVERABLE = 5;

    /** Message state at Smsc: Accepted */
    public static final int SM_STATE_ACCEPTED = 6;

    /** Message state at Smsc: Invalid message (final) */
    public static final int SM_STATE_INVALID = 7;

    /** Esm class: Mobile Terminated; Normal delivery, no address swapping */
    public static final int SMC_MT = 1;

    /** Esm class: Mobile originated */
    public static final int SMC_MO = 2;

    /** Esm class: Mobile Originated / Terminated */
    public static final int SMC_MOMT = 3;

    /** Esm class: Delivery receipt, no address swapping */
    public static final int SMC_RECEIPT = 4;

    /** Esm class: Predefined message */
    public static final int SMC_DEFMSG = 8;

    /** Esm class: Normal delivery , address swapping on */
    public static final int SMC_LOOPBACK_RECEIPT = 16;

    /** Esm class: Delivery receipt, address swapping on */
    public static final int SMC_RECEIPT_SWAP = 20;

    /** Esm class: Store message, do not send to Kernel */
    public static final int SMC_STORE = 32;

    /** Esm class: Store message and send to kernel */
    public static final int SMC_STORE_FORWARD = 36;

    /** Esm class: Distribution submission */
    public static final int SMC_DLIST = 64;

    /** Esm class: Multiple recipient submission */
    public static final int SMC_MULTI = 128;

    /** Esm class: Distribution list and multiple recipient submission */
    public static final int SMC_CAS_DL = 256;

    /** Esm class: Escalated message FFU */
    public static final int SMC_ESCALATED = 512;

    /** Esm class: Submit with replace message */
    public static final int SMC_SUBMIT_REPLACE = 1024;

    /** Esm class: Memory capacity error */
    public static final int SMC_MCE = 2048;

    /** Esme error code: No error */
    public static final int ESME_ROK = 0;

    /**
     * Version of this packet. This object controls valid settings for field
     * values.
     */
    protected SMPPVersion version = SMPPVersion.getDefaultVersion();

    /** Command ID. */
    protected int commandId;

    /** Command status. */
    protected int commandStatus;

    /** Packet sequence number. */
    protected int sequenceNum;

    /*
     * Almost all packets use one or more of these. These attributes were all
     * stuck in here for easier maintenance... instead of altering 5 different
     * packets, just alter it here!! Special cases like SubmitMulti and
     * QueryMsgDetailsResp maintain their own destination tables. Any packets
     * that wish to use these attribs should override the appropriate methods
     * defined below to be public and just call super.method()
     */

    /** Source address */
    protected Address source;

    /** Destination address */
    protected Address destination;

    /** The short message data */
    protected byte[] message;

    /** Service type for this msg */
    protected String serviceType;

    /** Scheduled delivery time */
    protected SMPPDate deliveryTime;

    /** Scheduled expiry time */
    protected SMPPDate expiryTime;

    /** Date of reaching final state */
    protected SMPPDate finalDate;

    /** Smsc allocated message Id */
    protected String messageId;

    /** Status of message */
    protected int messageStatus;

    /** Error associated with message */
    protected int errorCode;

    /** Message priority. */
    protected int priority;

    /** Registered delivery. */
    protected int registered;

    /** Replace if present. */
    protected int replaceIfPresent;

    /** ESM class. */
    protected int esmClass;

    /** GSM protocol ID. */
    protected int protocolID;

    /** Alphabet to use to encode this message's text. */
    private MessageEncoding encoding = EncodingFactory.getInstance().getDefaultAlphabet();

    /** GSM data coding (see GSM 03.38). */
    protected int dataCoding = encoding.getDataCoding();

    /** Default message number. */
    protected int defaultMsg;

    /** Optional parameter table. */
    protected TLVTable tlvTable = new TLVTable();

    /**
     * Create a new SMPPPacket with specified Id.
     * 
     * @param id
     *            Command Id value
     */
    protected SMPPPacket(int id) {
        this(id, 0);
    }

    /**
     * Create a new SMPPPacket with specified Id and sequence number.
     * 
     * @param id
     *            Command Id value
     * @param seqNum
     *            Command Sequence number
     */
    protected SMPPPacket(int id, int seqNum) {
        this.commandId = id;
        this.sequenceNum = seqNum;
    }

    protected SMPPPacket(int id, SMPPVersion version) {
        this.commandId = id;
        this.version = version;
    }

    protected SMPPPacket(int id, int seqNum, SMPPVersion version) {
        this.commandId = id;
        this.sequenceNum = seqNum;
        this.version = version;
    }

    /**
     * Get the version handler in use for this packet.
     * 
     * @see ie.omk.smpp.version.SMPPVersion
     */
    public SMPPVersion getVersion() {
        return version;
    }

    /**
     * Is this command a request packet.
     * @return <code>true</code> if this packet is an SMPP request, <code>
     * false</code> if it is a response.
     */
    public boolean isRequest() {
        return false;
    }
    
    /**
     * Set the version handler for this packet. If <code>null</code> is passed
     * in as the version, the default version will be used.
     * 
     * @param version
     *            the version handler to use.
     * @see ie.omk.smpp.version.SMPPVersion#getDefaultVersion
     */
    public void setVersion(SMPPVersion version) {
        if (version == null) {
            this.version = SMPPVersion.getDefaultVersion();
        } else {
            this.version = version;
        }
    }

    /**
     * Return the number of bytes this packet would be encoded as to an
     * OutputStream.
     * 
     * @return The size in bytes of the packet
     * @deprecated
     */
    public final int getCommandLen() {
        // TODO stop overriding this deprecated method.
        return getLength();
    }

    /**
     * Get the number of bytes this packet would be encoded as. This returns the
     * sum of the size of the header (always 16), the packet's body and all
     * optional parameters.
     * 
     * @return the number of bytes this packet would encode as.
     */
    public final int getLength() {
        return 16 + getBodyLength() + tlvTable.getLength();
    }

    /**
     * Get the number of bytes the body of this packet would encode as. This
     * method should only return the number of bytes the fields in the mandatory
     * parameters section of the packet would encode as. The total size of the
     * packet then is 16 (header length) + getBodyLength() + SUM(foreach
     * optionalParameter: getLength()).
     */
    public abstract int getBodyLength();

    /**
     * Get the Command Id of this SMPP packet.
     * 
     * @return The Command Id of this packet
     */
    public int getCommandId() {
        return commandId;
    }

    /**
     * Get the status of this packet.
     * 
     * @return The error status of this packet (only relevent to Response
     *         packets)
     */
    public int getCommandStatus() {
        return this.commandStatus;
    }

    /**
     * Get the sequence number of this packet.
     * 
     * @return The sequence number of this SMPP packet
     */
    public int getSequenceNum() {
        return this.sequenceNum;
    }

    /**
     * Set the sequence number of this packet.
     */
    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    /**
     * Set the source address..
     */
    public void setSource(Address s) throws InvalidParameterValueException {
        if (s != null) {
            if (version.validateAddress(s)) {
                this.source = s;
            } else {
                throw new InvalidParameterValueException("Bad source address.",
                        s);
            }
        } else {
            this.source = null;
        }
    }

    /**
     * Get the source address.
     * @return The source address or null if it is not set.
     */
    public Address getSource() {
        return source;
    }

    /**
     * Set the destination address.
     */
    public void setDestination(Address s) {
        if (s != null) {
            if (version.validateAddress(s)) {
                this.destination = s;
            } else {
                throw new InvalidParameterValueException(
                        "Bad destination address.", s);
            }
        } else {
            this.destination = null;
        }
    }

    /**
     * Get the destination address.
     * @return The destination address or null if it is not set.
     */
    public Address getDestination() {
        return destination;
    }

    /**
     * Set the 'priority' flag.
     * 
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If <code>p</code> is not a valid value for the priority
     *             flag.
     */
    public void setPriority(int p) throws InvalidParameterValueException {
        if (version.validatePriorityFlag(p)) {
            this.priority = p;
        } else {
            throw new InvalidParameterValueException("Bad priority flag value",
                    p);
        }
    }

    /**
     * Set 'registered delivery' flag.
     * 
     * @deprecated
     */
    public void setRegistered(boolean b) {
        this.registered = b ? 1 : 0;
    }

    /**
     * Set 'registered delivery' flag.
     * 
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If <code>r</code> is not a valid value for the registered
     *             delivery flag.
     */
    public void setRegistered(int r) throws InvalidParameterValueException {
        if (version.validateRegisteredDelivery(r)) {
            this.registered = r;
        } else {
            throw new InvalidParameterValueException(
                    "Bad registered delivery flag value", r);
        }
    }

    /**
     * Set 'replace if present'.
     * 
     * @deprecated
     */
    public void setReplaceIfPresent(boolean b) {
        this.replaceIfPresent = b ? 1 : 0;
    }

    /**
     * Set 'replace if present' flag.
     * 
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If <code>rip</code> is not a valid value for the replace if
     *             present flag.
     */
    public void setReplaceIfPresent(int rip)
            throws InvalidParameterValueException {
        if (version.validateReplaceIfPresent(rip)) {
            this.replaceIfPresent = rip;
        } else {
            throw new InvalidParameterValueException(
                    "Bad replace if present flag value", rip);
        }
    }

    /**
     * Set the esm class of the message.
     * 
     * @see ie.omk.smpp.util.GSMConstants
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the value passed is not a valid ESM class.
     */
    public void setEsmClass(int c) throws InvalidParameterValueException {
        if (version.validateEsmClass(c)) {
            this.esmClass = c;
        } else {
            throw new InvalidParameterValueException("Bad ESM class", c);
        }
    }

    /**
     * Set the protocol Id in the message flags.
     * 
     * @see ie.omk.smpp.util.GSMConstants
     * @deprecated ie.omk.smpp.message.SMPPPacket#setProtocolID
     */
    public void setProtocolId(int id) {
        this.protocolID = id;
    }

    /**
     * Set the GSM protocol ID.
     * 
     * @see ie.omk.smpp.util.GSMConstants
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the protocol ID supplied is invalid.
     */
    public void setProtocolID(int id) throws InvalidParameterValueException {
        if (version.validateProtocolID(id)) {
            this.protocolID = id;
        } else {
            throw new InvalidParameterValueException("Bad Protocol ID", id);
        }
    }

    /**
     * Set the GSM data coding of the message. This will also set the internal
     * encoding type of the message to match the DCS value. It will <b>not </b>
     * set the encoding type if the DCS is <code>0</code> as this code is
     * reserved to represent the default SMSC encoding type, which is dependent
     * on the SMSC implementation.
     * 
     * @see ie.omk.smpp.util.GSMConstants
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the data coding supplied is invalid.
     */
    public void setDataCoding(int dc) throws InvalidParameterValueException {
        if (version.validateDataCoding(dc)) {
            this.dataCoding = dc;
            if (dc > 0) {
                this.encoding = EncodingFactory.getInstance().getEncoding(dc);
            }
        } else {
            throw new InvalidParameterValueException("Bad data coding", dc);
        }
    }

    /**
     * Set the default message id in the message flags.
     */
    public void setDefaultMsg(int id) throws InvalidParameterValueException {
        if (version.validateDefaultMsg(id)) {
            this.defaultMsg = id;
        } else {
            throw new InvalidParameterValueException(
                    "Default message ID out of range", id);
        }
    }

    /**
     * Check is the message registered.
     * 
     * @deprecated
     */
    public boolean isRegistered() {
        return this.registered > 0;
    }

    /**
     * Get the 'registered' flag for the message.
     */
    public int getRegistered() {
        return registered;
    }

    /**
     * Check is the message submitted as priority.
     * 
     * @deprecated
     */
    public boolean isPriority() {
        return (this.priority == 0) ? false : true;
    }

    /**
     * Get the priority flag for the message.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Check if the message should be replaced if it is already present.
     * 
     * @deprecated
     */
    public boolean isReplaceIfPresent() {
        return this.replaceIfPresent > 0;
    }

    /**
     * Get the replace if present flag for the message.
     */
    public int getReplaceIfPresent() {
        return replaceIfPresent;
    }

    /**
     * Get the ESM class of the message.
     */
    public int getEsmClass() {
        return this.esmClass;
    }

    /**
     * Get the GSM protocol Id of the message.
     * 
     * @deprecated getProtocolID
     */
    public int getProtocolId() {
        return this.protocolID;
    }

    /**
     * Get the GSM protocol ID of the message.
     */
    public int getProtocolID() {
        return this.protocolID;
    }

    /**
     * Get the data coding.
     */
    public int getDataCoding() {
        return this.dataCoding;
    }

    /**
     * Get the default message to use.
     * 
     * @deprecated
     */
    public int getDefaultMsgId() {
        return this.defaultMsg;
    }

    /**
     * Get the default message to use.
     */
    public int getDefaultMsg() {
        return this.defaultMsg;
    }

    /**
     * Set the text of the message. This method sets the message text encoded
     * using the current alphabet for this message. The default alphabet to use
     * is obtained using
     * {@link ie.omk.smpp.util.EncodingFactory#getDefaultAlphabet}. If, at some
     * point, the encoding for the message has been altered to be one other than
     * a sub-class of {@link ie.omk.smpp.util.AlphabetEncoding} then calls to
     * this method will reset the encoding back to the default. The maximum
     * length of the message is determined by the SMPP version in use. Calling
     * this method affects the data_coding value.
     * 
     * @param text
     *            The short message text.
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the message text is too long.
     * @see ie.omk.smpp.util.EncodingFactory
     * @see ie.omk.smpp.util.DefaultAlphabetEncoding
     */
    public void setMessageText(String text)
            throws InvalidParameterValueException {
        if (!(encoding instanceof AlphabetEncoding)) {
            encoding = EncodingFactory.getInstance().getDefaultAlphabet();
        }

        AlphabetEncoding a = (AlphabetEncoding) encoding;
        setMessage(a.encodeString(text), a);
    }

    /**
     * Set the text of the message. This method sets the message text encoded
     * using the SMS alphabet <code>alphabet</code>. The
     * AlphabetEncoding.getDataCoding value will be used to set the data_coding
     * field.
     * 
     * @param text
     *            The short message text.
     * @param alphabet
     *            The SMS alphabet to use.
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the message text is too long.
     * @see ie.omk.smpp.util.AlphabetEncoding
     * @see ie.omk.smpp.util.AlphabetEncoding#getDataCoding
     * @see ie.omk.smpp.util.DefaultAlphabetEncoding
     */
    public void setMessageText(String text, AlphabetEncoding alphabet)
            throws InvalidParameterValueException {
        if (alphabet == null) {
            throw new NullPointerException("Alphabet cannot be null");
        }

        this.setMessage(alphabet.encodeString(text), alphabet);
    }

    /**
     * Set the message data. The data will be copied from the supplied byte
     * array into an internal one.
     * 
     * @param message
     *            The byte array to take message data from.
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the message data is too long.
     */
    public void setMessage(byte[] message)
            throws InvalidParameterValueException {
        this.setMessage(message, 0, message.length, null);
    }

    /**
     * Set the message data. The data will be copied from the supplied byte
     * array into an internal one.
     * 
     * @param message
     *            The byte array to take message data from.
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the message data is too long.
     */
    public void setMessage(byte[] message, MessageEncoding encoding)
            throws InvalidParameterValueException {
        this.setMessage(message, 0, message.length, encoding);
    }

    /**
     * Set the message data. The data will be copied from the supplied byte
     * array into an internal one. If <i>encoding </i> is not null, the
     * data_coding field will be set using the value returned by
     * MessageEncoding.getDataCoding.
     * 
     * @param message
     *            The byte array to take message data from.
     * @param start
     *            The index the message data begins at.
     * @param len
     *            The length of the message data.
     * @param encoding
     *            The encoding object representing the type of data in the
     *            message. If null, uses ie.omk.smpp.util.BinaryEncoding.
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the message data is too long.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             if start or len is less than zero or if the byte array length
     *             is less than <code>start +
     * len</code>.
     */
    public void setMessage(byte[] message, int start, int len,
            MessageEncoding encoding) throws InvalidParameterValueException {

        int dcs = -1;

        // encoding should never be null, but for resilience, we check it here
        // and default back to binary encoding if none is found.
        if (encoding == null) {
            encoding = new BinaryEncoding();
        }

        dcs = encoding.getDataCoding();

        if (message != null) {
            if ((start < 0) || (len < 0) || message.length < (start + len)) {
                throw new ArrayIndexOutOfBoundsException(
                        "Not enough bytes in array");
            }

            int encodedLength = len;
            int encodingLength = encoding.getEncodingLength();
            if (encodingLength != 8) {
                encodedLength = (len * encodingLength) / 8;
            }

            if (encodedLength > version.getMaxLength(SMPPVersion.MESSAGE_PAYLOAD)) {
                throw new InvalidParameterValueException("Message is too long",
                        message);
            }

            this.message = new byte[len];
            System.arraycopy(message, start, this.message, 0, len);
            this.dataCoding = dcs;
        } else {
            this.message = null;
        }
    }

    /**
     * Get the message data. This method returns a <i>copy </i> of the binary
     * message data.
     * 
     * @return A byte array copy of the message data. May be null.
     */
    public byte[] getMessage() {
        byte[] b = null;
        if (this.message != null) {
            b = new byte[this.message.length];
            System.arraycopy(this.message, 0, b, 0, b.length);
        }
        return b;
    }

    /**
     * Get the text of the message. The message will be decoded according to the
     * current encoding of the message (that is, according to it's DCS value).
     * If the current encoding is not some form of text encoding (that is, the
     * DCS indicates a binary encoding), <code>null</code> will be returned.
     * 
     * @return The text of the message, or <code>null</code> if the message is
     *         not text.
     * @see #getMessageText(ie.omk.smpp.util.AlphabetEncoding)
     * @see #setMessageText(java.lang.String, ie.omk.smpp.util.AlphabetEncoding)
     */
    public String getMessageText() {
        if (encoding instanceof AlphabetEncoding) {
            return ((AlphabetEncoding) encoding).decodeString(this.message);
        } else {
            return null;
        }
    }

    /**
     * Get the text of the message. Never returns null.
     * 
     * @param enc
     *            The alphabet to use to decode the message bytes.
     * @return The text of the message. Never returns null.
     * @see ie.omk.smpp.util.AlphabetEncoding
     */
    public String getMessageText(AlphabetEncoding enc) {
        return enc.decodeString(this.message);
    }

    /**
     * Get the number of octets in the message payload.
     * 
     * @return The number of octets (bytes) in the message payload.
     */
    public int getMessageLen() {
        return (message == null) ? 0 : message.length;
    }

    /**
     * Set the service type.
     * 
     * @param type
     *            The service type.
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             if the service type is too long.
     */
    public void setServiceType(String type)
            throws InvalidParameterValueException {
        if (type != null) {
            if (version.validateServiceType(type)) {
                this.serviceType = type;
            } else {
                throw new InvalidParameterValueException("Bad service type",
                        type);
            }
        } else {
            this.serviceType = null;
        }
    }

    /** Get the service type. */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Set the scheduled delivery time for the short message.
     * 
     * @param d
     *            The date and time the message should be delivered.
     */
    public void setDeliveryTime(SMPPDate d) {
        this.deliveryTime = d;
    }

    /**
     * Set the scheduled delivery time for the short message.
     * 
     * @param d
     *            The date and time the message should be delivered.
     */
    public void setDeliveryTime(Date d) {
        this.deliveryTime = new SMPPDate(d);
    }

    /**
     * Get the current value of the scheduled delivery time for the short
     * message.
     */
    public SMPPDate getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * Set the expiry time of the message. If the message is not delivered by
     * time 'd', it will be cancelled and never delivered to it's destination.
     * 
     * @param d
     *            the date and time the message should expire.
     */
    public void setExpiryTime(SMPPDate d) {
        expiryTime = d;
    }

    /**
     * Set the expiry time of the message. If the message is not delivered by
     * time 'd', it will be cancelled and never delivered to it's destination.
     */
    public void setExpiryTime(Date d) {
        expiryTime = new SMPPDate(d);
    }

    /**
     * Get the current expiry time of the message.
     */
    public SMPPDate getExpiryTime() {
        return expiryTime;
    }

    /**
     * Set the final date of the message. The final date is the date and time
     * that the message reached it's final destination.
     * 
     * @param d
     *            the date the message was delivered.
     */
    public void setFinalDate(SMPPDate d) {
        finalDate = d;
    }

    /**
     * Set the final date of the message. The final date is the date and time
     * that the message reached it's final destination.
     * 
     * @param d
     *            the date the message was delivered.
     */
    public void setFinalDate(Date d) {
        this.finalDate = new SMPPDate(d);
    }

    /**
     * Get the final date of the message.
     */
    public SMPPDate getFinalDate() {
        return finalDate;
    }

    /**
     * Set the message Id. Each submitted short message is assigned an Id by the
     * SMSC which is used to uniquely identify it. SMPP v3.3 message Ids are
     * hexadecimal numbers up to 9 characters long. This gives them a range of
     * 0x0 - 0xffffffff.
     * <p>
     * SMPP v3.4 Ids, on the other hand, are opaque objects represented as
     * C-Strings assigned by the SMSC and can be up to 64 characters (plus 1
     * nul-terminator).
     * 
     * @param id
     *            The message's id.
     * @throws ie.omk.smpp.message.InvalidParameterValueException
     *             If the message ID is invalid.
     */
    public void setMessageId(String id) throws InvalidParameterValueException {
        if (id != null) {
            if (version.validateMessageId(id)) {
                this.messageId = id;
            } else {
                throw new InvalidParameterValueException("Bad message Id", id);
            }
        } else {
            this.messageId = null;
        }
    }

    /**
     * Get the message id.
     */
    public String getMessageId() {
        return this.messageId;
    }

    /**
     * Set the message status. The message status (or message state) describes
     * the current state of the message at the SMSC. There are a number of
     * states defined in the SMPP specification.
     * 
     * @param st
     *            The message status.
     * @see ie.omk.smpp.util.PacketStatus
     */
    public void setMessageStatus(int st) throws InvalidParameterValueException {
        if (version.validateMessageState(st)) {
            this.messageStatus = st;
        } else {
            throw new InvalidParameterValueException(
                    "Invalid message state", st);
        }
    }

    /**
     * Get the message status.
     */
    public int getMessageStatus() {
        return this.messageStatus;
    }

    /**
     * Set the error code.
     * 
     * @param code
     *            The error code.
     */
    public void setErrorCode(int code) throws InvalidParameterValueException {
        if (version.validateErrorCode(code)) {
            errorCode = code;
        } else {
            throw new InvalidParameterValueException("Invalid error code", code);
        }
    }

    /**
     * Get the error code.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Get the optional parameter (TLV) table.
     * 
     * @see ie.omk.smpp.message.tlv.TLVTable
     */
    public TLVTable getTLVTable() {
        return tlvTable;
    }

    /**
     * Set the optional parameter (TLV) table. This method discards the entire
     * optional paramter table and replaces it with <code>table</code>. The
     * discarded table is returned. If <code>null</code> is passed in, a new,
     * empty TLVTable object will be created.
     * 
     * @see ie.omk.smpp.message.tlv.TLVTable
     * @return the old tlvTable.
     */
    public TLVTable setTLVTable(TLVTable table) {
        TLVTable t = this.tlvTable;
        if (table == null) {
            this.tlvTable = new TLVTable();
        } else {
            this.tlvTable = table;
        }

        return t;
    }

    /**
     * Set an optional parameter. This is a convenience method and merely calls
     * {@link ie.omk.smpp.message.tlv.TLVTable#set}on this message's optional
     * parameter table.
     * 
     * @param tag
     *            the tag of the parameter to set.
     * @param value
     *            the value object to set.
     * @throws ie.omk.smpp.message.tlv.BadValueTypeException
     *             if the type of <code>value</code> is incorrect for the
     *             <code>tag</code>.
     * @return the previous value of the parameter, or null if it was unset.
     */
    public Object setOptionalParameter(Tag tag, Object value) {
        return tlvTable.set(tag, value);
    }

    /**
     * Get an optional parameter. This is a convenience method and merely calls
     * {@link ie.omk.smpp.message.tlv.TLVTable#get}on this message's optional
     * parameter table.
     * 
     * @param tag
     *            the tag of the parameter value to get.
     */
    public Object getOptionalParameter(Tag tag) {
        return tlvTable.get(tag);
    }

    /**
     * Check if a particular optional parameter is set. This is a convenience
     * method and merely calls {@link ie.omk.smpp.message.tlv.TLVTable#isSet}on
     * this message's optional parameter table.
     * 
     * @param tag
     *            the tag of the parameter to check.
     * @return true if the parameter is set, false if it is not.
     */
    public boolean isSet(Tag tag) {
        return tlvTable.isSet(tag);
    }

    /**
     * Set the alphabet encoding and dcs value for this message. The data_coding
     * (dcs) value of this message will be set to the return value of
     * {@link MessageEncoding#getDataCoding}.
     * 
     * @param enc
     *            The alphabet to use. If null, use DefaultAlphabetEncoding.
     * @see ie.omk.smpp.util.AlphabetEncoding
     * @see ie.omk.smpp.util.DefaultAlphabetEncoding
     */
    public void setAlphabet(AlphabetEncoding enc) {
        if (enc == null) {
            this.encoding = EncodingFactory.getInstance().getDefaultAlphabet();
        } else {
            this.encoding = enc;
        }

        this.dataCoding = enc.getDataCoding();
    }

    /**
     * Set the alphabet encoding for this message with an alternate dcs.
     * <code>enc</code> will be used to encode the message but
     * <code>dcs</code> will be used as the data coding value. This method is
     * useful when the SMSC uses an alternate value to those built-in to the
     * smppapi.
     * @param enc The alphabet encoding to use to encode messages. If
     * <tt>null</tt>, the encoding returned by
     * {@link EncodingFactory#getDefaultAlphabet()} will be used.
     * @param dcs The data coding value to use.
     */
    public void setAlphabet(AlphabetEncoding enc, int dcs) {
        if (enc == null) {
            this.encoding = EncodingFactory.getInstance().getDefaultAlphabet();
        } else {
            this.encoding = enc;
        }
        this.dataCoding = dcs;
    }

    /**
     * Set the message encoding handler class for this packet.
     */
    public void setMessageEncoding(MessageEncoding enc) {
        if (enc == null) {
            this.encoding = EncodingFactory.getInstance().getDefaultAlphabet();
        } else {
            this.encoding = enc;
        }
    }

    /**
     * Get the current message encoding object.
     */
    public MessageEncoding getMessageEncoding() {
        return this.encoding;
    }

    /**
     * Return a String representation of this packet. This method does not
     * return any value which is useful programatically...it returns a
     * description of the packet's header as follows: <br>
     * <code>"SMPP(l:[len], c:[commandId], s:[status], n:[sequence])"</code>
     */
    public String toString() {
        return new StringBuffer("SMPP(l:").append(
                Integer.toString(getLength())).append(", c:0x").append(
                Integer.toHexString(commandId)).append(", s:").append(
                Integer.toString(commandStatus)).append(", n:").append(
                Integer.toString(sequenceNum)).append(")").toString();
    }

    /**
     * Encode the body of the SMPP Packet to the output stream. Sub classes
     * should override this method to output their packet-specific fields. This
     * method is called from SMPPPacket.writeTo(java.io.OutputStream) to encode
     * the message.
     * 
     * @param out
     *            The output stream to write to.
     * @throws java.io.IOException
     *             if there's an error writing to the output stream.
     */
    protected void encodeBody(OutputStream out) throws java.io.IOException {
        // some packets ain't got a body...provide a default adapter instead of
        // making it abstract.
    }

    /**
     * Write the byte representation of this SMPP packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to use
     * @throws java.io.IOException
     *             if there's an error writing to the output stream.
     */
    public final void writeTo(OutputStream out) throws java.io.IOException {
        this.writeTo(out, true);
    }

    /**
     * Write the byte representation of this SMPP packet to an OutputStream
     * 
     * @param out
     *            The OutputStream to use
     * @param withOptional
     *            true to send optional parameters over the link, false to only
     *            write the mandatory parameters.
     * @throws java.io.IOException
     *             if there's an error writing to the output stream.
     */
    public final void writeTo(OutputStream out, boolean withOptional)
            throws java.io.IOException {
        int commandLen = getLength();

        SMPPIO.writeInt(commandLen, 4, out);
        SMPPIO.writeInt(commandId, 4, out);
        SMPPIO.writeInt(commandStatus, 4, out);
        SMPPIO.writeInt(sequenceNum, 4, out);

        encodeBody(out);
        if (withOptional) {
            tlvTable.writeTo(out);
        }

    }

    /**
     * Decode an SMPP packet from a byte array.
     * 
     * @param b
     *            the byte array to read the SMPP packet's fields from.
     * @param offset
     *            the offset into <code>b</code> to begin reading the packet
     *            fields from.
     * @throws ie.omk.smpp.message.SMPPProtocolException
     *             if there is an error parsing the packet fields.
     */
    public void readFrom(byte[] b, int offset) throws SMPPProtocolException {
        // Clear out the TLVTable..
        tlvTable.clear();

        if (b.length < (offset + 16)) {
            throw new SMPPProtocolException("Not enough bytes for a header: "
                    + (b.length - (offset + 16)));
        }

        int len = SMPPIO.bytesToInt(b, offset, 4);
        int id = SMPPIO.bytesToInt(b, offset + 4, 4);

        if (id != commandId) {
            // Command type mismatch...ye can't do that, lad!
            throw new SMPPProtocolException(
                    "The packet on the input stream is not"
                            + " the same as this packet's type.");
        }
        if (b.length < (offset + len)) {
            // not enough bytes there for me to read in, buddy!
            throw new SMPPProtocolException(
                    "Header specifies the packet length is longer"
                            + " than the number of bytes available.");
        }

        commandStatus = SMPPIO.bytesToInt(b, offset + 8, 4);
        sequenceNum = SMPPIO.bytesToInt(b, offset + 12, 4);

        try {
            if (commandStatus == 0) {
                // Read the mandatory body parameters..
                int ptr = 16 + offset;
                readBodyFrom(b, ptr);

                // Read the optional parameters..
                int bl = getBodyLength();
                len -= 16 + bl;
                if (len > 0) {
                    tlvTable.readFrom(b, ptr + bl, len);
                }
            }
        } catch (ArrayIndexOutOfBoundsException x) {
            throw new SMPPProtocolException(
                    "Ran out of bytes to read for packet body", x);
        }

        // Set the message encoding type
        if (dataCoding != 0) {
            encoding = EncodingFactory.getInstance().getEncoding(dataCoding);
        }
        if (encoding == null) {
            encoding = EncodingFactory.getInstance().getDefaultAlphabet();
        }
    }

    /**
     * Read this packet's mandatory parameters from a byte array.
     * 
     * @param b
     *            the byte array to read the mandatory parameters from.
     * @param offset
     *            the offset into b that the mandatory parameter's begin at.
     * @throws ie.omk.smpp.message.SMPPProtocolException
     *             if there is an error parsing the packet fields.
     */
    protected abstract void readBodyFrom(byte[] b, int offset)
            throws SMPPProtocolException;
}

