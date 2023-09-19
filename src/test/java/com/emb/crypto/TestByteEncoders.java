package com.emb.crypto;

import com.emb.util.ByteUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestByteEncoders {

    @ParameterizedTest
    @ArgumentsSource(ByteEncoderArgumentProvider.class)
    public void testECBByteEncoder(byte[] original, byte[] controlValue) {
        final var byteEncoder = new ECBByteEncoder();
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        final var delimiter = " ";
        assertEquals(ByteUtils.toBinaryString(controlValue, delimiter), ByteUtils.toBinaryString(decoded, delimiter));
    }

    @ParameterizedTest
    @ArgumentsSource(ByteEncoderArgumentProvider.class)
    public void testCBCByteEncoder(byte[] original, byte[] controlValue) {
        final var byteEncoder = new CBCByteEncoder();
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        final var delimiter = " ";
        assertEquals(ByteUtils.toBinaryString(controlValue, delimiter), ByteUtils.toBinaryString(decoded, delimiter));
    }

    @ParameterizedTest
    @ArgumentsSource(ByteEncoderArgumentProvider.class)
    public void testOFBByteEncoder(byte[] original, byte[] controlValue) {
        final var byteEncoder = new OFBByteEncoder();
        final var encoded = byteEncoder.encode(original);
        final var decoded = byteEncoder.decode(encoded);
        final var delimiter = " ";
        assertEquals(ByteUtils.toBinaryString(controlValue, delimiter), ByteUtils.toBinaryString(decoded, delimiter));
    }

    public <K, V> String toStringEntry(Map.Entry<K, V> entry) {
        return "%s: %s".formatted(entry.getKey(), entry.getValue());
    }

    public <T> T same(T o, T n) {
        return n;
    }

    public String readStringFromFile(String fileName) {
        var path = Paths.get(fileName);
        try {
            final var classLoader = getClass().getClassLoader();

            System.out.println(classLoader.resources("")
                    .map(URL::getPath)
                    .collect(Collectors.joining("\n")));

            System.out.println();

            classLoader.getResources("").asIterator()
                    .forEachRemaining(System.out::println);

            System.out.println();

            System.out.println(classLoader.getResource(""));
            System.out.println(classLoader.getResource("").getFile());
            System.out.println(classLoader.getResource("").getPath());

            System.out.println();

            System.out.println(getClass().getResource(""));
            System.out.println(getClass().getResource("").getPath());
            System.out.println(getClass().getResource("").getRef());
            System.out.println(getClass().getResource("").getFile());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println();
        try (var stream = Files.lines(path)) {
            return stream.collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
