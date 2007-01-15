/*
 * Created on 16-May-2005
 */
package ie.omk.smpp.examples;

import ie.omk.smpp.Connection;
import ie.omk.smpp.ConnectionType;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.tools.ant.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author orank
 */
public class SMPPAPIExample extends Task {
    private static final Logger LOG = LoggerFactory.getLogger(SMPPAPIExample.class);
    
    protected String hostName = null;
    
    protected int port = 0;
    
    protected String systemID = null;
    
    protected String systemType = null;
    
    protected String password = null;
    
    protected int sourceTON = 0;
    
    protected int sourceNPI = 0;
    
    protected String sourceAddress = null;

    protected Connection myConnection = null;
    

    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName The hostName to set.
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return Returns the port.
     */
    public int getPort() {
        return port;
    }
    /**
     * @param port The port to set.
     */
    public void setPort(int port) {
        this.port = port;
    }
    /**
     * @return Returns the sourceAddress.
     */
    public String getSourceAddress() {
        return sourceAddress;
    }
    /**
     * @param sourceAddress The sourceAddress to set.
     */
    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
    /**
     * @return Returns the sourceNPI.
     */
    public int getSourceNPI() {
        return sourceNPI;
    }
    /**
     * @param sourceNPI The sourceNPI to set.
     */
    public void setSourceNPI(int sourceNPI) {
        this.sourceNPI = sourceNPI;
    }
    /**
     * @return Returns the sourceTON.
     */
    public int getSourceTON() {
        return sourceTON;
    }
    /**
     * @param sourceTON The sourceTON to set.
     */
    public void setSourceTON(int sourceTON) {
        this.sourceTON = sourceTON;
    }
    /**
     * @return Returns the systemID.
     */
    public String getSystemID() {
        return systemID;
    }
    /**
     * @param systemID The systemID to set.
     */
    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }
    /**
     * @return Returns the systemType.
     */
    public String getSystemType() {
        return systemType;
    }
    /**
     * @param systemType The systemType to set.
     */
    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }
    
    protected Connection createConnection() throws UnknownHostException {
        myConnection = new Connection(hostName, port);
        return myConnection;
    }

    protected void closeConnection() {
        try {
            myConnection.closeLink();
        } catch (Exception x) {
            LOG.error("Could not close the SMSC link", x);
        }
    }
    
    protected void doBind(ConnectionType connectionType) throws IOException {
        myConnection.bind(
                connectionType,
                systemID,
                password,
                systemType,
                sourceTON,
                sourceNPI,
                sourceAddress);
    }
}
