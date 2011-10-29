package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;

/**
 * A simple implementation of a FIFO queue. Need this to be as minimal as
 * possible so it's zippidy quick. No synchronization is done here, it's
 * handled by the relevant ThreadedEventDispatcher methods.
 */
class FIFOQueue {
    private static final int FULL = -1;
    private int head;
    private int tail;
    private NotificationDetails[] queue;

    public FIFOQueue(int queueSize) {
        if (queueSize < 1) {
            queueSize = 100;
        }
        queue = new NotificationDetails[queueSize];
        for (int i = 0; i < queueSize; i++) {
            queue[i] = new NotificationDetails();
        }
    }

    public void put(Connection c, SMPPPacket p) throws QueueFullException {
        put(c, null, p);
    }

    public void put(Connection c, SMPPEvent e) throws QueueFullException {
        put(c, e, null);
    }

    public NotificationDetails get() {
        NotificationDetails nd = null;
        if (!isEmpty()) {
            if (tail == FULL) {
                tail = head;
            }
            nd = queue[head++];
            if (head == queue.length) {
                head = 0;
            }
        }
        return nd;
    }

    public boolean isEmpty() {
        return tail == head;
    }

    public boolean isFull() {
        return tail == FULL;
    }
    
    public int capacity() {
        return queue.length;
    }
    
    public int size() {
        if (tail == FULL) {
            return capacity();
        } else if (tail >= head) {
            return tail - head;
        } else {
            return capacity() - (head - tail);
        }
    }

    private void put(Connection c, SMPPEvent e, SMPPPacket p) throws QueueFullException {
        if (isFull()) {
            throw new QueueFullException();
        }
        queue[tail++].setDetails(c, e, p);
        if (tail == queue.length) {
            tail = 0;
        }
        if (tail == head) {
            tail = FULL;
        }
    }
}
