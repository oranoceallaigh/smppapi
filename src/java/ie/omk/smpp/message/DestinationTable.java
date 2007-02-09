package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.util.ParsePosition;
import ie.omk.smpp.util.SMPPIO;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;

/**
 * A table of destinations, primarily used in
 * {@link ie.omk.smpp.message.SubmitMulti}.
 * @version $Id$
 */
public class DestinationTable implements Serializable {
    private static final long serialVersionUID = 1;
    
    private List<Address> addresses = new ArrayList<Address>();
    private List<String> distributionLists = new ArrayList<String>();

    /**
     * The length is the total number of bytes the table would encode as.
     */
    private int length;

    public DestinationTable() {
    }

    public void add(Address addr) {
        addresses.add(addr);
        // Plus 1 for the dest type flag.
        length += addr.getLength() + 1;
    }

    public void add(String distributionList) {
        distributionLists.add(distributionList);
        // nul byte plus dest type flag
        length += distributionList.length() + 2;
    }

    public void remove(Address addr) {
        int i = addresses.indexOf(addr);
        if (i > -1) {
            length -= addresses.remove(i).getLength() + 1;
        }
    }

    public void remove(String distributionList) {
        int i = distributionLists.indexOf(distributionList);
        if (i > -1) {
            length -= distributionLists.remove(i).length() + 2;
        }
    }

    public int getLength() {
        return length;
    }

    public int size() {
        return addresses.size() + distributionLists.size();
    }

    public Collection<Address> getAddresses() {
        return Collections.unmodifiableCollection(addresses);
    }
    
    public Collection<String> getDistributionLists() {
        return Collections.unmodifiableCollection(distributionLists);
    }
    
    public void writeTo(OutputStream out) throws java.io.IOException {
        for (Address address : addresses) {
            SMPPIO.writeByte(1, out);
            address.writeTo(out);
        }
        for (String list : distributionLists) {
            SMPPIO.writeByte(2, out);
            SMPPIO.writeCString(list, out);
        }
    }

    public void readFrom(byte[] table, ParsePosition position, int count) {
        for (int i = 0; i < count; i++) {
            int type = SMPPIO.bytesToByte(table, position.getIndex());
            position.inc();
            if (type == 1) {
                // SME address..
                Address a = new Address();
                a.readFrom(table, position);
                addresses.add(a);
            } else if (type == 2) {
                // Distribution list name
                String d = SMPPIO.readCString(table, position.getIndex());
                position.inc(d.length() + 1);
                distributionLists.add(d);
            } else {
                LoggerFactory.getLogger(DestinationTable.class).warn(
                        "Unidentified destination type on input.");
            }
        }
        calculateLength();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DestinationTable)) {
            return false;
        }
        DestinationTable other = (DestinationTable) obj;
        return length == other.length
                && addresses.equals(other.addresses)
                && distributionLists.equals(other.distributionLists); 
    }
    
    public int hashCode() {
        return addresses.hashCode() + distributionLists.hashCode();
    }
    
    public String toString() {
        List<Object> list = new ArrayList<Object>();
        list.addAll(addresses);
        list.addAll(distributionLists);
        return list.toString();
    }
    
    private void calculateLength() {
        // One byte for all type flags, plus 1 (null) byte for each distribution
        // list string
        length = addresses.size() + (distributionLists.size() * 2);
        for (Address address : addresses) {
            // For the destination type flag
            length += address.getLength();
        }
        for (String list : distributionLists) {
            length += list.length();
        }
    }
}
