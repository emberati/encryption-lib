package com.emb.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class TestByteUtils {
    private static final long number = 0xFA3CDEBL;
    private static final String binaryString = Long.toBinaryString(number);
    private final static String prettifiedBinaryString = "1111 1010 0011 1100 1101 1110 1011";
    private final static String prettifiedBinaryStringNoMatchingZeros = "0000 0000 0000 0000 0000 0000 0000 0000 0000 1111 1010 0011 1100 1101 1110 1011";
    private final byte[] byteArray = new byte[] {
            0b0101_0110, 0b0110_1110, 0b0101_1111, 0b0101_0110,
            0b0110_1101, 0b0001_0010, 0b0111_1100, 0b0000_1001
    };
    private final long longValue = 0b0101_0110_0110_1110_0101_1111_0101_0110_0110_1101_0001_0010_0111_1100_0000_1001L;

    private final long[] transactedLongArray = new long[] {
            0x11EF3C0A592FDL, 0x1AFE7292F5DDAE22L,
            0x2543BB8A3CD2B23FL, 0xCD198DEB25F035C3L
    };
    private final byte[] transactedByteArray = new byte[] {
            (byte) 0b0000_0000, (byte) 0b0000_0001, (byte) 0b0001_1110, (byte) 0b1111_0011,
            (byte) 0b110_00000, (byte) 0b101_00101, (byte) 0b100_10010, (byte) 0b1111_1101,
            (byte) 0b000_11010, (byte) 0b111_11110, (byte) 0b011_10010, (byte) 0b1001_0010,
            (byte) 0b111_10101, (byte) 0b110_11101, (byte) 0b101_01110, (byte) 0b0010_0010,
            (byte) 0b001_00101, (byte) 0b010_00011, (byte) 0b101_11011, (byte) 0b1000_1010,
            (byte) 0b001_11100, (byte) 0b110_10010, (byte) 0b101_10010, (byte) 0b0011_1111,
            (byte) 0b110_01101, (byte) 0b000_11001, (byte) 0b100_01101, (byte) 0b1110_1011,
            (byte) 0b001_00101, (byte) 0b111_10000, (byte) 0b001_10101, (byte) 0b1100_0011
    };

    @Test
    public void testPrettifiedBackwardCapability() {
        final var prettifiedBinaryString = ByteUtils.prettifyBinaryString(binaryString, 16);
        final var uglifiedBinaryString = removeSpaces(prettifiedBinaryString);

        try {
            assertEquals(Long.parseLong(uglifiedBinaryString, 2), number);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPrettifyBinaryString() {
        final var prettifiedBinaryString = ByteUtils.prettifyBinaryString(binaryString);
        assertEquals(TestByteUtils.prettifiedBinaryString, prettifiedBinaryString);
    }

    @Test
    public void testPrettifyBinaryStringNoMatchingZeros() {
        final var prettifiedBinaryString = ByteUtils.prettifyBinaryString(binaryString, 16);
        assertEquals(prettifiedBinaryStringNoMatchingZeros, prettifiedBinaryString);
    }

    @Test
    public void testByteArrayToLongTranslation() {
        assertEquals(longValue, ByteUtils.byteArrayToLong(byteArray));
    }

    @Test
    public void testByteArrayZeroTailToLongArray() {
        final var bytesZeroTail = new byte[] {
                (byte) 0b1010_1010, (byte) 0b1010_1010, (byte) 0b1010_1010, (byte) 0b1010_1010,
                (byte) 0b0000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000, (byte) 0b0000_0000

        };
        final var transactedLong = 0xAAAAAAAA00000000L;
        final var translatedLong = ByteUtils.byteArrayToLong(bytesZeroTail);

        assertEquals(ByteUtils.numberToPrettyBinaryString(transactedLong), ByteUtils.numberToPrettyBinaryString(translatedLong));
    }

    @ParameterizedTest
    @MethodSource("com.emb.util.TestByteUtilsArgumentProvider#testByteArrayToLong")
    public void testByteArrayToLong(byte[] bytes, long controlLong) {
        long convertedLong;
        convertedLong = ByteUtils.byteArrayToLong(bytes);
        assertEquals(ByteUtils.numberToPrettyBinaryString(controlLong), ByteUtils.numberToPrettyBinaryString(convertedLong));
    }

    @Test
    public void testLongToByteArrayTranslation() {
        final var translatedBytes = ByteUtils.longToByteArray(longValue);

        assertEquals(Arrays.toString(byteArray), Arrays.toString(translatedBytes));
    }

    @Test
    public void testByteArrayToLongArray() {
        final var translatedLongArray = ByteUtils.byteArrayToLongArray(transactedByteArray);

        assertEquals(Arrays.toString(transactedLongArray), Arrays.toString(translatedLongArray));
    }

    @Test
    public void testLongArrayToBytesArray() {
        final var translatedByteArray = ByteUtils.longArrayToByteArray(transactedLongArray);

        System.out.println(Arrays.toString(transactedByteArray));
        System.out.println(Arrays.toString(translatedByteArray));

        assertEquals(Arrays.toString(transactedByteArray), Arrays.toString(translatedByteArray));
    }

    private String removeSpaces(final String string) {
        return string.replace(" ", "");
    }
}
