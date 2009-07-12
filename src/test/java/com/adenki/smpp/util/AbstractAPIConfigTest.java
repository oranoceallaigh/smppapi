package com.adenki.smpp.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test
public class AbstractAPIConfigTest {

    public void testNumberIsConvertedProperly() throws Exception {
        APIConfig config = new EchoingAPIConfig();
        assertEquals(config.getLong("0"), 0L);
        assertEquals(config.getLong("1"), 1L);
        assertEquals(config.getLong("1827"), 1827L);
        assertEquals(config.getLong("-89123144"), -89123144L);
        assertEquals(config.getLong("2k"), 2048);
        assertEquals(config.getLong("1m"), 1048576L);
        assertEquals(config.getLong("1110100b"), 116L);
        assertEquals(config.getLong("000001110101b"), 117L);
        assertEquals(config.getLong("0xfeed9128"), 0xfeed9128L);
        assertEquals(config.getLong("0XdeadBEEF"), 0xdeadbeefL);
        assertEquals(config.getLong("034"), 034L);
        assertEquals(config.getLong("010"), 8L);
        assertEquals(config.getLong("011"), 9L);
    }

    @Test(expectedExceptions = {NumberFormatException.class})
    public void testExceptionIsThrownWhenNumberIsInvalid() throws Exception {
        new EchoingAPIConfig().getLong("aef");
    }
    
    @Test(expectedExceptions = {NumberFormatException.class})
    public void testExceptionIsThrownWhenOctalNumberContainsInvalidDigit() throws Exception {
        new EchoingAPIConfig().getLong("092");
    }
    
    @Test(expectedExceptions = {NumberFormatException.class})
    public void testExceptionIsThrownWhenBinaryNumberContainsInvalidDigit() throws Exception {
        new EchoingAPIConfig().getLong("1111112b");
    }
    
    @Test(expectedExceptions = {NumberFormatException.class})
    public void testExceptionIsThrownWhenHexNumberContainsInvalidDigit() throws Exception {
        new EchoingAPIConfig().getLong("0x293b8k");
    }
    
    public void testBooleanValuesAreConvertedProperly() throws Exception {
        APIConfig config = new EchoingAPIConfig();
        assertTrue(config.getBoolean("yes"));
        assertTrue(config.getBoolean("on"));
        assertTrue(config.getBoolean("true"));
        assertTrue(config.getBoolean("1"));
        assertTrue(config.getBoolean("65"));
        assertFalse(config.getBoolean("no"));
        assertFalse(config.getBoolean("off"));
        assertFalse(config.getBoolean("false"));
        assertFalse(config.getBoolean("0"));
    }

    @Test(expectedExceptions = {InvalidConfigurationException.class})
    public void testExceptionisThrownWhenBooleanValueIsInvalid() throws Exception {
        new EchoingAPIConfig().getBoolean("zero");
    }

    public void testShortGetter() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        assertEquals(config.getShort("4587"), (short) 4587);
    }
    
    public void testIntGetter() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        assertEquals(config.getInt("1783957164"), 1783957164);
    }

    public void testLongGetter() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        assertEquals(config.getLong("21474836401551"), 21474836401551L);
    }
    
    @Test(expectedExceptions = {InvalidConfigurationException.class})
    public void testExceptionIsThrownWhenNumberExceedsShortRange() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        config.getShort("70000");
    }
    
    @Test(expectedExceptions = {InvalidConfigurationException.class})
    public void testExceptionIsThrownWhenNumberExceedsIntRange() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        config.getInt("2147483651");
    }

    public void testObjectIsInstantiatedWhenRequested() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        String str = config.getClassInstance("java.lang.String", String.class);
        assertNotNull(str);
    }
    
    @Test(expectedExceptions = {InvalidConfigurationException.class})
    public void testExceptionIsThrownWhenInstantiatedObjectDoesNotMatchExpectedType() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        config.getClassInstance("java.lang.Object", String.class);
    }

    @Test(expectedExceptions = {InvalidConfigurationException.class})
    public void testExceptionIsThrownWhenObjectCannotBeInstantiated() throws Exception {
        EchoingAPIConfig config = new EchoingAPIConfig();
        config.getClassInstance("java.lang.Integer", Integer.class);
    }
    
    public void testIsSetReturnsTrueWhenPropertyIsFound() throws Exception {
        assertTrue(new EchoingAPIConfig().isSet("property"));
    }
    
    public void testIsSetReturnsFalseWhenPropertyIsNotFound() throws Exception {
        assertFalse(new EchoingAPIConfig().isSet("missing.property"));
    }

    public static class EchoingAPIConfig extends AbstractAPIConfig {
        
        public String getProperty(String property) throws PropertyNotFoundException {
            if (property.startsWith("missing.")) {
                throw new PropertyNotFoundException();
            } else {
                return property;
            }
        }
        
        public void initialise() {
        }
        
        public boolean reloadAPIConfig() {
            return false;
        }
    }
}
