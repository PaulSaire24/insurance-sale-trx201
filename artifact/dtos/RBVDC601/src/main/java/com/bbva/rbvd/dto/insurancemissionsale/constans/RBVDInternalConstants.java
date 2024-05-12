package com.bbva.rbvd.dto.insurancemissionsale.constans;



/**
 * The RBVDInternalConstants
 */
public class RBVDInternalConstants  {

	private RBVDInternalConstants(){}

	public static final class FlowProcess{
		public static final String LEGACY_FLOW_PROCESS = "LEGACY_FLOW_PROCESS";
		public static final String NEW_FLOW_PROCESS    = "NEW_FLOW_PROCESS";
	}


    public static final class RBVDProperties{
		public static final String UPDATE_CONTRACT_ENDORSEMENT = "PISD.UPDATE_CONTRACT_ENDORSEMENT";
	}

	public static final class ContractType{
		public static final String FIELD_EXTERNAL_CONTRACT = "EXTERNAL_CONTRACT";
		public static final String FIELD_INTERNAL_CONTRACT = "INTERNAL_CONTRACT";
		public static final String FIELD_SYSTEM = "SYSTEM";
	}

	public static final class Tables{
		public static final String T_PISD_INSURANCE_CONTRACT = "T_PISD_INSURANCE_CONTRACT";
		public static final String T_PISD_INSURANCE_CTR_RECEIPTS = "T_PISD_INSURANCE_CTR_RECEIPTS";
		public static final String T_PISD_INSRNC_CONTRACT_MOV = "T_PISD_INSRNC_CONTRACT_MOV";
		public static final String T_PISD_INSRNC_CTR_PARTICIPANT = "T_PISD_INSRNC_CTR_PARTICIPANT";
		public static final String T_PISD_ENDORSEMENT_INSRNC_CTR = "T_PISD_ENDORSEMENT_INSRNC_CTR";
	}


	public static final class ContactDetailNomenclature {
		public static final String ERROR_SPANISH_EMAIL = "correo";
		public static final String ERROR_SPANISH_PHONE = "celular";
		private ContactDetailNomenclature() {
		}
	}

	public static final class ReceiptDefaultValues{

		public static final String RECEIPT_DEFAULT_DATE_VALUE = "01/01/0001";
		public static final String COLLECTION_STATUS_NEXT_VALUES = "02";
		public static final String COLLECTION_STATUS_FIRST_RECEIPT_VALUE = "00";

		public static final String NEXT_RECEIPTS_STATUS_TYPE_VALUE = "INC";
		public static final String FIRST_RECEIPT_STATUS_TYPE_VALUE = "COB";

	}

	public static final class ContractStatusCompany{
		public static final String ERROR = "ERR";
		public static final String FORMALIZED = "FOR";
		public static final String PENDING = "PENDING";
		private ContractStatusCompany() {
		}
	}

	public static final class DataParticulars{
		public static final String PARTICULAR_DATA_THIRD_CHANNEL = "CANAL_TERCERO";
		public static final String PARTICULAR_DATA_ACCOUNT_DATA = "DATOS_DE_CUENTA";
		public static final String PARTICULAR_DATA_CERT_BANCO = "NRO_CERT_BANCO";
		public static final String PARTICULAR_DATA_SALE_OFFICE = "OFICINA_VENTA";
		public static final String TAG_OTHERS = "OTROS";
	}

	public static final class Payment{
		public static final String METHOD_DIRECT = "DIRECT_DEBIT";
		public static final String CARD_PRODUCT_ID = "CARD";
		public static final String CARD_METHOD_TYPE = "T";
		public static final String ACCOUNT_METHOD_TYPE = "C";
	}


	/**
	 * The PropertiesKey
	 */
	public static final class PropertiesKey {
		public static final String KEY_OBTAIN_PRODUCT_LIFE = "obtain.insurances.life.codes";
		private PropertiesKey() {
		}
	}

