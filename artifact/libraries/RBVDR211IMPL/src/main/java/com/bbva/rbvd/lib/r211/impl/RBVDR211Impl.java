package com.bbva.rbvd.lib.r211.impl;

import com.bbva.apx.exception.business.BusinessException;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
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

import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.OrganizacionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;

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

import org.apache.commons.lang3.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);
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

		CustomerListASO customerList = new CustomerListASO();

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

			InsuranceCtrReceiptsDAO firstReceiptInformation = this.mapperHelper.getFirstReceiptInformation(asoResponse, requestBody);
			Map<String, Object> argumentsForFirstReceipt = this.mapperHelper.createReceipt(firstReceiptInformation);
			argumentsForFirstReceipt.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveFirstReceipt parameter {} with value: {} *****", key, value));

			int insertedFirstReceipt = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(), argumentsForFirstReceipt,
					RBVDProperties.FIELD_POLICY_RECEIPT_ID.getValue());

			validateInsertion(insertedFirstReceipt, RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);

			IsrcContractMovDAO contractMovDao = this.mapperHelper.buildIsrcContractMov(asoResponse, requestBody.getCreationUser(), requestBody.getUserAudit());
			Map<String, Object> argumentsForContractMov = this.mapperHelper.createSaveContractMovArguments(contractMovDao);
			argumentsForContractMov.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy | SaveContractMov parameter {} with value: {} *****", key, value));

			int insertedContractMove = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), argumentsForContractMov);

			validateInsertion(insertedContractMove, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE);

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
				customerList = this.rbvdR201.executeGetCustomerInformation(requestBody.getHolder().getId());
				try {
					validateQueryCustomerResponse(customerList);
				} catch (BusinessException ex) {
					LOGGER.info("***** PISDR0019Impl - executeListCustomerResponse {} *****", ex.getMessage());
					return null;
				}
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

				List<InsuranceCtrReceiptsDAO> otherReceipts = this.mapperHelper.buildNextInsuranceCtrReceipt(firstReceiptInformation, rimacResponse);

				Map<String, Object>[] receiptsArguments = this.mapperHelper.createSaveReceiptsArguments(otherReceipts);
				Arrays.stream(receiptsArguments).
						forEach(receiptArguments -> receiptArguments.
								forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value)));
				validateMultipleInsertion(this.pisdR012.executeSaveReceipts(receiptsArguments), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);

				policyNumber = rimacResponse.getPayload().getNumeroPoliza();
			}

			responseBody = requestBody;

			this.mapperHelper.mappingOutputFields(responseBody, asoResponse, rimacResponse, emissionDao);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Building email object to send *****");

			CreateEmailASO email = generateEmailWithForker(responseBody.getProductId(), emissionDao, responseBody,
					policyNumber, customerList, legalName);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Sending email ... *****");
			this.rbvdR201.executeCreateEmail(email);

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

	private void validateQueryCustomerResponse(CustomerListASO customerList) {
		if (isEmpty(customerList.getData())) {
			throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE);
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
}
