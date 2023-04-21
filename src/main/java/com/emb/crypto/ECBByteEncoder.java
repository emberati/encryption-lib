package com.emb.crypto;

import com.emb.util.ByteUtils;

public class ECBByteEncoder extends FeistelCipher<byte[]> {

    @Override
    public byte[] encode(byte[] data) {
        return processSequenceFast(this::encryptBlock, data);
    }

    @Override
    public byte[] decode(byte[] data) {
        return ByteUtils.removeNonMatchingZeros(processSequenceFast(this::decryptBlock, data));
    }
}
