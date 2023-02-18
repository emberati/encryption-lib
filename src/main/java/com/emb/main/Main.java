package com.emb.main;

import java.util.Arrays;
import java.util.Collection;

import static com.emb.util.ByteUtils.numberToPrettyBinaryString;
import static com.emb.util.ByteUtils.prettifyBinaryString;

public class Main {
    public static void main(String[] args) {
        final var block = (byte) 0b0000_1111;
        final var shifted = block >>> 4;
        System.out.println(numberToPrettyBinaryString(block));
        System.out.println(numberToPrettyBinaryString(shifted));
    }
/*
    public static long[] byteArrayToLongArray(byte[] bytes) {
        final var longs = new LinkedList<Long>();
        final var mask = 0x0L;
        final var shift = 8;

        var sequence = mask;
        for (int i = 0; i < bytes.length; i++) {
//        for (int i = bytes.length - 1; i >= 0; --i) {
            var shiftedBytes = (long) bytes[i] << shift * i;
            if (64 - shift == shift * (i - 1)) {
                longs.add(sequence);
                sequence = mask;
                continue;
            }
            sequence |= shiftedBytes;
            System.out.printf("[%d]':\t%s%n", i, makePrettyBinaryString(sequence, 16));
//            System.out.printf("[%d]:\t%s%n", i, makePrettyBinaryString(bytes[i], 16));
        }
        return longCollectionToArray(longs);
    }
 */

    public static long[] longCollectionToArray(Collection<Long> longs) {
        final var array = new long[longs.size()];
        var index = 0;
        for (var value : longs) {
            array[index] = value;
            index++;
        }
        return array;
    }
}