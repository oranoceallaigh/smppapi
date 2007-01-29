package ie.omk.smpp.message.param;

import ie.omk.smpp.util.ParsePosition;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListParamDescriptor implements ParamDescriptor {
    private static final long serialVersionUID = 1;

    private int linkIndex;
    private ParamDescriptor listType;
    
    public ListParamDescriptor(ParamDescriptor listType, int linkIndex) {
        this.linkIndex = linkIndex;
        this.listType = listType;
    }
    
    public ParamDescriptor getListType() {
        return listType;
    }
    
    public int getLengthSpecifier() {
        return linkIndex;
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

    public Object readObject(byte[] data, ParsePosition position, int length) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < length; i++) {
            Object obj = listType.readObject(data, position, -1);
            list.add(obj);
        }
        return list;
    }
}
