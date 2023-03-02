package com.emb.util;

public enum Shift {
    BYTE(ShiftDeclaration.BYTE_SHIFT_LEFT, ShiftDeclaration.BYTE_SHIFT_RIGHT),
    SHORT(ShiftDeclaration.SHORT_SHIFT_LEFT, ShiftDeclaration.SHORT_SHIFT_RIGHT),
    INT(ShiftDeclaration.INT_SHIFT_LEFT, ShiftDeclaration.INT_SHIFT_RIGHT);

    enum ShiftType implements EnumUtils.EnumPrettyName {
        BYTE(0xFFL),
        SHORT(0xFFFFL),
        INT(0xFFFFFFFFL);

        private final long mask;
        ShiftType(long mask) {
            this.mask = mask;
        }

        public long mask() {
            return mask;
        }
    }

    enum ShiftDirection implements EnumUtils.EnumPrettyName {
        LEFT,
        RIGHT
    }

    public enum ShiftDeclaration implements EnumUtils.EnumPrettyName {
        UNSIGNED_BYTE_SHIFT_LEFT(ShiftType.BYTE, ShiftDirection.LEFT, 0xFFL, Byte.SIZE),
        UNSIGNED_BYTE_SHIFT_RIGHT(ShiftType.BYTE, ShiftDirection.RIGHT, 0xFFL, Byte.SIZE),
        UNSIGNED_SHORT_SHIFT_LEFT(ShiftType.SHORT, ShiftDirection.LEFT, 0xFFFFL, Short.SIZE),
        UNSIGNED_SHORT_SHIFT_RIGHT(ShiftType.SHORT, ShiftDirection.RIGHT, 0xFFFFL, Short.SIZE),
        UNSIGNED_INT_SHIFT_LEFT(ShiftType.INT, ShiftDirection.LEFT, 0xFFFFFFFFL, Integer.SIZE),
        UNSIGNED_INT_SHIFT_RIGHT(ShiftType.INT, ShiftDirection.RIGHT, 0xFFFFFFFFL, Integer.SIZE),
        BYTE_SHIFT_LEFT(ShiftType.BYTE, ShiftDirection.LEFT, ~0x0L, Byte.SIZE, UNSIGNED_BYTE_SHIFT_LEFT),
        BYTE_SHIFT_RIGHT(ShiftType.BYTE, ShiftDirection.RIGHT, ~0x0L, Byte.SIZE, UNSIGNED_BYTE_SHIFT_RIGHT),
        SHORT_SHIFT_LEFT(ShiftType.SHORT, ShiftDirection.LEFT, ~0x0L, Short.SIZE, UNSIGNED_SHORT_SHIFT_LEFT),
        SHORT_SHIFT_RIGHT(ShiftType.SHORT, ShiftDirection.RIGHT, ~0x0L, Short.SIZE, UNSIGNED_SHORT_SHIFT_RIGHT),
        INT_SHIFT_LEFT(ShiftType.INT, ShiftDirection.LEFT, ~0x0L, Integer.SIZE, UNSIGNED_INT_SHIFT_LEFT),
        INT_SHIFT_RIGHT(ShiftType.INT, ShiftDirection.RIGHT, ~0x0L, Integer.SIZE, UNSIGNED_INT_SHIFT_RIGHT);

        private final ShiftType type;
        private final ShiftDirection direction;
        private final long mask;
        private final int size;
        private final ShiftDeclaration unsigned;

        ShiftDeclaration(ShiftType type, ShiftDirection direction, long mask, int shift) {
            this.type = type;
            this.direction = direction;
            this.mask = mask;
            this.size = shift;
            this.unsigned = this;
        }

        ShiftDeclaration(ShiftType type, ShiftDirection direction, long mask, int shift, ShiftDeclaration unsigned) {
            this.type = type;
            this.direction = direction;
            this.mask = mask;
            this.size = shift;
            this.unsigned = unsigned;
        }

        public ShiftType type() {
            return this.type;
        }

        public ShiftDirection direction() {
            return this.direction;
        }

        public long mask() {
            return this.mask;
        }

        public int size() {
            return this.size;
        }

        public ShiftDeclaration unsigned() {
            return unsigned;
        }

        public boolean isUnsigned() {
            return mask != ~0x0L;
        }
    }

    public final ShiftDeclaration left;
    public final ShiftDeclaration right;

    Shift(ShiftDeclaration left, ShiftDeclaration right) {
        this.left = left;
        this.right = right;
    }
}
