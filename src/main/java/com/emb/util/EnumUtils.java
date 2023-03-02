package com.emb.util;

public class EnumUtils {
    public interface EnumPrettyName {
        default String prettifyEnumName(boolean firstUppercase) {
            return EnumUtils.prettifyEnumName(firstUppercase, (Enum<?>) this);
        }
    }

    public static String prettifyEnumName(boolean firstUppercase, Enum<?> en) {
        if (firstUppercase) {
            var shiftNameArray = en.name().toLowerCase().split("_");
            var uppercaseFirst = shiftNameArray[0].toCharArray();
            uppercaseFirst[0] = Character.toUpperCase(uppercaseFirst[0]);
            shiftNameArray[0] = String.valueOf(uppercaseFirst);
            return String.join(" ", shiftNameArray);
        } else return en.name().toLowerCase().replace("_", " ");
    }
}
