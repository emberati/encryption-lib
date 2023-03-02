package com.emb.util;

public enum Shift {
    BYTE(ShiftDeclaration.BYTE_SHIFT_LEFT, ShiftDeclaration.BYTE_SHIFT_RIGHT),
    SHORT(ShiftDeclaration.SHORT_SHIFT_LEFT, ShiftDeclaration.SHORT_SHIFT_RIGHT),
    INT(ShiftDeclaration.INT_SHIFT_LEFT, ShiftDeclaration.INT_SHIFT_RIGHT);

    enum ShiftType implements EnumUtils.EnumPrettyName {
        BYTE,
        SHORT,
        INT
    }

    enum ShiftDirection implements EnumUtils.EnumPrettyName {
        LEFT,
        RIGHT
    }

    public enum ShiftDeclaration implements EnumUtils.EnumPrettyName {
        BYTE_SHIFT_LEFT(ShiftType.BYTE, ShiftDirection.LEFT, Byte.MIN_VALUE, Byte.SIZE),
        BYTE_SHIFT_RIGHT(ShiftType.BYTE, ShiftDirection.RIGHT, Byte.MIN_VALUE, Byte.SIZE),
        SHORT_SHIFT_LEFT(ShiftType.SHORT, ShiftDirection.LEFT, Short.MIN_VALUE, Short.SIZE),
        SHORT_SHIFT_RIGHT(ShiftType.SHORT, ShiftDirection.RIGHT, Short.MIN_VALUE, Short.SIZE),
        INT_SHIFT_LEFT(ShiftType.INT, ShiftDirection.LEFT, Integer.MIN_VALUE, Integer.SIZE),
        INT_SHIFT_RIGHT(ShiftType.INT, ShiftDirection.RIGHT, Integer.MIN_VALUE, Integer.SIZE);

        private final ShiftType type;
        private final ShiftDirection direction;
        private final long mask;
        private final int size;

        ShiftDeclaration(ShiftType type, ShiftDirection direction, long mask, int shift) {
            this.type = type;
            this.direction = direction;
            this.mask = mask;
            this.size = shift;
        }

        public ShiftType getType() {
            return this.type;
        }

        public ShiftDirection getDirection() {
            return this.direction;
        }

        public long getMask() {
            return this.mask;
        }

        public int getSize() {
            return this.size;
        }
    }

    public final ShiftDeclaration left;
    public final ShiftDeclaration right;

    Shift(ShiftDeclaration left, ShiftDeclaration right) {
        this.left = left;
        this.right = right;
    }
}
