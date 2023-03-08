package com.emb.util;

import com.emb.util.exception.IllegalByteShift;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

@SuppressWarnings("unused")
public class TestByteUtilsArgumentProvider {
    public static final long lessLong = 0xFAABBBCDL;
    public static final byte[] bytesLessLong = new byte[] {
            (byte) 0xFA, (byte) 0xAB,
            (byte) 0xBB, (byte) 0xCD
    };
    public static final long longZeroSuffix = 0xFAABBBCD00000000L;
    public static final byte[] bytesZeroSuffix = new byte[] {
            (byte) 0xFA, (byte) 0xAB,
            (byte) 0xBB, (byte) 0xCD,
            (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,

    };
    public static final long longZeroBorder = 0x0000AAAAAAAA0000L;
    public static final byte[] bytesZeroBorder = new byte[] {
            (byte) 0x00, (byte) 0x00,
            (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA,
            (byte) 0x00, (byte) 0x00
    };
    public static final long longZeroPrefix = 0xFAABBBCDL;
    public static final byte[] bytesZeroPrefix = new byte[] {
            (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
            (byte) 0xFA, (byte) 0xAB,
            (byte) 0xBB, (byte) 0xCD,
    };
    public static final long longOnesBorder = 0xFFFFAAAAAAAAFFFFL;
    public static final byte[] bytesOnesBorder = new byte[] {
            (byte) 0xFF, (byte) 0xFF,
            (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA,
            (byte) 0xFF, (byte) 0xFF
    };
    public static final long longNegative = 0xFF55555555555555L;
    public static final byte[] bytesNegative = new byte[] {
            (byte) 0xFF, (byte) 0x55,
            (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0x55,
    };

    public static final long longFullOnes = 0xFFFFFFFFFFFFFFFFL;
    public static final byte[] bytesFullOnes = new byte[] {
            (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF,
    };

    public static final long longFullZeros = 0x0000000000000000L;
    public static final byte[] bytesFullZeros = new byte[] {
            (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00,
    };

    public static Stream<Arguments> testByteArrayToLong() {
        return Stream.of(
                of(bytesLessLong, lessLong),            // Check when byte array less length than long
                of(bytesZeroSuffix, longZeroSuffix),    // Check when byte array has zero tail
                of(bytesZeroPrefix, longZeroPrefix),
                of(bytesZeroBorder, longZeroBorder),
                of(bytesOnesBorder, longOnesBorder),
                of(bytesNegative, longNegative),        // Check when byte array has first negative value
                of(bytesFullOnes, longFullOnes),
                of(bytesFullZeros, longFullZeros)
        );
    }

    public static Stream<Arguments> testLongToByteArray() {
        return Stream.of(
                of(lessLong, bytesZeroPrefix),          // Check when byte array less length than long
                of(longZeroSuffix, bytesZeroSuffix),    // Check when byte array has zero tail
                of(longZeroPrefix, bytesZeroPrefix),
                of(longZeroBorder, bytesZeroBorder),
                of(longOnesBorder, bytesOnesBorder),
                of(longNegative, bytesNegative),        // Check when byte array has first negative value
                of(longFullOnes, bytesFullOnes),
                of(longFullZeros, bytesFullZeros)
        );
    }

    public static Stream<Arguments> testByteArrayToLongArray() {
        return Stream.of(
                of(bytesLessLong, new long[] {lessLong}),            // Check when byte array less length than long
                of(bytesZeroSuffix, new long[] {longZeroSuffix}),    // Check when byte array has zero tail
                of(bytesZeroPrefix, new long[] {longZeroPrefix}),
                of(bytesZeroBorder, new long[] {longZeroBorder}),
                of(bytesOnesBorder, new long[] {longOnesBorder}),
                of(bytesNegative, new long[] {longNegative}),        // Check when byte array has first negative value
                of(bytesFullOnes, new long[] {longFullOnes}),
                of(bytesFullZeros, new long[] {longFullZeros})
        );
    }

    public static Stream<Arguments> testLongArrayToByteArray() {
        return Stream.of(
                of(new long[] {lessLong}, bytesLessLong),            // Check when byte array less length than long
                of(new long[] {longZeroSuffix}, bytesZeroSuffix),    // Check when byte array has zero tail
                of(new long[] {longZeroPrefix}, bytesZeroPrefix),
                of(new long[] {longZeroBorder}, bytesZeroBorder),
                of(new long[] {longOnesBorder}, bytesOnesBorder),
                of(new long[] {longNegative}, bytesNegative),        // Check when byte array has first negative value
                of(new long[] {longFullOnes}, bytesFullOnes),
                of(new long[] {longFullZeros}, bytesFullZeros)
        );
    }

    public static Stream<Arguments> testShiftAll() {
        return Stream.of(
                of(0xAAL, Shift.BYTE.left.unsigned(), 1, 0xAA00L),
                of(0xAAL, Shift.BYTE.right.unsigned(), 1, 0x0L),
                of(0xAAAAL, Shift.INT.left.unsigned(), 1, 0xAAAA00000000L),
                of(0xAAAAL, Shift.INT.right.unsigned(), 1, 0x0L),
                of(0xAAAAL, Shift.INT.left.unsigned(), 2, 0xAAAAL)
        );
    }

    public static Stream<Arguments> testShiftAll_shouldThrow() {
        return Stream.of(
                of(0xAAAAL, Shift.BYTE.left, 1, IllegalByteShift.class),
                of(0xAAAAAAAAL, Shift.SHORT.left, 1, IllegalByteShift.class),
                of(0xAAAAAAAAAAAAL, Shift.INT.left, 1, IllegalByteShift.class)
        );
    }
}
