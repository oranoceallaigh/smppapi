# Java SMPPAPI

## Introduction
The Java smppapi is a library enabling applications to communicate with Short
Message Service Centres (SMSCs) using the "Short Message Peer to Peer" (SMPP)
protocol.

## License
The smppapi is released under a BSD-style license.

## Interface Versions
The smppapi library supports versions 3.3 and 3.4 of SMPP. Preliminary support
for 5.0 has begun but is still experimental.

## Ease Of Use versus Low-level Access
The smppapi endeavours to provide a straight-forward and easy to use interface
to SMPP. However, it does not attempt to hide the low-level details of the
protocol from applications; if an application requires access to these low-level
details, then we attempt to allow that access.

## Getting Started
The basic procedure for communicating with an SMSC is

1. Establish a network-level connection (TCP) to the SMSC.
2. Bind to the SMSC as a receiver, transmitter or transceiver.
3. Do SMPP operations.
4. Unbind.
5. Shut down the network connection.

### Network Connection
Network connections are represented by implementations of the
`com.adenki.smpp.net.SmscLink` interface. The most commonly used implementation
(and the only one provided by smppapi) is the `com.adenki.smpp.net.TcpLink`
class, which uses TCP/IP to communicate with the SMSC. Instantiate one with
either a host and port or a `java.net.Socket`.

```java
TcpLink tcpLink = new TcpLink("smsc.example.com", 2775);
TcpLink tcpLink = new TcpLink(mySocket);
```

### Binding to the SMSC
SMPP sessions are represented by implementations of the
`com.adenki.smpp.Session` interface, of which a default implementations is
provided `com.adenki.smpp.SessionImpl`. Instantiate one wrapping an `SmscLink`.

```java
Session session = new SessionImpl(tcpLink);
```

There is also a convenience constructor in `SessionImpl` that will handle
establishing a `TcpLink` for you:

```java
Session session = new SessionImpl("smsc.example.com", 2775);
```

Next, bind to the SMSC as the type of link you require.

```java
session.bind(
    SessionType.TRANSCEIVER,
    "systemId",
    "password",
    "systemType"
);
```

The bind details are provided to you by your SMSC provider. There are two other
forms of the `bind` method, one that accepts the bind address range should you
need to supply one and another that simple accepts a
`com.adenki.smpp.message.Bind` packet should you need more control over the bind
operation.

### Sending and Receiving Packets
smppapi uses an event-based approach to interacting with applications that use
it. Internally, it will listen for packets sent by the SMSC and deliver them
asynchronously to listeners registered with it.

Applications supply implementations of the
`com.adenki.smpp.event.SessionObserver` interface to the `Session.addObserver`
method. Once registered, any packet received from the SMSC will be notified to
observers via their `packetReceived` method. There are also some
smppapi-specific internal events that will be notified to applications via the
`update` method. See the `com.adenki.smpp.event.SMPPEvent` class for further
details.

### Synchronous Communication
For simple applications, a wrapper class is provided which mimics synchronous
communications with the SMSC, where the wrapper will block a calling thread when
an SMPP packet is sent to the SMSC until the corresponding response packet is
received and can be returned to the caller. This functionality is provided by
the `com.adenki.smpp.util.SyncWrapper` class. A `Session` should first be
established as discussed above, and then a `SyncWrapper` instantiated to
decorate it.

```java
SyncWrapper syncWrapper = new SyncWrapper(session);
```

### Auto-responder
When a session is established with an SMSC, it is possible for the SMSC to
initiate certain requests to the client, such as `EnquireLink, DataSM,
DeliverSM` and `Unbind`. Applications may choose to handle these themselves, or
they can register a `com.adenki.smpp.util.AutoResponder` which will
automatically generate a response packet and send it back to the SMSC.

```java
session.addObserver(new AutoResponder(true));
```
