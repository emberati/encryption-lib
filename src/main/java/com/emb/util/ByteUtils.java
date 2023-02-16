package com.emb.util;

import java.nio.ByteBuffer;

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

    public static String makePrettyBinaryString(long value) {
        var string = Long.toBinaryString(value);
        return makePrettyBinaryString(string);
    }

    public static String makePrettyBinaryString(long value, int nibblesAmount) {
        return makePrettyBinaryString(Long.toBinaryString(value), nibblesAmount);
    }

    public static String makePrettyBinaryString(String string) {
        var length = string.length();
        var nibblesAmount = length / 4;
        if (length % 4 != 0) nibblesAmount += 1;
        return makePrettyBinaryString(string, nibblesAmount);
    }

    public static String makePrettyBinaryString(String string, int nibblesAmount) {
        var binaryString = string.getBytes();
        var prettyString = new StringBuilder();
        var initialLength = binaryString.length;
        var finalLength = Math.max(nibblesAmount * 4, initialLength);
        var zerosAmount = finalLength - initialLength;

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

    public static long[] byteArrayToLongArray(byte[] bytes) {
        final var buffer = ByteBuffer.wrap(bytes);
        final var size = buffer.limit() % 8 == 0 ?
                buffer.limit() / Byte.SIZE :
                buffer.limit() / Byte.SIZE + 1;
        final var longs = new long[size];

        var i = 0;
        while (buffer.remaining() >= 8) {
            longs[i] = buffer.getLong();
            i++;
        }

        if (buffer.hasRemaining()) {
            final var shift = Long.SIZE - buffer.remaining() * Byte.SIZE;
            final var tail = buffer.getLong(buffer.remaining());
            longs[i] = tail << shift;
        }

        return longs;
    }
}
