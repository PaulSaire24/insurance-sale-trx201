package com.bbva.rbvd.lib.r211.impl.util;

public enum ContactTypeEnum2 {
    MA("EMAIL"),
    MT("WORK_EMAIL"),
    MF("FACEBOOK_EMAIL"),
    TF("PHONE_NUMBER"),
    MV("MOBILE_NUMBER"),
    TO("OFFICE_NUMBER");
    private final String value;
    ContactTypeEnum2(String value) { this.value = value; }
    public String getValue() { return value; }
    @Override public String toString() { return value; }
}
