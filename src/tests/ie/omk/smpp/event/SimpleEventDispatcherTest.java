package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;
import junit.framework.TestCase;

/**
 * @version $Id:$
 */
public class SimpleEventDispatcherTest extends TestCase {

    public void testAddObserver() {
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        TestConnectionObserver observer = new TestConnectionObserver();
        dispatcher.addObserver(observer);
        assertTrue(dispatcher.contains(observer));
        assertSame(observer, dispatcher.observerIterator().next());
    }

    public void testAddObserverDetectsDuplicates() {
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        TestConnectionObserver observer = new TestConnectionObserver();
        dispatcher.addObserver(observer);
        assertEquals(1, dispatcher.size());
        dispatcher.addObserver(observer);
        assertEquals(1, dispatcher.size());
    }
    
    public void testRemove() {
        TestConnectionObserver ob1 = new TestConnectionObserver();
        TestConnectionObserver ob2 = new TestConnectionObserver();
        TestConnectionObserver ob3 = new TestConnectionObserver();
        TestConnectionObserver ob4 = new TestConnectionObserver();
        
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        dispatcher.addObserver(ob1);
        dispatcher.addObserver(ob2);
        dispatcher.addObserver(ob3);
        dispatcher.addObserver(ob4);
        assertEquals(4, dispatcher.size());
        
        dispatcher.removeObserver(ob2);
        dispatcher.removeObserver(ob3);
        assertTrue(dispatcher.contains(ob1));
        assertFalse(dispatcher.contains(ob2));
        assertFalse(dispatcher.contains(ob3));
        assertTrue(dispatcher.contains(ob4));

        dispatcher.removeObserver(ob1);
        assertEquals(1, dispatcher.size());
        assertFalse(dispatcher.contains(ob1));
        assertFalse(dispatcher.contains(ob2));
        assertFalse(dispatcher.contains(ob3));
        assertTrue(dispatcher.contains(ob4));
    }
    
    private class TestConnectionObserver implements ConnectionObserver {
        public void packetReceived(Connection source, SMPPPacket packet) {
        }
        public void update(Connection source, SMPPEvent event) {
        }
    }
}
