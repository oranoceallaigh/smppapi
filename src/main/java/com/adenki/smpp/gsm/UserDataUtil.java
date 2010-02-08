package com.adenki.smpp.gsm;

import java.util.ArrayList;
import java.util.List;

import com.adenki.smpp.Address;
import com.adenki.smpp.message.DataSM;
import com.adenki.smpp.message.DeliverSM;
import com.adenki.smpp.message.SubmitSM;
import com.adenki.smpp.message.tlv.Tag;

/**
 * User data utility methods.
 * @version $Id$
 */
public final class UserDataUtil {

    private UserDataUtil() {
    }
    
    /**
     * Create {@link SubmitSM} packets from segments created by a
     * {@link UserData} implementation. This method sets the ESM class
     * on every packet to indicate the user data header is present.
     * @param segments The segments to create packets from.
     * @param from The source address to set on every packet, may be
     * <tt>null</tt>.
     * @param to The destination address to set on every packet.
     * @return An array of created <tt>SubmitSM</tt> packets.
     */
    public static SubmitSM[] createSubmits(
            byte[][] segments,
            Address from,
            Address to) {
        List<SubmitSM> packets = new ArrayList<SubmitSM>();
        for (byte[] segment : segments) {
            SubmitSM p = new SubmitSM();
            p.setSource(from);
            p.setDestination(to);
            p.setEsmClass(0x40);
            p.setMessage(segment);
        }
        return packets.toArray(new SubmitSM[packets.size()]);
    }
    
    /**
     * Create {@link DeliverSM} packets from segments created by a
     * {@link UserData} implementation. This method sets the ESM class
     * on every packet to indicate the user data header is present.
     * @param segments The segments to create packets from.
     * @param from The source address to set on every packet, may be
     * <tt>null</tt>.
     * @param to The destination address to set on every packet.
     * @return An array of created <tt>DeliverSM</tt> packets.
     */
    public static DeliverSM[] createDelivers(
            byte[][] segments,
            Address from,
            Address to) {
        List<DeliverSM> packets = new ArrayList<DeliverSM>();
        for (byte[] segment : segments) {
            DeliverSM p = new DeliverSM();
            p.setSource(from);
            p.setDestination(to);
            p.setEsmClass(0x40);
            p.setMessage(segment);
        }
        return packets.toArray(new DeliverSM[packets.size()]);
    }
    
    /**
     * Create {@link DataSM} packets from segments created by a
     * {@link UserData} implementation. This method sets the ESM class
     * on every packet to indicate the user data header is present.
     * @param segments The segments to create packets from.
     * @param from The source address to set on every packet, may be
     * <tt>null</tt>.
     * @param to The destination address to set on every packet.
     * @return An array of created <tt>DataSM</tt> packets.
     */
    public static DataSM[] createDataSM(
            byte[][] segments,
            Address from,
            Address to) {
        List<DataSM> packets = new ArrayList<DataSM>();
        for (byte[] segment : segments) {
            DataSM p = new DataSM();
            p.setSource(from);
            p.setDestination(to);
            p.setEsmClass(0x40);
            p.setTLV(Tag.MESSAGE_PAYLOAD, segment);
        }
        return packets.toArray(new DataSM[packets.size()]);
    }
}
