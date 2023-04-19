package com.emb.crypto;

import com.emb.util.ByteUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestECBByteEncoder {
    @ParameterizedTest
    @MethodSource("com.emb.crypto.TestECBByteEncoderArgumentProvider#testECBByteEncoderEncodeDecode")
    public void testECBByteEncoderEncodeDecode(byte[] original, byte[] controlValue) {
        final var byteEncoder = new ECBByteEncoder();
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        assertEquals(ByteUtils.toBinaryString(controlValue, ", "), ByteUtils.toBinaryString(decoded, ", "));
    }
}
