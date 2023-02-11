package com.emb.main;

import com.emb.util.ByteUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var unsLong = 0x96EA704CFB1CF672L;
        var sigLong = Long.toUnsignedString(unsLong);
        var unsignedLongBytesString = Long.toUnsignedString(unsLong, 2);
//        var str = new String(unsignedLongToByteArray(unsLong), StandardCharsets.UTF_16LE);

//        System.out.println(unsLong);
//        System.out.println(sigLong);
//        System.out.println(str);

        int bytes = 0b00000100;
        var decimal = Integer.toString(bytes);
        var binary = Integer.toBinaryString(bytes);
        var shifted = Integer.toBinaryString(bytes >> 1);

        System.out.println(decimal);
        System.out.println(binary);
        System.out.println(shifted);

//        System.out.println(Arrays.toString(ByteUtils.unsignedLongToByteArray(0xFF)));
        System.out.println(Long.toBinaryString(0xFF));
        System.out.println(ByteUtils.makePrettyBinaryString(0b1101));
        System.out.println(ByteUtils.makePrettyBinaryString(0b101));

        var times = 1000;
        var timer = new Timer();

//        System.out.println(timer.getSummary());

//        var separatedLoopsMethodTime = benchmark(Main::makePrettyBinaryStringSeparatedLoops, times);
//        var combinedLoopsMethodTime = benchmark(Main::makePrettyBinaryStringCombinedLoops, times);
        timer.bench(Main::makePrettyBinaryStringCombinedLoops, times);
        System.out.println(timer.getSummary());
        timer.bench(Main::makePrettyBinaryStringSeparatedLoops, times);
        System.out.println(timer.getSummary());
    }

    public static String makePrettyBinaryStringSeparatedLoops() {
        return ByteUtils.makePrettyBinaryString0(0x96EA704CFB1CF672L, 2);
    }

    public static String makePrettyBinaryStringCombinedLoops() {
        return ByteUtils.makePrettyBinaryString(0x96EA704CFB1CF672L, 2);
    }

    public static <T> long benchmark(Supplier<T> function, int times) {
        var startTime = 0L;
        var endTime = 0L;
        var totalTime = 0L;
        var averageTime = 0L;

        final T result = function.get();

        for (int i = 0; i < times; i++) {
            startTime = System.nanoTime();
            function.get();
            endTime = System.nanoTime();
            totalTime += endTime - startTime;
        }

        averageTime = totalTime / times;

        System.out.printf("Result: %s%n", result.toString());
        System.out.printf("Estimated (%s times): %d %f (sec) %n", times, totalTime, (double) totalTime / 1000 / 1000 / 1000);
        System.out.printf("Estimated in average: %d %f (sec) %n%n", averageTime, (double) averageTime / 1000 / 1000 / 1000);

        return totalTime;
    }
}
