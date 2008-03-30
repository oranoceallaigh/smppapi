package ie.omk.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigInteger;
import java.net.URL;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class TestAPIConfig {

    private APIConfig config;
    
    @BeforeTest
    public void setUp() throws Exception {
        config = APIConfig.getInstance();
    }
    
    public void testGetInstance() throws Exception {
        assertNotNull(APIConfig.getInstance());
    }
    
    public void testConvertToNumber() throws Exception {
        assertEquals(config.convertToNumber("0"), 0L);
        assertEquals(config.convertToNumber("1"), 1L);
        assertEquals(config.convertToNumber("1827"), 1827L);
        assertEquals(config.convertToNumber("-89123144"), -89123144L);
        assertEquals(config.convertToNumber("2k"), 2048);
        assertEquals(config.convertToNumber("1m"), 1048576L);
        assertEquals(config.convertToNumber("1110100b"), 116L);
        assertEquals(config.convertToNumber("000001110101b"), 117L);
        assertEquals(config.convertToNumber("0xfeed9128"), 0xfeed9128L);
        assertEquals(config.convertToNumber("0XdeadBEEF"), 0xdeadbeefL);
        assertEquals(config.convertToNumber("034"), 034L);
        assertEquals(config.convertToNumber("010"), 8L);
        assertEquals(config.convertToNumber("011"), 9L);
        
        try {
            config.convertToNumber("deadbeef");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException x) {
        }
        try {
            config.convertToNumber("092");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException x) {
        }
        try {
            config.convertToNumber("1111112b");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException x) {
        }
        try {
            config.convertToNumber("0x293b8k");
            fail("Expected NumberFormatException");
        } catch (NumberFormatException x) {
        }
    }
    
    public void testPropertyGetters() throws Exception {
        try {
            config.getProperty("random.property");
            fail("Expected PropertyNotFoundException");
        } catch (PropertyNotFoundException x) {
        }
        assertEquals("randomValue",
                config.getProperty("random.property", "randomValue"));
    }
    
    public void testBooleanPropertyGetters() throws Exception {
        try {
            config.getBoolean("random.property");
            fail("Expected PropertyNotFoundException");
        } catch (PropertyNotFoundException x) {
        }
        assertFalse(config.getBoolean("random.property", false));
        assertTrue(config.getBoolean("random.property", true));
        
        config.setProperty("b.default", "true");
        assertTrue(config.getBoolean("b.default", false));
        
        config.setProperty("b.yes", "yes");
        config.setProperty("b.on", "on");
        config.setProperty("b.true", "true");
        config.setProperty("b.1", "1");
        config.setProperty("b.number", "65");
        config.setProperty("b.no", "no");
        config.setProperty("b.off", "off");
        config.setProperty("b.false", "false");
        config.setProperty("b.0", "0");
        assertTrue(config.getBoolean("b.yes"));
        assertTrue(config.getBoolean("b.on"));
        assertTrue(config.getBoolean("b.true"));
        assertTrue(config.getBoolean("b.1"));
        assertTrue(config.getBoolean("b.number"));
        assertFalse(config.getBoolean("b.no"));
        assertFalse(config.getBoolean("b.off"));
        assertFalse(config.getBoolean("b.false"));
        assertFalse(config.getBoolean("b.0"));
        
        config.setProperty("b.zero", "zero");
        try {
            config.getBoolean("b.zero");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
    }
    
    public void testShortGetters() throws Exception {
        config.setProperty("s.number", "056");
        config.setProperty("s.invalid", "text");
        config.setProperty("s.tooLow",
                Integer.toString((int) Short.MIN_VALUE - 5));
        config.setProperty("s.tooHigh",
                Integer.toString((int) Short.MAX_VALUE + 5));

        assertEquals(config.getShort("random.property", (short) 5), 5);
        assertEquals(config.getShort("s.number"), 056);
        assertEquals(config.getShort("s.number", (short) 2048), 056);
        try {
            config.getShort("random.property");
            fail("Expected PropertyNotFoundException");
        } catch (PropertyNotFoundException x){
        }
        try {
            config.getShort("s.invalid");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
        try {
            config.getShort("s.tooLow");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
        try {
            config.getShort("s.tooHigh");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
    }
    
    public void testIntGetters() throws Exception {
        config.setProperty("i.number", "4k");
        config.setProperty("i.invalid", "text");
        config.setProperty("i.tooLow",
                Long.toString((long) Integer.MIN_VALUE - 5L));
        config.setProperty("i.tooHigh",
                Long.toString((long) Integer.MAX_VALUE + 5L));

        assertEquals(config.getInt("random.property", 5), 5);
        assertEquals(config.getInt("i.number"), 4096);
        assertEquals(config.getInt("i.number", 2048), 4096);
        try {
            config.getInt("random.property");
            fail("Expected PropertyNotFoundException");
        } catch (PropertyNotFoundException x){
        }
        try {
            config.getInt("i.invalid");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
        try {
            config.getInt("i.tooLow");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
        try {
            config.getInt("i.tooHigh");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
    }

    public void testLongGetters() throws Exception {
        BigInteger five = BigInteger.valueOf(5L);
        BigInteger tooLow = BigInteger.valueOf(Long.MIN_VALUE).subtract(five);
        BigInteger tooHigh = BigInteger.valueOf(Long.MAX_VALUE).add(five);
        config.setProperty("L.number", "4k");
        config.setProperty("L.invalid", "text");
        config.setProperty("L.tooLow", tooLow.toString(10));
        config.setProperty("L.tooHigh", tooHigh.toString(10));

        assertEquals(config.getLong("random.property", 5L), 5L);
        assertEquals(config.getLong("L.number"), 4096L);
        assertEquals(config.getLong("L.number", 2048L), 4096L);
        try {
            config.getLong("random.property");
            fail("Expected PropertyNotFoundException");
        } catch (PropertyNotFoundException x){
        }
        try {
            config.getLong("L.invalid");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
        try {
            config.getLong("L.tooLow");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
        try {
            config.getLong("L.tooHigh");
            fail("Expected InvalidConfigurationException");
        } catch (InvalidConfigurationException x) {
        }
    }
    
    public void testDefaultAPIProperties() throws Exception {
        // These tests match the defaults specified
        // in resources/smppapi.properties
        try {
            config.getInt(APIConfig.LINK_BUFFERSIZE_IN);
            fail("Expected PropertyNotFoundException");
        } catch (PropertyNotFoundException x) {
            // pass
        }
        try {
            config.getInt(APIConfig.LINK_BUFFERSIZE_OUT);
            fail("Expected PropertyNotFoundException");
        } catch (PropertyNotFoundException x) {
            // pass
        }
        assertTrue(config.getBoolean(APIConfig.LINK_AUTO_FLUSH));
        assertEquals(config.getInt(APIConfig.LINK_TIMEOUT), 120000);
        assertEquals(config.getInt(APIConfig.TOO_MANY_IO_EXCEPTIONS), 3);
        assertEquals(config.getInt(APIConfig.BIND_TIMEOUT), 180000);
        assertNull(config.getProperty(APIConfig.EVENT_DISPATCHER_CLASS, null));
        assertNull(config.getProperty(APIConfig.EVENT_THREAD_FIFO_QUEUE_SIZE, null));
        assertNull(config.getProperty(APIConfig.EVENT_THREAD_POOL_SIZE, null));
        assertNull(config.getProperty(APIConfig.LINK_AUTOCLOSE_SNOOP, null));
    }
    
    public void testConfigure() throws Exception {
        URL url = getClass().getClassLoader().getResource("TestAPIConfig.properties");
        assertNotNull(url);
        APIConfig.configure(url);
        assertSame(APIConfig.getInstance(), config);
        config = APIConfig.getInstance();
        
        assertEquals(config.getProperty("apiConfig.string"), "Some text");
        assertEquals(config.getLong("apiConfig.hexNumber"), 0x89L);
        assertEquals(config.getInt("apiConfig.octalNumber"), 0164);
        assertEquals(config.getInt("apiConfig.binaryNumber"), 5);
        assertEquals(config.getInt("apiConfig.kiloBytes"), 34 * 1024);
        assertEquals(config.getLong("apiConfig.megaBytes"), 2L * 1024L * 1024L);
        assertTrue(config.getBoolean("apiConfig.bool"));
    }
}
