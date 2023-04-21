package com.emb.crypto;

import com.emb.util.ByteUtils;

import java.util.Random;

public class CBCByteEncoder extends FeistelCipher<byte[]> {
    private final long vector;

    public CBCByteEncoder() {
        this(new Random().nextLong());
    }

    public CBCByteEncoder(long vector) {
        this.vector = vector;
    }

    public CBCByteEncoder(long seed, int roundsAmount, long vector) {
        super(seed, roundsAmount);
        this.vector = vector;
    }

    @Override
    protected byte[] processSequence(BlockCryptographyAction action, byte[] data) {
        final var longArray = ByteUtils.byteArrayToLongArray(data);

        longArray[0] ^= vector;

        for (int i = 1; i < longArray.length; i++) {
            longArray[i] = action.apply(longArray[i] ^ longArray[i - 1]);
        }

        var byteArray = ByteUtils.longArrayToByteArray(longArray);
        byteArray = ByteUtils.removeNonMatchingZeros(byteArray);

        return byteArray;
    }
}
