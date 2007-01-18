package ie.omk.smpp.message.param;

import java.text.ParseException;
import java.util.List;

// TODO document.
public abstract class AbstractParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private int linkIndex;
    
    protected AbstractParamDescriptor(int linkIndex) {
        this.linkIndex = linkIndex;
    }

    public int getLinkIndex() {
        return linkIndex;
    }
    
    protected int getCountFromBody(List body) throws ParseException {
        int count = 0;
        try {
            count = ((Number) body.get(linkIndex)).intValue();
        } catch (ArrayIndexOutOfBoundsException x) {
            throw new ParseException(
                    "Cannot read count from link index " + linkIndex, 0);
        } catch (ClassCastException x) {
            throw new ParseException("Mandatory parameter at index " + linkIndex
                    + " is not a java.lang.Number", 0);
        }
        return count;
    }
}
