package com.emb.util;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

@SuppressWarnings("unused")
public class TestByteUtilsArgumentProvider {

    public static Stream<Arguments> testByteArrayToLong() {
        final var bytesLessLong = new byte[] {
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD
        };
        final var controlLessLong = 0xFAABBBCD00000000L;
        final var bytesZeroTail = new byte[] {
                (byte) 0xFA, (byte) 0xAB,
                (byte) 0xBB, (byte) 0xCD,
                (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00,

        };
        final var controlLongZeroTail = 0xFAABBBCD00000000L;
        final var bytesNegative = new byte[] {
                (byte) 0b1111_1111, (byte) 0b0101_0101,
                (byte) 0b0101_0101, (byte) 0b0101_0101,
                (byte) 0b0101_0101, (byte) 0b0101_0101,
                (byte) 0b0101_0101, (byte) 0b0101_0101,
        };
        final var controlNegativeLong = 0xFF55555555555555L;
        return Stream.of(
                of(bytesLessLong, controlLessLong),     // Check when byte array less length than long
                of(bytesZeroTail, controlLongZeroTail), // Check when byte array has zero tail
                of(bytesNegative, controlNegativeLong)  // Check when byte array has first negative value
        );
    }
}
