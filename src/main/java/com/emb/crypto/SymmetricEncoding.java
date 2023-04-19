package com.emb.crypto;

import java.util.Arrays;

public class SymmetricEncoding implements Encoder<byte[]> {
    // Объявление констант
    private static final int ROUND_AMOUNT = 8; // Число раундов
    private static final int MASK_32_RIGHT = 0xFFFFFFFF; // 32 разрядное число
    private static final int razmer_K = 64;

    // Исходное сообщение
    // static long msg =  0x123456789ABCDEF0;
    private static long key = 0x96EA704CFB1CF672L;  // исходный ключ (64 битный)
    // Для реализации различных режимов шифрования (лаб 2)
    // Исходное сообщение (1й,2й блоки одинаковы, равно как и 3й с 4м)
    private static long[] msg = {0x123456789ABCDEF0L, 0x123456789ABCDEF0L, 0x1FBA85C953ABCFD0L, 0x1FBA85C953ABCFD0L};
    // Вектор для дополнительной шифровки первого блока сообщения (в режимах CBC и OFB)
    private static long IV = 0x18FD47203C7A23BCL;    // инициализационный вектор
    private static final int B = 4; // число блоков в исходном сообщении

    // Циклический сдвиг вправо для 32 бит
    static int vpravo32(int x, int t) {
        return ((x >> t) | (x << (32 - t)));
    }

    // Циклический сдвиг вправо для 64 бит
    static long vpravo64(long x, int t) {
        return ((x >> t) | (x << (64 - t)));
    }

    // Циклический сдвиг влево для 32 бит
    static int vlevo32(int x, int t) {
        return ((x << t) | (x >> (32 - t)));
    }

    // Циклический сдвиг влево для 64 бит
    static long vlevo64(long x, int t) {
        return ((x << t) | (x >> (64 - t)));
    }

    // Генерация 32 разрядного ключа на i-м раунде из исходного 64-разрядного
    static int roundKey(int i, long key) {
        return (int) vpravo64(key, i * 8);    // циклический сдвиг на 8 бит и обрезка правых 32 бит
    }

    // Образующая функция - функция, шифрующая половину блока halfBlock ключом K_i на i-м раунде
    static int F(int halfBlock, int K_i) {
        int f1 = vlevo32(halfBlock, 9);
        int f2 = vpravo32(K_i, 11) | halfBlock;
        return f1 ^ f2;
    }

    // Шифрование 64 разрядного блока
    static long encryptBlock(long block) {
        // Выделяем из 64 разрядного блока левую и правую части
        int leftHalfBlock = (int) ((block >> 32) & MASK_32_RIGHT); // левый подблок (32 битный)
        int rightHalfBlock = (int) (block & MASK_32_RIGHT); // правый подблок (32 битный)
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
        // Возвращаем зашифрованный блок
        return (long) leftHalfBlock << 32 | rightHalfBlock & MASK_32_RIGHT;
    }

    // Расшифровка 64 разрядного блока
    static long decryptBlock(long block) {
        // Выделяем из 64 разрядного блока левую и правую части
        int lev_b = (int) ((block >> 32) & MASK_32_RIGHT); // левый подблок (32 битный)
        int prav_b = (int) (block & MASK_32_RIGHT); // правый подблок (32 битный)
        // Выполняются 8 раундов шифрования
        for (int i = ROUND_AMOUNT - 1; i >= 0; i--) {
            int K_i = roundKey(i, key); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int lev_i = lev_b; // значение левого блока в конце раунда (такое же как в начале раунда)
            int prav_i = prav_b ^ F(lev_b, K_i); // новое значение правого блока (шифуется с помощью функуии F)
            // Вывод подблоков на входе раунда (для отладки)
            //System.out.println("in {0} left = {1:X}; right = {2:X}", i, lev_b, prav_b);
            // Если раунд не последний, то
            if (i > 0) {
                lev_b = prav_i; // правый подблок становится левым
                prav_b = lev_i; // а левый подблок теперь правый
            } else // После последнего раунда блоки не меняются местами
            {
                lev_b = lev_i;
                prav_b = prav_i;
            }
            // Вывод подблоков на выходе раунда (для отладки)
            // Выходной правый блок должен быть равен входному левому блоку для всех раундов КРОМЕ последнего
            // Для последнего раунда входной левый блок равен выходному левому
            //System.out.println("out {0} left = {1:X}; right = {2:X}", i, lev_b, prav_b);
        }
        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        long shifroblok = lev_b; // сначала записываем левую часть в правую половину
        shifroblok = (shifroblok << 32) | (prav_b & MASK_32_RIGHT); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
        // Возвращаем зашифрованный блок
        return shifroblok;
    }

