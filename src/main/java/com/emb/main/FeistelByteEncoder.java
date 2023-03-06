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
        return FeistelEncrypt.decrypt(data);
//        return removeZeroTail(FeistelEncrypt.decrypt0(data));
//        FeistelEncrypt.decrypt0(data);
    }

    private byte[] removeZeroTail(byte[] bytes) {
        int i = bytes.length - 1;
        for (; i >= 0; i--) {
            if (bytes[i] != 0) break;
            System.out.println(i);
        }
        byte[] result = new byte[i + 1];
        System.arraycopy(bytes, 0, result, 0, result.length);
        return result;
    }
}
