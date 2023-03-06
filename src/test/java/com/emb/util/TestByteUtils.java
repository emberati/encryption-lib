package com.emb.util;

import org.junit.jupiter.api.Disabled;
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
    public void testPrettifyBinaryString() {
        final var prettifiedBinaryString = ByteUtils.prettifyBinaryString(binaryString);
        assertEquals(TestByteUtils.prettifiedBinaryString, prettifiedBinaryString);
    }

    @Test
    public void testPrettifyBinaryStringNoMatchingZeros() {
        final var prettifiedBinaryString = ByteUtils.prettifyBinaryString(binaryString, 16);
        assertEquals(prettifiedBinaryStringNoMatchingZeros, prettifiedBinaryString);
    }

    @ParameterizedTest
    @MethodSource("com.emb.util.TestByteUtilsArgumentProvider#testByteArrayToLong")
    public void testByteArrayToLong(byte[] bytes, long controlLong) {
        final var convertedLong = ByteUtils.byteArrayToLong(bytes);
        final var controlValueString = ByteUtils.numberToPrettyBinaryString(controlLong);
        final var convertedLongString = ByteUtils.numberToPrettyBinaryString(convertedLong);
//        System.out.printf("Control  : %s%n", controlValueString);
//        System.out.printf("Converted: %s%n", convertedLongString);
        assertEquals(ByteUtils.numberToPrettyBinaryString(controlLong), ByteUtils.numberToPrettyBinaryString(convertedLong));
    }

    @ParameterizedTest
    @MethodSource("com.emb.util.TestByteUtilsArgumentProvider#testLongToByteArray")
    public void testLongToByteArray(long longValue, byte[] controlValue) {
        final var convertedBytes = ByteUtils.longToByteArray(longValue);
        final var controlValueString = ByteUtils.joinPrettyBytes(controlValue);
        final var convertedBytesString = ByteUtils.joinPrettyBytes(convertedBytes);
//        System.out.printf("Control  : %s%n", controlValueString);
//        System.out.printf("Converted: %s%n", convertedBytesString);
        assertEquals(controlValueString, convertedBytesString);
    }

    @Test
    @Disabled("Disabled `testByteArrayToLongArray()` until fixing basic conversion be done")
    public void testByteArrayToLongArray() {
        final var translatedLongArray = ByteUtils.byteArrayToLongArray(transactedByteArray);

        assertEquals(Arrays.toString(transactedLongArray), Arrays.toString(translatedLongArray));
    }

    @Test
    @Disabled("Disabled `testLongArrayToBytesArray()` until fixing basic conversion be done")
    public void testLongArrayToBytesArray() {
        final var translatedByteArray = ByteUtils.longArrayToByteArray(transactedLongArray);

        System.out.println(Arrays.toString(transactedByteArray));
        System.out.println(Arrays.toString(translatedByteArray));

        assertEquals(Arrays.toString(transactedByteArray), Arrays.toString(translatedByteArray));
    }

    @ParameterizedTest
    @MethodSource("com.emb.util.TestByteUtilsArgumentProvider#testShiftAll")
    public void testShiftAll(long block, Shift.ShiftDeclaration shift, int times, long controlValue) {
        final var shifted = ByteUtils.shiftAll(block, shift, times);
        assertEquals(ByteUtils.numberToPrettyBinaryString(controlValue, 16), ByteUtils.numberToPrettyBinaryString(shifted, 16));
    }

    @ParameterizedTest
    @MethodSource("com.emb.util.TestByteUtilsArgumentProvider#testShiftAll_shouldThrow")
    public void testShiftAll_shouldThrow(long block, Shift.ShiftDeclaration shift, int times, Class<? extends Exception> expectedException) {
        assertThrows(expectedException, () -> ByteUtils.shiftAll(block, shift, times));
    }
}
