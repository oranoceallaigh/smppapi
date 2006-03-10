package ie.omk.smpp.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    private static final String DEFAULT_ALPHABET_PROPNAME = "smpp.default_alphabet";
    
    private static final EncodingFactory INSTANCE = new EncodingFactory();
    
    private final Map mappingTable = new HashMap();
    private final Map langToAlphabet = new HashMap();
    private AlphabetEncoding defaultAlphabet;
    
    public EncodingFactory() {
        AlphabetEncoding gsmDefault = new DefaultAlphabetEncoding();
        addEncoding(gsmDefault);
        addEncoding(new ASCIIEncoding());
        addEncoding(new BinaryEncoding());
        addEncoding(Latin1Encoding.class);
        addEncoding(UCS2Encoding.class);
        langToAlphabet.put("en", gsmDefault);
        langToAlphabet.put("de", gsmDefault);
        langToAlphabet.put("fr", gsmDefault);
        langToAlphabet.put("it", gsmDefault);
        langToAlphabet.put("nl", gsmDefault);
        langToAlphabet.put("es", gsmDefault);
        try {
            addEncoding(new UCS2Encoding());
            langToAlphabet.put(null, new UCS2Encoding());
        } catch (UnsupportedEncodingException x) {
            try {
                langToAlphabet.put(null, new Latin1Encoding());
            } catch (UnsupportedEncodingException xx) {
                LOGGER.debug("JVM does not support UCS2 - encoding will not be used.");
                langToAlphabet.put(null, new ASCIIEncoding());
            }
        }
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
     * Add a message encoding to this factory. The supplied class must be
     * a sub-class of {@link MessageEncoding} and must have a
     * no-argument constructor.
     * @param encodingClass The class of the <code>MessageEncoding</code> to
     * add.
     */
    public void addEncoding(final Class encodingClass) {
        try {
            if (!MessageEncoding.class.isAssignableFrom(encodingClass)) {
                throw new IllegalArgumentException("Not an encoding class: "
                        + encodingClass);
            }
            Constructor c = encodingClass.getConstructor(new Class[0]);
            addEncoding((MessageEncoding) c.newInstance(new Object[0]));
        } catch (NoSuchMethodException x) {
            LOGGER.error(encodingClass.getName() + " does not have a"
                    + "no-argument constructor", x);
        } catch (IllegalAccessException x) {
            LOGGER.error(encodingClass.getName() + " does not have a visible"
                    + " no-argument constructor", x);
        } catch (InstantiationException x) {
            LOGGER.error("Cannot instantiate an instance of " + encodingClass, x);
        } catch (InvocationTargetException x) {
            Throwable cause = x.getCause();
            if (cause instanceof UnsupportedEncodingException) {
                LOGGER.debug(encodingClass.getName() + " is not supported by the JVM");
            } else {
                LOGGER.error(encodingClass.getName() + " constructor threw "
                        + "an exception", x);
            }
        } catch (IllegalArgumentException x) {
            LOGGER.error("This exception shouldn't happen", x);
        }
    }
    
    /**
     * Get an iterator over all known encodings by this factory.
     * @return An iterator over all the encodings known by this factory.
     */
    public Iterator getAllEncodings() {
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
        AlphabetEncoding enc = (AlphabetEncoding) langToAlphabet.get(lang);
        if (enc != null) {
            return enc;
        } else {
            return (AlphabetEncoding) langToAlphabet.get(null);
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
                Class alphaClass = Class.forName(className);
                defaultAlphabet = (AlphabetEncoding) alphaClass.newInstance();
            }
        } catch (Exception x) {
            LOGGER.warn("Couldn't load default alphabet " + className, x);
        }
        if (defaultAlphabet == null) {
            defaultAlphabet = new DefaultAlphabetEncoding();
        }
    }
}
