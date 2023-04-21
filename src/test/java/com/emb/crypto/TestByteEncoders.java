package com.emb.crypto;

import com.emb.util.ByteUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestByteEncoders {
    @ParameterizedTest
    @ArgumentsSource(ByteEncoderArgumentProvider.class)
    public void testECBByteEncoderEncodeDecode(byte[] original, byte[] controlValue) {
        final var byteEncoder = new ECBByteEncoder();
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        final var delimiter = " ";
        assertEquals(ByteUtils.toBinaryString(controlValue, delimiter), ByteUtils.toBinaryString(decoded, delimiter));
    }

    @ParameterizedTest
    @ArgumentsSource(ByteEncoderArgumentProvider.class)
    public void testCBCByteEncoderEncodeDecode(byte[] original, byte[] controlValue) {
        final var byteEncoder = new CBCByteEncoder();
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        final var delimiter = " ";
        assertEquals(ByteUtils.toBinaryString(controlValue, delimiter), ByteUtils.toBinaryString(decoded, delimiter));
    }
}
