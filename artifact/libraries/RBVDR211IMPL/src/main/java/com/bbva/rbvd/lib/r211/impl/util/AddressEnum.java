package com.bbva.rbvd.lib.r211.impl.util;

import org.apache.commons.lang3.StringUtils;

public enum AddressEnum {
    URB("URB","URBANIZATION"),
    AHH("AHH","AAHH"),
    AV("AV.","AVENUE"),
    CAL("CAL","STREET"),
    JR("JR.","JIRON"),
    PSJ("PSJ","PASSAGE"),
    SEC("SEC","NEIGHBORHOOD"),
    ALM("ALM","ALAMEDA"),
    CC("CC.","MALL"),
    CRT("CRT","ROAD"),
    GAL("GAL","SHOPPING_ARCADE"),
    MAL("MAL","JETTY"),
    OVA("OVA","OVAL"),
    PAS("PAS","PEDESTRIAN_WALK"),
    PLZ("PLZ","SQUARE"),
    PQE("PQE","PARK"),
    PRL("PRL","PROLONGATION"),
    PTE("PTE","BRIDGE"),
    AGR("AGR","GROUP"),
    CHB("CHB","HOUSING_COMPLEX"),
    COM("COM","INDIGENOUS_COMMUNITY"),
    CAM("CAM","PEASANT_COMMUNITY"),
    COV("COV","HOUSING_COOPERATIVE"),
    ETP("ETP","STAGE"),
    PJJ("PJJ","SHANTYTOWN"),
    UV("UV.","NEIGHBORHOOD_UNIT"),
    BAJ("BAJ","DESCENT"),
    POR("POR","PORTAL"),
    ASC("ASC","ASSOCIATION"),
    FUN("FUN","FUNDO"),
    MIN("MIN","MINING_CAMP"),
    RES("RES","RESIDENTIAL"),
    ZNA("ZNA","ZONE"),
    NA("NA","UNCATEGORIZED"),
    NP("NP","NOT_PROVIDED"),
    NO_COINCIDENCE(null, null);
    private final String hostValue;
    private final String value;
    AddressEnum(String hostValue, String value) {
        this.hostValue = hostValue;
        this.value = value;
    }
    public String getHostValue() { return hostValue; }
    public String getValue() { return value; }

    public static AddressEnum findByHostValue(String hostValue){
        if(StringUtils.isEmpty(hostValue)) return NO_COINCIDENCE;
        for(AddressEnum v : values()){
            if(hostValue.equals(v.getHostValue())){
                return v;
            }
        }
        return NO_COINCIDENCE;
    }

    @Override public String toString() { return value; }
}
