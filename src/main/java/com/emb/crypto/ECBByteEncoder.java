package com.emb.crypto;

import com.emb.util.ByteUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class ECBByteEncoder implements Encoder<byte[]> {

    private final long key;
    private final long seed;
    private final int roundsAmount;

    public ECBByteEncoder() {
        this(new Random().nextLong(), 8);
    }

    public ECBByteEncoder(long seed, int roundsAmount) {
        this.key = new Random(seed).nextLong();
        this.seed = seed;
        this.roundsAmount = roundsAmount; // TODO: Не учитывать циклические раунды
    }

    @Override
    public byte[] encode(byte[] data) {
        final var longArray = ByteUtils.byteArrayToLongArray(data);

        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = encryptBlock(longArray[i]);
        }

        var byteArray = ByteUtils.longArrayToByteArray(longArray);
        byteArray = ByteUtils.removeNonMatchingZeros(byteArray);

        return byteArray;
    }

    @Override
    public byte[] decode(byte[] data) {
        final var longArray = ByteUtils.byteArrayToLongArray(data);

        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = decryptBlock(longArray[i]);
        }

        var byteArray = ByteUtils.longArrayToByteArray(longArray);
        byteArray = ByteUtils.removeNonMatchingZeros(byteArray);

        return byteArray;
    }

    private long encryptBlock(long block) {
        return FeistelCipher.encrypt(key, block, roundsAmount, false);
    }

    private long decryptBlock(long block) {
        return FeistelCipher.encrypt(key, block, roundsAmount,true);
    }

    // Разбивание на блоки и параллельное шфрование/дешифрование
    private byte[] convertAndEncrypt(Function<Long, Long> action, byte[] source) {
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

        buffer = ByteUtils.removeNonMatchingZeros(target);

        return buffer;
    }

    public long getKey() {
        return key;
    }

    public long getSeed() {
        return seed;
    }
}
