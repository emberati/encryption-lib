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
//        return new String(FeistelEncrypt.encrypt(data.getBytes(charset)), charset);
        return new String(FeistelEncrypt.encrypt0(data.getBytes(charset)), charset);
    }

    @Override
    public String decrypt(String data) {
        //        return new String(FeistelEncrypt.decrypt(data.getBytes(charset)), charset);
        return new String(FeistelEncrypt.decrypt0(data.getBytes(charset)), charset);
    }
}
