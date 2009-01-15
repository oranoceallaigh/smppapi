package com.adenki.smpp.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.adenki.smpp.Session;
import com.adenki.smpp.message.EnquireLink;
import com.adenki.smpp.message.SMPPPacket;

@Test
public class TaskExecutorEventDispatcherTest {

    public void testNotifyEventWithDefaultExecutor() throws Exception {
        TaskExecutorEventDispatcher dispatcher = new TaskExecutorEventDispatcher();
        dispatcher.setThreadCount(1);
        dispatcher.init();
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        observer1.update(
                (Session) EasyMock.isNull(),
                EasyMock.isA(SMPPEvent.class));
        EasyMock.replay(observer1);
        dispatcher.addObserver(observer1);
        dispatcher.notifyObservers(null, new ReceiverExitEvent(null));
        ExecutorService executorService =
            (ExecutorService) dispatcher.getExecutor();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        EasyMock.verify(observer1);
    }
    
    public void testNotifyPacketWithDefaultExecutor() throws Exception {
        TaskExecutorEventDispatcher dispatcher = new TaskExecutorEventDispatcher();
        dispatcher.setThreadCount(1);
        dispatcher.init();
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        observer1.packetReceived(
                (Session) EasyMock.isNull(),
                EasyMock.isA(SMPPPacket.class));
        EasyMock.replay(observer1);
        dispatcher.addObserver(observer1);
        dispatcher.notifyObservers(null, new EnquireLink());
        ExecutorService executorService =
            (ExecutorService) dispatcher.getExecutor();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        EasyMock.verify(observer1);
    }
}
