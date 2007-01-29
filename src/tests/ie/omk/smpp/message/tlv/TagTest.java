package ie.omk.smpp.message.tlv;

import ie.omk.smpp.message.param.ParamDescriptor;
import junit.framework.TestCase;

public class TagTest extends TestCase {
    
    public void testTag() {
        Tag testTag = Tag.SMS_SIGNAL;
        int testTagVal = 0x1203;

        assertEquals(testTagVal, testTag.intValue());
        assertEquals(testTagVal, testTag.intValue());

        assertSame(testTag, Tag.getTag(testTagVal));
        assertEquals(testTag, Tag.getTag(testTagVal));
        assertTrue(testTag.equals(testTagVal));

        assertEquals(new Integer(testTagVal).hashCode(), testTag.hashCode());

        //
        // Define a new Tag type
        //
        int newTagVal = 0x1456;
        Tag newTag = Tag.defineTag(0x1456, ParamDescriptor.INTEGER4, 4);

        assertTrue(newTag.equals(newTagVal));
        assertSame(newTag, Tag.getTag(newTagVal));
    }

    public void testDefineAndUndefine() throws Exception {
        final int TAG_VALUE = 9000;
        assertFalse(Tag.isTagDefined(TAG_VALUE));
        Tag.defineTag(TAG_VALUE, ParamDescriptor.CSTRING, 30);
        assertTrue(Tag.isTagDefined(TAG_VALUE));
        Tag tag = Tag.getTag(TAG_VALUE);
        assertEquals(ParamDescriptor.CSTRING, tag.getParamDescriptor());
        assertEquals(TAG_VALUE, tag.intValue());
        assertEquals(30, tag.getMaxLength());
        assertEquals(30, tag.getMinLength());
        assertEquals(30, tag.getLength());
        Tag.undefineTag(tag);
        assertFalse(Tag.isTagDefined(TAG_VALUE));
    }
}
