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
package ie.omk.smpp.message.tlv;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/** Enumeration class for optional parameter tag values.
 */
public class Tag implements java.io.Serializable {

    /** Look-up table of statically defined tags.
     * This <b>must</b> be defined before all the tags as the Tag constructor
     * expects this object to be created.
     */
    private static Map tagTable = new HashMap();

    public static final Tag DEST_ADDR_SUBUNIT =
	new Tag(0x05, Number.class, 1);

    public static final Tag DEST_NETWORK_TYPE
	 = new Tag(0x06, Number.class, 1);

    public static final Tag DEST_BEARER_TYPE
	 = new Tag(0x07, Number.class, 1);

    public static final Tag DEST_TELEMATICS_ID
	 = new Tag(0x08, Number.class, 2);

    public static final Tag SOURCE_ADDR_SUBUNIT
	 = new Tag(0x0d, Number.class, 1);

    public static final Tag SOURCE_NETWORK_TYPE
	 = new Tag(0x0e, Number.class, 1);

    public static final Tag SOURCE_BEARER_TYPE
	 = new Tag(0x0f, Number.class, 1);

    public static final Tag SOURCE_TELEMATICS_ID
	 = new Tag(0x10, Number.class, 1);

    public static final Tag QOS_TIME_TO_LIVE
	 = new Tag(0x17, Number.class, 4);

    public static final Tag PAYLOAD_TYPE
	 = new Tag(0x19, Number.class, 1);

    public static final Tag ADDITIONAL_STATUS_INFO_TEXT
	 = new Tag(0x1d, String.class, 1, 256);

    public static final Tag RECEIPTED_MESSAGE_ID
	 = new Tag(0x1e, String.class, 1, 65);

    public static final Tag MS_MSG_WAIT_FACILITIES
	 = new Tag(0x30, BitSet.class, 1);

    public static final Tag PRIVACY_INDICATOR
	 = new Tag(0x201, Number.class, 1);

    public static final Tag SOURCE_SUBADDRESS
	 = new Tag(0x202, byte[].class, 2);

    public static final Tag DEST_SUBADDRESS
	 = new Tag(0x203, byte[].class, 2);

    public static final Tag USER_MESSAGE_REFERENCE
	 = new Tag(0x204, Number.class, 2);

    public static final Tag USER_RESPONSE_CODE
	 = new Tag(0x205, Number.class, 2);

    public static final Tag SOURCE_PORT
	 = new Tag(0x20a, Number.class, 2);

    public static final Tag DESTINATION_PORT
	 = new Tag(0x20b2, Number.class, 2);

    public static final Tag SAR_MSG_REF_NUM
	 = new Tag(0x20c, Number.class, 2);

    public static final Tag LANGUAGE_INDICATOR
	 = new Tag(0x20d, Number.class, 1);

    public static final Tag SAR_TOTAL_SEGMENTS
	 = new Tag(0x20e, Number.class, 1);

    public static final Tag SAR_SEGMENT_SEQNUM
	 = new Tag(0x20f, Number.class, 1);

    public static final Tag SC_INTERFACE_VERSION
	 = new Tag(0x210, Number.class, 1);

    public static final Tag CALLBACK_NUM_PRES_IND
	 = new Tag(0x302, BitSet.class, 1);

    public static final Tag CALLBACK_NUM_ATAG
	 = new Tag(0x303, byte[].class, 0, 65);

    public static final Tag NUMBER_OF_MESSAGES
	 = new Tag(0x304, Number.class, 1);

    public static final Tag CALLBACK_NUM
	 = new Tag(0x381, byte[].class, 4, 19);

    public static final Tag DPF_RESULT
	 = new Tag(0x420, Number.class, 1);

    public static final Tag SET_DPF
	 = new Tag(0x421, Number.class, 1);

    public static final Tag MS_AVAILABILITY_STATUS
	 = new Tag(0x422, Number.class, 1);

    public static final Tag NETWORK_ERROR_CODE
	 = new Tag(0x423, byte[].class, 3);

    public static final Tag MESSAGE_PAYLOAD
	 = new Tag(0x424, byte[].class, 0);

    public static final Tag DELIVERY_FAILURE_REASON
	 = new Tag(0x425, Number.class, 1);

    public static final Tag MORE_MESSAGES_TO_SEND
	 = new Tag(0x426, Number.class, 1);

    public static final Tag MESSAGE_STATE
	 = new Tag(0x427, Number.class, 1);

    public static final Tag USSD_SERVICE_OP
	 = new Tag(0x501, byte[].class, 1);

    public static final Tag DISPLAY_TIME
	 = new Tag(0x1201, Number.class, 1);

