package com.adenki.smpp.gsm;

/**
 * GSM error codes.
 * @version $Id$
 * @since 0.4.0
 */
public enum GSMError {
    ERR_NONE(0),
    ERR_P_UNKNOWN(1),
    ERR_P_PROVISION(11),
    ERR_T_BARRED(13),
    ERR_P_CUG(15),
    ERR_T_MSSUPPORT(19),
    ERR_T_MSERROR(20),
    ERR_T_SUPPORT(21),
    ERR_T_MEMCAP(22),
    ERR_T_ABSENT(29),
    ERR_T_ABSENT_DETACHED(30),
    ERR_T_ABSENT_PAGEFAIL(31),
    ERR_T_SUPPORT_ROAMING(32),
    ERR_T_SYSTEM(36);
    
    private int code;
    
    private GSMError(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}
