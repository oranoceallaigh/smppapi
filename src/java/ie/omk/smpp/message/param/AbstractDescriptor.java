package ie.omk.smpp.message.param;


/**
 * Abstract base descriptor which all SMPPAPI parameter descriptors
 * extend from.
 * @version $Id:$
 * @since 0.4.0
 */
public abstract class AbstractDescriptor implements ParamDescriptor {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        return getClass().isAssignableFrom(obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }
}
