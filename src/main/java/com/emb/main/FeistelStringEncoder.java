package com.emb.main;

import com.emb.util.ByteUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
        final var encoded = byteEncoder.encrypt(data.getBytes(charset));
        final var chars = byteArrayToCharArray(encoded);
        return new String(chars);
    }

    @Override
    public String decrypt(String data) {
        final var chars = data.toCharArray();
        final var bytes = charArrayToByteArray(chars);
        final var decoded = byteEncoder.decrypt(bytes);

        return new String(decoded);
    }

    private char[] byteArrayToCharArray(byte[] bytes) {
        final var chars = new char[bytes.length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (bytes[i] & 0xFF);
        }
        return chars;
    }

    private byte[] charArrayToByteArray(char[] chars) {
        final var bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    private static void printCompareBytes(Byte byteValue, Byte intValue) {
        var byteString = Optional.ofNullable(byteValue)
                .map(ByteUtils::toBinaryString)
                .orElse("         ");
        var codeString = ByteUtils.toBinaryString(intValue);
        System.out.printf("byte, code: %s, %s%s%n",
                byteString,
                codeString,
                byteString.equals(codeString) ? "" : " -- [No matching!]");
    }
}
