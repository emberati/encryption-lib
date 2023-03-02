package com.emb.util.exception;

import com.emb.util.ByteUtils;
import com.emb.util.Shift;

public class IllegalByteShift extends IllegalArgumentException {
    public IllegalByteShift(Shift.ShiftDeclaration shift, long block) {
        super("%s is not applicable to %s!"
              .formatted(shift.prettifyEnumName(true), ByteUtils.numberToPrettyBinaryString(block)));
    }

    public IllegalByteShift() {
        super("Can not apply byte shift on incompatible types!");
    }
}
