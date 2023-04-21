package com.emb.crypto;

import com.emb.util.ByteUtils;

import java.util.Random;

public class OFBByteEncoder extends FeistelCipher<byte[]> {

    private final long vector;

    public OFBByteEncoder() {
        this(new Random().nextLong());
    }

    public OFBByteEncoder(long vector) {
        this.vector = vector;
    }

    public OFBByteEncoder(long seed, int roundsAmount, long vector) {
        super(seed, roundsAmount);
        this.vector = vector;
    }

    @Override
    public byte[] encode(byte[] data) {
        return processSequenceFast(this::encryptBlock, data);
    }

    @Override
    public byte[] decode(byte[] data) {
        return ByteUtils.removeNonMatchingZeros(processSequenceFast(this::decryptBlock, data));
    }

    @Override
    protected long encryptBlock(long block) {
        return super.encryptBlock(vector) ^ block;
    }

    @Override
    protected long decryptBlock(long block) {
        return super.decryptBlock(vector) ^ block;
    }
}
