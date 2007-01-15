package ie.omk.smpp;

import junit.framework.TestCase;

// TODO this class needs to be fixed up. The connection had to be changed
// to an async one, and i think that'll mean the test will read packets
// out of order.
public class TestConnection extends TestCase {

    public TestConnection(String name) {
        super(name);
    }
}

