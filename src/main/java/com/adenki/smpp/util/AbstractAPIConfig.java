package com.adenki.smpp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for {@link APIConfig} instances. Provides some basic
 * functionality useful across all <tt>APIConfig</tt> instances.
 * <p>
 * <b>Boolean values:</b> Any of the strings "true", "on" or "yes" will
 * evaluate to a boolean <tt>true</tt>. The strings "false", "off" or "no"
 * will all evaluate to a boolean <tt>false</tt>. Additionally, booleans
 * can be numeric, where zero is evaluated as <tt>false</tt> and a non-zero
 * value is evaluated as <tt>true</tt>.
 * </p>
 * 
 * <p>
 * <b>Numbers</b>: Numbers can be specified in any of decimal, hexadecimal,
 * binary or octal notations. Decimal is the default. Prefixing a number
 * with "0x" causes it to be parsed as hexadecimal. Prefixing a number with
 * '0' causes it to be parsed as octal. Suffixing the number with a 'b'
 * causes it to be parsed as binary. For example:
 * </p>
 * <ul>
 * <li><tt>3757</tt> is a decimal number (base 10).</li>
 * <li><tt>0xa91</tt> is a hexadecimal number (base 16).</li>
 * <li><tt>0731</tt> is an octal number (base 8).</li>
 * <li><tt>1001110b</tt> is a binary number (base 2).</li>
 * </ul>
 * 
 * <p>
 * Decimal numbers may also be modified with a multiplier. Suffixing the
 * letters 'k' or 'm' at the end of a decimal number multiples it by
 * 1024 and 1048576 respectively. This is useful for specifying a number or
 * kilobytes or megabytes. For example
 * </p>
 * <ul>
 * <li><tt>4k</tt> is equivalent to <tt>4096</tt>.</li>
 * <li><tt>96m</tt> is equivalent to <tt>100663296</tt>.</li>
 * </ul>
 * @version $Id$
 */
public abstract class AbstractAPIConfig implements APIConfig {
    private static final Map<String, Boolean> BOOLEANS =
        new HashMap<String, Boolean>();
    
    static {
        BOOLEANS.put("1", Boolean.TRUE);
        BOOLEANS.put("true", Boolean.TRUE);
        BOOLEANS.put("on", Boolean.TRUE);
        BOOLEANS.put("yes", Boolean.TRUE);
        BOOLEANS.put("0", Boolean.FALSE);
        BOOLEANS.put("false", Boolean.FALSE);
        BOOLEANS.put("off", Boolean.FALSE);
        BOOLEANS.put("no", Boolean.FALSE);
    }

    public boolean isSet(String property) {
        try {
            getProperty(property);
            return true;
        } catch (PropertyNotFoundException x) {
            return false;
        }
    }
    
    public String getProperty(String property, String defaultValue) {
        try {
            return getProperty(property);
        } catch (PropertyNotFoundException x) {
            return defaultValue;
        }
    }

    public short getShort(String property) throws InvalidConfigurationException, PropertyNotFoundException {
        long value = getInt(property);
        if (value < (long) Short.MIN_VALUE || value > (long) Short.MAX_VALUE) {
            throw new InvalidConfigurationException(
                    "Property value exceeds valid short range: " + value, property);
        }
        return (short) value;
    }
    
    public short getShort(String property, short defaultValue) {
        short s;
        try {
            s = getShort(property);
        } catch (PropertyNotFoundException x) {
            s = defaultValue;
        }
        return s;
    }

    public int getInt(String property) throws InvalidConfigurationException, PropertyNotFoundException {
        long value = convertToNumber(getProperty(property));
        if (value < (long) Integer.MIN_VALUE || value > (long) Integer.MAX_VALUE) {
            throw new InvalidConfigurationException(
                    "Property value exceeds valid integer range: " + value,
                    property);
        }
        return (int) value;
    }
    
    public int getInt(String property, int defaultValue) {
        int value;
        try {
            value = getInt(property);
        } catch (PropertyNotFoundException x) {
            value = defaultValue;
        }
        return value;
    }

    public long getLong(String property) throws InvalidConfigurationException, PropertyNotFoundException {
        return convertToNumber(getProperty(property));
    }
    
    public long getLong(String property, long defaultValue) {
        long l;
        try {
            l = getLong(property);
        } catch (PropertyNotFoundException x) {
            l = defaultValue;
        }
        return l;
    }

    public boolean getBoolean(String property) throws InvalidConfigurationException, PropertyNotFoundException {
        return toBoolean(getProperty(property));
    }
    
    public boolean getBoolean(String property, boolean defaultValue) {
        boolean value;
        try {
            value = getBoolean(property);
        } catch (PropertyNotFoundException x) {
            value = defaultValue;
        }
        return value;
    }

    public <T> T getClassInstance(String property, Class<T> type) {
        String className = getProperty(property);
        try {
            Class<?> clazz = Class.forName(className);
            if (!type.isAssignableFrom(clazz)) {
                throw new InvalidConfigurationException(
                        className + " is not an instance of " + type, className);
            }
            @SuppressWarnings("unchecked")
            T obj = ((Class<T>) clazz).newInstance();
            return obj;
        } catch (Exception x) {
            throw new InvalidConfigurationException(
                    "Could not instantiate a " + className, x);
        }
    }
    
    public <T> T getClassInstance(String property, Class<T> type, T defaultValue) {
        try {
            return getClassInstance(property, type);
        } catch (PropertyNotFoundException x) {
            return defaultValue;
        }
    }
    
    /**
     * Convert a string value to a boolean.
     * @param value The value to determine a boolean value for.
     * @return A boolean value.
     * @throws InvalidConfigurationException If <tt>value</tt> is not
     * a valid boolean value.
     */
    protected boolean toBoolean(String value) {
        Boolean bool = BOOLEANS.get(value);
        if (bool == null) {
            try {
                if (Integer.parseInt(value) != 0) {
                    bool = Boolean.TRUE;
                } else {
                    bool = Boolean.FALSE;
                }
            } catch (NumberFormatException x) {
                throw new InvalidConfigurationException(
                        "Invalid boolean property", value);
            }
        }
        return bool.booleanValue();
    }

    /**
     * Convert a number string into a <code>long</code>, taking into account
     * base and multiplication specifiers.
     * @param num The String representing the number.
     * @return The parsed number.
     * @throws NumberFormatException If the String cannot be parsed as a number.
     */
    protected long convertToNumber(final String num) throws NumberFormatException {
        int base = 10;
        long multiplier = 1;
        String s;
        
        char firstChar = num.charAt(0);
        char lastChar = num.charAt(num.length() - 1);
        if (num.startsWith("0x") || num.startsWith("0X")) {
            base = 16;
            s = num.substring(2);
        } else if (lastChar == 'b') {
            base = 2;
            s = num.substring(0, num.length() - 1);
        } else if (lastChar == 'k') {
            multiplier = 1024L;
            s = num.substring(0, num.length() - 1);
        } else if (lastChar == 'm') {
            multiplier = 1048576L;
            s = num.substring(0, num.length() - 1);
        } else if (firstChar == '0' && num.length() > 1) {
            base = 8;
            s = num.substring(1);
        } else {
            s = num;
        }
        return Long.parseLong(s, base) * multiplier;
    }
}