	public static final class Endorsement {
		public static final String ENDORSEMENT = "ENDORSEE";
		public static final String RUC = "RUC";
		public static final String RUC_ID = "R";
		private Endorsement() {
		}
	}

	public static final class Messages {
		public static final String ERROR_DEFAULT_CONTACT_DETAIL = "No se encontro %s";
	}

	public static final class LabelCompany{
		public static final String PARTICULAR_DATA_MESES_DE_VIGENCIA = "MESES_DE_VIGENCIA";
		public static final String SIN_ESPECIFICAR = "N/A";
	}

	public static final class LabelRimac{
		public static final String PARTICULAR_DATA_MESES_DE_VIGENCIA = "MESES_DE_VIGENCIA";
		public static final String SIN_ESPECIFICAR = "N/A";
	}


	public static final class Period {
		public static final String ANNUAL   = "A";
		public static final String MONTHLY  = "M";
		public static final String FREE_PERIOD = "L";
		public static final String MONTHLY_LARGE  = "MONTHLY";


		public Period() {
		}
	}

	public static final class Crypto{
		public static final String BASE64_URL = "B64URL";
		public static final String INPUT_CONTEXT_CRYPTO_CONTACT_DETAIL = "operation=DO;type=contactDetailId;origin=ASO;endpoint=ASO;securityLevel=5";
		public static final String INPUT_CONTEXT_CRYPTO_CUSTOMER_ID = "operation=DO;type=fpextff1;origin=ASO;endpoint=ASO;securityLevel=5";
		public static final String APP_NAME = "apx-pe";
		public static final String CRED_EXTRA_PARAMS = "user=KSMK;country=PE";
		public static final String KEY_CYPHER_CODE = "apx-pe-fpextff1-do";
	}

	public static final class NotSpecified{
		public static final String NO_EXIST = "NotExist";

	}

	public static final class Channel{
		public static final String TELEMARKETING_CODE = "TM";
	}

	public static final class TimeUtil{
		public static final String GMT_TIME_ZONE = "GMT";
		public static final String LIMA_TIME_ZONE = "America/Lima";
	}

	public enum INDICATOR_PRE_FORMALIZED{
		PRE_FORMALIZED_S ("S","Se realiza la formalización parcial de un contrato."),
		NOT_PRE_FORMALIZED_N ("N","Se debe seguir el flujo antiguo de formalización ."),
		;

		private final String value;
		private final String description;
		INDICATOR_PRE_FORMALIZED(String value, String description) {
			this.value = value;
			this.description = description;
		}

		public String getValue() {
			return value;
		}

		public String getDescription() {
			return description;
		}
	}

	public static final class Status {
		public static final String OK = "OK";
		public static final String ENR = "ENR";
		public static final String EWR = "EWR";

		private Status() {
		}
	}

	public static final class ASO_VALUES {
		public static final String EMAIL ="EMAIL";
		public static final String PHONE = "PHONE";
		public static final String INTERNAL_CONTRACT = "INT";
		public static final String EXTERNAL_CONTRACT= "EXT";

		public static final String INTERNAL_CONTRACT_OUT = "INTERNAL_CONTRACT";
		public static final String EXTERNAL_CONTRACT_OUT= "EXTERNAL_CONTRACT";

		public static final String PAYMENT_MANAGER= "PAYMENT_MANAGER";
		public static final String LEGAL_REPRESENTATIVE= "LEGAL_REPRESENTATIVE";

		//Enum KEY
		public static final String INSURANCE_DOCUMENT_TYPE_ID= "insurance.documentType.id";
		public static final String INSURANCE_PERIOD_ID = "insurance.period.id";
		public static final String INSURANCE_PAYMENT_TYPE = "insurance.paymentType.id";
		public static final String INSURANCE_RELATED_CONTRACT_ID = "insurance.relatedContract.product.id";
		public static final String INSURANCE_RELATED_CONTRACT_CONTRACT_DETAIL_CONTRACT_TYPE ="insurance.relatedContract.contractDetail.contractType";

		private ASO_VALUES() {
		}
	}
}
