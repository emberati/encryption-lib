package com.emb.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.StringJoiner;

public class ByteUtils {
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
        final var finalLength = Math.max(nibblesAmount * 4, initialLength);
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
        var joiner = new StringJoiner(" ");
        for (var value: bytes) {
            joiner.add(numberToPrettyBinaryString(value));
        }
        return joiner.toString();
    }

    public static String joinPrettyBytes(long[] bytes) {
        var joiner = new StringJoiner(" ");
        for (var value: bytes) {
            joiner.add(numberToPrettyBinaryString(value));
        }
        return joiner.toString();
    }

    @Deprecated
    public static byte[] removeZeroTail(byte[] bytes) {
        var index = -1;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return Arrays.copyOf(bytes, bytes.length);
        } else {
            return Arrays.copyOf(bytes, bytes.length - (bytes.length - index));
        }
    }

    public static long[] byteArrayToLongArray(byte[] bytes) {
        final var size = bytes.length % Byte.SIZE == 0 ?
                bytes.length / Byte.SIZE :
                bytes.length / Byte.SIZE + 1;
        final var buffer = ByteBuffer.wrap(bytes);
        final var longs = new long[size];

        var i = 0;
        while (buffer.remaining() >= Byte.SIZE) {
            longs[i] = buffer.getLong();
            i++;
        }

        if (buffer.hasRemaining()) {
            final var shift = Long.SIZE - buffer.remaining() * Byte.SIZE;
            final var tail = buffer.getLong(buffer.remaining());
            longs[i] = tail << shift;
        }

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

    public static byte[] longToBytes(long value) {
        var bytes = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>= Byte.SIZE;
        }
        return bytes;
    }

    public static long bytesToLong(final byte[] bytes) {
        var result = 0L;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }
}
