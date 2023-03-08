package com.emb.main;

import com.emb.util.ByteUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFeistelStringEncoder {
    @ParameterizedTest
    @MethodSource("com.emb.main.TestFeistelStringEncoderArgumentProvider#testFeistelStringEncoderEncodeDecode")
    public void testFeistelStringEncoderEncryptDecrypt(String original, String controlValue) {
        final var byteEncoder = new FeistelStringEncoder();
        final var encoded = byteEncoder.encrypt(original); System.out.println(encoded.getBytes().length);
        final var decoded = byteEncoder.decrypt(encoded); System.out.println(decoded.getBytes().length);
        // [56, 82, 40, -72, -124, -45, 80, -100]
        // [56, 82, 40, -17, -65, -67, -17, -65, -67, -17, -65, -67, 80, -17, -65, -67]
        assertEquals(controlValue, decoded);
    }

    @Test
    @Disabled
    public void testFeistelStringEncoderBackwardCapability() {
        final var encoder = new FeistelStringEncoder();

        final var testMessageBytes = new byte[] {
                0b0101_0100, 0b0110_0101, 0b0111_0011, 0b0111_0100,
                0b0010_0000, 0b0110_1101, 0b0110_0101, 0b0111_0011,
                0b0111_0011, 0b0110_0001, 0b0110_0111, 0b0110_0101
        };

        final var testMessageBytes0 = new byte[]{84, 101, 115, 116, 32, 109, 101, 115, 115, 97, 103, 101};


        final var message = "Test message";
        var bytes = message.getBytes(StandardCharsets.UTF_8);
        var codes = message.toCharArray();

//        System.out.println(bytes.length);
//        System.out.println(codes.length);

        for (int i = 0; i < bytes.length; i++) {
//            printCompareBytes(bytes[i], codes[i]);
        }

//        final var message = new String(testMessageBytes0);
//        final var encoded = encoder.encrypt(message);
//        final var decoded = encoder.decrypt(encoded);
//
//        final var messageBytes = message.getBytes();
//        final var encodedBytes = encoded.getBytes();
//        final var decodedBytes = decoded.getBytes();
//
//        final var messageBytesString = ByteUtils.joinPrettyBytes(messageBytes, ", ");
//        final var encodedBytesString = ByteUtils.joinPrettyBytes(encodedBytes, ", ");
//        final var decodedBytesString = ByteUtils.joinPrettyBytes(decodedBytes, ", ");
//
//        System.out.printf("Message: %s%n", message);
//        System.out.printf("Encoded: %s%n", encoded);
//        System.out.printf("Decoded: %s%n", decoded);
////        System.out.println("1D4q��s�\u0011\"�t�\b*�");
//        System.out.println();
//        System.out.printf("Message bytes: %s%n", messageBytesString);
//        System.out.printf("Encoded bytes: %s%n", encodedBytesString);
//        System.out.printf("Decoded bytes: %s%n", decodedBytesString);
//
//        assertEquals(messageBytesString, decodedBytesString);
//        assertEquals(message, decoded);
    }

    private static void printCompareBytes(byte byteValue, int intValue) {
        var byteString = ByteUtils.numberToPrettyBinaryString(byteValue);
        var codeString = ByteUtils.numberToPrettyBinaryString(intValue);
        System.out.printf("byte, code: %s, %s%s%n",
                          byteString,
                          codeString,
                          byteString.equals(codeString) ? "" : " -- [No matching!]");
    }
}
