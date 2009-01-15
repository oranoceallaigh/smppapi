package com.adenki.smpp.message.param;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import com.adenki.smpp.util.PacketDecoder;
import com.adenki.smpp.util.PacketEncoder;

/**
 * Parameter descriptor. The parameter descriptor interface provides a way
 * for SMPP types to be read from byte arrays and written to output streams.
 * Descriptors are used for both mandatory and optional parameters.
 * @version $Id$
 */
public interface ParamDescriptor extends Serializable {
    /**
     * Get the index of another numerical mandatory parameter which specifies
     * the length of the parameter this descriptor represents. For example,
     * in a submit_sm packet, the length of the short_message parameter is
     * specified by the sm_length parameter, a 1-byte integer immediately
     * preceding short_message in the mandatory parameter section of the packet.
     * Therefore, the parameter descriptor that will be used to decode the
     * short_message would return the index of the sm_length parameter in the
     * body. This specified length can then be used to decode the correct
     * number of bytes for the short message.
     * <p>
     * As another example, take the submit_multi packet. It has a mandatory
     * parameter called dest_address(es) which specify all the destinations
     * the message should be submitted to. The number of destinations in the
     * destination table is specified by the number_of_dests mandatory
     * parameter. In this case, the descriptor used to read the dest_addresses
     * would return the index of number_of_dests from this method.
     * </p>
     * @return The index in the mandatory parameters of where to find the length
     * specifier for this descriptor. If this descriptor does not need or
     * support a length specifier, <code>-1</code> must be returned.
     */
    int getLengthSpecifier();

    /**
     * Get the encoded byte-size of <code>obj</code>.
     * @param obj The object to calculate the encoded size for.
     * @return The number of bytes the specified object would be encoded
     * to via the {@link #writeObject(Object, OutputStream)} method.
     */
    int sizeOf(Object obj);

    /**
     * Write the specified object to an output stream.
     * @param obj The object to encode.
     * @param out The output stream to write the object to.
     * @throws IOException If there was an error writing to the stream.
     */
    void writeObject(Object obj, PacketEncoder encoder) throws IOException;

    /**
     * Read an object from a byte array.
     * @param data The byte data to read (or decode) an object from.
     * @param position The position to begin parsing from. This position will
     * be updated upon return to point to the first byte after the decoded
     * object in the byte array.
     * @param length The number of bytes to use in reading the object. If the
     * length is unknown and intrinsic to the type being decoded (such as
     * a C-String, which is terminated by a nul-byte), then <code>-1</code>
     * may be supplied.
     * @return The decoded object.
     */
    // TODO this should throw something - a runtime exception
    Object readObject(PacketDecoder decoder, int length);
}
