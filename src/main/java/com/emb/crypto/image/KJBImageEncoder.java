package com.emb.crypto.image;

import com.emb.crypto.Encoder;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class is an implementation of Kutter-Jordan-Bossen encoding algorithm.
 * It is used for injecting hidden text messages into image.
 * KJBImageEncoder is a descendant of Encoder base interface. It uses KJBImage
 * as wrapper for BufferedImage bitmap and hidden text pair.
 * @see com.emb.crypto.Encoder
 * @see com.emb.crypto.image.KJBImage
 */
public class KJBImageEncoder implements Encoder<KJBImage> {
    private final static int roundsAmount = 15;
    private int x;
    private int y;

    /**
     * Encode text into a buffered image using Kutter-Jordan-Bossen algorithm.
     * @param image KJB image wrapper of BufferedImage where to place hidden text
     * @return encoded buffered image
     * @throws RuntimeException thrown if image is small for the text
     */
    @Override
    public KJBImage encode(KJBImage image) {
        var bitmap = image.image();
        var text = image.text();
        var message = prepareTextToEncode(text);
        var result = makeImageCopy(bitmap);

        x = y = 3;

        /* Checking if the image is big enough for the text */
        if ((message.length * 8 * roundsAmount) > ((bitmap.getWidth() / 4 - 1) * (bitmap.getHeight() / 4 - 1))) {
            throw new RuntimeException("Изображение слишком мало для заданного текста.");
        }

        /* Encoding */
        for (byte b : message) {
            writeByte(result, b);
        }

        return new KJBImage(result, text);
    }

    /**
     * Decode text from a buffered image using Kutter-Jordan-Bossen algorithm.
     * @return decoded text
     * @throws RuntimeException thrown if data is damaged
     */
    @Override
    public KJBImage decode(KJBImage image) {
        var bitmap = image.image();
        x = y = 3;

        /* Getting a length of encoded text */
        byte lenByte0 = readByte(bitmap);
        byte lenByte1 = readByte(bitmap);
        byte lenByte2 = readByte(bitmap);
        byte lenByte3 = readByte(bitmap);

        /* Converting lenByte into a decimal number */
        int msgLen = ((lenByte0 & 0xff) << 24) |
                ((lenByte1 & 0xff) << 16) |
                ((lenByte2 & 0xff) << 8) |
                (lenByte3 & 0xff);

        if ((msgLen <= 0) || ((msgLen * 8 * roundsAmount)
                > (bitmap.getWidth() / 4 - 1) * (bitmap.getHeight() / 4 - 1))) {

            throw new RuntimeException("Ошибка в процессе декодирования. Убедитесь, что изображение содержит текст.");
        }

        /* Decoding */
        byte[] msgBytes = new byte[msgLen];
        for (int i1 = 0; i1 < msgLen; i1++) {

            msgBytes[i1] = readByte(bitmap);
        }

        /* Converting byte array to string */
        return new KJBImage(null, new String(msgBytes));
    }

    /**
     * Prepare text to encode.
     * Make a byte array containing bytes of source text and a length of source text.
     * @param text source text
     * @return byte array of a source text and a length of it
     */
    byte[] prepareTextToEncode(String text) {
        /* Converting text to byte array */
        byte[] msgBytes = text.getBytes();

        /* Getting length of a byte array */
        byte[] lenBytes = getByteArrayLength(msgBytes);

        /* Preparing information to insert */
        byte[] message = new byte[msgBytes.length + 4];
        System.arraycopy(lenBytes, 0, message, 0, lenBytes.length);
        System.arraycopy(msgBytes, 0, message, lenBytes.length, msgBytes.length);

        return message;
    }

    /**
     * Write a single byte into buffered image.
     * @param img     buffered image
     * @param byteVal byte to write
     */
    private void writeByte(BufferedImage img, byte byteVal) {

        /* Loop through 8 bits of byteVal byte */
        for (int j = 7; j >= 0; j--) {

            int bitVal = (byteVal >>> j) & 1;
            writeBit(img, bitVal);
        }
    }

    /**
     * Read a single byte from buffered image.
     * @param img buffered image
     * @return read byte
     */
    private byte readByte(BufferedImage img) {
        byte byteVal = 0;

        /* Getting a byte from 8 bits */
        for (int i = 0; i < 8; i++) {
            /* Left shift founded bits and add a bit to the right */
            byteVal = (byte) ((byteVal << 1) | (readBit(img) & 1));
        }

        return byteVal;
    }

