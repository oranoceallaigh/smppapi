package com.adenki.smpp.encoding;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

public abstract class BaseAlphabetEncodingTest<T extends AlphabetEncoding> {

    @Test
    public void testDecodeNullArray() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        String decoded = encoding.decode(null);
        assertNotNull(decoded);
        assertEquals(decoded, "");
    }
    
    @Test(expectedExceptions = {NullPointerException.class})
    public void testDecodeNullArrayWithOffsetAndLength() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        encoding.decode(null, 1, 5);
    }
    
    @Test
    public void testDecodeFullArray() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        TestData testData = getArrayToDecode();
        String decoded = encoding.decode(testData.getBytes());
        assertNotNull(decoded);
        assertEquals(decoded, testData.string);
    }
    
    @Test
    public void testDecodePartialArray() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        int charSize = encoding.getEncodedSize("a");
        TestData testData = getArrayToDecode();
        assertTrue(testData.bytes.length > 6);
        String decoded = encoding.decode(
                testData.getBytes(), 1 * charSize, 3 * charSize);
        assertNotNull(decoded);
        assertEquals(decoded, testData.string.substring(1, 4));
    }
    
    @Test
    public void testEncodeNullReturnsZeroLengthArray() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        byte[] encoded = encoding.encode(null);
        assertNotNull(encoded);
        assertEquals(encoded.length, 0);
    }
    
    @Test
    public void testEncodeWithAllCharactersSupported() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        TestData testData = getFullySupportedStringToEncode();
        byte[] encoded = encoding.encode(testData.string);
        assertNotNull(encoded);
        assertEquals(encoded, testData.getBytes());
    }

    @Test
    public void testEncodeWithUnsupportedCharacters() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        TestData testData = getPartiallySupportedStringToEncode();
        byte[] encoded = encoding.encode(testData.string);
        assertNotNull(encoded);
        assertEquals(encoded, testData.getBytes());
    }
    
    @Test
    public void testGetEncodingIsNotNull() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        if (encoding.getCharset() != null) {
            new String(new byte[] {64}, encoding.getCharset());
        }
    }
    
    @Test
    public void testEncodedSizeIsGreaterThanZeroForStringWithContent() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        assertTrue(encoding.getEncodedSize("test") > 0);
    }
    
    @Test
    public void testEncodedSizeIsZeroForEmptyString() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        assertEquals(encoding.getEncodedSize(""), 0);
    }
    
    @Test
    public void testEncodedSizeIsZeroForNull() throws Exception {
        AlphabetEncoding encoding = getEncodingToTest();
        assertEquals(encoding.getEncodedSize(null), 0);
    }
    
    protected abstract T getEncodingToTest() throws UnsupportedEncodingException;
    
    protected abstract TestData getArrayToDecode();
    
    protected abstract TestData getFullySupportedStringToEncode();

    protected abstract TestData getPartiallySupportedStringToEncode();
    
    protected class TestData {
        int[] bytes;
        String string;
        
        public TestData(int[] expectedBytes, String actualString) {
            this.bytes = expectedBytes;
            this.string = actualString;
        }
        
        public TestData(String expectedString, int[] actualBytes) {
            this.string = expectedString;
            this.bytes = actualBytes;
        }
        
        public byte[] getBytes() {
            byte[] byteArray = new byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                byteArray[i] = (byte) bytes[i];
            }
            return byteArray;
        }
    }
}
