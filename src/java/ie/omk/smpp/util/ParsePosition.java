package ie.omk.smpp.util;

/**
 * An integer container for tracking  a parse position.
 * @version $Id:$
 */
public class ParsePosition {
    private int index;
    
    public ParsePosition(int startIndex) {
        index = startIndex;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void inc() {
        index++;
    }
    
    public void inc(int amount) {
        index += amount;
    }
}