    /**
     * Write a single bit into buffered image.
     * @param img buffered image
     * @param bit bit to write
     */
    private void writeBit(BufferedImage img, int bit) {
        /* Writing a bit for NUM_OF_REPEATS times */
        for (int i1 = 0; i1 < roundsAmount; i1++) {
            if (x + 4 > img.getWidth()) {
                x = 3;
                y += 4;
            }
            writeIntoPixel(img, x, y, bit, 0.25);
            x += 4;
        }
    }

    /**
     * Read a single bit from buffered image.
     *
     * @param img buffered image
     * @return read bit
     */
    private int readBit(BufferedImage img) {
        /* Probabilistic estimate of an information bit */
        float bitEstimate = 0;

        for (int i1 = 0; i1 < roundsAmount; i1++) {

            if (x + 4 > img.getWidth()) {
                x = 3;
                y += 4;
            }

            bitEstimate += readFromPixel(img, x, y);
            x += 4;
        }
        bitEstimate /= roundsAmount;


        /* if more than half of NUM_OF_REPEATS read bits were 1s, so consider 1 was encoded */
        if (bitEstimate > 0.5) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Write a single bit into a current image point.
     * @param image  buffered image
     * @param x      image point coordinate x
     * @param y      image point coordinate y
     * @param bit    bit to write
     * @param energy coefficient - energy of a bit
     */
    private void writeIntoPixel(BufferedImage image, int x, int y, int bit, double energy) {

        var pixel = new Color(image.getRGB(x, y));
        var red = pixel.getRed();
        var green = pixel.getGreen();
        var blue = pixel.getBlue();

        int pixelBrightness = (int) (0.29890 * red + 0.58662 * green + 0.11448 * blue);

        /* Variable blue component */
        int modifiedBlueComponent;
        if (bit > 0) {
            modifiedBlueComponent = (int) (blue + energy * pixelBrightness);
        } else {
            modifiedBlueComponent = (int) (blue - energy * pixelBrightness);
        }

        if (modifiedBlueComponent > 255) {
            modifiedBlueComponent = 255;
        }

        if (modifiedBlueComponent < 0) {
            modifiedBlueComponent = 0;
        }

        Color pixelModified = new Color(red, green, modifiedBlueComponent);
        image.setRGB(x, y, pixelModified.getRGB());
    }

    /**
     * Read a single bit from a current image point.
     * @param image buffered image
     * @param x     image point coordinate x
     * @param y     image point coordinate y
     * @return read bit
     */
    private int readFromPixel(BufferedImage image, int x, int y) {

        /* Summing up all the blue components of surrounding points */
        int estimate = 0;

        for (int i1 = 1; i1 <= 3; i1++) {
            Color pixel = new Color(image.getRGB(x + i1, y));
            estimate += pixel.getBlue();
        }

        for (int i1 = 1; i1 <= 3; i1++) {
            Color pixel = new Color(image.getRGB(x - i1, y));
            estimate += pixel.getBlue();
        }

        for (int i1 = 1; i1 <= 3; i1++) {
            Color pixel = new Color(image.getRGB(x, y + i1));
            estimate += pixel.getBlue();
        }

        for (int i1 = 1; i1 <= 3; i1++) {
            Color pixel = new Color(image.getRGB(x, y - i1));
            estimate += pixel.getBlue();
        }

        /* Average */
        estimate /= 12;

        Color pixel = new Color(image.getRGB(x, y));
        int blue = pixel.getBlue();

        if (blue > estimate) return 1;
        else return 0;
    }

    public static byte[] getByteArrayLength(byte[] array) {

        byte[] lenBytes = new byte[4];

        lenBytes[0] = (byte) ((array.length >>> 24) & 0xFF);
        lenBytes[1] = (byte) ((array.length >>> 16) & 0xFF);
        lenBytes[2] = (byte) ((array.length >>> 8) & 0xFF);
        lenBytes[3] = (byte) (array.length & 0xFF);

        return lenBytes;
    }

    private BufferedImage makeImageCopy(BufferedImage imageToCopy) {
        var result = new BufferedImage(imageToCopy.getWidth(), imageToCopy.getHeight(), imageToCopy.getType());
        var graphics = result.getGraphics();
        graphics.drawImage(imageToCopy, 0, 0, null);
        return result;
    }
}