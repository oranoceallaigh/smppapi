package ie.omk.smpp.message.tlv;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration class for optional parameter tag values.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public final class Tag implements java.io.Serializable {

    /**
     * Look-up table of statically defined tags. This <b>must</b> be defined
     * before all the tags as the Tag constructor expects this object to exist.
     */
    private static Map tagTable = new HashMap();

    static final long serialVersionUID = -418561932897398277L;
    
    public static final Tag DEST_ADDR_SUBUNIT = new Tag(0x05, Number.class, 1);

    public static final Tag DEST_NETWORK_TYPE = new Tag(0x06, Number.class, 1);

    public static final Tag DEST_BEARER_TYPE = new Tag(0x07, Number.class, 1);

    public static final Tag DEST_TELEMATICS_ID = new Tag(0x08, Number.class, 2);

    public static final Tag SOURCE_ADDR_SUBUNIT = new Tag(0x0d, Number.class, 1);

    public static final Tag SOURCE_NETWORK_TYPE = new Tag(0x0e, Number.class, 1);

    public static final Tag SOURCE_BEARER_TYPE = new Tag(0x0f, Number.class, 1);

    public static final Tag SOURCE_TELEMATICS_ID = new Tag(0x10, Number.class,
            1);

    public static final Tag QOS_TIME_TO_LIVE = new Tag(0x17, Number.class, 4);

    public static final Tag PAYLOAD_TYPE = new Tag(0x19, Number.class, 1);

    public static final Tag ADDITIONAL_STATUS_INFO_TEXT = new Tag(0x1d,
            String.class, 1, 256);

    public static final Tag RECEIPTED_MESSAGE_ID = new Tag(0x1e, String.class,
            1, 65);

    public static final Tag MS_MSG_WAIT_FACILITIES = new Tag(0x30,
            BitSet.class, 1);

    public static final Tag PRIVACY_INDICATOR = new Tag(0x201, Number.class, 1);

    public static final Tag SOURCE_SUBADDRESS = new Tag(0x202, byte[].class, 2,
            23);

    public static final Tag DEST_SUBADDRESS = new Tag(0x203, byte[].class, 2,
            23);

    public static final Tag USER_MESSAGE_REFERENCE = new Tag(0x204,
            Number.class, 2);

    public static final Tag USER_RESPONSE_CODE = new Tag(0x205, Number.class, 1);

    public static final Tag SOURCE_PORT = new Tag(0x20a, Number.class, 2);

    public static final Tag DESTINATION_PORT = new Tag(0x20b, Number.class, 2);

    public static final Tag SAR_MSG_REF_NUM = new Tag(0x20c, Number.class, 2);

    public static final Tag LANGUAGE_INDICATOR = new Tag(0x20d, Number.class, 1);

    public static final Tag SAR_TOTAL_SEGMENTS = new Tag(0x20e, Number.class, 1);

    public static final Tag SAR_SEGMENT_SEQNUM = new Tag(0x20f, Number.class, 1);

    public static final Tag SC_INTERFACE_VERSION = new Tag(0x210, Number.class,
            1);

    public static final Tag CALLBACK_NUM_PRES_IND = new Tag(0x302,
            BitSet.class, 1);

    public static final Tag CALLBACK_NUM_ATAG = new Tag(0x303, byte[].class, 0,
            65);

    public static final Tag NUMBER_OF_MESSAGES = new Tag(0x304, Number.class, 1);

    public static final Tag CALLBACK_NUM = new Tag(0x381, byte[].class, 4, 19);

    public static final Tag DPF_RESULT = new Tag(0x420, Number.class, 1);

    public static final Tag SET_DPF = new Tag(0x421, Number.class, 1);

    public static final Tag MS_AVAILABILITY_STATUS = new Tag(0x422,
            Number.class, 1);

    public static final Tag NETWORK_ERROR_CODE = new Tag(0x423, byte[].class, 3);

    public static final Tag MESSAGE_PAYLOAD = new Tag(0x424, byte[].class, -1);

    public static final Tag DELIVERY_FAILURE_REASON = new Tag(0x425,
            Number.class, 1);

    public static final Tag MORE_MESSAGES_TO_SEND = new Tag(0x426,
            Number.class, 1);

    public static final Tag MESSAGE_STATE = new Tag(0x427, Number.class, 1);

    public static final Tag USSD_SERVICE_OP = new Tag(0x501, byte[].class, 1);

    public static final Tag DISPLAY_TIME = new Tag(0x1201, Number.class, 1);

    public static final Tag SMS_SIGNAL = new Tag(0x1203, Number.class, 2);

    public static final Tag MS_VALIDITY = new Tag(0x1204, Number.class, 1);

    public static final Tag ALERT_ON_MESSAGE_DELIVERY = new Tag(0x130c, null, 0);

    public static final Tag ITS_REPLY_TYPE = new Tag(0x1380, Number.class, 1);

    public static final Tag ITS_SESSION_INFO = new Tag(0x1383, byte[].class, 2);

    /**
     * Integer value of this tag.
     */
    private Integer tag;

    /**
     * The minimum length a value of this tag type can be.
     */
    private int minLength = -1;

    /**
     * The maximum length a value of this tag type can be.
     */
    private int maxLength = -1;

    /**
     * The Java type a value of this tag type must be.
     */
    private Class type;

    /**
     * The class used for encoding and decoding values of this tag type.
     * 
     * @see ie.omk.smpp.message.tlv.Encoder
     */
    private Encoder encoder;

    /**
     * Create a new Tag. The encoder type will be chosen from the set of known
     * built-in encoders.
     * 
     * @param tag
     *            The integer value of the tag.
     * @param type
     *            The allowed Java type for the value.
     * @param fixedLength
     *            The fixed length allowed for the value.
     * @throws ie.omk.smpp.message.tlv.TagDefinedException
     *             If a tag with integer value <code>tag</code> has already
     *             been defined.
     */
    private Tag(int tag, Class type, int fixedLength)
            throws TagDefinedException {
        this(tag, type, null, fixedLength, fixedLength);
    }

    /**
     * Create a new Tag. he encoder type will be chosen from the set of known
     * built-in encoders.
     * 
     * @param tag
     *            The integer value of the tag.
     * @param type
     *            The allowed Java type of the value.
     * @param minLength
     *            The minimum length allowed for the value.
     * @param maxLength
     *            The maximum length allowed for the value.
     * @throws ie.omk.smpp.message.tlv.TagDefinedException
     *             If a tag with integer value <code>tag</code> has already
     *             been defined.
     */
    private Tag(int tag, Class type, int minLength, int maxLength)
            throws TagDefinedException {
        this(tag, type, null, minLength, maxLength);
    }

    /**
     * Create a new Tag.
     * 
     * @param tag
     *            The integer value of the tag.
     * @param type
     *            The allowed Java type for the value
     * @param enc
     *            The encoding class to use to encode and decode values.
     * @param fixedLength
     *            The fixed length allowed for the value.
     * @throws ie.omk.smpp.message.tlv.TagDefinedException
     *             If a tag with integer value <code>tag</code> has already
     *             been defined.
     */
    private Tag(int tag, Class type, Encoder enc, int fixedLength)
            throws TagDefinedException {
        this(tag, type, enc, fixedLength, fixedLength);
    }

    /**
     * Create a new Tag.
     * 
     * @param tag
     *            The integer value of the tag.
     * @param type
     *            The allowed Java type for the value
     * @param enc
     *            The encoding class to use to encode and decode values.
     * @param minLength
     *            The minimum length allowed for the value.
     * @param maxLength
     *            The maximum length allowed for the value.
     * @throws ie.omk.smpp.message.tlv.TagDefinedException
     *             If a tag with integer value <code>tag</code> has already
     *             been defined.
     */
    private Tag(int tag, Class type, Encoder enc, int minLength, int maxLength)
            throws TagDefinedException {
        this.tag = new Integer(tag);
        this.type = type;
        this.minLength = minLength;
        this.maxLength = maxLength;
        if (enc == null) {
            this.encoder = getEncoderForType(type);
        } else {
            this.encoder = enc;
        }

        synchronized (tagTable) {
            if (tagTable.containsKey(this.tag)) {
                throw new TagDefinedException(tag, "Tag 0x"
                        + Integer.toHexString(tag) + " is already defined.");
            }

            tagTable.put(this.tag, this);
        }
    }

    /**
     * Get an <code>Encoder</code> for a particular Java type.
     * 
     * @param type
     *            The Java type to get an encoder for.
     * @return The encoder/decoder for Java type <code>type</code>.
     */
    private Encoder getEncoderForType(Class type) {
        Encoder encoder;
        
        // If type is null and encoder is null, this is a "no value" tlv type.
        if (type == null) {
            encoder = new NullEncoder();
        } else if (java.lang.Number.class.isAssignableFrom(type)) {
            encoder = new NumberEncoder();
        } else if (java.lang.String.class.isAssignableFrom(type)) {
            encoder = new StringEncoder();
        } else if (java.util.BitSet.class.isAssignableFrom(type)) {
            encoder = new BitmaskEncoder();
        } else if (byte[].class.isAssignableFrom(type)) {
            encoder = new OctetEncoder();
        } else {
            throw new NoEncoderException(type, "No encoder for class type "
                    + type.getName());
        }
        return encoder;
    }

    /**
     * Get the integer value of this tag.
     * 
     * @return the integer value of this tag.
     */
    public int intValue() {
        return tag.intValue();
    }

    /**
     * Get the allowed length of a value of this tag type.
     * 
     * @return The allowed length, or the maximum length if a range is set.
     */
    public int getLength() {
        return maxLength < 0 ? minLength : maxLength;
    }

    /**
     * Get the minimum length of a value of this tag type.
     * 
     * @return the minimum length of a value of this tag type.
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Get the maximum length of a value of this tag type.
     * 
     * @return the maximum length of a value of this tag type.
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Get the Java type of values of this tag type.
     * 
     * @return the Java type of values of this tag type.
     */
    public Class getType() {
        return type;
    }

    /**
     * Test for equality. Two tags are equal if their integer values are
     * equivalent.
     * 
     * @return true if <code>obj</code> is Tag and has the same tag value.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Tag) {
            return ((Tag) obj).tag.equals(this.tag);
        } else {
            return false;
        }
    }

    /**
     * Test for equality against an integer.
     * 
     * @return true if this Tag's integer value is equal to <code>tag</code>.
     */
    public boolean equals(int tag) {
        return tag == this.tag.intValue();
    }

    /**
     * Get the hashCode for this Tag. The hashCode for a Tag is the same as:
     * <br>
     * <code>new Integer(tag.tagValue()).hashCode()</code>
     * 
     * @return A hash code for this tag.
     */
    public int hashCode() {
        return tag.hashCode();
    }

    /**
     * Convert this tag to a String. This returns a decimal representation of
     * the tag's integer value in a String.
     * 
     * @return This tag's string value.
     */
    public String toString() {
        return tag.toString();
    }

    /**
     * Convert this tag to a String. This returns a hex representation of the
     * tag's integer value in a String.
     * 
     * @return This tag's hexadecimal representation.
     */
    public String toHexString() {
        return Integer.toHexString(tag.intValue());
    }

    /**
     * Get the encoder used to encode values of this tag type.
     * 
     * @return the encoder used to encode values of this tag type.
     */
    public Encoder getEncoder() {
        return encoder;
    }

    /**
     * Get the Tag object that represents tag <code>tagValue</code>. If the
     * tag is known then the static Tag object representing the tag is returned.
     * If the tag is not known, a fresh instance of Tag will be returned which
     * uses an octet-string type.
     * 
     * <p><b>WARNING</b> The behaviour of this method may change to returning
     * <code>null</code> for an undefined tag. It needs to be determined
     * which behaviour is the best.</p>
     * 
     * @return The Tag object representing the tag <code>tagValue</code>.
     *         Will never return <code>null</code>.
     */
    public static Tag getTag(int tagValue) {
        Tag t = (Tag) tagTable.get(new Integer(tagValue));
        if (t == null) {
            return Tag.defineTag(tagValue, byte[].class, null, -1, -1);
        } else {
            return t;
        }
    }

    /**
     * Define a new tag type which has a fixed length.
     * @param tagValue
     *            the integer value of the tag.
     * @param type
     *            the parameter type.
     * @param enc
     *            the encoder used to serialize and deserialize. This may be
     *            null to use the API's default internally defined encoders.
     * @param fixedSize
     *            the defined size of the parameter.
     * @throws ie.omk.smpp.message.tlv.TagDefinedException
     *             if an attempt is made to define a tag with a integer value
     *             equivalent to an already defined tag.
     * @see Encoder
     */
    public static Tag defineTag(int tagValue, Class type, Encoder enc,
            int fixedSize) throws TagDefinedException {
        return new Tag(tagValue, type, enc, fixedSize);
    }

    /**
     * Define a new tag type with minimum and maximum sizes.
     * @param tagValue
     *            the integer value of the tag.
     * @param type
     *            the parameter type.
     * @param enc
     *            the encoder used to serialize and deserialize. This may be
     *            null to use the API's default internally defined encoders.
     * @param minSize
     *            the minimum size of the parameter.
     * @param maxSize
     *            the maximum size of the parameter.
     * @throws ie.omk.smpp.message.tlv.TagDefinedException
     *             if an attempt is made to define a tag with a integer value
     *             equivalent to an already defined tag.
     * @see Encoder
     */
    public static Tag defineTag(int tagValue, Class type, Encoder enc,
            int minSize, int maxSize) throws TagDefinedException {
        return new Tag(tagValue, type, enc, minSize, maxSize);
    }

    /**
     * Determine if a tag is defined for a particular tag integer value. 
     * @param tagValue The integer value of the tag.
     * @return <code>true</code> if the tag is defined, <code>false</code>
     * otherwise.
     */
    public static boolean isTagDefined(int tagValue) {
        return tagTable.containsKey(new Integer(tagValue));
    }
    
    /**
     * Undefine a tag. This removes all knoweledge of this tag type from the
     * internal tables. If there is no such tag defined already, this method
     * will do nothing.
     * 
     * @param tag
     *            The tag to undefine. null if there was no tag defined already.
     * @return The Tag object that has been undefined.
     */
    public static Tag undefineTag(Tag tag) {
        if (tag == null) {
            return null;
        }
        synchronized (tagTable) {
            return (Tag) tagTable.remove(tag.tag);
        }
    }
}
