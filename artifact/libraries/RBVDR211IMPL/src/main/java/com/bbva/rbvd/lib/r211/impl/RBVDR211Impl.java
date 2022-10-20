package com.bbva.rbvd.lib.r211.impl;

import com.bbva.apx.exception.business.BusinessException;

import com.bbva.ksmk.dto.caas.CredentialsDTO;
import com.bbva.ksmk.dto.caas.InputDTO;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEWUResponse;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.bo.AddressesBO;
import com.bbva.pisd.dto.insurance.bo.BirthDataBO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.ContactTypeBO;
import com.bbva.pisd.dto.insurance.bo.DocumentTypeBO;
import com.bbva.pisd.dto.insurance.bo.GenderBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupTypeBO;
import com.bbva.pisd.dto.insurance.bo.GeographicGroupsBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.pisd.dto.insurance.bo.LocationBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;

import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;

import com.bbva.rbvd.dto.homeinsrc.dao.SimltInsuredHousingDAO;

import com.bbva.rbvd.dto.homeinsrc.utils.HomeInsuranceProperty;

import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.CuotaFinancimientoBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.OrganizacionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;

import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;

import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;

import com.bbva.rbvd.dto.insrncsale.utils.ContactTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;

import com.bbva.rbvd.lib.r211.impl.util.AddressEnum;
import com.bbva.rbvd.lib.r211.impl.util.ContactTypeEnum2;
import com.bbva.rbvd.lib.r211.impl.util.GenderEnum;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

	private static final String CHANNEL_GLOMO = "pisd.channel.glomo.aap";
	private static final String BASE64_URL = "B64URL";
	private static final String APPNAME = "apx-pe";
	private static final String INPUT_CONTEXT_CRYPTO_CONTACTDETAIL = "operation=DO;type=contactDetailId;origin=ASO;endpoint=ASO;securityLevel=5";
	private static final String CRED_EXTRA_PARAMS = "user=KSMK;country=PE";
	private static final String KEY_PIC_CODE = "pic.code";
	private static final String KEY_AGENT_PROMOTER_CODE = "agent.and.promoter.code";
	private static final String KEY_TLMKT_CODE = "telemarketing.code";
	private static final String KEY_CYPHER_CODE = "apx-pe-fpextff1-do";
	private static final String LIMA_TIME_ZONE = "America/Lima";
	private static final String GMT_TIME_ZONE = "GMT";
	private static final String TAG_ENDORSEE = "ENDORSEE";
	private static final String TAG_RUC = "RUC";

	private static final String INSURANCE_PRODUCT_TYPE_VEH = "830";
	private static final String INSURANCE_PRODUCT_TYPE_HOME = "832";
	private static final String INSURANCE_PRODUCT_TYPE_FLEXIPYME = "833";

	private static final String GIFOLE_SALES_ASO = "enable_gifole_sales_aso";
	private static final String RUC_ID = "RUC";
	private static final String TAG_OTROS = "OTROS";

	private static final List<String> LIST_IDEDIR1 = Arrays.asList("ALAMEDA", "AVENUE", "STREET", "MALL", "ROAD", "SHOPPING_ARCADE", "JIRON", "JETTY", "OVAL", "PEDESTRIAN_WALK", "SQUARE", "PARK", "PROLONGATION", "PASSAGE", "BRIDGE", "DESCENT", "PORTAL", "UNCATEGORIZED", "NOT_PROVIDED");
	private static final List<String> LIST_IDEDIR2 = Arrays.asList("GROUP", "AAHH", "HOUSING_COMPLEX", "COMMUNITY", "HOUSING_COOPERATIVE", "STAGE", "SHANTYTOWN", "NEIGHBORHOOD", "URBANIZATION", "NEIGHBORHOOD_UNIT", "ZONE", "ASSOCIATION", "INDIGENOUS_COMMUNITY", "PEASANT_COMMUNITY", "FUNDO", "MINING_CAMP", "RESIDENTIAL");

	@Override
	public PolicyDTO executeBusinessLogicEmissionPrePolicy(PolicyDTO requestBody) {

		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy START *****");
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Param: {}", requestBody);

		EmisionBO rimacResponse = null;

		PolicyDTO responseBody;

		String policyNumber = this.applicationConfigurationService.getProperty("policyWithoutNumber");

		Boolean isEndorsement;

		String endosatarioRuc;

		Double endosatarioPorcentaje;

		CustomerListASO customerList = null;

		String legalName = null;

		try {

			Map<String, Object> quotationIdArgument = this.createSingleArgument(requestBody.getQuotationId(),
					RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue());

			Map<String, Object> responseValidateIfPolicyExists = pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(),
					quotationIdArgument);

			validateIfPolicyExists(responseValidateIfPolicyExists);

			Map<String, Object> responseQueryGetRequiredFields = pisdR012.executeGetASingleRow(RBVDProperties.DYNAMIC_QUERY_FOR_INSURANCE_CONTRACT.getValue(),
					quotationIdArgument);

			Map<String, Object> frequencyTypeArgument = this.createSingleArgument(requestBody.getInstallmentPlan().getPeriod().getId(),
					RBVDProperties.FIELD_POLICY_PAYMENT_FREQUENCY_TYPE.getValue());

			Map<String, Object> responseQueryGetPaymentPeriod = pisdR012.executeGetASingleRow(RBVDProperties.QUERY_SELECT_PAYMENT_PERIOD.getValue(),
					frequencyTypeArgument);

			RequiredFieldsEmissionDAO emissionDao = validateResponseQueryGetRequiredFields(responseQueryGetRequiredFields, responseQueryGetPaymentPeriod);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Required payment evaluation *****");
			evaluateRequiredPayment(requestBody);

			PolicyASO asoResponse = rbvdR201.executePrePolicyEmissionASO(this.mapperHelper.buildAsoRequest(requestBody));

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Is it coming from TLMKT? *****");
			evaluateBranchIdValue(requestBody);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | isDigitalSale validation *****");
			validateDigitalSale(requestBody);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Building Rimac request *****");
			EmisionBO rimacRequest = this.mapperHelper.buildRequestBodyRimac(requestBody.getInspection(), createSecondDataValue(asoResponse),
					requestBody.getSaleChannelId(), asoResponse.getData().getId(), requestBody.getBank().getBranch().getId());

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | AreThereMoreThanOneParticipant validation *****");
			isEndorsement = validateEndorsement(requestBody);

			InsuranceContractDAO contractDao = this.mapperHelper.buildInsuranceContract(requestBody, emissionDao, asoResponse.getData().getId(), isEndorsement);

			Map<String, Object> argumentsForSaveContract = this.mapperHelper.createSaveContractArguments(contractDao);
			argumentsForSaveContract.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveContract parameter {} with value: {} *****", key, value));

			int insertedContract = this.pisdR012.executeInsertSingleRow(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue(), argumentsForSaveContract,
					RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(),
					RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(),
					RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue(),
					RBVDProperties.FIELD_CUSTOMER_ID.getValue(), RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue(),
					RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), RBVDProperties.FIELD_USER_AUDIT_ID.getValue());

			validateInsertion(insertedContract, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);

			List<InsuranceCtrReceiptsDAO> receiptsList = this.mapperHelper.buildInsuranceCtrReceipts(asoResponse, requestBody);

			Map<String, Object>[] receiptsArguments = this.mapperHelper.createSaveReceiptsArguments(receiptsList);
			Arrays.stream(receiptsArguments).
					forEach(receipt -> receipt.
							forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value)));

			validateMultipleInsertion(this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(),
					receiptsArguments), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);

			IsrcContractMovDAO contractMovDao = this.mapperHelper.buildIsrcContractMov(asoResponse, requestBody.getCreationUser(), requestBody.getUserAudit());
			Map<String, Object> argumentsForContractMov = this.mapperHelper.createSaveContractMovArguments(contractMovDao);
			argumentsForContractMov.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy | SaveContractMov parameter {} with value: {} *****", key, value));

			int insertedContractMove = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), argumentsForContractMov);

			validateInsertion(insertedContractMove, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE);

			Map<String, Object> responseQueryRoles = this.pisdR012.executeGetRolesByProductAndModality(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());

			if(!isEmpty((List) responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue()))) {

				List<IsrcContractParticipantDAO> participants = this.mapperHelper.buildIsrcContractParticipants(requestBody, responseQueryRoles, asoResponse.getData().getId());

				Map<String, Object>[] participantsArguments = this.mapperHelper.createSaveParticipantArguments(participants);

				Arrays.stream(participantsArguments).forEach(
						participant -> participant.forEach(
								(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy | SaveParticipants parameter {} with value: {} *****", key, value)));

				validateMultipleInsertion(this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSRNC_CTR_PARTICIPANT.getValue(),
						participantsArguments), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE);
			}

			if(isEndorsement){
				endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
				endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();

				rimacRequest.getPayload().setEndosatario(new EndosatarioBO(endosatarioRuc, endosatarioPorcentaje.intValue()));

				Map<String, Object> argumentsForSaveEndorsement = this.mapperHelper.createSaveEndorsementArguments(contractDao, endosatarioRuc, endosatarioPorcentaje);
				argumentsForSaveEndorsement.forEach(
						(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveContractEndorsement parameter {} with value: {} *****", key, value));

				int insertedContractEndorsement = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_POLICY_ENDORSEMENT.getValue(), argumentsForSaveEndorsement);

				validateInsertion(insertedContractEndorsement, RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE);
			}

			if (!requestBody.getProductId().equals(RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH.getValue())) {
				customerList = getCustomerInformationFromCics(requestBody.getHolder().getId());
				LOGGER.info("***** RBVDR211Impl - CICS getCustomerInformation - customerList:{} *****", customerList);

				EmisionBO generalEmisionRequest = this.mapperHelper.mapRimacEmisionRequest(rimacRequest, requestBody, responseQueryGetRequiredFields, customerList);

				setOrganization(generalEmisionRequest, requestBody.getHolder().getId(), customerList);
				legalName = getLegalName(generalEmisionRequest);
				rimacResponse = rbvdR201.executePrePolicyEmissionService(generalEmisionRequest, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getTraceId(), requestBody.getProductId());
			} else {
				rimacResponse = rbvdR201.executePrePolicyEmissionService(rimacRequest, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getTraceId(), requestBody.getProductId());
			}

			if(nonNull(rimacResponse)) {
				Map<String, Object> argumentsRimacContractInformation = this.mapperHelper.getRimacContractInformation(rimacResponse, asoResponse.getData().getId());
				argumentsRimacContractInformation.forEach(
						(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - UpdateContract parameter {} with value: {} *****", key, value));

				int updatedContract = this.pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", argumentsRimacContractInformation,
						RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
						RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue());

				validateInsertion(updatedContract, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);

				List<InsuranceCtrReceiptsDAO> otherReceipts = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
						filter(cuota -> cuota.getCuota().compareTo(1L) > 0).map(cuota -> this.generateNextReceipt(asoResponse, cuota)).
						collect(toList());

				Map<String, Object>[] receiptUpdateArguments = this.mapperHelper.createSaveReceiptsArguments(otherReceipts);

				Arrays.stream(receiptUpdateArguments).
						forEach(receiptUpdated -> receiptUpdated.
								forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value)));

				validateMultipleInsertion(this.pisdR012.executeMultipleInsertionOrUpdate("PISD.UPDATE_EXPIRATION_DATE_RECEIPTS",
						receiptUpdateArguments), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);

				policyNumber = rimacResponse.getPayload().getNumeroPoliza();
			}

			responseBody = requestBody;

			this.mapperHelper.mappingOutputFields(responseBody, asoResponse, rimacResponse, emissionDao);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Building email object to send *****");

			CreateEmailASO email = generateEmailWithForker(responseBody.getProductId(), emissionDao, responseBody,
					policyNumber, customerList, legalName);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Sending email ... *****");
			Integer emailHttpStatus = this.rbvdR201.executeCreateEmail(email);

			String gifoleFlag = this.applicationConfigurationService.getProperty(GIFOLE_SALES_ASO);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Gifole Service Enabled: {}", gifoleFlag);
			gifoleLeadService(responseBody, customerList, Boolean.parseBoolean(gifoleFlag), legalName);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Response: {}", responseBody);
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy END *****");

			return responseBody;
		} catch (BusinessException ex) {
			LOGGER.debug("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Business exception message: {} *****", ex.getMessage());
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return null;
		}

	}

	private Map<String, Object> createSingleArgument(String argument, String parameterName) {
		Map<String, Object> mapArgument = new HashMap<>();
		if(RBVDProperties.FIELD_POLICY_PAYMENT_FREQUENCY_TYPE.getValue().equals(parameterName)) {
			String frequencyType = this.applicationConfigurationService.getProperty(argument);
			mapArgument.put(parameterName, frequencyType);
			return mapArgument;
		}
		mapArgument.put(parameterName, argument);
		return mapArgument;
	}

	private void validateIfPolicyExists(Map<String, Object> responseValidateIfPolicyExists) {
		BigDecimal resultNumber = (BigDecimal) responseValidateIfPolicyExists.get(RBVDProperties.FIELD_RESULT_NUMBER.getValue());
		if(nonNull(resultNumber) && resultNumber.compareTo(BigDecimal.ONE) == 0) {
			throw RBVDValidation.build(RBVDErrors.POLICY_ALREADY_EXISTS);
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

	private void evaluateBranchIdValue(PolicyDTO requestBody) {
		String tlmktValue = this.applicationConfigurationService.getProperty(KEY_TLMKT_CODE);
		String branchId = requestBody.getBank().getBranch().getId();
		if(tlmktValue.equals(branchId)) {
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | It's TLMKT Channel *****");
			requestBody.setSaleChannelId("TM");
		}
	}

	private void validateDigitalSale(PolicyDTO requestBody) {
		String picCodeValue = this.applicationConfigurationService.getProperty(KEY_PIC_CODE);
		if( !(picCodeValue.equals(requestBody.getSaleChannelId()) || "TM".equals(requestBody.getSaleChannelId())) ) {

			LOGGER.info("***** It's digital sale!! *****");

			String glomoAap = this.applicationConfigurationService.getProperty(CHANNEL_GLOMO);

			if(glomoAap.equals(requestBody.getAap())) {
				LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | It's GLOMO channel!! *****");
				this.getContactDetails(requestBody);
			}

			String defaultCode = this.applicationConfigurationService.getProperty(KEY_AGENT_PROMOTER_CODE);

			BusinessAgentDTO businessAgent = new BusinessAgentDTO();
			businessAgent.setId(defaultCode);

			if(isNull(requestBody.getPromoter())) {
				PromoterDTO promoter = new PromoterDTO();
				promoter.setId(defaultCode);
				requestBody.setPromoter(promoter);
			}
			requestBody.setBusinessAgent(businessAgent);
		}
	}

	private void getContactDetails(PolicyDTO requestBody) {
		String customerId = requestBody.getHolder().getId();

		String emailCode = requestBody.getHolder().getContactDetails().get(0).getId();
		String phoneCode = requestBody.getHolder().getContactDetails().get(1).getId();

		GetContactDetailsASO contactDetails = rbvdR201.executeGetContactDetailsService(customerId);

		String b64Email = this.encodeB64(emailCode.getBytes(StandardCharsets.UTF_8));

		List<OutputDTO> output = this.ksmkR002.executeKSMKR002(singletonList(new InputDTO(b64Email, BASE64_URL)), "", INPUT_CONTEXT_CRYPTO_CONTACTDETAIL,
				new CredentialsDTO(APPNAME, "", CRED_EXTRA_PARAMS));

		String emailCryptoCode = output.get(0).getData();
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** KSMK Response EmailCrypto: {}", emailCryptoCode);
		Optional<ContactDetailsBO> emailContact = contactDetails.getData().stream()
				.filter(contact -> emailCryptoCode.equals(contact.getContactDetailId())).findFirst();

		ContactDTO firstContact = new ContactDTO();
		firstContact.setContactDetailType("EMAIL");
		firstContact.setAddress(emailContact.map(ContactDetailsBO::getContact).orElse("No se encontro correo"));
		firstContact.setPhoneNumber("");

		requestBody.getHolder().getContactDetails().get(0).setContact(firstContact);

		String b64Phone = this.encodeB64(phoneCode.getBytes(StandardCharsets.UTF_8));

		output = this.ksmkR002.executeKSMKR002(singletonList(new InputDTO(b64Phone, BASE64_URL)), "", INPUT_CONTEXT_CRYPTO_CONTACTDETAIL,
				new CredentialsDTO(APPNAME, "", CRED_EXTRA_PARAMS));

		String phoneCryptoCode = output.get(0).getData();
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** KSMK Response PhoneCrypto: {}", phoneCryptoCode);
		Optional<ContactDetailsBO> phoneContact = contactDetails.getData().stream()
				.filter(contact -> phoneCryptoCode.equals(contact.getContactDetailId())).findFirst();

		ContactDTO secondContact = new ContactDTO();
		secondContact.setContactDetailType("PHONE");
		secondContact.setPhoneNumber(phoneContact.map(ContactDetailsBO::getContact).orElse("No se encontro celular"));
		secondContact.setAddress("");

		requestBody.getHolder().getContactDetails().get(1).setContact(secondContact);
	}

	private String createSecondDataValue(PolicyASO asoResponse) {
		RelatedContractASO relatedContract = asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);
		String kindOfAccount = relatedContract.getProduct().getId().equals("CARD") ? "TARJETA" : "CUENTA";
		int beginIndex = relatedContract.getNumber().length() - 4;
		String accountNumber = "***".concat(relatedContract.getNumber().substring(beginIndex));
		String accountCurrency = asoResponse.getData().getTotalAmount().getExchangeRate().getTargetCurrency();
		return kindOfAccount.concat("||").concat(accountNumber).concat("||").concat(accountCurrency);
	}

	private boolean validateEndorsement(PolicyDTO requestBody) {
		if(requestBody.getParticipants() != null && requestBody.getParticipants().size() > 1) {
			if (requestBody.getParticipants().get(1).getIdentityDocument() != null
					&& TAG_ENDORSEE.equals(requestBody.getParticipants().get(1).getParticipantType().getId())
					&& TAG_RUC.equals(requestBody.getParticipants().get(1).getIdentityDocument().getDocumentType().getId())
					&& requestBody.getParticipants().get(1).getBenefitPercentage() != null) {
				return true;
			}
		}
		return false;
	}

	private void validateInsertion(int insertedRows, RBVDErrors error) {
		if(insertedRows != 1) {
			throw RBVDValidation.build(error);
		}
	}

	private void validateMultipleInsertion(int[] insertedRows, RBVDErrors error) {
		if(isNull(insertedRows) || insertedRows.length == 0) {
			throw RBVDValidation.build(error);
		}
	}

	private void setOrganization(EmisionBO emision, String customerId, CustomerListASO customerList){
		PersonaBO persona = emision.getPayload().getAgregarPersona().getPersona().get(0);
		CustomerBO customer = customerList.getData().get(0);
		String tipoDoc = customer.getIdentityDocuments().get(0).getDocumentType().getId();
		String nroDoc = customer.getIdentityDocuments().get(0).getDocumentNumber();
		if (RUC_ID.equalsIgnoreCase(tipoDoc) && StringUtils.startsWith(nroDoc, "20")){

			String xcustomerId = this.rbvdR201.executeCypherService(new CypherASO(customerId, KEY_CYPHER_CODE));
			if (xcustomerId == null){
				BusinessException except = RBVDValidation.build(RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO);
				except.setMessage("ERROR AL ENCRIPTAR EL IDENTIFICADOR DEL CLIENTE");
				throw except;
			}

			ListBusinessesASO listBussinesses = this.rbvdR201.executeGetListBusinesses(xcustomerId, null);
			if (listBussinesses == null) {
				throw RBVDValidation.build(RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO);
			}
			List<OrganizacionBO> organizaciones = mapOrganizations(listBussinesses.getData().get(0), persona, customer);
			emision.getPayload().getAgregarPersona().setOrganizacion(organizaciones);
			emision.getPayload().getAgregarPersona().setPersona(null);
		}
	}

	private List<OrganizacionBO> mapOrganizations(final BusinessASO business, PersonaBO persona, CustomerBO customer) {
		List<OrganizacionBO> organizaciones = new ArrayList<>();

		String fijo = customer.getContactDetails().stream().filter(
						d -> ContactTypeEnum.PHONE_NUMBER.getValue().equals(d.getContactType().getId())).findFirst().
				orElse(new ContactDetailsBO()).getContact();
		String celular = customer.getContactDetails().stream().filter(
						d -> ContactTypeEnum.MOBILE_NUMBER.getValue().equals(d.getContactType().getId())).findFirst().
				orElse(new ContactDetailsBO()).getContact();
		String correo = customer.getContactDetails().stream().filter(
						d -> ContactTypeEnum.EMAIL.getValue().equals(d.getContactType().getId())).findFirst().
				orElse(new ContactDetailsBO()).getContact();

		int[] intArray = new int[]{8, 9, 23};
		for (int i = 0; i < intArray.length; i++) {
			OrganizacionBO organizacion = new OrganizacionBO();
			organizacion.setDireccion(persona.getDireccion());
			organizacion.setRol(intArray[i]);
			organizacion.setTipoDocumento("R");
			organizacion.setNroDocumento(business.getBusinessDocuments().get(0).getDocumentNumber());
			organizacion.setRazonSocial(business.getLegalName());
			organizacion.setNombreComercial(business.getLegalName());
			organizacion.setPaisOrigen(business.getFormation().getCountry().getName());
			organizacion.setFechaConstitucion(business.getFormation().getDate());
			organizacion.setFechaInicioActividad(business.getAnnualSales().getStartDate());
			organizacion.setTipoOrganizacion(business.getBusinessGroup().getId());
			organizacion.setGrupoEconomico(TAG_OTROS);
			organizacion.setCiiu(business.getEconomicActivity().getId());
			organizacion.setTelefonoFijo(fijo);
			organizacion.setCelular(celular);
			organizacion.setCorreoElectronico(correo);
			organizacion.setDistrito(persona.getDistrito());
			organizacion.setProvincia(persona.getProvincia());
			organizacion.setDepartamento(persona.getDepartamento());
			organizacion.setNombreVia(persona.getNombreVia());
			organizacion.setTipoVia(persona.getTipoVia());
			organizacion.setNumeroVia(persona.getNumeroVia());
			organizacion.setTipoPersona(this.mapperHelper.getPersonType(organizacion).getCode());
			organizaciones.add(organizacion);
		}
		return organizaciones;
	}

	private String getLegalName(EmisionBO generalEmisionRequest){
		if (generalEmisionRequest.getPayload().getAgregarPersona().getOrganizacion() != null) {
			return generalEmisionRequest.getPayload().getAgregarPersona().getOrganizacion().get(0).getNombreComercial();
		}
		return null;
	}

	private CreateEmailASO generateEmailWithForker(String productId, RequiredFieldsEmissionDAO emissionDao
			, PolicyDTO responseBody, String policyNumber, CustomerListASO customerList, String legalName){
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
			case INSURANCE_PRODUCT_TYPE_FLEXIPYME:
				Map<String, Object> responseQueryGetEdificationInfo = pisdR021.executeGetHomeInfoForEmissionService(responseBody.getQuotationId());
				Map<String, Object> responseQueryGetEdificationRiskDirection= pisdR021.executeGetHomeRiskDirection(responseBody.getQuotationId());
				email = this.mapperHelper.buildCreateEmailRequestFlexipyme(emissionDao, responseBody, policyNumber
						, customerList, validateResponseQueryGetHomeRequiredFields(responseQueryGetEdificationInfo),
						validateResponseQueryHomeRiskDirectionFields(responseQueryGetEdificationRiskDirection), legalName);
				break;
			default:
				break;
		}
		return email;
	}

	private InsuranceCtrReceiptsDAO generateNextReceipt(PolicyASO asoResponse, CuotaFinancimientoBO cuota) {
		InsuranceCtrReceiptsDAO nextReceipt = new InsuranceCtrReceiptsDAO();
		nextReceipt.setEntityId(asoResponse.getData().getId().substring(0, 4));
		nextReceipt.setBranchId(asoResponse.getData().getId().substring(4, 8));
		nextReceipt.setIntAccountId(asoResponse.getData().getId().substring(10));
		nextReceipt.setPolicyReceiptId(BigDecimal.valueOf(cuota.getCuota()));
		nextReceipt.setReceiptExpirationDate(this.mapperHelper.generateCorrectDateFormat(cuota.getFechaVencimiento()));
		return nextReceipt;
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

	private void gifoleLeadService(PolicyDTO policyDTO, CustomerListASO customerListASO, Boolean isGifoleEnabled, String legalName) {
		if (isGifoleEnabled) {
			GifoleInsuranceRequestASO gifoleRequest = this.mapperHelper.createGifoleRequest(policyDTO, customerListASO, legalName);
			this.rbvdR201.executeGifoleEmisionService(gifoleRequest);
		}
	}

	private String encodeB64(byte[] hash) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
	}

	private CustomerListASO getCustomerInformationFromCics(String customerID) {
		LOGGER.info("***** RBVDR211Impl - getCustomerInformationFromCics START *****");
		PEWUResponse listCustomerCics = this.pbtqR002.executeSearchInHostByCustomerId(customerID);
		validateListCustomerCicsResponse(listCustomerCics);

		CustomerListASO out = mapListCustomerCicsResponse(listCustomerCics);

		LOGGER.info("***** RBVDR211Impl - getCustomerInformationFromCics END *****");
		return out;
	}
	private CustomerListASO mapListCustomerCicsResponse(PEWUResponse listCustomerCics) {
		CustomerListASO out = new CustomerListASO();
		CustomerBO customer = new CustomerBO();

		IdentityDocumentsBO document = new IdentityDocumentsBO();
		DocumentTypeBO docType = new DocumentTypeBO();
		docType.setId(this.applicationConfigurationService.getProperty(listCustomerCics.getPemsalwu().getTdoi()));
		document.setDocumentType(docType);
		document.setDocumentNumber(listCustomerCics.getPemsalwu().getNdoi());

		customer.setFirstName(listCustomerCics.getPemsalwu().getNombres());
		customer.setLastName(listCustomerCics.getPemsalwu().getApellip());
		customer.setSecondLastName(listCustomerCics.getPemsalwu().getApellim());
		BirthDataBO birth = new BirthDataBO();
		birth.setBirthDate(listCustomerCics.getPemsalwu().getFechan());
		customer.setBirthData(birth);
		String genero = Objects.toString(EnumUtils.getEnum(GenderEnum.class, listCustomerCics.getPemsalwu().getSexo()), null);
		GenderBO gender = null;
		if(genero != null) {
			gender = new GenderBO();
			gender.setId(genero);
		}
		customer.setGender(gender);

		AddressesBO address = new AddressesBO();
		LocationBO location = new LocationBO();
		List<GeographicGroupsBO> geoGroups = new ArrayList<>();

		geoGroups.add(getNewGeoGroup("DEPARTMENT", listCustomerCics.getPemsalw4().getDesdept(), listCustomerCics.getPemsalwu().getCodigod()));
		geoGroups.add(getNewGeoGroup("PROVINCE", listCustomerCics.getPemsalw4().getDesprov(), listCustomerCics.getPemsalwu().getCodigop()));
		geoGroups.add(getNewGeoGroup("DISTRICT", listCustomerCics.getPemsalw4().getDesdist(), listCustomerCics.getPemsalwu().getCodigdi()));
		geoGroups.add(getNewGeoGroup("EXTERIOR_NUMBER", listCustomerCics.getPemsalwu().getNroext1(), listCustomerCics.getPemsalwu().getNroext1()));
		geoGroups.add(getNewGeoGroup("INTERIOR_NUMBER", listCustomerCics.getPemsalwu().getNroint1(), listCustomerCics.getPemsalwu().getNroext1()));
		geoGroups.add(getNewGeoGroup("BLOCK", listCustomerCics.getPemsalwu().getManzana(), listCustomerCics.getPemsalwu().getManzana()));
		geoGroups.add(getNewGeoGroup("LOT", listCustomerCics.getPemsalwu().getLote(), listCustomerCics.getPemsalwu().getManzana()));
		String idendi1 = AddressEnum.findByHostValue(listCustomerCics.getPemsalwu().getIdendi1()).toString();
		String idendi2 = AddressEnum.findByHostValue(listCustomerCics.getPemsalwu().getIdendi2()).toString();
		geoGroups.add(getNewGeoGroup(idendi1, listCustomerCics.getPemsalwu().getNombdi1()
				, LIST_IDEDIR1.stream().filter(s->s.equalsIgnoreCase(idendi1)).findFirst().orElse(null)));
		geoGroups.add(getNewGeoGroup(idendi2, listCustomerCics.getPemsalwu().getNombdi2()
				, LIST_IDEDIR2.stream().filter(s->s.equalsIgnoreCase(idendi2)).findFirst().orElse(null)));

		location.setGeographicGroups(geoGroups.stream().filter(Objects::nonNull).collect(Collectors.toList()));
		address.setLocation(location);
		customer.setAddresses(Arrays.asList(address));

		customer.setIdentityDocuments(Arrays.asList(document));

		customer.setContactDetails(getContactDetails(listCustomerCics));

		out.setData(Arrays.asList(customer));
		return out;
	}

	private static List<ContactDetailsBO> getContactDetails(PEWUResponse cics){
		List<ContactDetailsBO> out = new ArrayList<>();
		ContactDetailsBO detail = new ContactDetailsBO();
		detail.setContactDetailId(cics.getPemsalwu().getIdencon());
		ContactTypeBO contactType = new ContactTypeBO();
		contactType.setId(Objects.toString(EnumUtils.getEnum(ContactTypeEnum2.class, cics.getPemsalwu().getTipocon()), null));
		contactType.setName(cics.getPemsalw5().getDescmco());
		detail.setContactType(contactType);
		detail.setContact(cics.getPemsalwu().getContact());
		out.add(detail);

		detail = new ContactDetailsBO();
		detail.setContactDetailId(cics.getPemsalwu().getIdenco2());
		contactType = new ContactTypeBO();
		contactType.setId(Objects.toString(EnumUtils.getEnum(ContactTypeEnum2.class, cics.getPemsalwu().getTipoco2()), null));
		contactType.setName(cics.getPemsalw5().getDescmc1());
		detail.setContactType(contactType);
		detail.setContact(cics.getPemsalwu().getContac2());
		out.add(detail);

		detail = new ContactDetailsBO();
		detail.setContactDetailId(cics.getPemsalwu().getIdenco3());
		contactType = new ContactTypeBO();
		contactType.setId(Objects.toString(EnumUtils.getEnum(ContactTypeEnum2.class, cics.getPemsalwu().getTipoco3()), null));
		contactType.setName(cics.getPemsalw5().getDescmc2());
		detail.setContactType(contactType);
		detail.setContact(cics.getPemsalwu().getContac3());
		out.add(detail);
		return out.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	private GeographicGroupsBO getNewGeoGroup(String geoId, String geoName, String toValidate){
		if(Objects.isNull(toValidate)) return null;
		GeographicGroupsBO geoGroup = new GeographicGroupsBO();
		GeographicGroupTypeBO geoType = new GeographicGroupTypeBO();
		geoType.setId(geoId);
		geoGroup.setName(geoName);
		geoGroup.setGeographicGroupType(geoType);
		return geoGroup;
	}
	private void validateListCustomerCicsResponse(PEWUResponse cicsResponse) {
		if (!StringUtils.isEmpty(cicsResponse.getHostAdviceCode())) {
			BusinessException listCustomerCicsError = PISDValidation.build(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE);
			StringBuilder sb = new StringBuilder(listCustomerCicsError.getMessage());
			sb.append(" | ");
			sb.append(cicsResponse.getHostAdviceCode());
			sb.append("-");
			sb.append(cicsResponse.getHostMessage());
			listCustomerCicsError.setMessage(sb.toString());
			LOGGER.info("***** RBVDR211Impl - validateListCustomerCicsResponse ERROR:{} *****", listCustomerCicsError.getMessage());
			throw listCustomerCicsError;
		}
	}
}
