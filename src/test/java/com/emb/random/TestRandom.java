package com.emb.random;

import com.emb.random.SeedBasedRandom;
import com.emb.random.TimeBasedRandom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

public class TestRandom {
    @Test
    public void testSeedRandom() {
        var rand = new SeedBasedRandom();
        for (int i = 0; i < 100; i++) {
            rand.nextInt();
        }

        System.out.println(Arrays.toString(rand.intSequence()));
    }

    @Test
    public void testTimeRandom() {
        var rand0 = new TimeBasedRandom();
        var rand1 = new TimeBasedRandom();

        for (int i = 0; i < 100; i++) {
            rand0.nextInt();
            rand1.nextInt();
        }

        System.out.println(Arrays.toString(rand0.intSequence()));
        System.out.println(Arrays.toString(rand1.intSequence()));

        assertNotEquals(Arrays.toString(rand0.intSequence()), Arrays.toString(rand1.intSequence()));
    }
}
