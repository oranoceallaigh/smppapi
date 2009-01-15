package com.adenki.smpp.message.tlv;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test
public class TagTest {
    
    public void testTag() {
        Tag testTag = Tag.SMS_SIGNAL;
        int testTagVal = 0x1203;

        assertEquals(testTag.intValue(), testTagVal);
        assertEquals(testTag.intValue(), testTagVal);

        assertSame(Tag.getTag(testTagVal), testTag);
        assertEquals(Tag.getTag(testTagVal), testTag);
        assertTrue(testTag.equals(testTagVal));

        assertEquals(testTag.hashCode(), new Integer(testTagVal).hashCode());

        //
        // Define a new Tag type
        //
        int newTagVal = 0x1456;
        Tag newTag = Tag.defineTag(0x1456, BasicDescriptors.INTEGER4, 4);

        assertTrue(newTag.equals(newTagVal));
        assertSame(Tag.getTag(newTagVal), newTag);
    }

    public void testDefineAndUndefine() throws Exception {
        final int TAG_VALUE = 9000;
        assertFalse(Tag.isTagDefined(TAG_VALUE));
        Tag.defineTag(TAG_VALUE, BasicDescriptors.CSTRING, 30);
        assertTrue(Tag.isTagDefined(TAG_VALUE));
        Tag tag = Tag.getTag(TAG_VALUE);
        assertEquals(tag.getParamDescriptor(), BasicDescriptors.CSTRING);
        assertEquals(tag.intValue(), TAG_VALUE);
        assertEquals(tag.getMaxLength(), 30);
        assertEquals(tag.getMinLength(), 30);
        assertEquals(tag.getLength(), 30);
        Tag.undefineTag(tag);
        assertFalse(Tag.isTagDefined(TAG_VALUE));
    }
}
