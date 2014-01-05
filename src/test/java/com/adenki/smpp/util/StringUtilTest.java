package com.adenki.smpp.util;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class StringUtilTest {
    public void testEscapeJavaString() throws Exception {
        Assert.assertEquals(
                StringUtil.escapeJava("123456789abcdeFGHIJKLmnopqrstuvwxyz"),
                "123456789abcdeFGHIJKLmnopqrstuvwxyz");
        Assert.assertEquals(
                StringUtil.escapeJava("\0\n\t\u0003\u0005Blah"),
                "\\u0000\n\t\\u0003\\u0005Blah");
    }

    public void testEscapeJavaByteArray() throws Exception {
        Assert.assertEquals(
                StringUtil.escapeJava("Hello".getBytes("US-ASCII")),
                "Hello");
        Assert.assertEquals(
                StringUtil.escapeJava("H\0\n\t\u0002\u0006Z"),
                "H\\u0000\n\t\\u0002\\u0006Z");
    }
}
