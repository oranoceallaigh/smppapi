package ie.omk.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import ie.omk.smpp.encoding.ASCIIEncoding;
import ie.omk.smpp.encoding.BinaryEncoding;
import ie.omk.smpp.encoding.DefaultAlphabetEncoding;
import ie.omk.smpp.encoding.EncodingFactory;
import ie.omk.smpp.encoding.Latin1Encoding;
import ie.omk.smpp.encoding.UCS2Encoding;

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
