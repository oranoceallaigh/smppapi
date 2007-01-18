package ie.omk.smpp.message;

import ie.omk.smpp.message.param.ParamDescriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class describing the make-up of a packet&apos;s mandatory parameters.
 * @version $Id: $
 */
class BodyDescriptor implements Serializable {
    public static final BodyDescriptor ONE_CSTRING = new BodyDescriptor();
    
    private static final long serialVersionUID = 1;
    private List<ParamDescriptor> body = new ArrayList<ParamDescriptor>();

    static {
        ONE_CSTRING.add(ParamDescriptor.CSTRING);
    }
    
    BodyDescriptor add(ParamDescriptor descriptor) {
        body.add(descriptor);
        return this;
    }
    
    void clear() {
        body.clear();
    }
    
    List<ParamDescriptor> getBody() {
        return body;
    }
    
    int getSize() {
        return body.size();
    }
}
