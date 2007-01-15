package ie.omk.smpp.message;

import ie.omk.smpp.Address;

import java.util.List;

/**
 * Alert notification. This packet type is sent from the SMSC to an ESME to
 * signal that a particular mobile subscriber has become available and a
 * delivery pending flag had previously been set for that subscriber by a
 * <code>data_sm</code> packet.
 * <p>
 * Note that there is no response packet to an <code>alert_notification</code>.
 * 
 * @version 1.0
 * @author Oran Kelly
 */
public class AlertNotification extends SMPPPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    private Address source;
    private Address destination;

    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.ADDRESS);
    }
    
    /**
     * Create a new alert_notification object.
     */
    public AlertNotification() {
        super(ALERT_NOTIFICATION);
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public Address getSource() {
        return source;
    }

    public void setSource(Address source) {
        this.source = source;
    }

    public String toString() {
        return "alert_notification";
    }

    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        return new Object[] {
                source,
                destination,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        source = (Address) params.get(0);
        destination = (Address) params.get(1);
    }
}

