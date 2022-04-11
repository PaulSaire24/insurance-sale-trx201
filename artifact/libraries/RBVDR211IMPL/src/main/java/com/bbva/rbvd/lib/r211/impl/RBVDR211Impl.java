package com.bbva.rbvd.lib.r211.impl;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import com.bbva.rbvd.dto.homeinsrc.dao.SimltInsuredHousingDAO;
import com.bbva.rbvd.dto.homeinsrc.utils.HomeInsuranceProperty;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;

public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);
	private static final String KEY_PIC_CODE = "pic.code";
	private static final String KEY_AGENT_PROMOTER_CODE = "agent.and.promoter.code";
	private static final String KEY_TLMKT_CODE = "telemarketing.code";
	private static final String LIMA_TIME_ZONE = "America/Lima";
	private static final String GMT_TIME_ZONE = "GMT";
	private static final String TAG_ENDORSEE = "ENDORSEE";
	private static final String TAG_RUC = "RUC";

	private static final String INSURANCE_PRODUCT_TYPE_VEH = "830";
    private static final String INSURANCE_PRODUCT_TYPE_HOME = "832";

	@Override
	public PolicyDTO executeBusinessLogicEmissionPrePolicy(PolicyDTO requestBody) {

		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy START *****");
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Param: {}", requestBody);

		EmisionBO rimacResponse = null;

		PolicyDTO responseBody = null;
		Boolean isEndorsement = false;
		String endosatarioRuc = "";
		Double endosatarioPorcentaje = 0.0;

		CustomerListASO customerList = new CustomerListASO();

		try {

			Map<String, Object> responseQueryGetRequiredFields = pisdR012.executeGetRequiredFieldsForEmissionService(requestBody.getQuotationId());

			Map<String, Object> responseQueryGetPaymentPeriod = pisdR012.
					executeGetPaymentPeriod(this.applicationConfigurationService.getProperty(requestBody.getInstallmentPlan().getPeriod().getId()));

			RequiredFieldsEmissionDAO emissionDao = validateResponseQueryGetRequiredFields(responseQueryGetRequiredFields, responseQueryGetPaymentPeriod);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Required payment evaluation *****");
			evaluateRequiredPayment(requestBody);

			PolicyASO asoResponse = rbvdR201.executePrePolicyEmissionASO(this.mapperHelper.buildAsoRequest(requestBody));

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Is it coming from TLMKT? *****");
			evaluateBranchIdValue(requestBody);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Building Rimac request *****");
			EmisionBO rimacRequest = this.mapperHelper.buildRequestBodyRimac(requestBody.getInspection(), createSecondDataValue(asoResponse),
					requestBody.getSaleChannelId(), asoResponse.getData().getId(), requestBody.getBank().getBranch().getId());

			if(requestBody.getParticipants() != null && requestBody.getParticipants().size() > 1) {
				if (requestBody.getParticipants().get(1).getIdentityDocument() != null
						&& TAG_ENDORSEE.equals(requestBody.getParticipants().get(1).getParticipantType().getId())
						&& TAG_RUC.equals(requestBody.getParticipants().get(1).getIdentityDocument().getDocumentType().getId())
						&& requestBody.getParticipants().get(1).getBenefitPercentage() != null) {
					endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
					endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();
					LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | endosatarioRuc: {}, endosatarioPorcentaje: {} *****", endosatarioRuc, endosatarioPorcentaje);
					rimacRequest.getPayload().setEndosatario(new EndosatarioBO(endosatarioRuc, endosatarioPorcentaje.intValue()));
					isEndorsement = true;
				}
			}

			if (!requestBody.getProductId().equals(RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH.getValue())) {

				customerList = this.rbvdR201.executeGetCustomerInformation(requestBody.getHolder().getId());

				try {
					validateQueryCustomerResponse(customerList);
				} catch (BusinessException ex) {
					LOGGER.info("***** PISDR0019Impl - executeListCustomerResponse {} *****", ex.getMessage());
					return null;
				}

				EmisionBO generalEmisionRequest = this.mapperHelper.mapRimacEmisionRequest(rimacRequest, requestBody, responseQueryGetRequiredFields, customerList);

				rimacResponse = rbvdR201.executePrePolicyEmissionService(generalEmisionRequest, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getTraceId(), requestBody.getProductId());
			}else{

			rimacResponse = rbvdR201.executePrePolicyEmissionService(rimacRequest, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getTraceId(), requestBody.getProductId());
			
			}
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | isDigitalSale validation *****");
			validateDigitalSale(requestBody);

			InsuranceContractDAO contractDao = this.mapperHelper.buildInsuranceContract(rimacResponse, requestBody, emissionDao, asoResponse.getData().getId(), isEndorsement);

			Map<String, Object> argumentsForSaveContract = this.mapperHelper.createSaveContractArguments(contractDao);
			argumentsForSaveContract.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveContract parameter {} with value: {} *****", key, value));

			validateInsertion(this.pisdR012.executeSaveContract(argumentsForSaveContract), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);

			Map<String, Object>[] receiptsArguments = this.mapperHelper.
					createSaveReceiptsArguments(this.mapperHelper.buildInsuranceCtrReceipt(asoResponse, rimacResponse, requestBody));

			Arrays.stream(receiptsArguments).
					forEach(receiptArguments -> receiptArguments.
							forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value)));

			validateMultipleInsertion(this.pisdR012.executeSaveReceipts(receiptsArguments), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);

			IsrcContractMovDAO contractMovDao = this.mapperHelper.buildIsrcContractMov(asoResponse, requestBody.getCreationUser(), requestBody.getUserAudit());
			Map<String, Object> argumentsForContractMov = this.mapperHelper.createSaveContractMovArguments(contractMovDao);
			argumentsForContractMov.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy | SaveContractMov parameter {} with value: {} *****", key, value));

			validateInsertion(this.pisdR012.executeSaveContractMove(argumentsForContractMov), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE);

			Map<String, Object> responseQueryRoles = this.pisdR012.executeGetRolesByProductAndModality(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());

			if(!isEmpty((List) responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue()))) {

				List<IsrcContractParticipantDAO> participants = this.mapperHelper.buildIsrcContractParticipants(requestBody, responseQueryRoles, asoResponse.getData().getId());

				Map<String, Object>[] arguments = this.mapperHelper.createSaveParticipantArguments(participants);

				Arrays.stream(arguments).forEach(
						argumentsMap -> argumentsMap.forEach(
								(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy | SaveParticipants parameter {} with value: {} *****", key, value)));

				validateMultipleInsertion(this.pisdR012.executeSaveParticipants(arguments), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE);
			}

			if(isEndorsement){
				Map<String, Object> argumentsForSaveEndorsement = this.mapperHelper.createSaveEndorsementArguments(contractDao, endosatarioRuc, endosatarioPorcentaje);
				argumentsForSaveEndorsement.forEach(
						(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveContractEndorsement parameter {} with value: {} *****", key, value));

				validateInsertion(this.pisdR012.executeSaveContractEndoserment(argumentsForSaveEndorsement), RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE);
			}

			responseBody = requestBody;

			this.mapperHelper.mappingOutputFields(responseBody, asoResponse, rimacResponse, emissionDao);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Building email object to send *****");

			CreateEmailASO email = generateEmailWithForker(responseBody.getProductId(), emissionDao, responseBody,
					rimacResponse.getPayload().getNumeroPoliza(), customerList);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Send Email *****");
			Integer httpStatusEmail = this.rbvdR201.executeCreateEmail(email);

			if(Objects.nonNull(httpStatusEmail) && httpStatusEmail == HttpStatus.OK.value()) {
				LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Email sent *****");
			} else {
				LOGGER.debug("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Email not sent, something went wrong *****");
			}

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Response: {}", responseBody);
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy END *****");

			return responseBody;
		} catch (BusinessException ex) {
			LOGGER.debug("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Business exception message: {} *****", ex.getMessage());
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return null;
		}

	}

	private void evaluateRequiredPayment(PolicyDTO requestBody) {
		DateTimeZone dateTimeZone = DateTimeZone.forID(LIMA_TIME_ZONE);

		DateTime currentLocalDate = new DateTime(new Date(), dateTimeZone);
		Date currentDate = currentLocalDate.toDate();
		LOGGER.info("***** Current date: {} *****", currentDate);

		dateTimeZone = DateTimeZone.forID(GMT_TIME_ZONE);
		LocalDate startLocalDate = new LocalDate(requestBody.getValidityPeriod().getStartDate(), dateTimeZone);
		Date startDate = startLocalDate.toDateTimeAtStartOfDay().toDate();
		LOGGER.info("***** Policy start date: {} *****", startDate);

		if(startDate.after(currentDate)) {
			LOGGER.info("***** Deferred policy *****");
			requestBody.getFirstInstallment().setIsPaymentRequired(false);
		} else {
			LOGGER.info("***** Not deferred policy *****");
			requestBody.getFirstInstallment().setIsPaymentRequired(true);
		}

	}

	private void validateDigitalSale(PolicyDTO requestBody) {
		String picCodeValue = this.applicationConfigurationService.getProperty(KEY_PIC_CODE);
		if( !(picCodeValue.equals(requestBody.getSaleChannelId()) || "TM".equals(requestBody.getSaleChannelId())) ) {

			LOGGER.info("***** It's digital sale!! *****");

			String defaultCode = this.applicationConfigurationService.getProperty(KEY_AGENT_PROMOTER_CODE);

			BusinessAgentDTO businessAgent = new BusinessAgentDTO();
			businessAgent.setId(defaultCode);
			PromoterDTO promoter = new PromoterDTO();
			promoter.setId(defaultCode);

			requestBody.setBusinessAgent(businessAgent);
			requestBody.setPromoter(promoter);
		}
	}

	private void evaluateBranchIdValue(PolicyDTO requestBody) {
		String tlmktValue = this.applicationConfigurationService.getProperty(KEY_TLMKT_CODE);
		String branchId = requestBody.getBank().getBranch().getId();
		if(tlmktValue.equals(branchId)) {
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | It's TLMKT Channel *****");
			requestBody.setSaleChannelId("TM");
		}
	}

	private RequiredFieldsEmissionDAO validateResponseQueryGetRequiredFields(Map<String, Object> responseQueryGetRequiredFields, Map<String, Object> responseQueryGetPaymentPeriod) {
		if(isEmpty(responseQueryGetRequiredFields)) {
			throw RBVDValidation.build(RBVDErrors.NON_EXISTENT_QUOTATION);
		}
		RequiredFieldsEmissionDAO emissionDao = new RequiredFieldsEmissionDAO();
		emissionDao.setInsuranceProductId((BigDecimal) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
		emissionDao.setContractDurationNumber((BigDecimal) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue()));
		emissionDao.setContractDurationType((String) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_CONTRACT_DURATION_TYPE.getValue()));
		emissionDao.setPaymentFrequencyId((BigDecimal) responseQueryGetPaymentPeriod.get(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue()));
		emissionDao.setInsuranceCompanyQuotaId((String) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue()));
		emissionDao.setInsuranceProductDesc((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue()));
		emissionDao.setInsuranceModalityName((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_INSURANCE_MODALITY_NAME.getValue()));
		emissionDao.setPaymentFrequencyName((String) responseQueryGetPaymentPeriod.get(PISDProperties.FIELD_PAYMENT_FREQUENCY_NAME.getValue()));
		emissionDao.setVehicleBrandName((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_BRAND_NAME.getValue()));
		emissionDao.setVehicleModelName((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_MODEL_NAME.getValue()));
		emissionDao.setVehicleYearId((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_YEAR_ID.getValue()));
		emissionDao.setVehicleLicenseId((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_LICENSE_ID.getValue()));
		emissionDao.setGasConversionType((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_GAS_CONVERSION_TYPE.getValue()));
		emissionDao.setVehicleCirculationType((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_CIRCULATION_SCOPE_TYPE.getValue()));
		emissionDao.setCommercialVehicleAmount((BigDecimal) responseQueryGetRequiredFields.get(PISDProperties.FIELD_COMMERCIAL_VEHICLE_AMOUNT.getValue()));

		return emissionDao;
	}

	private SimltInsuredHousingDAO validateResponseQueryGetHomeRequiredFields(Map<String, Object> responseQueryGetHomeRequiredFields) {
		if(isEmpty(responseQueryGetHomeRequiredFields)) {
			throw RBVDValidation.build(RBVDErrors.NON_EXISTENT_QUOTATION);
		}
		SimltInsuredHousingDAO emissionDao = new SimltInsuredHousingDAO();
		emissionDao.setDepartmentName((String) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_DEPARTMENT_NAME.getValue()));
		emissionDao.setProvinceName((String) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_PROVINCE_NAME.getValue()));
		emissionDao.setDistrictName((String) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_DISTRICT_NAME.getValue()));
		emissionDao.setHousingType((String) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_HOUSING_TYPE.getValue()));
		emissionDao.setAreaPropertyNumber((BigDecimal) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_AREA_PROPERTY_1_NUMBER.getValue()));
		emissionDao.setPropSeniorityYearsNumber((BigDecimal) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_PROP_SENIORITY_YEARS_NUMBER.getValue()));
		emissionDao.setFloorNumber((BigDecimal) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_FLOOR_NUMBER.getValue()));
		emissionDao.setEdificationLoanAmount((BigDecimal) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_EDIFICATION_LOAN_AMOUNT.getValue()));
		emissionDao.setHousingAssetsLoanAmount((BigDecimal) responseQueryGetHomeRequiredFields.get(HomeInsuranceProperty.FIELD_HOUSING_ASSETS_LOAN_AMOUNT.getValue()));

		return emissionDao;
	}

	private String validateResponseQueryHomeRiskDirectionFields(Map<String, Object> responseQueryGetHomeRiskDirection) {
		if(isEmpty(responseQueryGetHomeRiskDirection)) {
			throw RBVDValidation.build(RBVDErrors.NON_EXISTENT_QUOTATION);
		}

		return (String) responseQueryGetHomeRiskDirection.get(HomeInsuranceProperty.FIELD_LEGAL_ADDRESS_DESC.getValue());
	}

	private void validateInsertion(int insertedRows, RBVDErrors error) {
		if(insertedRows != 1) {
			throw RBVDValidation.build(error);
		}
	}

	private void validateMultipleInsertion(int[] insertedRows, RBVDErrors error) {
		if(Objects.isNull(insertedRows) || insertedRows.length == 0) {
			throw RBVDValidation.build(error);
		}
	}

	private String createSecondDataValue(PolicyASO asoResponse) {
		RelatedContractASO relatedContract = asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);
		String kindOfAccount = relatedContract.getProduct().getId().equals("CARD") ? "TARJETA" : "CUENTA";
		int beginIndex = relatedContract.getNumber().length() - 4;
		String accountNumber = "***".concat(relatedContract.getNumber().substring(beginIndex));
		String accountCurrency = asoResponse.getData().getTotalAmount().getExchangeRate().getTargetCurrency();
		return kindOfAccount.concat("||").concat(accountNumber).concat("||").concat(accountCurrency);
	}

	private void validateQueryCustomerResponse(CustomerListASO customerList) {
		if (isEmpty(customerList.getData())) {
			throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE);
		}
	}

	private CreateEmailASO generateEmailWithForker(String productId, RequiredFieldsEmissionDAO emissionDao, PolicyDTO responseBody, String policyNumber, CustomerListASO customerList){
		CreateEmailASO email = null;
		switch (productId) {
			case INSURANCE_PRODUCT_TYPE_VEH:
				email = this.mapperHelper.buildCreateEmailRequestVeh(emissionDao, responseBody, policyNumber);
				break;
			case INSURANCE_PRODUCT_TYPE_HOME:
				Map<String, Object> responseQueryGetHomeInfo = pisdR021.executeGetHomeInfoForEmissionService(responseBody.getQuotationId());
				Map<String, Object> responseQueryGetHomeRiskDirection= pisdR021.executeGetHomeRiskDirection(responseBody.getQuotationId());
				email = this.mapperHelper.buildCreateEmailRequestHome(emissionDao, responseBody, policyNumber, customerList, validateResponseQueryGetHomeRequiredFields(responseQueryGetHomeInfo),
				validateResponseQueryHomeRiskDirectionFields(responseQueryGetHomeRiskDirection));
				break;
			default:
				break;
		}
		return email;
	}
}
