package ie.omk.smpp.message.tlv;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for a value type encoder. Implementations of this interface are
 * responsible for encoding Java types to byte arrays for optional parameter
 * values.
 * <p>
 * Optional parameter support in this API is based on using standard Java types
 * for parameter values. Therefore, where the SMPP specification defines a
 * parameter as being a C octet string, applications should be able to us the
 * standard java.lang.String. An appropriate {@link StringEncoder}class will
 * then handle encoding the Java String to appropriate byte values. The
 * following table details the default encoders supplied with the API and the
 * types of values they encode.
 * </p>
 * <center><table cols="3" border="1" width="90%">
 * <tr>
 * <th>SMPP type</th>
 * <th>Encoder</th>
 * <th>Java type</th>
 * </tr>
 * <tr>
 * <td>Bit mask</td>
 * <td>{@link BitmaskEncoder}</td>
 * <td>java.util.BitSet</td>
 * </tr>
 * <tr>
 * <td>Integer</td>
 * <td>{@link NumberEncoder}</td>
 * <td>java.lang.Number <br>
 * (java.lang.Byte, java.lang.Short, java.lang.Integer, java.lang.Long)</td>
 * </tr>
 * <tr>
 * <td>Octet string</td>
 * <td>{@link OctetEncoder}</td>
 * <td>byte[]</td>
 * </tr>
 * <tr>
 * <td>C-Octet string</td>
 * <td>{@link StringEncoder}</td>
 * <td>java.lang.String</td>
 * </tr>
 * </table> </center> There is also one more encoder, {@link NullEncoder},
 * which is used in special cases where a particular optional parameter has a
 * tag but no value.
 * 
 * @author Oran Kelly
 * @version $Id$
 */
public interface Encoder {

    /**
     * Encode a value to a byte array.
     * 
     * @param tag
     *            The tag of the value to encode.
     * @param value
     *            The value to encode.
     * @param b
     *            The byte array to encode the value to.
     * @param offset
     *            The offset within <code>b</code> to begin encoding from.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             if the encoding tries to write beyond the end of the byte
     *             array.
     */
    // TODO deprecate or remove?
    void writeTo(Tag tag, Object value, byte[] b, int offset);

    /**
     * Encode a value to an output stream.
     * @param tag The tag.
     * @param value The value to be encoded.
     * @param out The output stream to write the encoded value to.
     */
    void writeTo(Tag tag, Object value, OutputStream out) throws IOException;
    
    /**
     * Decode a value from a byte array.
     * 
     * @param tag
     *            The tag of the value to decode.
     * @param b
     *            The byte array to decode the value from.
     * @param offset
     *            The offset in <code>b</code> to begin decoding from.
     * @param length
     *            The length of the value to decode.
     * @return The value object.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             if the decoding tries to read beyond the end of the byte
     *             array.
     */
    Object readFrom(Tag tag, byte[] b, int offset, int length);

    /**
     * Calculate the length, in bytes, that the value will encode as. The value
     * returned from this method must exactly match the number of bytes that
     * <code>writeTo</code> will attempt to encode to a byte array.
     * 
     * @param tag
     *            The tag of the value to get the length for.
     * @param value
     *            The value to get the length for.
     * @return The length <code>value</code> will encode to in bytes.
     */
    int getValueLength(Tag tag, Object value);
}

