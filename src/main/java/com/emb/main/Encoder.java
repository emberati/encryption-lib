package com.emb.main;

import com.emb.util.ByteUtils;

import java.util.Arrays;
import java.util.function.Function;

public interface Encoder<T> {
    T encrypt(T data);
    T decrypt(T data);

    static byte[] process(Function<Long, Long> action, byte[] source) {
        final var target = new byte[Long.BYTES * ByteUtils.amountOfLongsInByteArray(source)];

        var buffer = new byte[Long.BYTES];
        var i = 0;
        var section = Math.min(Long.BYTES, source.length - i);
        var block = 0L;

        while (section > 0) {
            Arrays.fill(buffer, 0, Long.BYTES - section, (byte) 0);

            System.arraycopy(source, i, buffer, Long.BYTES - section, section);
            block = ByteUtils.byteArrayToLong(buffer);

            block = action.apply(block);
            buffer = ByteUtils.longToByteArray(block);

            System.arraycopy(buffer, 0, target, i, Long.BYTES);

            i += section;
            section = Math.min(Long.BYTES, source.length - i);
        }

        // cleaning zeros

        byte[] tail;
        byte[] head;

        tail = Arrays.copyOfRange(target, target.length - Long.BYTES, target.length);
        tail = ByteUtils.removeZeroPrefix(tail);
        head = Arrays.copyOfRange(target, 0, target.length - Long.BYTES);

        buffer = new byte[target.length - Long.BYTES + tail.length];

        System.arraycopy(head, 0, buffer, 0, head.length);
        System.arraycopy(tail, 0, buffer, head.length, tail.length);

        return buffer;
    }
}
