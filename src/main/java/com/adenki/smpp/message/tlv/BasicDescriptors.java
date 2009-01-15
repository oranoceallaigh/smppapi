package com.adenki.smpp.message.tlv;

import com.adenki.smpp.message.param.BitmaskParamDescriptor;
import com.adenki.smpp.message.param.BytesParamDescriptor;
import com.adenki.smpp.message.param.CStringParamDescriptor;
import com.adenki.smpp.message.param.DateParamDescriptor;
import com.adenki.smpp.message.param.IntegerParamDescriptor;
import com.adenki.smpp.message.param.NullParamDescriptor;
import com.adenki.smpp.message.param.ParamDescriptor;

/**
 * A static class holding some basic parameter descriptors, used
 * primarily by the {@link Tag} class.
 * @version $Id$
 * @since 0.4.0
 */
public class BasicDescriptors {
    public static final ParamDescriptor INTEGER1 = new IntegerParamDescriptor(1);
    public static final ParamDescriptor INTEGER2 = new IntegerParamDescriptor(2);
    public static final ParamDescriptor INTEGER4 = new IntegerParamDescriptor(4);
    public static final ParamDescriptor INTEGER8 = new IntegerParamDescriptor(8);
    public static final ParamDescriptor BYTES = new BytesParamDescriptor();
    public static final ParamDescriptor CSTRING = new CStringParamDescriptor();
    public static final ParamDescriptor BITMASK = new BitmaskParamDescriptor();
    public static final ParamDescriptor DATE = new DateParamDescriptor();
    public static final ParamDescriptor NULL = new NullParamDescriptor();
}
