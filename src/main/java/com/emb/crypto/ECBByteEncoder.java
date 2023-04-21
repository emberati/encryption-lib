package com.emb.crypto;

import com.emb.util.ByteUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class ECBByteEncoder extends FeistelCipher<byte[]> {

    // Разбивание на блоки и параллельное шфрование/дешифрование
    @Override
    protected byte[] processSequence(BlockCryptographyAction action, byte[] data) {
        final var target = new byte[Long.BYTES * ByteUtils.amountOfLongsInByteArray(data)];

        var buffer = new byte[Long.BYTES];
        var i = 0;
        var section = Math.min(Long.BYTES, data.length - i);
        var block = 0L;

        while (section > 0) {
            Arrays.fill(buffer, 0, Long.BYTES - section, (byte) 0);

            System.arraycopy(data, i, buffer, Long.BYTES - section, section);
            block = ByteUtils.byteArrayToLong(buffer);

            block = action.apply(block);
            buffer = ByteUtils.longToByteArray(block);

            System.arraycopy(buffer, 0, target, i, Long.BYTES);

            i += section;
            section = Math.min(Long.BYTES, data.length - i);
        }

        // cleaning zeros

        buffer = ByteUtils.removeNonMatchingZeros(target);

        return buffer;
    }
}
