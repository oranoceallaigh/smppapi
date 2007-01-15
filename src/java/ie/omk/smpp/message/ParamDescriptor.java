package ie.omk.smpp.message;

import ie.omk.smpp.Address;
import ie.omk.smpp.ErrorAddress;
import ie.omk.smpp.util.SMPPDateFormat;
import ie.omk.smpp.util.SMPPIO;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A mandatory parameter descriptor.
 */
final class ParamDescriptor implements Serializable {
    public static final ParamDescriptor INTEGER1 = new ParamDescriptor(Types.INTEGER, 1);
    public static final ParamDescriptor INTEGER2 = new ParamDescriptor(Types.INTEGER, 2);
    public static final ParamDescriptor INTEGER4 = new ParamDescriptor(Types.INTEGER, 4);
    public static final ParamDescriptor INTEGER8 = new ParamDescriptor(Types.INTEGER, 4);
    public static final ParamDescriptor CSTRING = new ParamDescriptor(Types.CSTRING);
    public static final ParamDescriptor ADDRESS = new ParamDescriptor(Types.ADDRESS);
    public static final ParamDescriptor ERROR_ADDRESS = new ParamDescriptor(Types.ERROR_ADDRESS);
    public static final ParamDescriptor DATE = new ParamDescriptor(Types.DATE);
    
    private static final long serialVersionUID = 1;
    private static final Format DATE_FORMAT = new SMPPDateFormat();

    private Types type;
    private int length;
    private int linkIndex;
    private ParamDescriptor listType;

    private ParamDescriptor() {
    }
    
    private ParamDescriptor(Types type) {
        this.type = type;
    }
    
    private ParamDescriptor(Types type, int length) {
        this.type = type;
        this.length = length;
    }
    
    public Types getType() {
        return type;
    }
    
    public int getLength() {
        return length;
    }
    
    public int getLinkIndex() {
        return linkIndex;
    }
    
    public ParamDescriptor getListType() {
        return listType;
    }
    
    static ParamDescriptor getListInstance(ParamDescriptor listType, int linkIndex) {
        ParamDescriptor descriptor = new ParamDescriptor();
        descriptor.type = Types.LIST;
        descriptor.length = -1;
        descriptor.linkIndex = linkIndex;
        descriptor.listType = listType;
        return descriptor;
    }
    
    static ParamDescriptor getBytesInstance(int linkIndex) {
        ParamDescriptor descriptor = new ParamDescriptor();
        descriptor.type = Types.BYTES;
        descriptor.length = -1;
        descriptor.linkIndex = linkIndex;
        return descriptor;
    }
    
    static ParamDescriptor getDestinationTableInstance(int linkIndex) {
        ParamDescriptor descriptor = new ParamDescriptor();
        descriptor.type = Types.DEST_TABLE;
        descriptor.length = -1;
        descriptor.linkIndex = linkIndex;
        return descriptor;
    }

