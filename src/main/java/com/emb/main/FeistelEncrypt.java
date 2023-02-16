package com.emb.main;

public class FeistelEncrypt {
    // Объявление констант
    static final int roundsAmount = 8; // Число раундов
    static final int max32BitNumber = 0xFFFFFFFF; // 32 разрядное число

    // Исходное сообщение
    static long msg = 0x123456789ABCDEF0L;
    static long K = 0x96EA704CFB1CF672L;  // исходный ключ (64 битный)

    // Циклический сдвиг вправо для 32 бит
    static int right32(int block, int shift) {
        return ((block >> shift) | (block << (32 - shift)));
    }

    // Циклический сдвиг вправо для 64 бит
    static long right64(long block, int shift) {
        return ((block >> shift) | (block << (64 - shift)));
    }

    // Циклический сдвиг влево для 32 бит
    static int left32(int block, int shift) {
        return ((block << shift) | (block >> (32 - shift)));
    }

    // Циклический сдвиг влево для 64 бит
    static long left64(long block, int shift) {
        return ((block << shift) | (block >> (64 - shift)));
    }

    // Генерация 32 разрядного ключа на i-м раунде из исходного 64-разрядного
    static int Ki(int i) {
        return (int) right64(K, i * 8);    // циклический сдвиг на 8 бит и обрезка правых 32 бит
    }

    // Образующая функция - функция, шифрующая половину блока halfBlock ключом K_i на i-м раунде
    static int F(int halfBlock, int K_i) {
        int f1 = left32(halfBlock, 9);
        int f2 = right32(K_i, 11) | halfBlock;
        return f1 ^ f2;
    }

    static long encryptBlock(long block) {
        return encryptBlock(block, 64);
    }

    // Шифрование 64 разрядного блока
    static long encryptBlock(long block, final int blockWidth) {
        final var halfShift = blockWidth / 2;
        // Выделяем из 64 разрядного блока левую и правую части
        int leftHalfBlock = (int) ((block >> halfShift) & max32BitNumber); // левый подблок (32 битный)
        int rightHalfBlock = (int) (block & max32BitNumber); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = 0; i < roundsAmount; i++) {
            int K_i = Ki(i); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int lev_i = leftHalfBlock; // значение левого блока в конце раунда (такое же как в начале раунда)
            int prav_i = rightHalfBlock ^ F(leftHalfBlock, K_i); // новое значение правого блока (шифуется с помощью функуии F)

            // Вывод подблоков на входе раунда (для отладки)
            // Console.WriteLine("in {0} left = {1:X}; right = {2:X}", i, leftHalfBlock, rightHalfBlock);

            // Если раунд не последний, то
            if (i < roundsAmount - 1) {
                leftHalfBlock = prav_i; // правый подблок становится левым
                rightHalfBlock = lev_i; // а левый подблок теперь правый
            } else // После последнего раунда блоки не меняются местами
            {
                leftHalfBlock = lev_i;
                rightHalfBlock = prav_i;
            }

            // Вывод подблоков на выходе раунда (для отладки)
            // Выходной правый блок должен быть равен входному левому блоку для всех раундов КРОМЕ последнего
            // Для последнего раунда входной левый блок равен выходному левому
            // Console.WriteLine("out {0} left = {1:X}; right = {2:X}", i, leftHalfBlock, rightHalfBlock);
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        long encryptedBlock = leftHalfBlock; // сначала записываем левую часть в правую половину
        encryptedBlock = (encryptedBlock << halfShift) | (rightHalfBlock & max32BitNumber); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
        // Возвращаем зашифрованный блок
        return encryptedBlock;
    }

    static long decryptBlock(long block) {
        return decryptBlock(block, 64);
    }

    // Расшифровка 64 разрядного блока
    static long decryptBlock(long block, final int blockWidth) {
        final var halfShift = blockWidth / 2;
        // Выделяем из 64 разрядного блока левую и правую части
        int leftHalfBlock = (int) ((block >> halfShift) & max32BitNumber); // левый подблок (32 битный)
        int rightHalfBlock = (int) (block & max32BitNumber); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = roundsAmount - 1; i >= 0; i--) {
            int K_i = Ki(i); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int lev_i = leftHalfBlock; // значение левого блока в конце раунда (такое же как в начале раунда)
            int prav_i = rightHalfBlock ^ F(leftHalfBlock, K_i); // новое значение правого блока (шифуется с помощью функуии F)

            // Вывод подблоков на входе раунда (для отладки)
            // Console.WriteLine("in {0} left = {1:X}; right = {2:X}", i, leftHalfBlock, rightHalfBlock);

            // Если раунд не последний, то
            if (i > 0) {
                leftHalfBlock = prav_i; // правый подблок становится левым
                rightHalfBlock = lev_i; // а левый подблок теперь правый
            } else // После последнего раунда блоки не меняются местами
            {
                leftHalfBlock = lev_i;
                rightHalfBlock = prav_i;
            }

            // Вывод подблоков на выходе раунда (для отладки)
            // Выходной правый блок должен быть равен входному левому блоку для всех раундов КРОМЕ последнего
            // Для последнего раунда входной левый блок равен выходному левому
            // Console.WriteLine("out {0} left = {1:X}; right = {2:X}", i, leftHalfBlock, rightHalfBlock);
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        long decryptedBlock = leftHalfBlock; // сначала записываем левую часть в правую половину
        decryptedBlock = (decryptedBlock << halfShift) | (rightHalfBlock & max32BitNumber); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
        // Возвращаем зашифрованный блок
        return decryptedBlock;
    }

    public static long[] encrypt(byte[] bytes) {
        var encryptedBytes = new long[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            encryptedBytes[i] = encryptBlock(bytes[i]);
        }
        return encryptedBytes;
    }

    public static byte[] decrypt(long[] bytes) {
        var encryptedBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            encryptedBytes[i] = (byte) decryptBlock((byte) bytes[i]);
        }
        return encryptedBytes;
    }
}
