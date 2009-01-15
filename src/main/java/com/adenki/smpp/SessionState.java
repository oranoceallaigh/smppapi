package com.adenki.smpp;

/**
 * Enumeration of connection states.
 * @version $Id$
 */
public enum SessionState {
    UNBOUND(0),
    BINDING(1),
    UNBINDING(2),
    BOUND(3);
    
    private int state;
    
    private SessionState(int state) {
        this.state = state;
    }
    
    public int intValue() {
        return state;
    }
    
    public static final SessionState valueOf(int value) {
        SessionState[] states = SessionState.values();
        if (value >=0 && value < states.length) {
            return states[value];
        } else {
            return null;
        }
    }
}
