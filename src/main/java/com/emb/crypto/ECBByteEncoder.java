package com.emb.crypto;

import com.emb.util.ByteUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class ECBByteEncoder implements Encoder<byte[]> {

    private static final int ROUND_AMOUNT = 8; // Число раундов
    private static final long MASK_32_RIGHT = 0xFFFFFFFFL; // 32 разрядное число
    private final long key;
    private final long seed;

    public ECBByteEncoder() {
        this(0x96EA704CFB1CF672L);
    }

    public ECBByteEncoder(long seed) {
        this.key = new Random(seed).nextLong();
        this.seed = seed;
    }

    @Override
    public byte[] encode(byte[] data) {
        final var longArray = ByteUtils.byteArrayToLongArray(data);

        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = encryptBlock(longArray[i]);
        }

        var byteArray = ByteUtils.longArrayToByteArray(longArray);
        byteArray = ByteUtils.removeNonMatchingZeros(byteArray);

        return byteArray;
    }

    @Override
    public byte[] decode(byte[] data) {
        final var longArray = ByteUtils.byteArrayToLongArray(data);

        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = decryptBlock(longArray[i]);
        }

        var byteArray = ByteUtils.longArrayToByteArray(longArray);
        byteArray = ByteUtils.removeNonMatchingZeros(byteArray);

        return byteArray;
    }

    private long encryptBlock(long block) {
        return processBlock(block, false);
    }

    private long decryptBlock(long block) {
        return processBlock(block, true);
    }

    // Разбивание на блоки и параллельное шфрование/дешифрование
    private byte[] convertAndEncrypt(Function<Long, Long> action, byte[] source) {
        final var target = new byte[Long.BYTES * ByteUtils.amountOfLongsInByteArray(source)];

        var buffer = new byte[Long.BYTES];
        var i = 0;
        var section = Math.min(Long.BYTES, source.length - i);
        var block = 0L;

        while (section > 0) {
            Arrays.fill(buffer, 0, Long.BYTES - section, (byte) 0);

            System.arraycopy(source, i, buffer, Long.BYTES - section, section);
            block = ByteUtils.byteArrayToLong(buffer);

            block = action.apply(block);
            buffer = ByteUtils.longToByteArray(block);

            System.arraycopy(buffer, 0, target, i, Long.BYTES);

            i += section;
            section = Math.min(Long.BYTES, source.length - i);
        }

        // cleaning zeros

        buffer = ByteUtils.removeNonMatchingZeros(target);

        return buffer;
    }

    private long processBlock(long block, final boolean reverse) {
        final var roundDirection = reverse ? -1 : 1;
        final var stopIndex = reverse ? -1 : ROUND_AMOUNT;
        var roundIndex = reverse ? ROUND_AMOUNT - 1 : 0;

        // Выделяем из 64 разрядного блока левую и правую части
        var leftHalfBlock = (int) ((block >> 32) & MASK_32_RIGHT); // левый подблок (32 битный)
        var rightHalfBlock = (int) (block & (int) MASK_32_RIGHT); // правый подблок (32 битный)
        var leftHalfBlockRound = 0;
        var rightHalfBlockRound = 0;

        while(roundIndex != stopIndex) {                                    // Выполняются 8 раундов шифрования
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
    private static int rightShift32(int block, int shift) {
        return (block >>> shift) | (block << (32 - shift));
    }

    // Циклический сдвиг вправо для 64 бит
    private static long rightShift64(long block, int shift) {
        return (block >>> shift) | (block << (64 - shift));
    }

    // Циклический сдвиг влево для 32 бит
    private static int leftShift32(int block, int shift) {
        return (block << shift) | (block >>> (32 - shift));
    }

    // Циклический сдвиг влево для 64 бит
    private static long leftShift64(long block, int shift) {
        return (block << shift) | (block >>> (64 - shift));
    }

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

    public long getKey() {
        return key;
    }

    public long getSeed() {
        return seed;
    }
}
