package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.util.APIConfig;
import com.adenki.smpp.util.APIConfigFactory;

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
    /**
     * Name of the system property that the 'default' alphabet encoding
     * will be read from.
     */
    static final String DEFAULT_ALPHABET_PROPNAME = "smpp.default_alphabet";
    private static final Logger LOG = LoggerFactory.getLogger(EncodingFactory.class);
    
    private static final EncodingFactory INSTANCE = new EncodingFactory();
    
    private final Map<Integer, MessageEncoding<?>> mappingTable =
        new HashMap<Integer, MessageEncoding<?>>();
    private final Map<String, AlphabetEncoding> langToAlphabet =
        new HashMap<String, AlphabetEncoding>();
    private AlphabetEncoding defaultAlphabet;
    
    public EncodingFactory() {
        addEncoding(new BinaryEncoding());
        AlphabetEncoding gsmDefault = new DefaultAlphabetEncoding();
        addEncoding(gsmDefault);
        langToAlphabet.put("en", gsmDefault);
        langToAlphabet.put("de", gsmDefault);
        langToAlphabet.put("fr", gsmDefault);
        langToAlphabet.put("it", gsmDefault);
        langToAlphabet.put("nl", gsmDefault);
        langToAlphabet.put("es", gsmDefault);
        try {
            ASCIIEncoding ascii = new ASCIIEncoding();
            addEncoding(ascii);
            langToAlphabet.put(null, ascii);
        } catch (UnsupportedEncodingException x) {
            LOG.debug("UCS2 encoding is not supported.");
        }
        try {
            Latin1Encoding latin1 = new Latin1Encoding();
            addEncoding(latin1);
            langToAlphabet.put(null, latin1);
        } catch (UnsupportedEncodingException x) {
            LOG.debug("UCS2 encoding is not supported.");
        }
        try {
            AlphabetEncoding ucs2 = new UCS2Encoding();
            addEncoding(ucs2);
            langToAlphabet.put(null, ucs2);
        } catch (UnsupportedEncodingException x) {
            LOG.debug("UCS2 encoding is not supported.");
        }
        LOG.debug("Using {} as the catch-all language alphabet.",
                langToAlphabet.get(null).getClass().getSimpleName());
        initDefaultAlphabet();
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
    public MessageEncoding<?> getEncoding(final int dataCoding) {
        return mappingTable.get(Integer.valueOf(dataCoding));
    }
    
    /**
     * Add a message encoding to this factory.
     * @param encoding The encoding to add to the factory.
     */
    public void addEncoding(final MessageEncoding<?> encoding) {
        mappingTable.put(Integer.valueOf(encoding.getDataCoding()), encoding);
    }

    /**
     * Add a message encoding to this factory. The supplied class must be
     * a sub-class of {@link MessageEncoding} and must have a
     * no-argument constructor. This method will swallow any exceptions
     * that may occur during instance creation - primarily to silently
     * fail on encodings that rely on a particular charset that the current
     * JVM does not support. Log messages will be generated for failure
     * conditions.
     * @param encodingClass The class of the <code>MessageEncoding</code> to
     * add.
     */
    public void addEncoding(final Class<? extends MessageEncoding<?>> encodingClass) {
        addEncoding(instantiateEncoding(encodingClass));
    }
    
    /**
     * Get an iterator over all known encodings by this factory.
     * @return An iterator over all the encodings known by this factory.
     */
    public Iterator<MessageEncoding<?>> getAllEncodings() {
        return mappingTable.values().iterator();
    }
    
    /**
     * Return the default alphabet for this runtime environment. The default
     * alphabet is usually {@link DefaultAlphabetEncoding}. This can be altered
     * by setting the <b>smpp.default_alphabet </b> system property to the name
     * of an implementation of {@link AlphabetEncoding}.
     * 
     * <p>
     * For example:<br />
     * <code>java -cp .:smppapi.jar -Dsmpp.default_alphabet=myPackage.MyAlphabet
     * ...</code>
     * @return The default alphabet encoding.
     */
    public AlphabetEncoding getDefaultAlphabet() {
        return defaultAlphabet;
    }

    /**
     * Get the SMSAlphabet needed for encoding messages in a particular
     * language.
     * @param lang
     *            The ISO code for the language the message is in.
     */
    public AlphabetEncoding getAlphabet(String lang) {
        AlphabetEncoding enc = langToAlphabet.get(lang);
        if (enc != null) {
            return enc;
        } else {
            return langToAlphabet.get(null);
        }
    }

    /**
     * Initialise the default alphabet.
     */
    private void initDefaultAlphabet() {
        String className = "";
        try {
            className = System.getProperty(DEFAULT_ALPHABET_PROPNAME);
            if (className != null) {
                Class<?> alphaClass = Class.forName(className);
                defaultAlphabet = instantiateEncoding(alphaClass);
            } else {
                APIConfig cfg = APIConfigFactory.getConfig();
                defaultAlphabet = cfg.getClassInstance(
                        APIConfig.DEFAULT_ALPHABET,
                        AlphabetEncoding.class,
                        null);
            }
        } catch (Exception x) {
            LOG.warn("Couldn't load default alphabet {}: {}",
                    className, x.getMessage());
        }
        if (defaultAlphabet == null) {
            defaultAlphabet = new DefaultAlphabetEncoding();
        }
    }
    
    private AlphabetEncoding instantiateEncoding(final Class<?> encodingClass) {
        try {
            return (AlphabetEncoding) encodingClass.newInstance();
        } catch (Exception x) {
            LOG.error("Could not instantiate a {}: {}", encodingClass, x);
            throw new SMPPRuntimeException(x);
        }
    }
}
