package com.emb.main;

import com.emb.util.ByteUtils;

import java.util.function.Function;

public class FeistelEncrypt {
    // Объявление констант
    private static final int roundAmount = 8; // Число раундов
    private static final long mask32right = 0xFFFFFFFFL; // 32 разрядное число
    private static final int size = 64;

    // Исходное сообщение
    private static long longMessage = 0x123456789ABCDEF0L;
    private static long key = 0x96EA704CFB1CF672L;  // исходный ключ (64 битный)

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
    private static int roundKey(int i) {
        return (int) rightShift64(key, i * 8);    // циклический сдвиг на 8 бит и обрезка правых 32 бит
    }

    // Образующая функция - функция, шифрующая половину блока halfBlock ключом K_i на i-м раунде
    private static int F(int halfBlock, int K_i) {
        int f1 = leftShift32(halfBlock, 9);
        int f2 = rightShift32(K_i, 11) | halfBlock;
        return f1 ^ f2;
    }

    // Шифрование 64 разрядного блока
    public static long encryptBlock(long block) {
        // Выделяем из 64 разрядного блока левую и правую части
        // FIXME: 19.02.2023
        int leftHalfBlock = (int) ((block >>> 32) & mask32right); // левый подблок (32 битный)
        int rightHalfBlock = (int) (block & (int) mask32right); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = 0; i < roundAmount; i++) {
            int roundKey = roundKey(i); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int leftHalfBlockRound = leftHalfBlock; // значение левого блока в конце раунда (такое же как в начале раунда)
            int rightHalfBlockRound = rightHalfBlock ^ F(leftHalfBlock, roundKey); // новое значение правого блока (шифуется с помощью функуии F)

            // Вывод подблоков на входе раунда (для отладки)
//            System.out.printf("in %d left = %d; right = %d%n", i, leftHalfBlock, rightHalfBlock);

            // Если раунд не последний, то
            if (i < roundAmount - 1) {
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
//            System.out.printf("out %d left = %d; right = %d%n", i, leftHalfBlock, rightHalfBlock);
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
//        long encryptedBlock = leftHalfBlock; // сначала записываем левую часть в правую половину
//        encryptedBlock = (encryptedBlock << 32) | (rightHalfBlock & mask32right); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
        long encryptedBlock = (long) leftHalfBlock << 32 | rightHalfBlock & mask32right;
        // Возвращаем зашифрованный блок
        return encryptedBlock;
    }

    // Расшифровка 64 разрядного блока
    public static long decryptBlock(long block) {
        // Выделяем из 64 разрядного блока левую и правую части
        int leftHalfBlock = (int) ((block >> 32) & mask32right); // левый подблок (32 битный)
        int rightHalfBlock = (int) (block & mask32right); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = roundAmount - 1; i >= 0; i--) {
            int K_i = roundKey(i); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int leftHalfBlockRound = leftHalfBlock; // значение левого блока в конце раунда (такое же как в начале раунда)
            int rightHalfBlockRound = rightHalfBlock ^ F(leftHalfBlock, K_i); // новое значение правого блока (шифуется с помощью функуии F)

            // Вывод подблоков на входе раунда (для отладки)
//            System.out.printf("in %s left = %d; right = %d%n", i, leftHalfBlock, rightHalfBlock);

            // Если раунд не последний, то
            if (i > 0) {
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
//            System.out.printf("out %d left = %d; right = %d%n", i, leftHalfBlock, rightHalfBlock);
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        // FIXME: 19.02.2023 
//        long decryptedBlock = (long) leftHalfBlock << 32 | (long) rightHalfBlock << 32; // сначала записываем левую часть в правую половину
        long decryptedBlock = leftHalfBlock;
        decryptedBlock = (decryptedBlock << 32) | (rightHalfBlock & mask32right); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
//        long decryptedBlock = (long) leftHalfBlock << 32 | rightHalfBlock & mask32right;
        return decryptedBlock;
    }

    private static byte[] process(Function<Long, Long> action, byte[] source) {
        final var target = new byte[source.length];
        System.out.println("encrypting...");
        // FIXME: 19.02.2023
        var longBuffer = new byte[Byte.SIZE];
        var i = 0;
        var section = Math.min(Byte.SIZE, source.length - i);
        var block = 0L;

        while (section > 0) {
            longBuffer = new byte[Byte.SIZE];

            System.arraycopy(source, i, longBuffer, 0, section);
            block = ByteUtils.byteArrayToLong(longBuffer);

            printBufferAndBlock(longBuffer, block);

            block = action.apply(block);
            longBuffer = ByteUtils.longToByteArray(block);

            printBufferAndBlock(longBuffer, block);

            System.arraycopy(longBuffer, 0, target, i, section);

            i += section;
            section = Math.min(Byte.SIZE, source.length - i);
        }
        System.out.println();
        return target;
    }

    private static byte[] processAlternate(Function<Long, Long> action, byte[] source) {
        final var longs = ByteUtils.byteArrayToLongArray(source);

        for (int i = 0; i < longs.length; i++) {
            longs[i] = action.apply(longs[i]);
        }

        return ByteUtils.longArrayToByteArray(longs);
    }

    public static byte[] encrypt(byte[] bytes) {
        return process(FeistelEncrypt::encryptBlock, bytes);
    }

    public static byte[] decrypt(byte[] bytes) {
        return process(FeistelEncrypt::decryptBlock, bytes);
    }

    public static byte[] encrypt0(byte[] bytes) {
        return processAlternate(FeistelEncrypt::encryptBlock, bytes);
    }

    public static byte[] decrypt0(byte[] bytes) {
        return processAlternate(FeistelEncrypt::decryptBlock, bytes);
    }

    private static void printBufferAndBlock(byte[] longBuffer, long block) {
        System.out.printf("block : %s%n", ByteUtils.numberToPrettyBinaryString(block, 16));
        System.out.printf("buffer: %s%n", ByteUtils.joinPrettyBytes(longBuffer, " "));
        System.out.println();
    }
}