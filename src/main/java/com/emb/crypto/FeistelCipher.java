package com.emb.crypto;

import com.emb.util.ByteUtils;
import com.emb.util.Shift;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public abstract class FeistelCipher<T> implements Encoder<T> {

    protected interface BlockCryptographyAction extends Function<Long, Long> {}

    protected final long key;
    protected final long seed;
    protected final int roundsAmount;

    public FeistelCipher() {
        this(new Random().nextLong(), 8);
    }

    public FeistelCipher(long seed, int roundsAmount) {
        this.key = new Random(seed).nextLong();
        this.seed = seed;
        this.roundsAmount = roundsAmount;
    }

    /**
     * Processing abstract cryptography action on a single sequence block.
     * @param block what to process;
     * @param reverse how to process (backward or forward);
     * @return result of processing.
     */
    protected long processBlock(long block, final boolean reverse) {
        final var roundDirection = reverse ? -1 : 1;
        final var stopIndex = reverse ? -1 : roundsAmount;
        var roundIndex = reverse ? roundsAmount - 1 : 0;

        // Выделяем из 64 разрядного блока левую и правую части
        var leftHalfBlock = (int) ((block >> 32) & Shift.INT.mask());  // левый подблок (32 битный)
        var rightHalfBlock = (int) (block & (int) Shift.INT.mask());   // правый подблок (32 битный)
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
        return (long) leftHalfBlock << 32 | rightHalfBlock & Shift.INT.mask();
    }

    // Разбивание на блоки и параллельное шфрование/дешифрование
    protected byte[] processSequenceFast(BlockCryptographyAction action, byte[] data) {
        final var target = new byte[Long.BYTES * ByteUtils.amountOfLongsInByteArray(data)];

        var buffer = new byte[Long.BYTES];
        var i = 0;
        var section = Math.min(Long.BYTES, data.length - i);
        var block = 0L;

        while (section > 0) {
            Arrays.fill(buffer, 0, Long.BYTES - section, (byte) 0);

            System.arraycopy(data, i, buffer, Long.BYTES - section, section);
            block = ByteUtils.byteArrayToLong(buffer);

            block = action.apply(block);
            buffer = ByteUtils.longToByteArray(block);

            System.arraycopy(buffer, 0, target, i, Long.BYTES);

            i += section;
            section = Math.min(Long.BYTES, data.length - i);
        }

        return target;
    }

    /**
     * A function instance of {@link BlockCryptographyAction}.
     * Implements <strong>encryption</strong> process for the single block of sequence.
     * @param block is a block to encrypt;
     * @return <strong>encrypted</strong> block.
     */
    protected long encryptBlock(long block) {
        return processBlock(block, false);
    }

    /**
     * A function instance of {@link BlockCryptographyAction}.
     * Implements <strong>decryption</strong> process for the single block of sequence.
     * @param block is a block to decrypt;
     * @return <strong>decrypted</strong> block.
     */
    protected long decryptBlock(long block) {
        return processBlock(block, true);
    }

    // Генерация 32 разрядного ключа на i-м раунде из исходного 64-разрядного
    protected int roundKey(int i, long key) {
        return (int) ByteUtils.shiftAll(key, Shift.INT.right, i * 8);
    }

    // Образующая функция - функция, шифрующая половину блока halfBlock ключом K_i на i-м раунде
    protected int F(int halfBlock, int K_i) {
        int f1 = (int) ByteUtils.shift(halfBlock, Shift.INT.left, 9);
        int f2 = (int) ByteUtils.shift(K_i, Shift.INT.right, 11) | halfBlock;
        return f1 ^ f2;
    }


    public final long getKey() {
        return key;
    }

    public final long getSeed() {
        return seed;
    }
}
