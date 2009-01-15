package com.adenki.smpp.message;

import org.slf4j.LoggerFactory;

import com.adenki.smpp.util.SMPPDate;

/**
 * Deliver message. This message is sent from the SMSC to a Receiver ESME to
 * deliver a short message. It is also used to notify an ESME that submitted a
 * message using registered delivery that a message has reached it's end point
 * successfully.
 * 
 * @version $Id$
 */
public class DeliverSM extends SubmitSM {
    private static final long serialVersionUID = 2L;
    private static final String SPEC_VIOLATION = "Setting the {} on a "
        + "deliver_sm is in violation of the SMPP specification";
    
    /**
     * Construct a new DeliverSM.
     */
    public DeliverSM() {
        super(CommandId.DELIVER_SM);
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

    @Override
    public int hashCode() {
        return super.hashCode() * 103;
    }
}
