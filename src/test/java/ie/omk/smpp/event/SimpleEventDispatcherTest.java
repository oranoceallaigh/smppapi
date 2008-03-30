package ie.omk.smpp.event;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import ie.omk.smpp.Session;
import ie.omk.smpp.message.SMPPPacket;

import org.testng.annotations.Test;

/**
 * @version $Id:$
 */
@Test
public class SimpleEventDispatcherTest {

    public void testAddObserver() {
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        TestConnectionObserver observer = new TestConnectionObserver();
        dispatcher.addObserver(observer);
        assertTrue(dispatcher.contains(observer));
        assertSame(dispatcher.observerIterator().next(), observer);
    }

    public void testAddObserverDetectsDuplicates() {
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        TestConnectionObserver observer = new TestConnectionObserver();
        dispatcher.addObserver(observer);
        assertEquals(dispatcher.size(), 1);
        dispatcher.addObserver(observer);
        assertEquals(dispatcher.size(), 1);
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
        assertEquals(dispatcher.size(), 4);
        
        dispatcher.removeObserver(ob2);
        dispatcher.removeObserver(ob3);
        assertTrue(dispatcher.contains(ob1));
        assertFalse(dispatcher.contains(ob2));
        assertFalse(dispatcher.contains(ob3));
        assertTrue(dispatcher.contains(ob4));

        dispatcher.removeObserver(ob1);
        assertEquals(dispatcher.size(), 1);
        assertFalse(dispatcher.contains(ob1));
        assertFalse(dispatcher.contains(ob2));
        assertFalse(dispatcher.contains(ob3));
        assertTrue(dispatcher.contains(ob4));
    }
    
    private class TestConnectionObserver implements SessionObserver {
        public void packetReceived(Session source, SMPPPacket packet) {
        }
        public void update(Session source, SMPPEvent event) {
        }
    }
}
