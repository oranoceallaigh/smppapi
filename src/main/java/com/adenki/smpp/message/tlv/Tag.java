package com.adenki.smpp.message.tlv;

import java.util.HashMap;
import java.util.Map;

import com.adenki.smpp.message.param.ParamDescriptor;

/**
 * Enumeration class for optional parameter tag values.
 * 
 * @version $Id$
 */
public final class Tag implements java.io.Serializable {

    /**
     * Look-up table of statically defined tags. This <b>must</b> be defined
     * before all the tags as the Tag constructor expects this object to exist.
     */
    private static Map<Integer, Tag> tagTable = new HashMap<Integer, Tag>();

    static final long serialVersionUID = 2L;
    
    public static final Tag DEST_ADDR_SUBUNIT =
        new Tag(0x05, BasicDescriptors.INTEGER1, 1);

    public static final Tag DEST_NETWORK_TYPE =
        new Tag(0x06, BasicDescriptors.INTEGER1, 1);

    public static final Tag DEST_BEARER_TYPE =
        new Tag(0x07, BasicDescriptors.INTEGER1, 1);

    public static final Tag DEST_TELEMATICS_ID =
        new Tag(0x08, BasicDescriptors.INTEGER2, 2);

    public static final Tag SOURCE_ADDR_SUBUNIT =
        new Tag(0x0d, BasicDescriptors.INTEGER1, 1);

    public static final Tag SOURCE_NETWORK_TYPE =
        new Tag(0x0e, BasicDescriptors.INTEGER1, 1);

    public static final Tag SOURCE_BEARER_TYPE =
        new Tag(0x0f, BasicDescriptors.INTEGER1, 1);

    public static final Tag SOURCE_TELEMATICS_ID =
        new Tag(0x10, BasicDescriptors.INTEGER1, 1);

    public static final Tag QOS_TIME_TO_LIVE =
        new Tag(0x17, BasicDescriptors.INTEGER4, 4);

    public static final Tag PAYLOAD_TYPE =
        new Tag(0x19, BasicDescriptors.INTEGER1, 1);

    public static final Tag ADDITIONAL_STATUS_INFO_TEXT =
        new Tag(0x1d, BasicDescriptors.CSTRING, 1, 256);

    public static final Tag RECEIPTED_MESSAGE_ID =
        new Tag(0x1e, BasicDescriptors.CSTRING, 1, 65);

    public static final Tag MS_MSG_WAIT_FACILITIES =
        new Tag(0x30, BasicDescriptors.BITMASK, 1);

    public static final Tag PRIVACY_INDICATOR =
        new Tag(0x201, BasicDescriptors.INTEGER1, 1);

    public static final Tag SOURCE_SUBADDRESS =
        new Tag(0x202, BasicDescriptors.BYTES, 2, 23);

    public static final Tag DEST_SUBADDRESS =
        new Tag(0x203, BasicDescriptors.BYTES, 2, 23);

    public static final Tag USER_MESSAGE_REFERENCE =
        new Tag(0x204, BasicDescriptors.INTEGER2, 2);

    public static final Tag USER_RESPONSE_CODE =
        new Tag(0x205, BasicDescriptors.INTEGER1, 1);

    public static final Tag SOURCE_PORT =
        new Tag(0x20a, BasicDescriptors.INTEGER2, 2);

    public static final Tag DESTINATION_PORT =
        new Tag(0x20b, BasicDescriptors.INTEGER2, 2);

    public static final Tag SAR_MSG_REF_NUM =
        new Tag(0x20c, BasicDescriptors.INTEGER2, 2);

    public static final Tag LANGUAGE_INDICATOR =
        new Tag(0x20d, BasicDescriptors.INTEGER1, 1);

    public static final Tag SAR_TOTAL_SEGMENTS =
        new Tag(0x20e, BasicDescriptors.INTEGER1, 1);

    public static final Tag SAR_SEGMENT_SEQNUM =
        new Tag(0x20f, BasicDescriptors.INTEGER1, 1);

    public static final Tag SC_INTERFACE_VERSION =
        new Tag(0x210, BasicDescriptors.INTEGER1, 1);

    public static final Tag CALLBACK_NUM_PRES_IND =
        new Tag(0x302, BasicDescriptors.BITMASK, 1);

    public static final Tag CALLBACK_NUM_ATAG =
        new Tag(0x303, BasicDescriptors.BYTES, 0, 65);

    public static final Tag NUMBER_OF_MESSAGES =
        new Tag(0x304, BasicDescriptors.INTEGER1, 1);

    public static final Tag CALLBACK_NUM =
        new Tag(0x381, BasicDescriptors.BYTES, 4, 19);

    public static final Tag DPF_RESULT =
        new Tag(0x420, BasicDescriptors.INTEGER1, 1);

    public static final Tag SET_DPF =
        new Tag(0x421, BasicDescriptors.INTEGER1, 1);

    public static final Tag MS_AVAILABILITY_STATUS =
        new Tag(0x422, BasicDescriptors.INTEGER1, 1);

    public static final Tag NETWORK_ERROR_CODE =
        new Tag(0x423, BasicDescriptors.BYTES, 3);

    public static final Tag MESSAGE_PAYLOAD =
        new Tag(0x424, BasicDescriptors.BYTES, -1);

    public static final Tag DELIVERY_FAILURE_REASON =
        new Tag(0x425, BasicDescriptors.INTEGER1, 1);

    public static final Tag MORE_MESSAGES_TO_SEND =
        new Tag(0x426, BasicDescriptors.INTEGER1, 1);

