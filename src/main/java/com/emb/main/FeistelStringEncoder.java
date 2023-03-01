package com.emb.main;

import com.emb.util.ByteUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FeistelStringEncoder implements Encoder<String> {

    private final FeistelByteEncoder byteEncoder = new FeistelByteEncoder();
    private final Charset charset;

    public FeistelStringEncoder() {
        this(StandardCharsets.UTF_8);
    }

    public FeistelStringEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String encrypt(String data) {
        var bytes = data.getBytes(charset);

        final var longs = ByteUtils.byteArrayToLongArray(bytes);

        for (int i = 0; i < longs.length; i++) {
            longs[i] = FeistelEncrypt.encryptBlock(longs[i]);
        }

        bytes = ByteUtils.longArrayToByteArray(longs);
        return new String(bytes, charset);
    }

    @Override
    public String decrypt(String data) {
        var bytes = data.getBytes(charset);

        final var longs = ByteUtils.byteArrayToLongArray(bytes);

        for (int i = 0; i < longs.length; i++) {
            longs[i] = FeistelEncrypt.decryptBlock(longs[i]);
        }

        bytes = ByteUtils.longArrayToByteArray(longs);
        return new String(bytes, charset);
    }
}
