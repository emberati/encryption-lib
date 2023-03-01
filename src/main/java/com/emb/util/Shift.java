package com.emb.util;

public enum Shift {
    BYTE(ShiftDeclaration.BYTE_LEFT_SHIFT, ShiftDeclaration.BYTE_RIGHT_SHIFT),
    SHORT(ShiftDeclaration.SHORT_LEFT_SHIFT, ShiftDeclaration.SHORT_RIGHT_SHIFT),
    INT(ShiftDeclaration.INT_LEFT_SHIFT, ShiftDeclaration.INT_RIGHT_SHIFT);

    private enum ShiftType {
        BYTE,
        SHORT,
        INT
    }

    private enum ShiftDirection {
        LEFT,
        RIGHT
    }

    public enum ShiftDeclaration {
        BYTE_LEFT_SHIFT(ShiftType.BYTE, ShiftDirection.LEFT),
        BYTE_RIGHT_SHIFT(ShiftType.BYTE, ShiftDirection.RIGHT),
        SHORT_LEFT_SHIFT(ShiftType.SHORT, ShiftDirection.LEFT),
        SHORT_RIGHT_SHIFT(ShiftType.SHORT, ShiftDirection.RIGHT),
        INT_LEFT_SHIFT(ShiftType.INT, ShiftDirection.LEFT),
        INT_RIGHT_SHIFT(ShiftType.INT, ShiftDirection.RIGHT);

        private final ShiftType type;
        private final ShiftDirection direction;

        ShiftDeclaration(ShiftType type, ShiftDirection direction) {
            this.type = type;
            this.direction = direction;
        }

        public ShiftType getType() {
            return this.type;
        }

        public ShiftDirection getDirection() {
            return this.direction;
        }

    }

    public final ShiftDeclaration left;
    public final ShiftDeclaration right;

    Shift(ShiftDeclaration left, ShiftDeclaration right) {
        this.left = left;
        this.right = right;
    }
}