    public static final Tag MESSAGE_STATE =
        new Tag(0x427, BasicDescriptors.INTEGER1, 1);

    public static final Tag USSD_SERVICE_OP =
        new Tag(0x501, BasicDescriptors.BYTES, 1);

    public static final Tag DISPLAY_TIME =
        new Tag(0x1201, BasicDescriptors.INTEGER1, 1);

    public static final Tag SMS_SIGNAL =
        new Tag(0x1203, BasicDescriptors.INTEGER2, 2);

    public static final Tag MS_VALIDITY =
        new Tag(0x1204, BasicDescriptors.INTEGER1, 1);

    public static final Tag ALERT_ON_MESSAGE_DELIVERY =
        new Tag(0x130c, null, 0);

    public static final Tag ITS_REPLY_TYPE =
        new Tag(0x1380, BasicDescriptors.INTEGER1, 1);

    public static final Tag ITS_SESSION_INFO =
        new Tag(0x1383, BasicDescriptors.BYTES, 2);

    public static final Tag BROADCAST_AREA_IDENTIFIER =
        new Tag(0x0606, BasicDescriptors.BYTES, -1);
    
    public static final Tag BROADCAST_AREA_SUCCESS =
        new Tag(0x0608, BasicDescriptors.INTEGER1, 1);
    
    public static final Tag BROADCAST_CONTENT_TYPE_INFO =
        new Tag(0x0602, BasicDescriptors.BYTES, 1, 255);
    
    public static final Tag BROADCAST_CHANNEL_INDICATOR =
        new Tag(0x0600, BasicDescriptors.INTEGER1, 1);
    
    public static final Tag BROADCAST_CONTENT_TYPE =
        new Tag(0x0601, BasicDescriptors.BYTES, 1, 255);

    public static final Tag BROADCAST_END_TIME = 
        new Tag(0x0609, BasicDescriptors.DATE, 16, 16);
    
    public static final Tag BROADCAST_ERROR_STATUS =
        new Tag(0x607, BasicDescriptors.INTEGER4, 4);

    public static final Tag BROADCAST_FREQUENCY_INTERVAL =
        new Tag(0x0605, BasicDescriptors.BYTES, 3);

    public static final Tag BROADCAST_MESSAGE_CLASS =
        new Tag(0x0603, BasicDescriptors.INTEGER1, 1);
    
    public static final Tag BROADCAST_REP_NUM = 
        new Tag(0x604, BasicDescriptors.INTEGER2, 2);
    
    public static final Tag BROADCAST_SERVICE_GROUP =
        new Tag(0x060A, BasicDescriptors.BYTES, 1, 255);
    
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
     * The class used for encoding and decoding values of this tag type.
     * @see com.adenki.smpp.message.param.ParamDescriptor
     */
    private ParamDescriptor paramDescriptor;

    private Tag(int tag,
            ParamDescriptor paramDescriptor,
            int fixedLength) throws TagDefinedException {
        this(tag, paramDescriptor, fixedLength, fixedLength);
    }
    
    /**
     * Create a new Tag.
     * 
     * @param tag
     *            The integer value of the tag.
     * @param enc
     *            The encoding class to use to encode and decode values.
     * @param minLength
     *            The minimum length allowed for the value.
     * @param maxLength
     *            The maximum length allowed for the value.
     * @throws com.adenki.smpp.message.tlv.TagDefinedException
     *             If a tag with integer value <code>tag</code> has already
     *             been defined.
     */
    private Tag(int tag,
            ParamDescriptor paramDescriptor,
            int minLength,
            int maxLength) throws TagDefinedException {
        this.tag = new Integer(tag);
        this.paramDescriptor = paramDescriptor;
        this.minLength = minLength;
        this.maxLength = maxLength;
        synchronized (tagTable) {
            if (tagTable.containsKey(this.tag)) {
                throw new TagDefinedException(tag, "Tag 0x"
                        + Integer.toHexString(tag) + " is already defined.");
            }
            tagTable.put(this.tag, this);
        }
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

    public ParamDescriptor getParamDescriptor() {
        return paramDescriptor;
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
            return Tag.defineTag(tagValue, BasicDescriptors.BYTES, -1);
        } else {
            return t;
        }
    }

    /**
     * Define a new tag type which has a fixed length.
     * @param tagValue The integer value of the tag.
     * @param paramDescriptor The parameter type descriptor.
     * @param fixedSize The defined size of the parameter.
     * @throws com.adenki.smpp.message.tlv.TagDefinedException If an attempt is
     * made to define a tag with a integer value equivalent to an already
     * defined tag.
     * @see ParamDescriptor
     */
    public static Tag defineTag(int tagValue,
            ParamDescriptor paramDescriptor,
            int fixedSize) throws TagDefinedException {
        return new Tag(tagValue, paramDescriptor, fixedSize, fixedSize);
    }

    /**
     * Define a new tag type with minimum and maximum sizes.
     * @param tagValue The integer value of the tag.
     * @param paramDescriptor The parameter type descriptor.
     * @param minSize The minimum size of the parameter.
     * @param maxSize The maximum size of the parameter.
     * @throws com.adenki.smpp.message.tlv.TagDefinedException If an attempt is
     * made to define a tag with a integer value equivalent to an already
     * defined tag.
     * @see ParamDescriptor
     */
    public static Tag defineTag(int tagValue,
            ParamDescriptor paramDescriptor,
            int minSize,
            int maxSize) throws TagDefinedException {
        return new Tag(tagValue, paramDescriptor, minSize, maxSize);
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
