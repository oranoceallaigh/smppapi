package ie.omk.smpp.message;

import ie.omk.smpp.util.SMPPDate;

import org.slf4j.LoggerFactory;

/**
 * Deliver message. This message is sent from the SMSC to a Receiver ESME to
 * deliver a short message. It is also used to notify an ESME that submitted a
 * message using registered delivery that a message has reached it's end point
 * successfully.
 * 
 * @author Oran Kelly
 * @version $Id: $
 */
public class DeliverSM extends SMPacket {
    private static final String SPEC_VIOLATION = "Setting the {} on a "
        + "deliver_sm is in violation of the SMPP specification";
    
    /**
     * Construct a new DeliverSM.
     */
    public DeliverSM() {
        super(SMPPPacket.DELIVER_SM);
    }

    /**
     * Setting a delivery time on a deliver_sm is in violation of the SMPP
     * specification.
     */
    public void setDeliveryTime(SMPPDate d) {
        LoggerFactory.getLogger(DeliverSM.class).warn(
                SPEC_VIOLATION, "delivery time");
        super.setDeliveryTime(d);
    }

    /**
     * Setting an expiry time on a deliver_sm is in violation of the SMPP
     * specification.
     */
    public void setExpiryTime(SMPPDate d) {
        LoggerFactory.getLogger(DeliverSM.class).warn(
                SPEC_VIOLATION, "expiry time");
        super.setExpiryTime(d);
    }
}
