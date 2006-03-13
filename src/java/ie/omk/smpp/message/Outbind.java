package ie.omk.smpp.message;

/**
 * $Id:$
 */
public class Outbind extends SMPPRequest {

    private String systemId;
    private String password;
    
    public Outbind() {
        super(SMPPPacket.OUTBIND);
    }
    
    public String getSystemId() {
        return systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBodyLength() {
        int len = 2;
        if (systemId != null) {
            len += systemId.length();
        }
        if (password != null) {
            len += password.length();
        }
        return len;
    }

    protected void readBodyFrom(byte[] b, int offset)
            throws SMPPProtocolException {
    }
}
