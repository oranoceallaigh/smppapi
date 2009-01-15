package com.adenki.smpp;

/**
 * Interface definition for an object that can act as a receiver for
 * an SMPP session.
 * @version $Id$
 * @since 0.4.0
 */
public interface Receiver {
    /**
     * Get the name of this receiver.
     * @return The name of this receiver.
     */
    String getName();
    
    /**
     * Set the name of this receiver.
     * @param name The name of this receiver.
     */
    void setName(String name);
    
    /**
     * Get the session that this receiver is using.
     * @return The SMPP session this receiver is using.
     */
    Session getSession();
    
    /**
     * Se the session that this receiver is using.
     * @param session The SMPP session this receiver is using.
     */
    void setSession(Session session);
    
    /**
     * Test if this receiver is currently started.
     * @return <code>true</code> if this receiver is running, <code>false</code>
     * otherwise.
     */
    boolean isStarted();
    
    /**
     * Start this receiver.
     */
    void start();
    
    /**
     * Stop this receiver. A receiver may not have stopped by the time
     * this method returns. Callers should use the {@link #isStarted} method
     * to ensure the receiver has been fully stopped.
     */
    void stop();
}
