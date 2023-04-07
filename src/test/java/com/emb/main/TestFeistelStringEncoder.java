package com.emb.main;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFeistelStringEncoder {
    @ParameterizedTest
    @MethodSource("com.emb.main.TestFeistelStringEncoderArgumentProvider#testFeistelStringEncoderEncodeDecode")
    public void testFeistelStringEncoderEncryptDecrypt(String original, String controlValue) {
        final var byteEncoder = new FeistelStringEncoder();
        final var encoded = byteEncoder.encrypt(original);
        final var decoded = byteEncoder.decrypt(encoded);
        assertEquals(controlValue, decoded);
    }
}
