package com.emb.main;

public class FeistelEncrypt {
    // Объявление констант
    static final int N = 8; // Число раундов
    static final int F32 = 0xFFFFFFFF; // 32 разрядное число
    static final int razmer_K = 64;

    // Исходное сообщение
    static long msg = 0x123456789ABCDEF0L;
    static long K = 0x96EA704CFB1CF672L;  // исходный ключ (64 битный)

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
    static int Ki(int i) {
        return (int) vpravo64(K, i * 8);    // циклический сдвиг на 8 бит и обрезка правых 32 бит
    }

    // Образующая функция - функция, шифрующая половину блока polblok ключом K_i на i-м раунде
    static int F(int polblok, int K_i) {
        int f1 = vlevo32(polblok, 9);
        int f2 = vpravo32(K_i, 11) | polblok;
        return f1 ^ f2;
    }

    // Шифрование 64 разрядного блока
    static long shifr(long blok) {
        // Выделяем из 64 разрядного блока левую и правую части
        int lev_b = (int) ((blok >> 32) & F32); // левый подблок (32 битный)
        int prav_b = (int) (blok & F32); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = 0; i < N; i++) {
            int K_i = Ki(i); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int lev_i = lev_b; // значение левого блока в конце раунда (такое же как в начале раунда)
            int prav_i = prav_b ^ F(lev_b, K_i); // новое значение правого блока (шифуется с помощью функуии F)

            // Вывод подблоков на входе раунда (для отладки)
//            Console.WriteLine("in {0} left = {1:X}; right = {2:X}", i, lev_b, prav_b);

            // Если раунд не последний, то
            if (i < N - 1) {
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
//            Console.WriteLine("out {0} left = {1:X}; right = {2:X}", i, lev_b, prav_b);
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        long shifroblok = lev_b; // сначала записываем левую часть в правую половину
        shifroblok = (shifroblok << 32) | (prav_b & F32); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
        // Возвращаем зашифрованный блок
        return shifroblok;
    }

    // Расшифровка 64 разрядного блока
    static long rasshifr(long blok) {
        // Выделяем из 64 разрядного блока левую и правую части
        int lev_b = (int) ((blok >> 32) & F32); // левый подблок (32 битный)
        int prav_b = (int) (blok & F32); // правый подблок (32 битный)

        // Выполняются 8 раундов шифрования
        for (int i = N - 1; i >= 0; i--) {
            int K_i = Ki(i); // генерация ключа для i-го раунда
            // На i-м раунде значения подблоков изменяются
            int lev_i = lev_b; // значение левого блока в конце раунда (такое же как в начале раунда)
            int prav_i = prav_b ^ F(lev_b, K_i); // новое значение правого блока (шифуется с помощью функуии F)

            // Вывод подблоков на входе раунда (для отладки)
//            Console.WriteLine("in {0} left = {1:X}; right = {2:X}", i, lev_b, prav_b);

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
//            Console.WriteLine("out {0} left = {1:X}; right = {2:X}", i, lev_b, prav_b);
        }

        // После всех раундов шифрования объединяем левый и правый подблоки в один большой шифрованный блок (64 битный)
        long shifroblok = lev_b; // сначала записываем левую часть в правую половину
        shifroblok = (shifroblok << 32) | (prav_b & F32); // потом сдвигаем её влево и дописываем правую часть в освободившиеся биты
        // Возвращаем зашифрованный блок
        return shifroblok;
    }
}
