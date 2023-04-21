package com.emb.crypto;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

@SuppressWarnings("unused")
public class TestStringEncoderArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
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
