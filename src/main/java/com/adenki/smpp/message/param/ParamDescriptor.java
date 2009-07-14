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
    Object readObject(PacketDecoder decoder, int length) throws IOException;
}
