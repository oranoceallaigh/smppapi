package ie.omk.smpp.message.param;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListParamDescriptor extends AbstractParamDescriptor {
    private static final long serialVersionUID = 1;

    private ParamDescriptor listType;
    
    public ListParamDescriptor(ParamDescriptor listType, int linkIndex) {
        super(linkIndex);
        this.listType = listType;
    }
    
    public ParamDescriptor getListType() {
        return listType;
    }
    
    public int sizeOf(Object obj) {
        int size = 0;
        List list = (List) obj;
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            size += listType.sizeOf(iter.next());
        }
        return size;
    }

    public void writeObject(Object obj, OutputStream out) throws IOException {
        if (obj != null) {
            List list = (List) obj;
            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                listType.writeObject(iter.next(), out);
            }
        }
    }

    public int readObject(List body, byte[] data, int offset)
            throws ParseException {
        int count = getCountFromBody(body);
        int readLength = 0;
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < count; i++) {
            readLength += listType.readObject(list, data, offset + readLength);
        }
        body.add(list);
        return readLength;
    }
}
