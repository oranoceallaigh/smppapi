package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

import com.adenki.smpp.SMPPRuntimeException;

@Test
public class AlphabetEncodingSetCharsetTest {

    @Test(expectedExceptions = {SMPPRuntimeException.class})
    public void testResettingCharsetThrowsException() throws Exception {
        class TestEncoding extends AlphabetEncoding {
            TestEncoding() throws UnsupportedEncodingException {
                super(Integer.MAX_VALUE);
                setCharset("US-ASCII");
            }
            
            void resetCharset() throws UnsupportedEncodingException {
                setCharset("Latin-1");
            }
        }
        new TestEncoding().resetCharset();
    }
}
