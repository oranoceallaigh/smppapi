package com.adenki.smpp;

/**
 * Numbering plan indicator constants.
 * @version $Id$
 * @since 0.4.0
 */
public class Npi {
    public static final int UNKNOWN = 0;
    public static final int E164 = 1;
    public static final int ISDN = E164;
    public static final int X121 = 3;
    public static final int TELEX = 4;
    public static final int LAND_MOBILE = 6;
    public static final int NATIONAL = 8;
    public static final int PRIVATE = 9;
    public static final int ERMES = 10;
    public static final int IP = 13;
    public static final int RESERVED_EXTN = 15;
    public static final int WAP_CLIENT_ID = 18;
}