    public static final Tag SMS_SIGNAL
	 = new Tag(0x1203, Number.class, 2);

    public static final Tag MS_VALIDITY
	 = new Tag(0x1204, Number.class, 1);

    public static final Tag ALERT_ON_MESSAGE_DELIVERY
	 = new Tag(0x130c, null, 0);

    public static final Tag ITS_REPLY_TYPE
	 = new Tag(0x1380, Number.class, 1);

    public static final Tag ITS_SESSION_INFO
	 = new Tag(0x1383, byte[].class, 2);

                                    
    private Integer tag = null;

    /** The minimum, or fixed, length.
     */
    private int minLength = -1;

    /** The maximum length of a value of this tag.
     */
    private int maxLength = -1;

    private Class type = null;

    private Encoder encoder = null;


    private Tag(int tag, Class type, int fixedLength) {
	this (tag, type, null, fixedLength, fixedLength);
    }

    private Tag(int tag, Class type, int minLength, int maxLength) {
	this (tag, type, null, minLength, maxLength);
    }

    // XXX document that the TagDefinedException it thrown
    private Tag(int tag, Class type, Encoder enc, int fixedLength) {
	this (tag, type, fixedLength, fixedLength);
    }

    // XXX document that the TagDefinedException it thrown
    private Tag(int tag, Class type, Encoder enc,
	    int minLength, int maxLength) {
	this.tag = new Integer(tag);
	this.type = type;
	this.minLength = minLength;
	this.maxLength = maxLength;
	if (enc == null)
	    this.encoder = getEncoderForType(type);
	else
	    this.encoder = enc;

	synchronized (tagTable) {
	    if (tagTable.containsKey(this.tag))
		throw new TagDefinedException("Tag 0x"
			+ Integer.toHexString(tag)
			+ " is already defined.");

	    tagTable.put(this.tag, this);
	}
    }

    private Encoder getEncoderForType(Class type) {
	// If type is null and encoder is null, this is a "no value" tlv type.
	if (type == null)
	    return (NullEncoder.getInstance());

	if (java.lang.Number.class.isAssignableFrom(type))
	    return (NumberEncoder.getInstance());
	else if (java.lang.String.class.isAssignableFrom(type))
	    return (StringEncoder.getInstance());
	else if (java.util.BitSet.class.isAssignableFrom(type))
	    return (BitmaskEncoder.getInstance());
	else if (byte[].class.isAssignableFrom(type))
	    return (OctetEncoder.getInstance());
	else
	    throw new NoEncoderException("No encoder for class type "
		    + type.getName());
    }

    public int getTag() {
	return (tag.intValue());
    }

    public int intValue() {
	return (tag.intValue());
    }

    public int getLength() {
	return (maxLength < 0 ? minLength : maxLength);
    }

    public int getMinLength() {
	return (minLength);
    }

    public int getMaxLength() {
	return (maxLength);
    }

    public Class getType() {
	return (type);
    }

    public boolean equals(Object obj) {
	if (obj instanceof Tag)
	    return (((Tag)obj).tag.equals(this.tag));
	else
	    return (false);
    }

    public boolean equals(int tag) {
	return (tag == this.tag.intValue());
    }

    public int hashCode() {
	return (tag.hashCode());
    }

    public String toString() {
	return (tag.toString());
    }

    public String toHexString() {
	return (Integer.toHexString(tag.intValue()));
    }

    public Encoder getEncoder() {
	return (encoder);
    }

    public static final Tag getTag(int tagValue) {
	return ((Tag)tagTable.get(new Integer(tagValue)));
    }

    /** Define a new tag value type. This method adds a new tag type to the
     * internal tag table.
     * @param tagValue the integer value of the tag.
     * @param type the parameter type.
     * @param enc the encoder used to serialize and deserialize. This may be
     * null to use one of the API's internally defined encoders.
     * @param fixedSize the defined size of the parameter.
     */
    public static final Tag defineTag(int tagValue, Class type, Encoder enc,
	    int fixedSize) {
	return (new Tag(tagValue, type, enc, fixedSize));
    }

    /** Define a new tag value type. This method adds a new tag type to the
     * internal tag table.
     * @param tagValue the integer value of the tag.
     * @param type the parameter type.
     * @param enc the encoder used to serialize and deserialize. This may be
     * null to use one of the API's internally defined encoders.
     * @param minSize the minimum size of the parameter.
     * @param maxSize the maximum size of the parameter.
     * @see Encoder
     */
    public static final Tag defineTag(int tagValue, Class type, Encoder enc,
	    int minSize, int maxSize) {
	return (new Tag(tagValue, type, enc, minSize, maxSize));
    }

    // XXX defineTags method that can read definitions from a properties file
}
