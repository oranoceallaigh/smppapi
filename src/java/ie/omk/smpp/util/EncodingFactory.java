package ie.omk.smpp.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class for obtaining message encoding instances.
 * By default, instances of this class are aware of the following encoding
 * types:
 * <ul>
 * <li>{@link DefaultAlphabetEncoding}</li>
 * <li>{@link ASCIIEncoding}</li>
 * <li>{@link Latin1Encoding}</li>
 * <li>{@link BinaryEncoding}</li>
 * <li>{@link UCS2Encoding} (only if the JVM supports the UCS2 charset).</li>
 * </ul>
 * 
 * <p>Other encoding types may be added to an instance of the factory but 
 * it should be noted that there can only be <b>one</b> encoding mapping
 * for any particular data coding value. If a second encoding is added which
 * has the same data coding value as another encoding, the existing encoding
 * will be overwritten in the factory.</p>
 * 
 * <p>The rest of the SMPPAPI uses the singleton instance of this factory
 * as returned by the static {@link #getInstance()} method of this class.</p>
 */
public class EncodingFactory {

    private static final Log LOGGER = LogFactory.getLog(EncodingFactory.class);
    
    private static final EncodingFactory INSTANCE = new EncodingFactory();
    
    private final Map mappingTable = new HashMap();
    
    public EncodingFactory() {
        addEncoding(new DefaultAlphabetEncoding());
        addEncoding(new ASCIIEncoding());
        addEncoding(new Latin1Encoding());
        addEncoding(new BinaryEncoding());
        try {
            addEncoding(new UCS2Encoding());
        } catch (UnsupportedEncodingException x) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("JVM does not support UCS2 - encoding will not be used.");
            }
        }
    }
    
    /**
     * Get a static instance of this factory class.
     * @return A static instance of <code>EncodingFactory</code>.
     */
    public static final EncodingFactory getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get the message encoding represented by the specified data coding
     * value.
     * @param dataCoding The data coding to retrieve a message encoding instance
     * for.
     * @return The message encoding instance representing data coding value
     * <code>dataCoding</code> or <code>null</code> if there is no encoding
     * registered for that value.
     */
    public MessageEncoding getEncoding(final int dataCoding) {
        return (MessageEncoding) mappingTable.get(new Integer(dataCoding));
    }
    
    /**
     * Add a message encoding to this factory.
     * @param encoding The encoding to add to the factory.
     */
    public void addEncoding(final MessageEncoding encoding) {
        mappingTable.put(new Integer(encoding.getDataCoding()), encoding);
    }
    
    /**
     * Get an iterator over all known encodings by this factory.
     * @return An iterator over all the encodings known by this factory.
     */
    public Iterator getAllEncodings() {
        return mappingTable.values().iterator();
    }
}
