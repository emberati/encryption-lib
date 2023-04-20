package com.emb.crypto;

public final class FeistelCipher {
    private static final long MASK_32_RIGHT = 0xFFFFFFFFL;  // 32 разрядное число

    private FeistelCipher() {}

    static long encrypt(long key, long block, int roundAmount, final boolean reverse) {
        final var roundDirection = reverse ? -1 : 1;
        final var stopIndex = reverse ? -1 : roundAmount;
        var roundIndex = reverse ? roundAmount - 1 : 0;

        // Выделяем из 64 разрядного блока левую и правую части
        var leftHalfBlock = (int) ((block >> 32) & MASK_32_RIGHT);  // левый подблок (32 битный)
        var rightHalfBlock = (int) (block & (int) MASK_32_RIGHT);   // правый подблок (32 битный)
        var leftHalfBlockRound = 0;
        var rightHalfBlockRound = 0;

        while(roundIndex != stopIndex) {                            // Выполняются 8 раундов шифрования
            int roundKey = roundKey(roundIndex, key);

            leftHalfBlockRound = leftHalfBlock;
            rightHalfBlockRound = rightHalfBlock ^ F(leftHalfBlock, roundKey);

            leftHalfBlock = rightHalfBlockRound;
            rightHalfBlock = leftHalfBlockRound;

            roundIndex += roundDirection;
        }

        leftHalfBlock = leftHalfBlockRound;
        rightHalfBlock = rightHalfBlockRound;

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        return (long) leftHalfBlock << 32 | rightHalfBlock & MASK_32_RIGHT;
    }

    // Циклический сдвиг вправо для 32 бит
    @Deprecated(forRemoval = true)
    private static int rightShift32(int block, int shift) {
        return (block >>> shift) | (block << (32 - shift));
    }

    // Циклический сдвиг вправо для 64 бит
    @Deprecated(forRemoval = true)
    private static long rightShift64(long block, int shift) {
        return (block >>> shift) | (block << (64 - shift));
    }

    // Циклический сдвиг влево для 32 бит
    @Deprecated(forRemoval = true)
    private static int leftShift32(int block, int shift) {
        return (block << shift) | (block >>> (32 - shift));
    }

    @Deprecated(forRemoval = true)
    // Циклический сдвиг влево для 64 бит
    private static long leftShift64(long block, int shift) {
        return (block << shift) | (block >>> (64 - shift));
    }

    @Deprecated(forRemoval = true)
    // Генерация 32 разрядного ключа на i-м раунде из исходного 64-разрядного
    private static int roundKey(int i, long key) {
        return (int) rightShift64(key, i * 8);    // циклический сдвиг на 8 бит и обрезка правых 32 бит
    }

    // Образующая функция - функция, шифрующая половину блока halfBlock ключом K_i на i-м раунде
    private static int F(int halfBlock, int K_i) {
        int f1 = leftShift32(halfBlock, 9);
        int f2 = rightShift32(K_i, 11) | halfBlock;
        return f1 ^ f2;
    }
}
