package ie.omk.smpp.event;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.net.StreamLink;

public class FIFOQueueTest extends TestCase {

    private Connection conn;
    private SMPPPacket pak;
    
    public void setUp() {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        conn = new Connection(new StreamLink(in, out));
        pak = new SubmitSM();
    }
    
    public void testEmptyQueue() throws Exception {
        FIFOQueue q  = new FIFOQueue(3);
        assertTrue(q.isEmpty());
        assertFalse(q.isFull());
    }
    
    public void testFullQueue() throws Exception {
        FIFOQueue q  = new FIFOQueue(3);
        q.put(conn, pak);
        q.put(conn, pak);
        q.put(conn, pak);
        assertTrue(q.isFull());
        assertFalse(q.isEmpty());
    }
    
    public void testExceptionIsThrownWhenPutCalledOnFullQueue() throws Exception {
        FIFOQueue q  = new FIFOQueue(3);
        q.put(conn, pak);
        q.put(conn, pak);
        q.put(conn, pak);
        try {
            q.put(conn, pak);
            fail("QueueFullException should have been thrown.");
        } catch (QueueFullException x) {
            // success
        }
    }
    
    public void testSize() throws Exception {
        FIFOQueue q  = new FIFOQueue(3);
        assertEquals(0, q.size());
        q.put(conn, pak);
        assertEquals(1, q.size());
        q.put(conn, pak);
        assertEquals(2, q.size());
        q.put(conn, pak);
        assertEquals(3, q.size());
        q.get();
        assertEquals(2, q.size());
        q.get();
        assertEquals(1, q.size());
        q.put(conn, pak);
        assertEquals(2, q.size());
        q.put(conn, pak);
        assertEquals(3, q.size());
        q.get();
        assertEquals(2, q.size());
        q.put(conn, pak);
        assertEquals(3, q.size());
        q.get();
        assertEquals(2, q.size());
        q.get();
        assertEquals(1, q.size());
        q.get();
        assertEquals(0, q.size());
    }
    
    public void testHeadAndTailWrapAround() throws Exception {
        FIFOQueue q  = new FIFOQueue(3);
        q.put(conn, pak);
        q.put(conn, pak);
        q.put(conn, pak);
        assertEquals(3, q.size());
        assertTrue(q.isFull());
        q.get();
        q.get();
        assertEquals(1, q.size());
        assertFalse(q.isFull());
        assertFalse(q.isEmpty());
        // tail wraps after this call
        q.put(conn, pak);
        assertEquals(2, q.size());
        assertFalse(q.isFull());
        assertFalse(q.isEmpty());
        q.put(conn, pak);
        assertEquals(3, q.size());
        assertTrue(q.isFull());
        assertFalse(q.isEmpty());
        q.get();
        // head wraps after this call:
        q.get();
        assertEquals(1, q.size());
        assertFalse(q.isFull());
        assertFalse(q.isEmpty());
    }
}
