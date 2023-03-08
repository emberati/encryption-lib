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
        assertEquals(controlValueString, convertedLongString);
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

    @ParameterizedTest
    @MethodSource("com.emb.util.TestByteUtilsArgumentProvider#testByteArrayToLongArray")
    @Disabled("Disabled `testByteArrayToLongArray()` until fixing basic conversion be done")
    public void testByteArrayToLongArray(byte[] bytes, long[] controlValue) {
        final var convertedLongArray = ByteUtils.byteArrayToLongArray(bytes);

        assertEquals(Arrays.toString(controlValue), Arrays.toString(convertedLongArray));
    }

    @ParameterizedTest
    @MethodSource("com.emb.util.TestByteUtilsArgumentProvider#testLongArrayToByteArray")
    @Disabled("Disabled `testLongArrayToBytesArray()` until fixing basic conversion be done")
    public void testLongArrayToByteArray(long[] longs, byte[] controlValue) {
        final var convertedByteArray = ByteUtils.longArrayToByteArray(longs);

        assertEquals(Arrays.toString(controlValue), Arrays.toString(convertedByteArray));
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
