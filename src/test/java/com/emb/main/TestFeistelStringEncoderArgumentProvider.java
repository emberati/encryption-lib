package com.emb.main;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

@SuppressWarnings("unused")
public class TestFeistelStringEncoderArgumentProvider {
    public static Stream<Arguments> testFeistelStringEncoderEncodeDecode() {
        final var abobaString = "Aboba";
        final var niggaString = "Fuck u nigga!";
        return Stream.of(
                of(abobaString, abobaString),
                of(niggaString, niggaString)
        );
    }
}