    public static void main(String[] args) {
        // Исходное сообщение
        System.out.printf("%s%n", Arrays.toString(msg));

        // Зашифрованное сообщение
        long c_msg = encryptBlock(key); // FIXME
        System.out.printf("%s%n", c_msg);

        // Расшифрованное сообщение
        long msg_ = decryptBlock(c_msg);
        System.out.printf("%s%n", msg_);

        // Отображаем на консоли исходный ключ K (и IV) для зашифровки и исходное сообщение text (все это объявлено в первых строках)
        System.out.printf("Init Key %s", key);      // большой ключ K (64 бит) из битов которого создаются маленькие ключи K_i (по 32 бита)
        System.out.printf("Init V %s", IV);         // дополнительный ключ (вектор) для шифровки первого блока сообщения врежимах CBC и OFB (64 бит)

        // Вывод блоков сообщения до шифрования
        System.out.println("Text (message blocks)");

        for (int b = 0; b < B; b++)
            System.out.printf("%s ", msg[b]);    // выводим очередной блок сообщения

        // 1. Шифрование
        // 1.1. Шифрование в режиме ECB (электронная кодовая книга)
        long[] msg_ecb = new long[B];
        System.out.println("\nShifr ECB:");

        // Шифрование последовательно каждого блока без дополнительных преобразований
        for (int b = 0; b < B; b++) {
            msg_ecb[b] = encryptBlock(msg[b]);        // шифруем блок
            System.out.printf("%d ", msg_ecb[b]);    // выводим очередной блок сообщения	// выводим зашифрованный блок на консоль
            // В зашифрованном тексте 1й и 2й блоки одинаковы (3й с 4м тоже) как и в исходном сообщении - это недостаток режима ECB
        }

        // 1.2. Шифрование в режиме CBC (режим сцепления блоков шифротекста)
        long[] msg_cbc = new long[B];
        System.out.println("\nShifr CBC:");

        // Первый блок сообщения xor'ится с IV перед шифрованием:
        long blok = msg[0] ^ IV;
        msg_cbc[0] = encryptBlock(blok);        // шифруем блок
        System.out.printf("%s ", msg_cbc[0]);   // выводим зашифрованный первый блок на консоль

        // Каждый последующий блок перед шифрованием xor'ится с предыдущим зашифрованным блоком:
        for (int b = 1; b < B; b++) {
            blok = msg[b] ^ msg_cbc[b - 1]; // xor с предыдущим зашифрованным
            msg_cbc[b] = encryptBlock(blok); // шифруем блок
            System.out.printf("%s ", msg_cbc[b]);    // выводим зашифрованный блок на консоль
            // В зашифрованном тексте все блоки будут разными, не смотря на то что в исходном сообщении они повторялись
        }
        // 1.3. Шифрование в режиме OFB (режим обратной связи по выходу)
        long[] msg_ofb = new long[B];
        System.out.println("\nShifr OFB:");
        blok = IV; // дополнительный ключ для зашифровки блоков текста

        for (int b = 0; b < B; b++) {
            blok = encryptBlock(blok);    // на каждом шаге шифруется этот дополнительный ключ
            msg_ofb[b] = blok ^ msg[b]; // и xor'ится с очередным блоком сообщения - получается зашифрованный блок сообщения
            System.out.printf("%s ", msg_ofb[b]);    // выводим зашифрованный блок на консоль
            // В зашифрованном тексте все блоки будут разными, не смотря на то что в исходном сообщении они повторялись
        }

        // 2. Расшифрование
        // 2.1. Расшифровка в режиме ECB (электронная кодовая книга)
        long msg_b;    // блок расшифрованного текста
        System.out.println("\nText ECB:");

        // Расшифровка последовательно каждого блока без дополнительных преобразований
        for (int b = 0; b < B; b++) {
            msg_b = decryptBlock(msg_ecb[b]);        // расшифровка блока
            System.out.printf("%s ", msg_b);        // выводим расшифрованный блок на консоль
        }

        // 2.2. Расшифровка в режиме CBC (режим сцепления блоков шифротекста)
        System.out.println("\nText CBC:");

        // Первый блок сообщения xor'ится с IV после расшифровки:
        msg_b = decryptBlock(msg_cbc[0]);    // расшифровка блока
        msg_b ^= IV; // xor'им с IV после расшифровки
        System.out.printf("%s ", msg_b);    // выводим расшифрованный первый блок на консоль

        // Каждый последующий блок после расшифровки xor'ится с предыдущим зашифрованным блоком:
        for (int b = 1; b < B; b++) {
            msg_b = decryptBlock(msg_cbc[b]);    // расшифровка блока
            msg_b ^= msg_cbc[b - 1];        // xor с предыдущим зашифрованным
            System.out.printf("%s ", msg_b);    // выводим расшифрованный блок на консоль
        }

        // 2.3. Расшифровка в режиме OFB (режим обратной связи по выходу)
        System.out.println("\nText OFB:");
        blok = IV; // дополнительный ключ для расшифровки блоков текста
        for (int b = 0; b < B; b++) {
            blok = encryptBlock(blok);    // на каждом шаге шифруется этот дополнительный ключ (точно так же как при шифровании)
            msg_b = blok ^ msg_ofb[b];    // расшифрованный блок сообщения получается в результате операции xor зашифрованного блока сообщения и этого ключа
            System.out.printf("%s ", msg_b);    // выводим расшифрованный блок на консоль
        }
    }

    @Override
    public byte[] encode(byte[] data) {
        return new byte[0];
    }

    @Override
    public byte[] decode(byte[] data) {
        return new byte[0];
    }
}
