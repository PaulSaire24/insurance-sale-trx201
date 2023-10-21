package com.bbva.rbvd.lib.r211.impl.util;

public class ConstantsUtil {

    private ConstantsUtil(){}

    public class Participant{
        public static final String ENDORSEE = "ENDORSEE";
        public static final String PAYMENT_MANAGER = "PAYMENT_MANAGER";
        public static final String INSURED = "INSURED";
    }

    public static final String FIELD_PRODUCT_SHORT_DESC = "PRODUCT_SHORT_DESC";


    public class DocumentType{
        public static final String RUC = "RUC";
    }

    public class Queries{
        public static final String QUERY_SELECT_PRODUCT_BY_PRODUCT_TYPE = "PISD.SELECT_PRODUCT_BY_PRODUCT_TYPE";
        public static final String QUERY_GET_INSURED_DATA_LIFE = "PISD.GET_INSURED_DATA_LIFE";
    }


    public class Delimites{
        public static final String VERTICAL_BAR = "|";

    }

    public class Number{
        public static final int TRES = 3;
    }

    public enum ParticipantRol{

        CONTRACTOR(8),
        INSURED(9),
        PAYMENT_MANAGER(23);

        private final int rol;


        ParticipantRol(int rol) {
            this.rol = rol;
        }

        public int getRol() {
            return rol;
        }
    }


}
