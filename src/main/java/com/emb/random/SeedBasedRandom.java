package com.emb.random;

import java.util.ArrayList;
import java.util.List;

public class SeedBasedRandom implements Random {
    public final int a = 45;
    public final int c = 21;
    public final int m = 67;
    public int seed;

    private final List<Integer> intSequence;

    public SeedBasedRandom(int seed) {
        this.seed = seed;
        this.intSequence = new ArrayList<>();
    }

    public SeedBasedRandom() {
        this(2);
    }

    public int nextInt() {
        seed = (a * seed + c) % m;
        intSequence.add(seed);
        return seed;
    }

    public int[] intSequence() {
        var sequence = new int[intSequence.size()];
        for (int i = 0; i < intSequence.size(); i++) {
            sequence[i] = intSequence.get(i);
        }
        return sequence;
    }
}
