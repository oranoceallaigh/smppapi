package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import ie.omk.smpp.SMPPRuntimeException;

import java.util.Iterator;
import java.util.Locale;

import org.testng.annotations.Test;

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
            @SuppressWarnings("unused")
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
    
    public void testDefaultAlphabetFromSystemProperty() {
        System.setProperty(
                EncodingFactory.DEFAULT_ALPHABET_PROPNAME,
                "ie.omk.smpp.encoding.UTF16Encoding");
        EncodingFactory factory = new EncodingFactory();
        assertNotNull(factory.getDefaultAlphabet());
        assertEquals(factory.getDefaultAlphabet().getClass(), UTF16Encoding.class);
    }
}
