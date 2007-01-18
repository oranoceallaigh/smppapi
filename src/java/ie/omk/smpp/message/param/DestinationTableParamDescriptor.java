package ie.omk.smpp.message.param;

import ie.omk.smpp.message.DestinationTable;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

public class DestinationTableParamDescriptor extends AbstractParamDescriptor {
    private static final long serialVersionUID = 1;

    public DestinationTableParamDescriptor(int linkIndex) {
        super(linkIndex);
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

    public int readObject(List body, byte[] data, int offset) throws ParseException {
        int count = getCountFromBody(body);
        DestinationTable table = new DestinationTable();
        table.readFrom(data, offset, count);
        body.add(table);
        return table.getLength();
    }
}
