package com.adenki.smpp.event;

import static org.testng.Assert.assertTrue;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.adenki.smpp.Session;
import com.adenki.smpp.message.SMPPPacket;
import com.adenki.smpp.message.SubmitSM;

/**
 * @version $Id:$
 */
@Test
public class SimpleEventDispatcherTest {
    
    public void testConstructWithObserver() {
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        EasyMock.replay(observer1);
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher(observer1);
        assertTrue(dispatcher.contains(observer1));
        EasyMock.verify(observer1);
    }
    
    public void testNotifyOfEvent() {
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        observer1.update(
                (Session) EasyMock.isNull(),
                EasyMock.isA(ReceiverExitEvent.class));
        EasyMock.replay(observer1);
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher(observer1);
        dispatcher.notifyObservers(null, new ReceiverExitEvent(null));
        EasyMock.verify(observer1);
    }
    
    public void testNotifyOfPacket() {
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        observer1.packetReceived(
                (Session) EasyMock.isNull(),
                EasyMock.isA(SMPPPacket.class));
        EasyMock.replay(observer1);
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher(observer1);
        assertTrue(dispatcher.contains(observer1));
        dispatcher.notifyObservers(null, new SubmitSM());
        EasyMock.verify(observer1);
    }
}
