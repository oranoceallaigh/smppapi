package com.adenki.smpp.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class for event dispatchers that maintain their observers in a
 * <code>java.util.List</code>.
 * @version $Id$
 */
public abstract class AbstractEventDispatcher implements EventDispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractEventDispatcher.class);
    
    private List<SessionObserver> observers =
        new ArrayList<SessionObserver>();

    public void addObserver(SessionObserver observer) {
        synchronized (observers) {
            if (!observers.contains(observer)) {
                observers.add(observer);
            } else {
                LOG.info("Not adding observer because it's already registered");
            }
        }
    }

    public void removeObserver(SessionObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public Collection<SessionObserver> getObservers() {
        return Collections.unmodifiableCollection(observers);
    }
    
    public Iterator<SessionObserver> observerIterator() {
        return Collections.unmodifiableList(observers).iterator();
    }

    public boolean contains(SessionObserver observer) {
        return observers.contains(observer);
    }

    public int size() {
        return observers.size();
    }

    /**
     * Get the list of observers as an array.
     * @return An array of all registered observers.
     */
    protected SessionObserver[] getObserverList() {
        SessionObserver[] observerList =
            observers.toArray(new SessionObserver[0]);
        return observerList;
    }
}
