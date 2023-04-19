package com.emb.crypto;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

public class TestECBByteEncoderArgumentProvider {
    public static Stream<Arguments> testECBByteEncoderEncodeDecode() {
        final var bytesLessLong = new byte[] {
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD
        };
        final var bytesBiggerLong = new byte[] {
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD,
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD,
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD
        };
        final var bytesZeroTail = new byte[] {
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD,
                (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00,

        };
        final var bytesNegative = new byte[] {
                (byte) 0xFF, (byte) 0xAA,
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0xAA, (byte) 0xAA,
                (byte) 0xAA, (byte) 0xAA,
        };
        final var bytesZeroHead = new byte[] {
                (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00,
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD,
        };
        return Stream.of(
                of(bytesLessLong, bytesLessLong),       // Check when byte array less length than long
                of(bytesBiggerLong, bytesBiggerLong),   // Check when byte array bigger length than long
                of(bytesZeroTail, bytesZeroTail),       // Check when byte array has zero tail
                of(bytesZeroHead, bytesLessLong),
                of(bytesNegative, bytesNegative)        // Check when byte array has first negative value
        );
    }
}
