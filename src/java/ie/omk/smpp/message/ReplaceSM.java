package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.message.param.BytesParamDescriptor;
import ie.omk.smpp.message.param.ParamDescriptor;
import ie.omk.smpp.util.SMPPDate;
import ie.omk.smpp.version.SMPPVersion;

import java.util.List;

/**
 * Replace a message. This message submits a short message to the SMSC replacing
 * a previously submitted message.
 * 
 * @author Oran Kelly
 * @version $Id: $
 */
public class ReplaceSM extends SMPacket {
    private static final BodyDescriptor BODY_DESCRIPTOR = new BodyDescriptor();
    
    static {
        BODY_DESCRIPTOR.add(ParamDescriptor.CSTRING)
        .add(ParamDescriptor.ADDRESS)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.DATE)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(ParamDescriptor.INTEGER1)
        .add(new BytesParamDescriptor(6));
    }
    
    /**
     * Construct a new ReplaceSM.
     */
    public ReplaceSM() {
        super(REPLACE_SM);
    }
    
    @Override
    protected void toString(StringBuffer buffer) {
        buffer.append("messageId=").append(messageId)
        .append(",source=").append(source)
        .append(",deliveryTime=").append(deliveryTime)
        .append(",expiryTime=").append(expiryTime)
        .append(",registered=").append(registered)
        .append(",defaultMsg=").append(defaultMsg)
        .append(",length=").append(getMessageLen())
        .append(",message=").append(message);
    }
    
    @Override
    protected void validateMandatory(SMPPVersion smppVersion) {
        smppVersion.validateMessageId(messageId);
        smppVersion.validateAddress(source);
        smppVersion.validateRegisteredDelivery(registered);
        smppVersion.validateDefaultMsg(defaultMsg);
        smppVersion.validateMessage(message, 0, getMessageLen());
    }
    
    @Override
    protected BodyDescriptor getBodyDescriptor() {
        return BODY_DESCRIPTOR;
    }
    
    @Override
    protected Object[] getMandatoryParameters() {
        int length = 0;
        if (message != null) {
            length = message.length;
        }
        return new Object[] {
                messageId,
                source,
                deliveryTime,
                expiryTime,
                Integer.valueOf(registered),
                Integer.valueOf(defaultMsg),
                Integer.valueOf(length),
                message,
        };
    }
    
    @Override
    protected void setMandatoryParameters(List<Object> params) {
        messageId = (String) params.get(0);
        source = (Address) params.get(1);
        deliveryTime = (SMPPDate) params.get(2);
        expiryTime = (SMPPDate) params.get(3);
        registered = ((Number) params.get(4)).intValue();
        defaultMsg = ((Number) params.get(5)).intValue();
        // index 6 intentionally skipped
        message = (byte[]) params.get(7);
    }
}
