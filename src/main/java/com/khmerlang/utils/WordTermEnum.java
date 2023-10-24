package com.khmerlang.utils;

public enum WordTermEnum {
    NAN(-1),
    KH_WORD(0),
    WORD(0),
    PUNCT(1),
    NUMBER(2),
    OTHER(3),
    SYMBOL(4),
    SPACE(5),
    NOT_WORD(6);

    private final int value;

    WordTermEnum(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
