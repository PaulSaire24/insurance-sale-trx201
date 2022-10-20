package com.bbva.rbvd.lib.r211.impl.util;

public enum GenderEnum {
    M("MALE"),
    F("FEMALE");
    private final String value;
    GenderEnum(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override public String toString() { return value; }
}
