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
    public byte[] encode(byte[] data) {
        final var longArray = ByteUtils.byteArrayToLongArray(data);

        longArray[0] = encryptBlock(longArray[0] ^ vector);

        for (int i = 1; i < longArray.length; i++) {
            longArray[i] = encryptBlock(longArray[i] ^ longArray[i - 1]);
        }

        return ByteUtils.longArrayToByteArray(longArray);
    }

    @Override
    public byte[] decode(byte[] data) {
        final var longArray = ByteUtils.byteArrayToLongArray(data);

        longArray[0] = decryptBlock(longArray[0]) ^ vector;

        for (int i = 1; i < longArray.length; i++) {
            longArray[i] = decryptBlock(longArray[i]) ^ longArray[i - 1];
        }

        var byteArray = ByteUtils.longArrayToByteArray(longArray);
        byteArray = ByteUtils.removeNonMatchingZeros(byteArray);

        return byteArray;
    }
}
