package com.emb.main;

import com.emb.util.ByteUtils;

public class FeistelByteEncoder implements Encoder<byte[]> {
    @Override
    public byte[] encrypt(byte[] data) {
        return FeistelEncrypt.encrypt(data);
//        return FeistelEncrypt.encrypt0(data);
    }

    @Override
    public byte[] decrypt(byte[] data) {
//        return FeistelEncrypt.decrypt(data);
        return ByteUtils.removeZeroPrefix(FeistelEncrypt.decrypt(data));
//        FeistelEncrypt.decrypt0(data);
    }
}
