package com.bbva.rbvd.dto.insurancemissionsale.constans;

public class ConstantsUtil {

    private ConstantsUtil(){}

    public static class Participant{
        public static final String ENDORSEE = "ENDORSEE";
        public static final String PAYMENT_MANAGER = "PAYMENT_MANAGER";
        public static final String INSURED = "INSURED";
        public static final String LEGAL_REPRESENTATIVE = "LEGAL_REPRESENTATIVE";
        public static final String BENEFICIARY = "BENEFICIARY";
    }

    public static final String FIELD_PRODUCT_SHORT_DESC = "PRODUCT_SHORT_DESC";
    public static final String PRODUCT_CODES_WITHOUT_THIRD_PARTY_VALIDATION = "product.codes.without.third.party.validation";

    public static final class DocumentType{
        public static final String RUC = "RUC";
    }

    public static final class Queries{
        public static final String QUERY_SELECT_PRODUCT_BY_PRODUCT_TYPE = "PISD.SELECT_PRODUCT_BY_PRODUCT_TYPE";
        public static final String QUERY_GET_INSURED_DATA_LIFE = "PISD.GET_INSURED_DATA_LIFE";
    }


    public static final class Delimeter{
        public static final String VERTICAL_BAR = "|";

    }

    public final class Number{
        public static final int DIEZ = 10;
        public static final int TRES = 3;
        public static final int DOS = 2;
        public static final int UNO = 1;
        public static final int CERO = 0;
    }

    public enum ParticipantRol{

        CONTRACTOR(8),
        INSURED(9),
        PAYMENT_MANAGER(23);

        private final Integer rol;


        ParticipantRol(Integer rol) {
            this.rol = rol;
        }

        public Integer getRol() {
            return rol;
        }
    }

    public enum Relationship {
        HOLDER("01", "TITULAR"),
        SPOUSE("02", "CONYUGE"),
        CHILDREN("03", "HIJO(A)"),
        FATHER("04", "PADRE"),
        MOTHER("05", "MADRE"),
        COHABITANT("06", "CONVIVIENTE"),
        BROTHERS("07", "HERMANO(A)"),
        UNCLES("08", "TIO(A)"),
        OTHERS("09", "OTROS"),
        GRANDCHILDREN("10", "NIETO(A)"),
        COUPLE("11", "NOVIO(A)"),
        COUSINS("12", "PRIMO(A)"),
        PARENTS("13", "PADRES");

        private final String code;
        private final String value;

        Relationship(String code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getCode() {return code;}

        public String getValue() {return value;}

        public static String getRelationshipValue(String relationshipCode) {
            for(Relationship relationship : Relationship.values()) {
                if(relationshipCode.equals(relationship.getCode())) {
                    return relationship.getValue();
                }
            }
            return null;
        }

    }


}