    int sizeOf(Object obj) {
        int size;
        if (type == Types.LIST) {
            size = 0;
            List list = (List) obj;
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                size += sizeOf(listType, iter.next());
            }
        } else {
            size = sizeOf(this, obj);
        }
        return size;
    }
    
    void writeObject(Object obj, OutputStream out) throws IOException {
        if (type == Types.LIST) {
            List list = (List) obj;
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                writeObjectInternal(listType, iter.next(), out);
            }
        } else {
            writeObjectInternal(this, obj, out);
        }
    }
    
    int readObject(List<Object> body,
            byte[] data,
            int offset) throws ParseException {
        int readLength = 0;
        int objectLength = length;
        if (objectLength < 0) {
            objectLength = ((Number) body.get(linkIndex)).intValue();
        }
        if (type == Types.LIST) {
            List<Object> list = new ArrayList<Object>(objectLength);
            for (int i = 0; i < objectLength; i++) {
                readLength += readObjectInternal(
                        listType, list, data, offset + readLength, 0);
            }
            body.add(list);
        } else {
            readLength =
                readObjectInternal(this, body, data, offset, objectLength);
        }
        return readLength;
    }
 
    private int readObjectInternal(ParamDescriptor param,
            List<Object> list,
            byte[] data,
            int offset,
            int length) throws ParseException {
        int readLength = length;
        if (param.type == Types.INTEGER) {
            list.add(Integer.valueOf(SMPPIO.bytesToInt(data, offset, length)));
        } else if (param.type == Types.BYTES) {
            byte[] bytes = new byte[length];
            System.arraycopy(data, offset, bytes, 0, length);
            list.add(bytes);
        } else if (param.type == Types.CSTRING) {
            String string = SMPPIO.readCString(data, offset);
            list.add(string);
            readLength = string.length() + 1;
        } else if (param.type == Types.STRING) {
            list.add(SMPPIO.readString(data, offset, length));
        } else if (param.type == Types.DATE) {
            String dateString = SMPPIO.readCString(data, offset);
            if (dateString.length() > 0) {
                list.add(DATE_FORMAT.parseObject(dateString));
            } else {
                list.add(null);
            }
            readLength = dateString.length() + 1;
        } else if (param.type == Types.ADDRESS) {
            Address address = new Address();
            address.readFrom(data, offset);
            list.add(address);
            readLength = address.getLength();
        } else if (param.type == Types.ERROR_ADDRESS) {
            ErrorAddress address = new ErrorAddress();
            address.readFrom(data, offset);
            list.add(address);
            readLength = address.getLength();
        } else if (param.type == Types.DEST_TABLE) {
            DestinationTable table = new DestinationTable();
            table.readFrom(data, offset, length);
            list.add(table);
            readLength = table.getLength();
        }
        return readLength;
    }
    
    private void writeObjectInternal(ParamDescriptor param,
            Object obj,
            OutputStream out) throws IOException {
        if (param.type == Types.INTEGER) {
            int val = ((Number) obj).intValue();
            SMPPIO.writeInt(val, param.length, out);
        } else if (param.type == Types.ADDRESS) {
            if (obj == null) {
                out.write(new byte[] {0, 0, 0});
            } else {
                ((Address) obj).writeTo(out);
            }
        } else if (param.type == Types.CSTRING) {
            SMPPIO.writeCString((String) obj, out);
        } else if (param.type == Types.DATE) {
            if (obj != null) {
                SMPPIO.writeCString(DATE_FORMAT.format(obj), out);
            } else {
                out.write(0);
            }
        } else if (param.type == Types.BYTES) {
            if (obj != null) {
                out.write((byte[]) obj);
            }
        } else if (param.type == Types.DEST_TABLE) {
            if (obj != null) {
                ((DestinationTable) obj).writeTo(out);
            }
        } else if (param.type == Types.ERROR_ADDRESS) {
            if (obj != null) {
                ((ErrorAddress) obj).writeTo(out);
            }
        }
    }
    
    private int sizeOf(ParamDescriptor param, Object obj) {
        int size = 0;
        if (param.type == Types.INTEGER) {
            size = length;
        } else if (param.type == Types.BYTES) {
            if (obj != null) {
                size = ((byte[]) obj).length;
            }
        } else if (param.type == Types.CSTRING) {
            if (obj != null) {
                size = ((String) obj).length() + 1;
            } else {
                size = 1;
            }
        } else if (param.type == Types.STRING) {
            throw new UnsupportedOperationException("Laziness on the coder's part");
        } else if (param.type == Types.DATE) {
            if (obj != null) {
                String dateString = DATE_FORMAT.format(obj);
                size = dateString.length() + 1;
            } else {
                size = 1;
            }
        } else if (param.type == Types.ADDRESS) {
            if (obj != null) {
                size = ((Address) obj).getLength();
            } else {
                size = 3;
            }
        } else if (param.type == Types.ERROR_ADDRESS) {
            if (obj != null) {
                size = ((ErrorAddress) obj).getLength();
            } else {
                throw new RuntimeException("This code path should never execute");
            }
        } else if (param.type == Types.DEST_TABLE) {
            if (obj != null) {
                size = ((DestinationTable) obj).getLength();
            }
        }
        return size;
    }
}
