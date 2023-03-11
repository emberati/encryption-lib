package com.emb.main;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

@SuppressWarnings("unused")
public class TestFeistelStringEncoderArgumentProvider {
    public static Stream<Arguments> testFeistelStringEncoderEncodeDecode() {
        final var smallString = "Test";
        final var middleString = "Long test message!";
        final var longString = "It is a long string for testing encoding algorithms.";
        return Stream.of(
                of(smallString, smallString),
                of(middleString, middleString),
                of(longString, longString)
        );
    }
}
