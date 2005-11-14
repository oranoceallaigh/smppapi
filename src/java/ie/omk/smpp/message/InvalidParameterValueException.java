/*
 * Java SMPP API Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net Java SMPP API Homepage:
 * http://smppapi.sourceforge.net/ $Id: InvalidParameterValueException.java,v
 * 1.1 2002/10/13 16:09:30 orank Exp $
 */
package ie.omk.smpp.message;

public class InvalidParameterValueException extends
        ie.omk.smpp.SMPPRuntimeException {

    public static final int BAD_VALUE_OBJECT = 0;

    public static final int BAD_VALUE_BYTE = 1;

    public static final int BAD_VALUE_CHAR = 2;

    public static final int BAD_VALUE_INT = 3;

    public static final int BAD_VALUE_LONG = 4;

    public static final int BAD_VALUE_FLOAT = 5;

    public static final int BAD_VALUE_DOUBLE = 6;

    public static final int BAD_VALUE_STRING = 7;

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
        return (invalidValueType);
    }

    public Object getInvalidValue() {
        return (invalidValue);
    }
}