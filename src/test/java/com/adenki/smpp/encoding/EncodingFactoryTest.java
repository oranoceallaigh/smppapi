package com.adenki.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.Locale;

import org.testng.annotations.Test;

import com.adenki.smpp.SMPPRuntimeException;
import com.adenki.smpp.util.APIConfig;
import com.adenki.smpp.util.APIConfigFactory;
import com.adenki.smpp.util.PropertiesAPIConfig;

@Test
public class EncodingFactoryTest {

    public void testEncodingFactory() {
        EncodingFactory factory = EncodingFactory.getInstance();
        assertNotNull(factory.getDefaultAlphabet());
        assertEquals(factory.getEncoding(0).getClass(), DefaultAlphabetEncoding.class);
        assertEquals(factory.getEncoding(1).getClass(), ASCIIEncoding.class);
        assertEquals(factory.getEncoding(3).getClass(), Latin1Encoding.class);
        assertEquals(factory.getEncoding(4).getClass(), BinaryEncoding.class);
        assertEquals(factory.getEncoding(8).getClass(), UCS2Encoding.class);
        assertNull(factory.getEncoding(10));
        assertNull(factory.getEncoding(100));
        assertNull(factory.getEncoding(189));
        assertEquals(DefaultAlphabetEncoding.class,
                factory.getAlphabet("en").getClass());
        Locale chinese = Locale.CHINESE;
        Locale korean = Locale.KOREAN;
        assertEquals(UCS2Encoding.class,
                factory.getAlphabet(chinese.getLanguage()).getClass());
        assertEquals(UCS2Encoding.class,
                factory.getAlphabet(korean.getLanguage()).getClass());
    }
    
    public void testAddEncoding() {
        class TestEncoding1 extends AlphabetEncoding {
            TestEncoding1() {
                super(100);
            }
        }
        EncodingFactory factory = new EncodingFactory();
        factory.addEncoding(new TestEncoding1());
        assertNotNull(factory.getEncoding(100));
        assertEquals(factory.getEncoding(100).getClass(), TestEncoding1.class);
    }
    
    @Test(expectedExceptions = {SMPPRuntimeException.class})
    public void testAddNullEncodingThrowsException() {
        EncodingFactory factory = new EncodingFactory();
        factory.addEncoding((Class<AlphabetEncoding>) null);
    }

    @Test(expectedExceptions = {SMPPRuntimeException.class})
    public void testExceptionIsThrownWhenEncodingConstructorExceptions() {
        class TestEncoding2 extends AlphabetEncoding {
            TestEncoding2() {
                super(101);
                throw new RuntimeException("This exception is expected.");
            }
        }
        EncodingFactory factory = new EncodingFactory();
        factory.addEncoding(TestEncoding2.class);
    }
    
    public void testGetAllEncodings() {
        Iterator<MessageEncoding<?>> encodings =
            EncodingFactory.getInstance().getAllEncodings();
        assertNotNull(encodings);
        assertTrue(encodings.hasNext());
    }

    public void testDefaultAlphabetUsesGsmDefaultAlphabetIfNotOtherwiseSpecified() throws Exception {
        assertNull(System.getProperty(EncodingFactory.DEFAULT_ALPHABET_PROPNAME));
        APIConfig config = APIConfigFactory.getConfig();
        assertFalse(config.isSet(APIConfig.DEFAULT_ALPHABET));
        EncodingFactory factory = new EncodingFactory();
        assertEquals(factory.getDefaultAlphabet().getClass(), DefaultAlphabetEncoding.class);
    }
    
    public void testInstantiatingDefaultAlphabetFromSystemProperty() {
        try {
            System.setProperty(
                    EncodingFactory.DEFAULT_ALPHABET_PROPNAME,
                    "com.adenki.smpp.encoding.UTF16Encoding");
            EncodingFactory factory = new EncodingFactory();
            assertNotNull(factory.getDefaultAlphabet());
            assertEquals(factory.getDefaultAlphabet().getClass(), UTF16Encoding.class);
        } finally {
            System.clearProperty(EncodingFactory.DEFAULT_ALPHABET_PROPNAME);
        }
    }
    
    public void testInstantiatingDefaultAlphabetFromAPIConfig() throws Exception {
        try {
            assertNull(System.getProperty(EncodingFactory.DEFAULT_ALPHABET_PROPNAME));
            PropertiesAPIConfig config = new PropertiesAPIConfig();
            config.initialise();
            config.setProperty(APIConfig.DEFAULT_ALPHABET, ASCIIEncoding.class.getName());
            APIConfigFactory.setCachedConfig(config);
            EncodingFactory factory = new EncodingFactory();
            assertNotNull(factory.getDefaultAlphabet());
            assertEquals(factory.getDefaultAlphabet().getClass(), ASCIIEncoding.class);
        } finally {
            APIConfigFactory.reset();
        }
    }
    
    public void testDefaultAlphabetSpecifiedInSystemPropertyOverridesAPIConfig() throws Exception {
        try {
            System.setProperty(
                    EncodingFactory.DEFAULT_ALPHABET_PROPNAME,
                    UCS2Encoding.class.getName());
            PropertiesAPIConfig config = new PropertiesAPIConfig();
            config.initialise();
            config.setProperty(APIConfig.DEFAULT_ALPHABET, HPRoman8Encoding.class.getName());
            APIConfigFactory.setCachedConfig(config);
            EncodingFactory factory = new EncodingFactory();
            assertNotNull(factory.getDefaultAlphabet());
            assertEquals(factory.getDefaultAlphabet().getClass(), UCS2Encoding.class);
        } finally {
            System.clearProperty(EncodingFactory.DEFAULT_ALPHABET_PROPNAME);
            APIConfigFactory.reset();
        }
    }
}
