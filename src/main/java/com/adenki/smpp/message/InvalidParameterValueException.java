package com.adenki.smpp.message;

import com.adenki.smpp.SMPPRuntimeException;

public class InvalidParameterValueException extends SMPPRuntimeException {
    public static final int BAD_VALUE_OBJECT = 0;
    public static final int BAD_VALUE_BYTE = 1;
    public static final int BAD_VALUE_CHAR = 2;
    public static final int BAD_VALUE_INT = 3;
    public static final int BAD_VALUE_LONG = 4;
    public static final int BAD_VALUE_FLOAT = 5;
    public static final int BAD_VALUE_DOUBLE = 6;
    public static final int BAD_VALUE_STRING = 7;

    static final long serialVersionUID = 2L;

    private Object invalidValue = new Object();

    private int invalidValueType = BAD_VALUE_OBJECT;

    public InvalidParameterValueException(String msg, byte invalidValue) {
        super(msg);
        this.invalidValue = new Byte(invalidValue);
        this.invalidValueType = BAD_VALUE_BYTE;
    }

    public InvalidParameterValueException(String msg, char invalidValue) {
        super(msg);
        this.invalidValue = new Character(invalidValue);
        this.invalidValueType = BAD_VALUE_CHAR;
    }

    public InvalidParameterValueException(String msg, int invalidValue) {
        super(msg);
        this.invalidValue = new Integer(invalidValue);
        this.invalidValueType = BAD_VALUE_INT;
    }

    public InvalidParameterValueException(String msg, long invalidValue) {
        super(msg);
        this.invalidValue = new Long(invalidValue);
        this.invalidValueType = BAD_VALUE_LONG;
    }

    public InvalidParameterValueException(String msg, float invalidValue) {
        super(msg);
        this.invalidValue = new Float(invalidValue);
        this.invalidValueType = BAD_VALUE_FLOAT;
    }

    public InvalidParameterValueException(String msg, double invalidValue) {
        super(msg);
        this.invalidValue = new Double(invalidValue);
        this.invalidValueType = BAD_VALUE_DOUBLE;
    }

    public InvalidParameterValueException(String msg, String invalidValue) {
        super(msg);
        this.invalidValue = invalidValue;
        this.invalidValueType = BAD_VALUE_STRING;
    }

    public InvalidParameterValueException(String msg, Object invalidValue) {
        super(msg);
        this.invalidValue = invalidValue;
        this.invalidValueType = BAD_VALUE_OBJECT;
    }

    public int getInvalidValueType() {
        return invalidValueType;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}

