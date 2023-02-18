package com.emb.main;

import com.emb.util.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestMain {
    private static final Charset charset = StandardCharsets.UTF_8;
    private static final String message = "Fuck u nigga!";
    private static final byte[] messageBytes = message.getBytes(charset);

    @Test
    public void testMessageEncryptDecrypt() {
//        Assert.fail("Not implemented yet!");
        var encryptedMessageBytes = FeistelEncrypt.encrypt(message.getBytes(charset));
        var decryptedMessageBytes = FeistelEncrypt.decrypt(encryptedMessageBytes);

        var encryptedMessage = new String(encryptedMessageBytes, charset);
        var decryptedMessage = new String(decryptedMessageBytes, charset);

        System.out.printf("Original bytes [%d]:%n%s%n", messageBytes.length, ByteUtils.joinPrettyBytes(messageBytes));
        System.out.printf("Original stroke:%n%s%n", message);
        System.out.printf("Encrypted bytes [%d]:%n%s%n", encryptedMessageBytes.length, ByteUtils.joinPrettyBytes(encryptedMessageBytes));
        System.out.printf("Encrypted stroke:%n%s%n", encryptedMessage);
        System.out.printf("Decrypted bytes [%d]:%n%s%n", decryptedMessageBytes.length, ByteUtils.joinPrettyBytes(decryptedMessageBytes));
        System.out.printf("Decrypted stroke:%n%s%n", decryptedMessage);

        Assert.assertEquals(messageBytes.length, encryptedMessageBytes.length);
        Assert.assertEquals(messageBytes.length, decryptedMessageBytes.length);
        Assert.assertEquals(ByteUtils.joinPrettyBytes(messageBytes), ByteUtils.joinPrettyBytes(decryptedMessageBytes));
        Assert.assertEquals(message, decryptedMessage);
    }

    @Test
    public void testLongEncryptDecrypt() {
        final var longValue = 0x1AFE7292F5DDAE22L;
        final var encryptedBytesLong = FeistelEncrypt.encryptBlock(longValue);
        final var decryptedByteLong = FeistelEncrypt.decryptBlock(encryptedBytesLong);
        final var originalBytesString = ByteUtils.numberToPrettyBinaryString(longValue, 16);
        final var decryptedBytesString = ByteUtils.numberToPrettyBinaryString(decryptedByteLong, 16);

        Assert.assertEquals(originalBytesString, decryptedBytesString);
    }

    @Test
    public void testByteArrayEncryptDecrypt() {
        final var bytes = new byte[] {
                (byte) 0b0000_0000, (byte) 0b0000_0001, (byte) 0b0001_1110, (byte) 0b1111_0011,
                (byte) 0b110_00000, (byte) 0b101_00101, (byte) 0b100_10010, (byte) 0b1111_1101
        };
        final var condensedBytesLong = ByteUtils.bytesToLong(bytes);
        final var encryptedBytesLong = FeistelEncrypt.encryptBlock(condensedBytesLong);
        final var decryptedByteLong = FeistelEncrypt.decryptBlock(encryptedBytesLong);
        final var originalBytesString = ByteUtils.numberToPrettyBinaryString(condensedBytesLong, 16);
        final var decryptedBytesString = ByteUtils.numberToPrettyBinaryString(decryptedByteLong, 16);

        Assert.assertEquals(originalBytesString, decryptedBytesString);
    }
}
