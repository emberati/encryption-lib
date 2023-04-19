package com.emb.crypto;

import com.emb.util.ByteUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ECBStringEncoder implements Encoder<String> {

    private final ECBByteEncoder byteEncoder = new ECBByteEncoder();
    private final Charset charset;

    public ECBStringEncoder() {
        this(StandardCharsets.UTF_8);
    }

    public ECBStringEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String encode(String data) {
        final var encoded = byteEncoder.encode(data.getBytes(charset));
        final var chars = byteArrayToCharArray(encoded);
        return new String(chars);
    }

    @Override
    public String decode(String data) {
        final var chars = data.toCharArray();
        final var bytes = charArrayToByteArray(chars);
        final var decoded = byteEncoder.decode(bytes);

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
