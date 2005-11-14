/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 * $Id$
 */

package ie.omk.smpp.util;

import java.util.Hashtable;

/**
 * GSM constant values. This class includes static values for known Type-of
 * numbers, numbering plan indicators and GSM error codes.
 */
public final class GSMConstants {
    /** No error */
    public static final int GSM_ERR_NONE = 0;

    /** Customer unknown */
    public static final int GSM_ERR_P_UNKNOWN = 1;

    /** Not provisioned */
    public static final int GSM_ERR_P_PROVISION = 11;

    /** Call barred */
    public static final int GSM_ERR_T_BARRED = 13;

    /** CUG reject */
    public static final int GSM_ERR_P_CUG = 15;

    /** SMS not supported by MS */
    public static final int GSM_ERR_T_MSSUPPORT = 19;

    /** error in receiving MS */
    public static final int GSM_ERR_T_MSERROR = 20;

    /** facility not supported */
    public static final int GSM_ERR_T_SUPPORT = 21;

    /** memory capacity exceeded */
    public static final int GSM_ERR_T_MEMCAP = 22;

    /** absent subscriber */
    public static final int GSM_ERR_T_ABSENT = 29;

    /** absent subscriber detached */
    public static final int GSM_ERR_T_ABSENT_DETACHED = 30;

    /** MS didn't respond to 2 pages */
    public static final int GSM_ERR_T_ABSENT_PAGEFAIL = 31;

    /** Subscriber roamed to new PLMN */
    public static final int GSM_ERR_T_SUPPORT_ROAMING = 32;

    /** system failure */
    public static final int GSM_ERR_T_SYSTEM = 36;

    public static final int GSM_TON_UNKNOWN = 0;

    public static final int GSM_TON_INTERNATIONAL = 1;

    public static final int GSM_TON_NATIONAL = 2;

    public static final int GSM_TON_NETWORK = 3;

    public static final int GSM_TON_SUBSCRIBER = 4;

    public static final int GSM_TON_ALPHANUMERIC = 5;

    public static final int GSM_TON_ABBREVIATED = 6;

    public static final int GSM_TON_RESERVED_EXTN = 7;

    public static final int GSM_NPI_UNKNOWN = 0;

    public static final int GSM_NPI_E164 = 1;

    public static final int GSM_NPI_ISDN = GSM_NPI_E164;

    public static final int GSM_NPI_X121 = 3;

    public static final int GSM_NPI_TELEX = 4;

    public static final int GSM_NPI_NATIONAL = 8;

    public static final int GSM_NPI_PRIVATE = 9;

    public static final int GSM_NPI_ERMES = 10;

    public static final int GSM_NPI_RESERVED_EXTN = 15;

    private static Hashtable errorTable = new Hashtable();

    private static void initTables() {
        synchronized (errorTable) {
            if (errorTable.size() > 0)
                return;
        }

        // Error table
        errorTable.put(new Integer(GSM_ERR_NONE), "No error");
        errorTable.put(new Integer(GSM_ERR_P_UNKNOWN), "Unknown Customer");
        errorTable.put(new Integer(GSM_ERR_P_PROVISION), "Not provisioned");
        errorTable.put(new Integer(GSM_ERR_T_BARRED), "Call is barred");
        errorTable.put(new Integer(GSM_ERR_P_CUG), "CUG Rejected");
        errorTable.put(new Integer(GSM_ERR_T_MSSUPPORT),
                "Sms not supported by MS");
        errorTable
                .put(new Integer(GSM_ERR_T_MSERROR), "Error in receiveing MS");
        errorTable
                .put(new Integer(GSM_ERR_T_SUPPORT), "Facility not supported");
        errorTable.put(new Integer(GSM_ERR_T_MEMCAP),
                "Memory capacity exceeded");
        errorTable.put(new Integer(GSM_ERR_T_ABSENT), "Absent Subscriber");
        errorTable.put(new Integer(GSM_ERR_T_ABSENT_DETACHED),
                "Absent Subscriber detached");
        errorTable.put(new Integer(GSM_ERR_T_ABSENT_PAGEFAIL),
                "MS did not respond to 2 pages");
        errorTable.put(new Integer(GSM_ERR_T_SUPPORT_ROAMING),
                "Subscriber roamed to new PLMN");
        errorTable.put(new Integer(GSM_ERR_T_SYSTEM), "System failure");
    }

    /**
     * Get a string for the GSM error code.
     */
    public static final String getGsmErr(int code) {
        initTables();
        String s = (String) errorTable.get(new Integer(code));
        return (s == null ? "" : s);
    }

    // Prevent class from being instansiated.
    private GSMConstants() {
    }
}