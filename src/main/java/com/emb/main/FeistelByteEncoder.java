package com.emb.main;

public class FeistelByteEncoder implements Encoder<byte[]> {
    @Override
    public byte[] encrypt(byte[] data) {
        return FeistelEncrypt.encrypt(data);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return FeistelEncrypt.decrypt(data);
    }
}
