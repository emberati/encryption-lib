package com.emb.random;

public class TimeBasedRandom extends SeedBasedRandom {
    public TimeBasedRandom() {
        super((int) (System.nanoTime() & 0xFFFFFFFFL));
    }
}
