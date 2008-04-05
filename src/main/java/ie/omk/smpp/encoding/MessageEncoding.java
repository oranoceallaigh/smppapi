package ie.omk.smpp.encoding;

public interface MessageEncoding<T> {
    int getDataCoding();
    byte[] encode(T object);
    T decode(byte[] bytes);
}
