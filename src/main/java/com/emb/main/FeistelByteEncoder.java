package com.emb.main;

import com.emb.util.ByteUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class FeistelByteEncoder implements Encoder<byte[]> {

    private static final int ROUND_AMOUNT = 8; // Число раундов
    private static final long MASK_32_RIGHT = 0xFFFFFFFFL; // 32 разрядное число
    private final long key;
    private final long seed;

    public FeistelByteEncoder() {
        this(0x96EA704CFB1CF672L);
    }

    public FeistelByteEncoder(long seed) {
        this.key = new Random(seed).nextLong();
        this.seed = seed;
    }

    @Override
    public byte[] encrypt(byte[] data) {
        return process(this::encryptBlock, data);
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return process(this::decryptBlock, data);
    }

    @Deprecated
    public byte[] encrypt0(byte[] bytes) {
        return processAlternate(this::encryptBlock, bytes);
    }

    @Deprecated
    public byte[] decrypt0(byte[] bytes) {
        return processAlternate(this::decryptBlock, bytes);
    }

    private static byte[] process(Function<Long, Long> action, byte[] source) {
        final var target = new byte[Long.BYTES * ByteUtils.amountOfLongsInByteArray(source)];
        System.out.printf("s: %s, t: %s, b: %s%n", source.length, target.length, ByteUtils.amountOfLongsInByteArray(source));

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

        byte[] tail;
        byte[] head;

        tail = Arrays.copyOfRange(target, target.length - Long.BYTES, target.length);
        tail = ByteUtils.removeZeroPrefix(tail);
        head = Arrays.copyOfRange(target, 0, target.length - Long.BYTES);

        buffer = new byte[target.length - Long.BYTES + tail.length];

        System.arraycopy(head, 0, buffer, 0, head.length);
        System.arraycopy(tail, 0, buffer, head.length, tail.length);

        return buffer;
    }

    @Deprecated
    private static byte[] processAlternate(Function<Long, Long> action, byte[] source) {
        final var longs = ByteUtils.byteArrayToLongArray(source);

        for (int i = 0; i < longs.length; i++) {
            longs[i] = action.apply(longs[i]);
        }

        return ByteUtils.longArrayToByteArray(longs);
    }

    private long encryptBlock(long block) {
        // Выделяем из 64 разрядного блока левую и правую части
        // FIXME: 19.02.2023
        int leftHalfBlock = (int) ((block >>> 32) & MASK_32_RIGHT); // левый подблок (32 битный)
        int rightHalfBlock = (int) (block & (int) MASK_32_RIGHT); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = 0; i < ROUND_AMOUNT; i++) {
            int roundKey = roundKey(i, key); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int leftHalfBlockRound = leftHalfBlock; // значение левого блока в конце раунда (такое же как в начале раунда)
            int rightHalfBlockRound = rightHalfBlock ^ F(leftHalfBlock, roundKey); // новое значение правого блока (шифуется с помощью функуии F)

            // Если раунд не последний, то
            if (i < ROUND_AMOUNT - 1) {
                leftHalfBlock = rightHalfBlockRound; // правый подблок становится левым
                rightHalfBlock = leftHalfBlockRound; // а левый подблок теперь правый
            } else // После последнего раунда блоки не меняются местами
            {
                leftHalfBlock = leftHalfBlockRound;
                rightHalfBlock = rightHalfBlockRound;
            }

            // Вывод подблоков на выходе раунда (для отладки)
            // Выходной правый блок должен быть равен входному левому блоку для всех раундов КРОМЕ последнего
            // Для последнего раунда входной левый блок равен выходному левому
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        long encryptedBlock = (long) leftHalfBlock << 32 | rightHalfBlock & MASK_32_RIGHT;
        // Возвращаем зашифрованный блок
        return encryptedBlock;
    }

    private long decryptBlock(long block) {
        // Выделяем из 64 разрядного блока левую и правую части
        int leftHalfBlock = (int) ((block >> 32) & MASK_32_RIGHT); // левый подблок (32 битный)
        int rightHalfBlock = (int) (block & MASK_32_RIGHT); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = ROUND_AMOUNT - 1; i >= 0; i--) {
            int K_i = roundKey(i, key); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int leftHalfBlockRound = leftHalfBlock; // значение левого блока в конце раунда (такое же как в начале раунда)
            int rightHalfBlockRound = rightHalfBlock ^ F(leftHalfBlock, K_i); // новое значение правого блока (шифуется с помощью функуии F)

            // Если раунд не последний, то
            if (i > 0) {
                leftHalfBlock = rightHalfBlockRound; // правый подблок становится левым
                rightHalfBlock = leftHalfBlockRound; // а левый подблок теперь правый
            } else // После последнего раунда блоки не меняются местами
            {
                leftHalfBlock = leftHalfBlockRound;
                rightHalfBlock = rightHalfBlockRound;
            }

            // Выходной правый блок должен быть равен входному левому блоку для всех раундов КРОМЕ последнего
            // Для последнего раунда входной левый блок равен выходному левому
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        // FIXME: 19.02.2023
        long decryptedBlock = leftHalfBlock;
        decryptedBlock = (decryptedBlock << 32) | (rightHalfBlock & MASK_32_RIGHT); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
        return decryptedBlock;
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
