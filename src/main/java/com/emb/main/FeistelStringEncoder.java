package com.emb.main;

import com.emb.util.ByteUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

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
        final var encoded = FeistelEncrypt.encrypt(data.getBytes(charset));
        final var chars = new char[encoded.length];


        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (encoded[i] & 0xFF);
//            chars[i] = (char) (encoded[i]);
//            printCompareBytes(encoded[i], chars[i]);
        }

        var str = new String(chars).toCharArray();
        System.out.println(encoded.length);
        System.out.println(str.length);
        for (int i = 0; i < str.length; i++) {

            var enc = i < encoded.length ? encoded[i] : null;
            System.out.printf("%d, %d, \"%s\"%n", enc, (byte) str[i], str[i]);
            printCompareBytes(enc, (byte) str[i]);

        }

        return new String(chars);
//        return new String(FeistelEncrypt.encrypt0(data.getBytes(charset)), charset);
//        return new String(byteEncoder.encrypt(data.getBytes(charset)), charset);
    }

    @Override
    public String decrypt(String data) {
        final var chars = data.toCharArray();
        final var bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        final var decoded = FeistelEncrypt.decrypt(bytes);

        return new String(decoded, charset);
    }

    private static void printCompareBytes(Byte byteValue, Byte intValue) {
        var byteString = Optional.ofNullable(byteValue)
                .map(ByteUtils::numberToPrettyBinaryString)
                .orElse("         ");
        var codeString = ByteUtils.numberToPrettyBinaryString(intValue);
        System.out.printf("byte, code: %s, %s%s%n",
                byteString,
                codeString,
                byteString.equals(codeString) ? "" : " -- [No matching!]");
    }
}
