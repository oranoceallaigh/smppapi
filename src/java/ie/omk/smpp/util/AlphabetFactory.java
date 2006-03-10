package ie.omk.smpp.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.LogFactory;

/**
 * Factory class for getting SMS alphabet encoding objects. The API maintains
 * what it considers a 'default alphabet' for the current VM. If nothing else is
 * specified, that alphabet will be an instance of the SMS default alphabet
 * (implemented by the ie.omk.smpp.util.DefaultAlphabetExt class). Changing the
 * API's default alphabet to another is merely a case of setting the
 * "smpp.default_alphabet" System property to the name of another class, which
 * must implement the ie.omk.smpp.util.SMSAlphabet interface.
 * 
 * @deprecated Use {@link ie.omk.smpp.util.EncodingFactory}
 */
public final class AlphabetFactory {
    private static final Map LANG_TO_ALPHABET = new HashMap();
    private static AlphabetEncoding defaultAlphabet;

    private static final String DEFAULT_ALPHABET_PROPNAME = "smpp.default_alphabet";

    static {
        AlphabetEncoding gsmDefault = new DefaultAlphabetEncoding();
        try {
            LANG_TO_ALPHABET.put(null, new UCS2Encoding());
        } catch (UnsupportedEncodingException x) {
            try {
                LANG_TO_ALPHABET.put(null, new Latin1Encoding());
            } catch (UnsupportedEncodingException xx) {
                LANG_TO_ALPHABET.put(null, new ASCIIEncoding());
            }
        }
        LANG_TO_ALPHABET.put("en", gsmDefault);
        LANG_TO_ALPHABET.put("de", gsmDefault);
        LANG_TO_ALPHABET.put("fr", gsmDefault);
        LANG_TO_ALPHABET.put("it", gsmDefault);
        LANG_TO_ALPHABET.put("nl", gsmDefault);
        LANG_TO_ALPHABET.put("es", gsmDefault);
    }
    
    private AlphabetFactory() {
        // AlphabetFactory.Sounds like something off Sesame Street, doesn't it?
        // ;-)
    }

    /**
     * Return the default alphabet for this runtime environment. The default
     * alphabet is usually the SMS Default alphabet
     * (ie.omk.smpp.util.DefaultAlphabetExt). This can be altered by setting the
     * <b>smpp.default_alphabet </b> system property to the name of a concrete
     * sub-class of ie.omk.smpp.util.SMSAlphabet. For example, if you have
     * written an alphabet class called 'it.smpp.MyAlphabet', then when running
     * your smppapi-based application, supply a system property using the -D
     * switch: <br>
     * <code>java -cp .:smppapi.jar -Dsmpp.default_alphabet=ie.smpp.MyAlphabet
     * ...</code>
     */
    public static AlphabetEncoding getDefaultAlphabet() {
        if (defaultAlphabet == null) {
            init();
        }

        return defaultAlphabet;
    }

    private static void init() {
        String className = "";
        try {
            className = System.getProperty(DEFAULT_ALPHABET_PROPNAME);
            if (className != null) {
                Class alphaClass = Class.forName(className);
                defaultAlphabet = (AlphabetEncoding) alphaClass.newInstance();
            } else {
                defaultAlphabet = new DefaultAlphabetEncoding();
            }
        } catch (Exception x) {
            // Leave the alphabet as DefaultAlphabet
            LogFactory.getLog(AlphabetFactory.class).warn(
                    "Couldn't load default alphabet " + className, x);
            defaultAlphabet = new DefaultAlphabetEncoding();
        }
    }

    /**
     * Get the SMSAlphabet needed for encoding messages in a particular
     * language.
     * 
     * @param lang
     *            The ISO code for the language the message is in.
     */
    public static AlphabetEncoding getAlphabet(String lang) {
        AlphabetEncoding enc = (AlphabetEncoding) LANG_TO_ALPHABET.get(lang);
        if (enc != null) {
            return enc;
        } else {
            return (AlphabetEncoding) LANG_TO_ALPHABET.get(null);
        }
    }
}

