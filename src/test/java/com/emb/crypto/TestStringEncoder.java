package com.emb.crypto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStringEncoder {
    @ParameterizedTest
    @ArgumentsSource(TestStringEncoderArgumentProvider.class)
    public void testECBStringEncoderEncryptDecrypt(String original, String controlValue) {
        final var byteEncoder = new StringEncoder();
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        assertEquals(controlValue, decoded);
    }
}
