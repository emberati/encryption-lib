package com.emb.util;

import com.emb.util.exception.IllegalByteShift;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.StringJoiner;

@SuppressWarnings("unused")
public class ByteUtils {

    public static final int MASK_8_BIT = 0xFF;
    public static final int MASK_16_BIT = 0xFFFF;
    public static final int MASK_32_BIT = 0xFFFFFFFF;
    public static final int BYTE_MASK = MASK_8_BIT;
    public static final int SHORT_MASK = MASK_16_BIT;
    public static final int BYTE = MASK_32_BIT;

    public static byte[] unsignedLongToByteArray(long unsignedLong) {
        byte[] bytes = new byte[8];
        byte mask = (byte) 0xFF;

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((unsignedLong >> i * 8) & mask);
        }

        bytes[0] = (byte) (unsignedLong & mask);
        bytes[1] = (byte) ((unsignedLong >> 8) & mask);
        bytes[2] = (byte) ((unsignedLong >> 16) & mask);
        bytes[3] = (byte) ((unsignedLong >> 24) & mask);
        bytes[4] = (byte) ((unsignedLong >> 32) & mask);
        bytes[5] = (byte) ((unsignedLong >> 40) & mask);
        bytes[6] = (byte) ((unsignedLong >> 48) & mask);
        bytes[7] = (byte) ((unsignedLong >> 56) & mask);
        return bytes;
    }

    public static String numberToPrettyBinaryString(byte value) {
        var string = Integer.toBinaryString(value);
        return prettifyBinaryString(string, 2);
    }

    public static String numberToPrettyBinaryString(short value) {
        var string = Integer.toBinaryString(value);
        return prettifyBinaryString(string, 4);
    }

    public static String numberToPrettyBinaryString(int value) {
        var string = Integer.toBinaryString(value);
        return prettifyBinaryString(string);
    }

    public static String numberToPrettyBinaryString(long value) {
        var string = Long.toBinaryString(value);
        return prettifyBinaryString(string);
    }

    public static String numberToPrettyBinaryString(long value, int nibblesAmount) {
        return prettifyBinaryString(Long.toBinaryString(value), nibblesAmount);
    }

    public static String prettifyBinaryString(String string) {
        var length = string.length();
        var nibblesAmount = length / 4;
        if (length % 4 != 0) nibblesAmount += 1;
        return prettifyBinaryString(string, Math.max(nibblesAmount, 2));
    }

    public static String prettifyBinaryString(String string, int nibblesAmount) {
        final var binaryString = string.getBytes();
        final var prettyString = new StringBuilder();
        final var initialLength = binaryString.length;
        final var finalLength = nibblesAmount * 4;
        final var zerosAmount = finalLength - initialLength;

        int i = 0;
        while (i < finalLength - 1) {
            if (i < zerosAmount)
                prettyString.append("0");
            else
                prettyString.append((char) binaryString[i - zerosAmount]);
            if ((i + 1) % 4 == 0)
                prettyString.append(" ");

            i++;
        }
        prettyString.append((char) binaryString[i - zerosAmount]);

        return prettyString.toString();
    }

    public static String joinPrettyBytes(byte[] bytes) {
        return joinPrettyBytes(bytes, ", ");
    }

    public static String joinPrettyBytes(byte[] bytes, String delim) {
        var joiner = new StringJoiner(delim);
        for (var value: bytes) {
            joiner.add(numberToPrettyBinaryString(value));
        }
        return joiner.toString();
    }

    public static String joinPrettyBytes(long[] longs) {
        return joinPrettyBytes(longs, ", ");
    }

    public static String joinPrettyBytes(long[] longs, String delim) {
        var joiner = new StringJoiner(delim);
        for (var value: longs) {
            joiner.add(numberToPrettyBinaryString(value));
        }
        return joiner.toString();
    }

    public static long[] byteArrayToLongArray(byte[] bytes) {
        final var size = amountOfLongsInByteArray(bytes);
        final var buffer = ByteBuffer.wrap(bytes);
        final var longs = new long[size];

        var i = 0;
        while (buffer.remaining() >= Long.BYTES) {
            longs[i] = buffer.getLong();
            i++;
        }

        System.out.println("bytes:");
        System.out.println(ByteUtils.joinPrettyBytes(buffer.array(), " "));
//        System.out.printf("capacity: %d, remaining: %d, position: %d, i: %d%n", buffer.capacity(), buffer.remaining(), buffer.position(), i);
        if (buffer.hasRemaining()) {
            var tail = 0L;
            while (buffer.hasRemaining()) {
                System.out.printf("capacity: %d, remaining: %d, position: %d, i: %d%n", buffer.capacity(), buffer.remaining(), buffer.position(), i);
                tail = ((long) buffer.get() & 0xFF) << ((buffer.remaining() - 1) * Byte.SIZE);
            }
            longs[i] = tail;
//            final var shift = Long.SIZE - buffer.remaining() * Byte.SIZE;
//            final var longTail = buffer.getLong(buffer.position());
//            longs[i] = longTail << shift;
        }
        System.out.println("longs:");
        System.out.println(ByteUtils.joinPrettyBytes(longs, " "));

//        System.out.printf("longs array len: %d%n", size);

        return longs;
    }

    public static byte[] longArrayToByteArray(long[] longs) {
        final var size = Long.SIZE * longs.length / Byte.SIZE;
        final var buffer = ByteBuffer.allocate(size);

        for (var value : longs) {
            buffer.putLong(value);
//            System.out.println(numberToPrettyBinaryString(value));
        }
//        System.out.printf("bytes array len: %d%n", size);

        return buffer.array();
    }

    public static byte[] longToByteArray(long value) {
        var bytes = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>= Byte.SIZE;
        }
        return bytes;
    }

    public static long byteArrayToLong(byte[] bytes) {
        bytes = stretchByteArrayToLong(bytes);
        var result = 0L;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    /**
     * Shifts block specified on parameter {@code block} by the way specified by parameter
     * {@code shift}, on bits count depending on {@code shift}, only one per shift.
     * {@link com.emb.util.ByteUtils#shift(long, Shift.ShiftDeclaration, int)}
     * with last parameter passed to 1.
     * For example, calling {@code shiftUnsignedTimes(0xDD, Shift.BYTE.left)}
     * will result in {@code 0xDD00L}.
     * In other words 0xDD (1101 1101), will result on 0x1BA (1101 1101 0000 0000)
     * Here is a byte left shift of 8 bits (byte length) only once.
     * @param block block what to shift
     * @param shift type of shifting, affects on shift direction,
     *              shift depth (how many bits are shifted),
     *              mask used to shift and on unsigned operation type
     * @return shifted long value
     * @see com.emb.util.ByteUtils#shiftAll(long, Shift.ShiftDeclaration, int)
     */
    public static long shiftAll(long block, Shift.ShiftDeclaration shift) {
        return shift(block, shift, shift.size());
    }

    /**
     * Shifts block specified on parameter {@code block} by the way specified by parameter
     * {@code shift}, on bits count depending on {@code shift}, as many times as specified by {@code times}.
     * For example, calling {@code shiftTimes(0xDD, Shift.BYTE.left, 1)}
     * will result in {@code 0xDD00L}.
     * In other words 0xDD (1101 1101), will result on 0x1BA (1101 1101 0000 0000)
     * Here is a byte left shift of 8 bits (byte length) 1 times.
     * <br/><br/>
     * Passing {@code Shift.BYTE.left}, byte mask {@code 0xFFL} will be used,
     * and will result shifting on {@code left} direction by the byte number of bits (8 bit).
     * <br/><br/>
     * Passing {@code Shift.INT.right}, int mask {@code 0xFFFFFFFFL} will be used,
     * and will result shift on {@code right} direction by the int number of bits (32 bit).
     * <br/><br/>
     * Last parameter will result how much will be shifted by the specified number of bits.
     * @param block block what to shift
     * @param shift type of shifting, affects on shift direction,
     *              shift depth (how many bits are shifted),
     *              mask used to shift and on unsigned operation type
     * @param times how many times to shift the {@code block}
     * @return shifted long value
     * @see com.emb.util.ByteUtils#shift(long, Shift.ShiftDeclaration, int)
     */
    public static long shiftAll(long block, Shift.ShiftDeclaration shift, int times) {
        return shift(block, shift, shift.size() * times);
    }

    /**
     * Shifts block specified on parameter {@code block} by the way specified by parameter
     * {@code shift} on bits count specified by the parameter {@code on}.
     * The shift occurs only once per specified number of bits.
     * For example, calling {@code shiftUnsigned(0xDD, Shift.BYTE, 1)}
     * will result in {@code 0x1BAL}.
     * In other words 0xDD (1101 1101), will result on 0x1BA (0001 1011 1010).
     * @param block block what to shift
     * @param shift type of shift, affects on shift direction,
     *              mask used to shift and on unsigned operation type.
     *              Unlike the {@link com.emb.util.ByteUtils#shiftAll(long, Shift.ShiftDeclaration, int)}
     *              shift depth (how many bits are shifted) depends only on parameter {@code on}.
     * @param on on how many bits to shift
     * @return shifted long value.
     */
    public static long shift(long block, Shift.ShiftDeclaration shift, int on) {
        if ((block & ~shift.type().mask()) != 0x0L)
            throw new IllegalByteShift(shift, block);
        if (shift.direction() == Shift.ShiftDirection.LEFT) {
            return (block & shift.mask()) << on;
        } else {
            return (block & shift.mask()) >> on;
        }
    }

    public static byte[] stretchByteArrayToLong(byte[] bytes) {
        int shift;
        if ((shift = Long.BYTES - bytes.length) < 0) throw new RuntimeException(
                "Length %d of bytes is bigger than 8 bytes in long!"
                        .formatted(bytes.length));
        final var bytesLongLength = new byte[Long.BYTES];
        System.arraycopy(bytes, 0, bytesLongLength, shift, bytes.length);
        return bytesLongLength;
    }

    public static int amountOfLongsInByteArray(byte[] bytes) {
        final var amount = bytes.length / Long.BYTES;
        if (bytes.length % Long.BYTES == 0) return amount;
        else return amount + 1;
    }

    public static byte[] removeZeroPrefix(byte[] bytes) {
        int i = 0;
        for (; i < bytes.length; i++) {
            if (bytes[i] != 0) break;
        }
        return Arrays.copyOfRange(bytes, i, bytes.length);
    }

    public static byte[] removeZeroSuffix(byte[] bytes) {
        int i = bytes.length - 1;
        for (; i >= 0; i--) {
            if (bytes[i] != 0) break;
        }
        return Arrays.copyOfRange(bytes, 0, bytes.length);
//        return removePrefix(bytes, i + 1);
    }

    public static byte[] removePrefix(byte[] bytes, int length) {
//        byte[] result = new byte[length];
//        System.arraycopy(bytes, bytes.length - length, result, 0, result.length);
//        return result;
        return Arrays.copyOfRange(bytes, bytes.length - length, bytes.length);
    }
}
