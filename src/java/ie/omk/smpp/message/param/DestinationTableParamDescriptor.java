package ie.omk.smpp.message.param;

import ie.omk.smpp.message.DestinationTable;
import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;

public class DestinationTableParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;
    private int linkIndex;
    
    public DestinationTableParamDescriptor(int linkIndex) {
        this.linkIndex = linkIndex;
    }
    
    public int getLengthSpecifier() {
        return linkIndex;
    }
    
    public int sizeOf(Object obj) {
        if (obj != null) {
            return ((DestinationTable) obj).getLength();
        } else {
            return 0;
        }
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        if (obj != null) {
            ((DestinationTable) obj).writeTo(out);
        }
    }

    public Object readObject(byte[] data, ParsePosition position, int length) {
        DestinationTable table = new DestinationTable();
        table.readFrom(data, position, length);
        return table;
    }
}
