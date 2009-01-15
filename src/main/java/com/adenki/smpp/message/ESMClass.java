package com.adenki.smpp.message;

/**
 * ESM class enumeration.
 * @version $Id$
 * @since 0.4.0
 */
public class ESMClass {
    /** Mobile Terminated; Normal delivery, no address swapping */
    public static final int SMC_MT = 1;

    /** Mobile originated */
    public static final int SMC_MO = 2;

    /** Mobile Originated / Terminated */
    public static final int SMC_MOMT = 3;

    /** Delivery receipt, no address swapping */
    public static final int SMC_RECEIPT = 4;

    /** Predefined message */
    public static final int SMC_DEFMSG = 8;

    /** Normal delivery , address swapping on */
    public static final int SMC_LOOPBACK_RECEIPT = 16;

    /** Delivery receipt, address swapping on */
    public static final int SMC_RECEIPT_SWAP = 20;

    /** Store message, do not send to Kernel */
    public static final int SMC_STORE = 32;

    /** Store message and send to kernel */
    public static final int SMC_STORE_FORWARD = 36;

    /** Distribution submission */
    public static final int SMC_DLIST = 64;

    /** Multiple recipient submission */
    public static final int SMC_MULTI = 128;

    /** Distribution list and multiple recipient submission */
    public static final int SMC_CAS_DL = 256;

    /** Escalated message FFU */
    public static final int SMC_ESCALATED = 512;

    /** Submit with replace message */
    public static final int SMC_SUBMIT_REPLACE = 1024;

    /** Memory capacity error */
    public static final int SMC_MCE = 2048;
}
