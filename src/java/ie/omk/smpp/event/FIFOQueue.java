package ie.omk.smpp.event;

import ie.omk.smpp.Session;
import ie.omk.smpp.message.SMPPPacket;

/**
 * A simple implementation of a FIFO queue. Need this to be as minimal as
 * possible so it's zippidy quick. No synchronization is done here, it's
 * handled by the relevant ThreadedEventDispatcher methods.
 */
class FIFOQueue {
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

    public void put(Session c, SMPPPacket p) throws QueueFullException {
        if (isFull()) {
            throw new QueueFullException();
        }
        queue[tail++].setDetails(c, null, p);
        if (tail >= queue.length) {
            tail = 0;
        }
    }

    public void put(Session c, SMPPEvent e) throws QueueFullException {
        if (isFull()) {
            throw new QueueFullException();
        }
        queue[tail++].setDetails(c, e, null);
        if (tail >= queue.length) {
            tail = 0;
        }
    }

    public NotificationDetails get() {
        NotificationDetails nd = null;
        if (!isEmpty()) {
            nd = queue[head++];
            if (head >= queue.length) {
                head = 0;
            }
        }
        return nd;
    }

    public boolean isEmpty() {
        return tail == head;
    }

    public boolean isFull() {
        if (tail > head) {
            return (tail == queue.length - 1) && head == 0;
        } else {
            return tail == (head - 1);
        }
    }
}
