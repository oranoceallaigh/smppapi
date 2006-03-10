package ie.omk.smpp.util;

import java.math.BigInteger;
import java.net.URL;

import junit.framework.TestCase;

public class TestAPIConfig extends TestCase {

    private APIConfig config;
    
    public TestAPIConfig(String name) {
        super(name);
    }

    public void testGetInstance() throws Exception {
        assertNotNull(APIConfig.getInstance());
    }
    
    public void testConvertToNumber() throws Exception {
        assertEquals(0L, config.convertToNumber("0"));
        assertEquals(1L, config.convertToNumber("1"));
        assertEquals(1827L, config.convertToNumber("1827"));
        assertEquals(-89123144L, config.convertToNumber("-89123144"));
        assertEquals(2048, config.convertToNumber("2k"));
        assertEquals(1048576L, config.convertToNumber("1m"));
        assertEquals(116L, config.convertToNumber("1110100b"));
        assertEquals(117L, config.convertToNumber("000001110101b"));
        assertEquals(0xfeed9128L, config.convertToNumber("0xfeed9128"));
        assertEquals(0xdeadbeefL, config.convertToNumber("0XdeadBEEF"));
        assertEquals(034L, config.convertToNumber("034"));
        assertEquals(8L, config.convertToNumber("010"));
        assertEquals(9L, config.convertToNumber("011"));
        
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

        assertEquals(5, config.getShort("random.property", (short) 5));
        assertEquals(056, config.getShort("s.number"));
        assertEquals(056, config.getShort("s.number", (short) 2048));
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

        assertEquals(5, config.getInt("random.property", 5));
        assertEquals(4096, config.getInt("i.number"));
        assertEquals(4096, config.getInt("i.number", 2048));
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

        assertEquals(5L, config.getLong("random.property", 5L));
        assertEquals(4096L, config.getLong("L.number"));
        assertEquals(4096L, config.getLong("L.number", 2048L));
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
        assertEquals(120000, config.getInt(APIConfig.LINK_TIMEOUT));
        assertEquals(3, config.getInt(APIConfig.TOO_MANY_IO_EXCEPTIONS));
        assertEquals(180000, config.getInt(APIConfig.BIND_TIMEOUT));
        assertNull(config.getProperty(APIConfig.EVENT_DISPATCHER_CLASS, null));
        assertNull(config.getProperty(APIConfig.EVENT_THREAD_FIFO_QUEUE_SIZE, null));
        assertNull(config.getProperty(APIConfig.EVENT_THREAD_POOL_SIZE, null));
        assertNull(config.getProperty(APIConfig.LINK_AUTOCLOSE_SNOOP, null));
    }
    
    public void testConfigure() throws Exception {
        URL url = getClass().getResource("TestAPIConfig.properties");
        assertNotNull(url);
        APIConfig.configure(url);
        assertSame(config, APIConfig.getInstance());
        config = APIConfig.getInstance();
        
        assertEquals("Some text", config.getProperty("apiConfig.string"));
        assertEquals(0x89L, config.getLong("apiConfig.hexNumber"));
        assertEquals(0164, config.getInt("apiConfig.octalNumber"));
        assertEquals(5, config.getInt("apiConfig.binaryNumber"));
        assertEquals(34 * 1024, config.getInt("apiConfig.kiloBytes"));
        assertEquals(2L * 1024L * 1024L, config.getLong("apiConfig.megaBytes"));
        assertTrue(config.getBoolean("apiConfig.bool"));
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        config = APIConfig.getInstance();
    }
}
