package com.emb.main;

import com.emb.util.ByteUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public class TestFeistelByteEncoder {
    @ParameterizedTest
    @MethodSource("com.emb.main.TestFeistelByteEncoderArgumentProvider#testFeistelByteEncoderEncodeDecode")
    public void testFeistelByteEncoderEncodeDecode(byte[] original, byte[] controlValue) {
        final var byteEncoder = new FeistelByteEncoder();
        final var encoded = byteEncoder.encrypt(original);
        final var decoded = byteEncoder.decrypt(encoded);
        System.out.println("Original: " + ByteUtils.joinPrettyBytes(original, " "));
        System.out.println("Encoded : " + ByteUtils.joinPrettyBytes(encoded, " "));
        System.out.println("Decoded : " + ByteUtils.joinPrettyBytes(decoded, " "));
        assertEquals(ByteUtils.joinPrettyBytes(controlValue, " "), ByteUtils.joinPrettyBytes(decoded, " "));
    }
}
