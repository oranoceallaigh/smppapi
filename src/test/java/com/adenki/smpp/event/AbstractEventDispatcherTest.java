package com.adenki.smpp.event;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

@Test
public class AbstractEventDispatcherTest {

    public void testAddingAndRemovingObservers() throws Exception {
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        EasyMock.replay(observer1);
        SessionObserver observer2 = EasyMock.createMock(SessionObserver.class);
        EasyMock.replay(observer2);
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        assertEquals(dispatcher.size(), 0);
        assertFalse(dispatcher.contains(observer1));
        assertFalse(dispatcher.contains(observer2));
        dispatcher.addObserver(observer1);
        dispatcher.addObserver(observer2);
        assertEquals(dispatcher.size(), 2);
        assertTrue(dispatcher.contains(observer1));
        assertTrue(dispatcher.contains(observer2));
        dispatcher.addObserver(observer1);
        assertEquals(dispatcher.size(), 2);
        assertTrue(dispatcher.contains(observer1));
        assertTrue(dispatcher.contains(observer2));
        dispatcher.removeObserver(observer1);
        assertEquals(dispatcher.size(), 1);
        assertFalse(dispatcher.contains(observer1));
        assertTrue(dispatcher.contains(observer2));
        dispatcher.removeObserver(observer2);
        assertEquals(dispatcher.size(), 0);
        assertFalse(dispatcher.contains(observer1));
        assertFalse(dispatcher.contains(observer2));
        EasyMock.verify(observer1);
        EasyMock.verify(observer2);
    }
    
    public void testGetAllObserversReturnsCollectionOfObservers() {
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        EasyMock.replay(observer1);
        SessionObserver observer2 = EasyMock.createMock(SessionObserver.class);
        EasyMock.replay(observer2);
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        dispatcher.addObserver(observer1);
        dispatcher.addObserver(observer2);
        Collection<SessionObserver> observers = dispatcher.getObservers();
        assertNotNull(observers);
        assertEquals(observers.size(), 2);
        assertTrue(observers.contains(observer1));
        assertTrue(observers.contains(observer2));
        EasyMock.verify(observer1);
        EasyMock.verify(observer2);
    }

    @Test(expectedExceptions = {UnsupportedOperationException.class})
    public void testGetAllObserversReturnsUnmodifiableCollection() {
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        Collection<SessionObserver> observers = dispatcher.getObservers();
        assertNotNull(observers);
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        EasyMock.replay(observer1);
        observers.add(observer1);
        EasyMock.verify(observer1);
    }
    
    @Test(expectedExceptions = {UnsupportedOperationException.class})
    public void testObserverIteratorIsUnmodifiable() {
        SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
        SessionObserver observer1 = EasyMock.createMock(SessionObserver.class);
        EasyMock.replay(observer1);
        dispatcher.addObserver(observer1);
        Iterator<SessionObserver> iter = dispatcher.observerIterator();
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        assertNotNull(iter.next());
        iter.remove();
    }
}
