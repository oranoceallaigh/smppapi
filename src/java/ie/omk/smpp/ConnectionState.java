package ie.omk.smpp;

/**
 * Enumeration of connection states.
 * @version $Id:$
 */
public enum ConnectionState {
    UNBOUND(0),
    BINDING(1),
    UNBINDING(2),
    BOUND(3);
    
    private int state;
    
    private ConnectionState(int state) {
        this.state = state;
    }
    
    public int intValue() {
        return state;
    }
    
    public static final ConnectionState valueOf(int value) {
        ConnectionState[] states = ConnectionState.values();
        if (value >=0 && value < states.length) {
            return states[value];
        } else {
            return null;
        }
    }
}
