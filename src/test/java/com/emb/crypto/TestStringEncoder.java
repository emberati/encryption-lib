package com.emb.crypto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStringEncoder {
    @ParameterizedTest
    @ArgumentsSource(TestStringEncoderArgumentProvider.class)
    public void testECBStringEncoder(String original, String controlValue) {
        final var byteEncoder = new StringEncoder(new ECBByteEncoder());
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        assertEquals(controlValue, decoded);
    }

    @ParameterizedTest
    @ArgumentsSource(TestStringEncoderArgumentProvider.class)
    public void testCBCStringEncoder(String original, String controlValue) {
        final var byteEncoder = new StringEncoder(new CBCByteEncoder());
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        assertEquals(controlValue, decoded);
    }

    @ParameterizedTest
    @ArgumentsSource(TestStringEncoderArgumentProvider.class)
    public void testOFBStringEncoder(String original, String controlValue) {
        final var byteEncoder = new StringEncoder(new OFBByteEncoder());
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        assertEquals(controlValue, decoded);
    }
}
