package ie.omk.smpp;

import ie.omk.smpp.message.SMPacket;
import ie.omk.smpp.util.AlphabetEncoding;
import ie.omk.smpp.util.EncodingFactory;
import ie.omk.smpp.util.MessageEncoding;

public class TextMessage extends Message {
    private static final long serialVersionUID = 1;

    private String messageText;

    public TextMessage() {
        super(EncodingFactory.getInstance().getDefaultAlphabet());
    }
    
    public TextMessage(String messageText) {
        this();
        this.messageText = messageText;
    }
    
    public TextMessage(SMPacket packet) {
        this();
        EncodingFactory factory = EncodingFactory.getInstance();
        MessageEncoding encoding = factory.getEncoding(packet.getDataCoding());
        if (encoding instanceof AlphabetEncoding) {
            setAlphabet((AlphabetEncoding) encoding);
            messageText =
                ((AlphabetEncoding) encoding).decodeString(packet.getMessage());
        }
    }
    
    @Override
    public byte[] getMessage() {
        return ((AlphabetEncoding) encoding).encodeString(messageText);
    }

    public AlphabetEncoding getAlphabet() {
        return (AlphabetEncoding) encoding;
    }

    public void setAlphabet(AlphabetEncoding alphabet) {
        super.encoding = alphabet;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    public String toString() {
        return messageText;
    }
}
