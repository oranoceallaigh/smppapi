package com.adenki.smpp;

import com.adenki.smpp.event.SessionObserver;
import com.adenki.smpp.message.Bind;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.net.SmscLink;
import com.adenki.smpp.version.SMPPVersion;

import java.io.IOException;

/**
 */
public interface Session {
    String getSessionId();

    void addObserver(SessionObserver observer);

    void removeObserver(SessionObserver observer);

    Receiver getReceiver();

    void setReceiver(Receiver receiver);

    SmscLink getSmscLink();

    SMPPVersion getVersion();

    void setVersion(SMPPVersion version);

    boolean isValidating();

    void setValidating(boolean validating);

    void bind(SessionType type,
              String systemID,
              String password,
              String systemType) throws IOException;

    void bind(SessionType type,
              String systemID,
              String password,
              String systemType,
              int typeOfNumber,
              int numberPlanIndicator,
              String addressRange) throws IOException;

    void bind(Bind bindRequest) throws IOException;

    void unbind() throws IOException;

    void send(SMPPPacket packet) throws IOException;

    void closeLink() throws IOException;

    SessionState getState();
}
