package com.bbva.rbvd.lib.r211.impl.util;

public class ConstantsUtil {

    private ConstantsUtil(){}

    public static final String PARTICIPANT_TYPE_ENDORSEE = "ENDORSEE";
    public static final String PARTICIPANT_TYPE_PAYMENT_MANAGER = "PAYMENT_MANAGER";
    public static final String PARTICIPANT_TYPE_INSURED = "INSURED";
    public static final String DOCUMENT_TYPE_RUC = "RUC";
    public static final String DELIMITER = "|";
    public static final String QUERY_GET_INSURED_DATA = "PISD.GET_INSURED_DATA_LIFE";

    public class ParticipantData{

        private ParticipantData(){}

        public static final String FIELD_CLIENT_LAST_NAME = "CLIENT_LAST_NAME";
        public static final String FIELD_INSURED_CUSTOMER_NAME = "INSURED_CUSTOMER_NAME";
        public static final String FIELD_CUSTOMER_BIRTH_DATE = "CUSTOMER_BIRTH_DATE";
        public static final String FIELD_GENDER_ID = "GENDER_ID";
        public static final String FIELD_USER_EMAIL_PERSONAL_DESC = "USER_EMAIL_PERSONAL_DESC";
        public static final String FIELD_PHONE_ID = "PHONE_ID";
    }


}
