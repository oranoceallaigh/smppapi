package ie.omk.smpp.util;

import java.util.Locale;

import junit.framework.TestCase;

public class EncodingFactoryTest extends TestCase {

    public void testEncodingFactory() {
        EncodingFactory factory = EncodingFactory.getInstance();
        assertNotNull(factory.getDefaultAlphabet());
        assertEquals(DefaultAlphabetEncoding.class, factory.getEncoding(0).getClass());
        assertEquals(ASCIIEncoding.class, factory.getEncoding(1).getClass());
        assertEquals(Latin1Encoding.class, factory.getEncoding(3).getClass());
        assertEquals(BinaryEncoding.class, factory.getEncoding(4).getClass());
        assertEquals(UCS2Encoding.class, factory.getEncoding(8).getClass());
        assertNull(factory.getEncoding(10));
        assertNull(factory.getEncoding(100));
        assertNull(factory.getEncoding(189));

        assertEquals(DefaultAlphabetEncoding.class,
                factory.getAlphabet("en").getClass());
        assertEquals(DefaultAlphabetEncoding.class,
                factory.getAlphabet("en").getClass());
        assertEquals(DefaultAlphabetEncoding.class,
                factory.getAlphabet("en").getClass());
        assertEquals(DefaultAlphabetEncoding.class,
                factory.getAlphabet("en").getClass());
        assertEquals(DefaultAlphabetEncoding.class,
                factory.getAlphabet("en").getClass());

        Locale chinese = Locale.CHINESE;
        Locale korean = Locale.KOREAN;
        assertEquals(UCS2Encoding.class,
                factory.getAlphabet(chinese.getLanguage()).getClass());
        assertEquals(UCS2Encoding.class,
                factory.getAlphabet(korean.getLanguage()).getClass());
    }
}
