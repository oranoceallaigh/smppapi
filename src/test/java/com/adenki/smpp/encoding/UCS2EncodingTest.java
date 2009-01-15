package com.adenki.smpp.encoding;

import java.io.UnsupportedEncodingException;

import org.testng.annotations.Test;

@Test
public class UCS2EncodingTest extends BaseAlphabetEncodingTest<UCS2Encoding> {
    private static final String STRING =
        "abcdefJKLMN1234567890\u00c0\u00c3\u20ac";

    private static final int[] BYTES = {
        0x00, 0x61, 0x00, 0x62, 0x00, 0x63, 0x00, 0x64,
        0x00, 0x65, 0x00, 0x66, 0x00, 0x4a, 0x00, 0x4b,
        0x00, 0x4c, 0x00, 0x4d, 0x00, 0x4e, 0x00, 0x31,
        0x00, 0x32, 0x00, 0x33, 0x00, 0x34, 0x00, 0x35,
        0x00, 0x36, 0x00, 0x37, 0x00, 0x38, 0x00, 0x39,
        0x00, 0x30, 0x00, 0xc0, 0x00, 0xc3, 0x20, 0xac,
    };

    @Override
    protected TestData getArrayToDecode() {
        return new TestData(STRING, BYTES);
    }

    @Override
    protected UCS2Encoding getEncodingToTest() throws UnsupportedEncodingException {
        return new UCS2Encoding();
    }

    @Override
    protected TestData getFullySupportedStringToEncode() {
        return new TestData(BYTES, STRING);
    }

    @Override
    protected TestData getPartiallySupportedStringToEncode() {
        return new TestData(BYTES, STRING);
    }
}
