package com.bbva.rbvd.lib.r211.impl.util;

public class ConstantsUtil {

    private ConstantsUtil(){}

    public class Participant{
        public static final String PARTICIPANT_TYPE_ENDORSEE = "ENDORSEE";
    }


    public static final String PARTICIPANT_TYPE_PAYMENT_MANAGER = "PAYMENT_MANAGER";
    public static final String PARTICIPANT_TYPE_INSURED = "INSURED";
    public static final String DOCUMENT_TYPE_RUC = "RUC";

    public static final String QUERY_GET_INSURED_DATA = "PISD.GET_INSURED_DATA_LIFE";


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
