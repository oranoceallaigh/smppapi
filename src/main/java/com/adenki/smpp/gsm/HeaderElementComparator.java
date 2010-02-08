package com.adenki.smpp.gsm;

import java.util.Comparator;

/**
 * Comparator of {@link HeaderElements} that allows elements to be ordered
 * based on whether they repeat across concatenated messages or not.
 */
public class HeaderElementComparator implements Comparator<HeaderElement> {

    public int compare(HeaderElement o1, HeaderElement o2) {
        if (o1.isRecurring()) {
            if (o2.isRecurring()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (!o2.isRecurring()) {
                return 0;
            } else {
                return 1;
            }
        }
    }

}
