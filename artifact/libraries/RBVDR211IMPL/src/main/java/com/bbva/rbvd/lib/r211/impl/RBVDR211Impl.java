package com.bbva.rbvd.lib.r211.impl;

import com.bbva.apx.exception.business.BusinessException;

import com.bbva.ksmk.dto.caas.CredentialsDTO;
import com.bbva.ksmk.dto.caas.InputDTO;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;

import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;

import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;

import com.bbva.pisd.dto.insurancedao.entities.QuotationEntity;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.CuotaFinancimientoBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.OrganizacionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;

import com.bbva.rbvd.dto.insrncsale.events.StatusDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;

import com.bbva.rbvd.dto.insrncsale.utils.ContactTypeEnum;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;

import com.bbva.rbvd.lib.r211.impl.util.ConstantsUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.nio.charset.StandardCharsets;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import static org.springframework.util.CollectionUtils.isEmpty;


public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

	private static final String CHANNEL_GLOMO = "pisd.channel.glomo.aap";
	private static final String CHANNEL_CONTACT_DETAIL = "pisd.channel.contact.detail.aap";
	private static final String BASE64_URL = "B64URL";
	private static final String APPNAME = "apx-pe";
	private static final String INPUT_CONTEXT_CRYPTO_CONTACTDETAIL = "operation=DO;type=contactDetailId;origin=ASO;endpoint=ASO;securityLevel=5";
	private static final String CRED_EXTRA_PARAMS = "user=KSMK;country=PE";
	private static final String KEY_PIC_CODE = "pic.code";
	private static final String KEY_CONTACT_CENTER_CODE = "cc.code";
	private static final String KEY_AGENT_PROMOTER_CODE = "agent.and.promoter.code";
	private static final String KEY_TLMKT_CODE = "telemarketing.code";
	private static final String KEY_CYPHER_CODE = "apx-pe-fpextff1-do";
	private static final String LIMA_TIME_ZONE = "America/Lima";
	private static final String GMT_TIME_ZONE = "GMT";
	private static final String TAG_ENDORSEE = "ENDORSEE";
	private static final String TAG_RUC = "RUC";

	private static final String RUC_ID = "RUC";
	private static final String TAG_OTROS = "OTROS";

	private static final String FIELD_PREMIUM_AMOUNT = "PREMIUM_AMOUNT";
	private static final String FIELD_PREMIUM_CURRENCY_ID = "PREMIUM_CURRENCY_ID";
	private static final String FIELD_POLICY_PAYMENT_FREQUENCY_TYPE = "POLICY_PAYMENT_FREQUENCY_TYPE";
	private static final String PROPERTY_RANGE_PAYMENT_AMOUNT = "property.range.payment.amount.insurance";
	private static final String PROPERTY_VALIDATION_RANGE = "property.validation.range.";
	private static final String PROPERTY_ONLY_FIRST_RECEIPT = "products.modalities.only.first.receipt";

	private static final String FIELD_INTERNAL_CONTRACT = "INTERNAL_CONTRACT";
	private static final String FIELD_EXTERNAL_CONTRACT = "EXTERNAL_CONTRACT";
	private static final String FIELD_BLANK = "";

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

			Map<String,Object> responseQueryGetProductById = (Map<String,Object>) this.pisdR401.executeGetProductById(ConstantsUtil.Queries.QUERY_SELECT_PRODUCT_BY_PRODUCT_TYPE,
					singletonMap(RBVDProperties.FIELD_INSURANCE_PRODUCT_TYPE.getValue(), requestBody.getProductId()));

			RequiredFieldsEmissionDAO emissionDao = validateResponseQueryGetRequiredFields(responseQueryGetRequiredFields, responseQueryGetPaymentPeriod);

			if(this.applicationConfigurationService.getDefaultProperty(PROPERTY_VALIDATION_RANGE + requestBody.getProductId() + "." + requestBody.getSaleChannelId(), "0").equals("1"))
				validateAmountQuotation(responseQueryGetRequiredFields, requestBody);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Required payment evaluation *****");
			evaluateRequiredPayment(requestBody);

			PolicyASO asoResponse = rbvdR201.executePrePolicyEmissionASO(this.mapperHelper.buildAsoRequest(requestBody));

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Setting branchId provided by HOST *****");
			String hostBranchId = asoResponse.getData().getBank().getBranch().getId();
			requestBody.getBank().getBranch().setId(hostBranchId);

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

			List<InsuranceCtrReceiptsDAO> receiptsList = this.mapperHelper.buildInsuranceCtrReceipts(asoResponse, requestBody, responseQueryGetRequiredFields);

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

			if(!isEmpty(requestBody.getRelatedContracts())) {
				List<RelatedContractDAO> relatedContractsDao = this.mapperHelper.buildRelatedContractsWithInsurance(requestBody, contractDao);
				Map<String, Object>[] relatedContractsArguments = this.mapperHelper.createSaveRelatedContractsArguments(relatedContractsDao);
				Arrays.stream(relatedContractsArguments).
						forEach(receipt -> receipt.
								forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveRelatedContractsArguments parameter {} with value: {} *****", key, value)));
				this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CONTRACT_DETAILS.getValue(), relatedContractsArguments);
			}

			if(isEndorsement){
				endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
				endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();

				rimacRequest.getPayload().setEndosatario(new EndosatarioBO(endosatarioRuc, endosatarioPorcentaje.intValue()));
				LOGGER.info("RBVDR211Impl - call buildRequestBodyRimac rimacRequest v2 => {}",rimacRequest);

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
				EmisionBO generalEmisionRequest = this.mapperHelper.mapRimacEmisionRequest(rimacRequest, requestBody,
						responseQueryGetRequiredFields,responseQueryGetProductById, customerList);
				LOGGER.info("***** RBVDR211 generalEmisionRequest => {} ****",generalEmisionRequest);

				setOrganization(generalEmisionRequest,  requestBody, customerList);
				rimacResponse = rbvdR201.executePrePolicyEmissionService(generalEmisionRequest, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getTraceId(), requestBody.getProductId());
			} else {
				rimacResponse = rbvdR201.executePrePolicyEmissionService(rimacRequest, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getTraceId(), requestBody.getProductId());
			}

			LOGGER.info("rimacResponse => {}",rimacResponse);

			if(nonNull(rimacResponse)) {
				LOGGER.info("RBVDR211 rimacResponse cuotasFinanciamiento => {}",rimacResponse.getPayload().getCuotasFinanciamiento());

				Map<String, Object> argumentsRimacContractInformation = this.mapperHelper.getRimacContractInformation(rimacResponse, asoResponse.getData().getId());
				argumentsRimacContractInformation.forEach(
						(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - UpdateContract parameter {} with value: {} *****", key, value));

				int updatedContract = this.pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", argumentsRimacContractInformation,
						RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
						RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue());

				validateInsertion(updatedContract, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);

				String productsCalculateValidityMonths = this.applicationConfigurationService.getDefaultProperty(PROPERTY_ONLY_FIRST_RECEIPT,"");
				String operacionGlossaryDesc = responseQueryGetRequiredFields.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString();

				if (!Arrays.asList(productsCalculateValidityMonths.split(",")).contains(operacionGlossaryDesc)) {
					List<InsuranceCtrReceiptsDAO> otherReceipts = rimacResponse.getPayload().getCuotasFinanciamiento().stream().
							filter(cuota -> cuota.getCuota().compareTo(1L) > 0).map(cuota -> this.generateNextReceipt(asoResponse, cuota)).
							collect(toList());

					Map<String, Object>[] receiptUpdateArguments = this.mapperHelper.createSaveReceiptsArguments(otherReceipts);

					Arrays.stream(receiptUpdateArguments).
							forEach(receiptUpdated -> receiptUpdated.
									forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value)));

					validateMultipleInsertion(this.pisdR012.executeMultipleInsertionOrUpdate("PISD.UPDATE_EXPIRATION_DATE_RECEIPTS",
							receiptUpdateArguments), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);
				}

				policyNumber = rimacResponse.getPayload().getNumeroPoliza();

				String intAccountId = asoResponse.getData().getId().substring(10);

				isItNecessaryUpdateEndorsementRow(isEndorsement, policyNumber, intAccountId);

			}

			responseBody = requestBody;

			this.mapperHelper.mappingOutputFields(responseBody, asoResponse, rimacResponse, emissionDao);
			LOGGER.info("***** Before Response - responseBody => {} *****",responseBody);
			LOGGER.info("***** Before Response - asoResponse => {} *****",asoResponse);
			LOGGER.info("***** Before Response - rimacResponse => {} *****",rimacResponse);
			LOGGER.info("***** Before Response - emissionDao => {} *****",emissionDao);

			CreatedInsrcEventDTO createdInsrcEventDTO = this.mapperHelper.buildCreatedInsuranceEventObject(responseBody);

			Integer httpStatusCode = this.rbvdR201.executePutEventUpsilonService(createdInsrcEventDTO);

			LOGGER.info("***** RBVDR211Impl - Executing createdInsurance event -> Http status code: {} *****", httpStatusCode);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Response: {}", responseBody);
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy END *****");

			return responseBody;
		} catch (BusinessException ex) {
			LOGGER.debug("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Business exception message: {} *****", ex.getMessage());
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return null;
		}

	}
	@Override
	public PolicyDTO executeBusinessLogicEmissionPrePolicyLifeProduct(PolicyDTO requestBody){
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes START *****");
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes ***** Param: {}", requestBody);

		EmisionBO rimacResponse = null;

		PolicyDTO responseBody;
		Boolean isEndorsement;
		String endosatarioRuc;
		Double endosatarioPorcentaje;

		CustomerListASO customerList = null;

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

			Map<String,Object> responseQueryGetProductById = (Map<String,Object>) this.pisdR401.executeGetProductById(ConstantsUtil.Queries.QUERY_SELECT_PRODUCT_BY_PRODUCT_TYPE,
					singletonMap(RBVDProperties.FIELD_INSURANCE_PRODUCT_TYPE.getValue(), requestBody.getProductId()));

			RequiredFieldsEmissionDAO emissionDao = validateResponseQueryGetRequiredFields(responseQueryGetRequiredFields, responseQueryGetPaymentPeriod);

			if(this.applicationConfigurationService.getDefaultProperty(PROPERTY_VALIDATION_RANGE + requestBody.getProductId() + "." + requestBody.getSaleChannelId(), "0").equals("1"))
				validateAmountQuotation(responseQueryGetRequiredFields, requestBody);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes | Required payment evaluation *****");
			evaluateRequiredPayment(requestBody);

			PolicyASO asoResponse = rbvdR201.executePrePolicyEmissionASO(this.mapperHelper.buildAsoRequest(requestBody));

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes | Setting branchId provided by HOST *****");
			String hostBranchId = asoResponse.getData().getBank().getBranch().getId();
			requestBody.getBank().getBranch().setId(hostBranchId);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes | Is it coming from TLMKT? *****");
			evaluateBranchIdValue(requestBody);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes | isDigitalSale validation *****");
			validateDigitalSale(requestBody);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes | Building Rimac request *****");
			String insuranceBusinessName = this.mapperHelper.getInsuranceBusinessNameFromDB(responseQueryGetProductById);
			String branchRequest = requestBody.getBank().getBranch().getId();
			RelatedContractASO relatedContractASO = asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);

			EmisionBO requestEmisionLife = this.mapperHelper.generateRimacRequestLife(
					insuranceBusinessName, requestBody.getSaleChannelId(), asoResponse.getData().getId(), branchRequest	,
					this.getKindOfAccount(relatedContractASO),this.getAccountNumberInDatoParticular(relatedContractASO), asoResponse.getData().getFirstInstallment().getOperationNumber(), requestBody, (String)responseQueryGetRequiredFields.get(RBVDProperties.FIELD_ACCOUNT_ID.getValue()));
			LOGGER.info("***** RBVDR211Impl - generateRimacRequestLife | Emission Life Rimac request : {} *****",requestEmisionLife);

			isEndorsement = ValidationUtil.validateEndorsementInParticipantsRequest(requestBody);

			InsuranceContractDAO contractDao = this.mapperHelper.buildInsuranceContract(requestBody, emissionDao, asoResponse.getData().getId(), isEndorsement);
			LOGGER.info("***** RBVDR211Impl - buildInsuranceContract | Mapping to save contract life : {} *****",contractDao);

			Map<String, Object> argumentsForSaveContract = this.mapperHelper.createSaveContractArguments(contractDao);
			argumentsForSaveContract.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicyLifeEasyYes - SaveContract parameter {} with value: {} *****", key, value));

			int insertedContract = this.pisdR012.executeInsertSingleRow(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue(), argumentsForSaveContract,
					RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(),
					RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(),
					RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue(),
					RBVDProperties.FIELD_CUSTOMER_ID.getValue(), RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue(),
					RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), RBVDProperties.FIELD_USER_AUDIT_ID.getValue());

			validateInsertion(insertedContract, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);

			List<InsuranceCtrReceiptsDAO> receiptsList = this.mapperHelper.buildInsuranceCtrReceipts(asoResponse, requestBody, responseQueryGetRequiredFields);

			Map<String, Object>[] receiptsArguments = this.mapperHelper.createSaveReceiptsArguments(receiptsList);
			Arrays.stream(receiptsArguments).
					forEach(receipt -> receipt.
							forEach((key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicyLifeEasyYes - SaveReceipt parameter {} with value: {} *****", key, value)));

			validateMultipleInsertion(this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSURANCE_CTR_RECEIPTS.getValue(),
					receiptsArguments), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);

			IsrcContractMovDAO contractMovDao = this.mapperHelper.buildIsrcContractMov(asoResponse, requestBody.getCreationUser(), requestBody.getUserAudit());
			Map<String, Object> argumentsForContractMov = this.mapperHelper.createSaveContractMovArguments(contractMovDao);
			argumentsForContractMov.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicyLifeEasyYes | SaveContractMov parameter {} with value: {} *****", key, value));

			int insertedContractMove = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_CONTRACT_MOV.getValue(), argumentsForContractMov);

			validateInsertion(insertedContractMove, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE);

			Map<String, Object> responseQueryRoles = this.pisdR012.executeGetRolesByProductAndModality(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());

			if(!isEmpty((List) responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue()))) {

				List<IsrcContractParticipantDAO> participants = this.mapperHelper.buildIsrcContractParticipants(requestBody, responseQueryRoles, asoResponse.getData().getId());

				Map<String, Object>[] participantsArguments = this.mapperHelper.createSaveParticipantArguments(participants);

				Arrays.stream(participantsArguments).forEach(
						participant -> participant.forEach(
								(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicyLifeEasyYes | SaveParticipants parameter {} with value: {} *****", key, value)));

				validateMultipleInsertion(this.pisdR012.executeMultipleInsertionOrUpdate(RBVDProperties.QUERY_INSERT_INSRNC_CTR_PARTICIPANT.getValue(),
						participantsArguments), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE);
			}

			customerList = this.rbvdR201.executeGetCustomerInformation(requestBody.getHolder().getId());
			try {
				validateQueryCustomerResponse(customerList);
			} catch (BusinessException ex) {
				LOGGER.info("***** PISDR0019Impl - executeListCustomerResponse {} *****", ex.getMessage());
				return null;
			}

			if(isEndorsement){
				ParticipantDTO participantEndorse = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),TAG_ENDORSEE);
				endosatarioRuc = participantEndorse.getIdentityDocument().getNumber();
				endosatarioPorcentaje = participantEndorse.getBenefitPercentage();

				List<EndosatarioBO> endosatarios = new ArrayList<>();
				EndosatarioBO endosatario = new EndosatarioBO(endosatarioRuc,endosatarioPorcentaje.intValue());
				endosatarios.add(endosatario);
				requestEmisionLife.getPayload().setEndosatarios(endosatarios);

				Map<String, Object> argumentsForSaveEndorsement = this.mapperHelper.createSaveEndorsementArguments(contractDao, endosatarioRuc, endosatarioPorcentaje);
				argumentsForSaveEndorsement.forEach(
						(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicyLifeEasyYes - SaveContractEndorsement key {} with value: {} *****", key, value));

				int insertedContractEndorsement = this.pisdR012.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_POLICY_ENDORSEMENT.getValue(), argumentsForSaveEndorsement);
				validateInsertion(insertedContractEndorsement, RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE);
			}

			//llamada a add participants
			if(!applicationConfigurationService.getProperty(ConstantsUtil.PRODUCT_CODES_WITHOUT_THIRD_PARTY_VALIDATION).contains(requestBody.getProductId())) {
				Map<String, Object> dataInsuredFromDB = this.getDataInsuredParticipantFromDB(requestBody, responseQueryGetRequiredFields);
				AgregarTerceroBO requestAddParticipants = this.mapperHelper.generateRequestAddParticipants(insuranceBusinessName,
						requestBody, this.rbvdR201, responseQueryGetRequiredFields, dataInsuredFromDB);
				LOGGER.info("***** RBVDR211Impl - generateRequestAddParticipants | Request add Participants Rimac Service : {} *****", requestAddParticipants);

				AgregarTerceroBO responseAddParticipants = rbvdR201.executeAddParticipantsService(requestAddParticipants, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getProductId(), requestBody.getTraceId());
				LOGGER.info("**** RBVDR211Impl - executeAddParticipantsService | responseAddParticipants => {} ****", responseAddParticipants);

				validateResponseAddParticipantsService(responseAddParticipants);
			}

			//llamada a emision
			if(!applicationConfigurationService.getProperty(ConstantsUtil.PRODUCT_CODES_NOT_EMIT).contains(requestBody.getProductId())) {
				rimacResponse = rbvdR201.executePrePolicyEmissionService(requestEmisionLife, emissionDao.getInsuranceCompanyQuotaId(), requestBody.getTraceId(), requestBody.getProductId());
			}
			LOGGER.info("**** RBVDR211Impl - executePrePolicyEmissionService | rimacResponse => {} ****",rimacResponse);

			if(nonNull(rimacResponse)) {
				LOGGER.info("**** RBVDR211  PolicyEmissionService | rimacResponse cuotasFinanciamiento => {} ****",rimacResponse.getPayload().getCuotasFinanciamiento());

				Map<String, Object> argumentsRimacContractInformation = this.mapperHelper.getRimacContractInformationLifeEasyYes(rimacResponse, asoResponse.getData().getId());
				argumentsRimacContractInformation.forEach(
						(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicyLifeEasyYes - UpdateContract parameter {} with value: {} *****", key, value));

				int updatedContract = this.pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", argumentsRimacContractInformation,
						RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(), RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
						RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(), RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue());

				validateInsertion(updatedContract, RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);

				String policyNumber = rimacResponse.getPayload().getNumeroPoliza();
				String intAccountId = asoResponse.getData().getId().substring(10);

				isItNecessaryUpdateEndorsementRow(isEndorsement, policyNumber, intAccountId);
			}

			responseBody = requestBody;

			this.mapperHelper.mappingOutputFields(responseBody, asoResponse, rimacResponse, emissionDao);
			LOGGER.info("***** Before Response - responseBody => {} *****",responseBody);
			LOGGER.info("***** Before Response - asoResponse => {} *****",asoResponse);
			LOGGER.info("***** Before Response - rimacResponse => {} *****",rimacResponse);
			LOGGER.info("***** Before Response - emissionDao => {} *****",emissionDao);

			CreatedInsrcEventDTO createdInsrcEventDTO = this.mapperHelper.buildCreatedInsuranceEventObject(responseBody);

			QuotationEntity quotationEntity = this.pisdR601.executeFindQuotationByReferenceAndPayrollId(requestBody.getQuotationId());
			LOGGER.info("***** RBVDR211Impl - executeFindQuotationByReferenceAndPayrollId: {} *****", quotationEntity);

			String status = isNull(quotationEntity.getRfqInternalId())  ? "CONTRACTED" : "PAID";
			createdInsrcEventDTO.getCreatedInsurance().setStatus(new StatusDTO());
			createdInsrcEventDTO.getCreatedInsurance().getStatus().setId(status);
			createdInsrcEventDTO.getCreatedInsurance().getStatus().setName(status);
			LOGGER.info("***** RBVDR211Impl - createdInsrcEventDTO: {} *****", createdInsrcEventDTO);


			Integer httpStatusCode = this.rbvdR201.executePutEventUpsilonService(createdInsrcEventDTO);

			LOGGER.info("***** RBVDR211Impl - Executing createdInsurance event -> Http status code: {} *****", httpStatusCode);

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes ***** Response: {}", responseBody);
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes END *****");

			return responseBody;
		} catch (BusinessException ex) {
			LOGGER.debug("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicyLifeEasyYes | Business exception message: {} *****", ex.getMessage());
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return null;
		}
	}

	private Map<String, Object> getDataInsuredParticipantFromDB(PolicyDTO requestBody, Map<String, Object> responseQueryGetRequiredFields) {
		Map<String,Object> argumentForGetDataInsured = new HashMap<>();
		argumentForGetDataInsured.put(RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue(),
				requestBody.getQuotationId());
		argumentForGetDataInsured.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(),
				responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
		argumentForGetDataInsured.put(RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(), requestBody.getProductPlan().getId());
		return this.pisdR350.executeGetASingleRow(ConstantsUtil.Queries.QUERY_GET_INSURED_DATA_LIFE,argumentForGetDataInsured);
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
		String contactCenterCodeValue = this.applicationConfigurationService.getProperty(KEY_CONTACT_CENTER_CODE);
		if( !(picCodeValue.equals(requestBody.getSaleChannelId()) || "TM".equals(requestBody.getSaleChannelId()) || requestBody.getSaleChannelId().equals(contactCenterCodeValue)) ) {

			LOGGER.info("***** It's digital sale!! *****");
			String appGlomo = this.applicationConfigurationService.getProperty(CHANNEL_GLOMO);
			String appContactDetail = this.applicationConfigurationService.getProperty(CHANNEL_CONTACT_DETAIL);
			String[] appSearchContactDetail = Objects.isNull(appContactDetail) ? StringUtils.EMPTY.split(";") : appContactDetail.split(";");

			if(appGlomo.equalsIgnoreCase(requestBody.getAap()) || Arrays.stream(appSearchContactDetail).anyMatch(value -> value.equalsIgnoreCase(requestBody.getAap()))){
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
				.filter(contact -> emailCryptoCode.contains(contact.getContactDetailId())).findFirst();

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
				.filter(contact -> phoneCryptoCode.contains(contact.getContactDetailId())).findFirst();

		ContactDTO secondContact = new ContactDTO();
		secondContact.setContactDetailType("PHONE");
		secondContact.setPhoneNumber(phoneContact.map(ContactDetailsBO::getContact).orElse("No se encontro celular"));
		secondContact.setAddress("");

		requestBody.getHolder().getContactDetails().get(1).setContact(secondContact);
	}

	private String createSecondDataValue(PolicyASO asoResponse) {
		RelatedContractASO relatedContract = asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);
		String kindOfAccount = getKindOfAccount(relatedContract);
		String accountNumber = getAccountNumberInDatoParticular(relatedContract);
		String accountCurrency = asoResponse.getData().getTotalAmount().getExchangeRate().getTargetCurrency();
		return kindOfAccount.concat("||").concat(accountNumber).concat("||").concat(accountCurrency);
	}

	private String getKindOfAccount(RelatedContractASO relatedContract){
		if(relatedContract != null && relatedContract.getProduct() != null && Objects.nonNull(relatedContract.getProduct().getId())){
			return relatedContract.getProduct().getId().equals("CARD") ? "TARJETA" : "CUENTA";
		}else{
			return "";
		}
	}

	private String getAccountNumberInDatoParticular(RelatedContractASO relatedContract){
		if(relatedContract != null && Objects.nonNull(relatedContract.getNumber())){
			int beginIndex = relatedContract.getNumber().length() - 4;
			return "***".concat(relatedContract.getNumber().substring(beginIndex));
		}else{
			return "";
		}
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

	private void isItNecessaryUpdateEndorsementRow(Boolean isEndorsement, String policyNumber, String intAccountId) {
		if(isEndorsement) {
			Map<String, Object> policyIdForEndorsementTable = new HashMap<>();
			policyIdForEndorsementTable.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), policyNumber);
			policyIdForEndorsementTable.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), intAccountId);

			int updateEndorsement = this.pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", policyIdForEndorsementTable,
					RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue());

			validateInsertion(updateEndorsement, RBVDErrors.INSERTION_ERROR_IN_ENDORSEMENT_TABLE);
		}
	}

	private void validateQueryCustomerResponse(CustomerListASO customerList) {
		if (isEmpty(customerList.getData())) {
			throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE);
		}
	}

	private void validateResponseAddParticipantsService(AgregarTerceroBO responseAddParticipants) {
		if (responseAddParticipants == null) {
			throw RBVDValidation.build(RBVDErrors.ERROR_CALL_ADD_PARTICIPANTS_RIMAC_SERVICE);
		}
	}

	private void setOrganization(EmisionBO emision, PolicyDTO requestBody, CustomerListASO customerList){
		PersonaBO persona = emision.getPayload().getAgregarPersona().getPersona().get(0);
		CustomerBO customer = customerList.getData().get(0);
		String tipoDoc = customer.getIdentityDocuments().get(0).getDocumentType().getId();
		String nroDoc = customer.getIdentityDocuments().get(0).getDocumentNumber();
		if (RUC_ID.equalsIgnoreCase(tipoDoc) && StringUtils.startsWith(nroDoc, "20")){

			String xcustomerId = this.rbvdR201.executeCypherService(new CypherASO(requestBody.getHolder().getId(), KEY_CYPHER_CODE));
			if (xcustomerId == null){
				BusinessException except = RBVDValidation.build(RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO);
				except.setMessage("ERROR AL ENCRIPTAR EL IDENTIFICADOR DEL CLIENTE");
				throw except;
			}

			ListBusinessesASO listBussinesses = this.rbvdR201.executeGetListBusinesses(xcustomerId, null);
			if (listBussinesses == null) {
				throw RBVDValidation.build(RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO);
			}
			List<OrganizacionBO> organizaciones = mapOrganizations(listBussinesses.getData().get(0), persona, customer, requestBody);
			emision.getPayload().getAgregarPersona().setOrganizacion(organizaciones);
			emision.getPayload().getAgregarPersona().setPersona(null);
		}
	}

	private List<OrganizacionBO> mapOrganizations(final BusinessASO business, PersonaBO persona, CustomerBO customer,PolicyDTO requestBody) {
		List<OrganizacionBO> organizaciones = new ArrayList<>();

		ContactDetailDTO correoSelect= requestBody.getHolder().getContactDetails().stream().
				filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals("EMAIL")).findFirst().orElse(new ContactDetailDTO());

		ContactDetailDTO celularSelect= requestBody.getHolder().getContactDetails().stream().
				filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals("PHONE")).findFirst().orElse(new ContactDetailDTO());


		String fijo = customer.getContactDetails().stream().filter(
						d -> ContactTypeEnum.PHONE_NUMBER.getValue().equals(d.getContactType().getId())).findFirst().
				orElse(new ContactDetailsBO()).getContact();
		String celular = customer.getContactDetails().stream().filter(
						d -> ContactTypeEnum.MOBILE_NUMBER.getValue().equals(d.getContactType().getId())).findFirst().
				orElse(new ContactDetailsBO()).getContact();
		String correo = customer.getContactDetails().stream().filter(
						d -> ContactTypeEnum.EMAIL.getValue().equals(d.getContactType().getId())).findFirst().
				orElse(new ContactDetailsBO()).getContact();

		correo = StringUtils.isNotBlank(correoSelect.getContact().getAddress()) ?correoSelect.getContact().getAddress() : correo;
		celular = StringUtils.isNotBlank(celularSelect.getContact().getPhoneNumber()) ? celularSelect.getContact().getPhoneNumber() : celular;

		int[] intArray = new int[]{8, 9, 23};
		for (int i = 0; i < intArray.length; i++) {
			OrganizacionBO organizacion = new OrganizacionBO();
			organizacion.setDireccion(persona.getDireccion());
			organizacion.setRol(intArray[i]);
			organizacion.setTipoDocumento("R");
			organizacion.setNroDocumento(business.getBusinessDocuments().get(0).getDocumentNumber());
			organizacion.setRazonSocial(business.getLegalName());
			organizacion.setNombreComercial(business.getLegalName());
			if(Objects.nonNull(business.getFormation())) {
				organizacion.setPaisOrigen(business.getFormation().getCountry().getName());
				organizacion.setFechaConstitucion(business.getFormation().getDate());
			} else {
				organizacion.setPaisOrigen("PERU");
				organizacion.setFechaConstitucion(null);
			}
			organizacion.setFechaInicioActividad(Objects.isNull(business.getAnnualSales()) ? null : business.getAnnualSales().getStartDate());
			organizacion.setTipoOrganizacion(Objects.isNull(business.getBusinessGroup()) ? FIELD_BLANK : business.getBusinessGroup().getId());
			organizacion.setGrupoEconomico(TAG_OTROS);
			organizacion.setCiiu(Objects.isNull(business.getEconomicActivity()) ? FIELD_BLANK : business.getEconomicActivity().getId());
			organizacion.setTelefonoFijo(fijo);
			organizacion.setCelular(celular);
			organizacion.setCorreoElectronico(correo);
			organizacion.setDistrito(persona.getDistrito());
			organizacion.setProvincia(persona.getProvincia());
			organizacion.setDepartamento(persona.getDepartamento());
			organizacion.setUbigeo(persona.getUbigeo());
			organizacion.setNombreVia(persona.getNombreVia());
			organizacion.setTipoVia(persona.getTipoVia());
			organizacion.setNumeroVia(persona.getNumeroVia());
			organizacion.setTipoPersona(this.mapperHelper.getPersonType(organizacion).getCode());
			organizaciones.add(organizacion);
		}
		return organizaciones;
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

	private void validateAmountQuotation(Map<String, Object> quotation, PolicyDTO request ) {
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: inicio *****");

		if(Objects.isNull(quotation.get(FIELD_POLICY_PAYMENT_FREQUENCY_TYPE)) ||
				Objects.isNull(quotation.get(FIELD_PREMIUM_CURRENCY_ID)) ||
				Objects.isNull(quotation.get(FIELD_PREMIUM_AMOUNT)))
			throw RBVDValidation.build(RBVDErrors.QUERY_EMPTY_RESULT);

		String frequency = quotation.get(FIELD_POLICY_PAYMENT_FREQUENCY_TYPE).toString();

		Integer paymentAmount = ((BigDecimal) quotation.get(FIELD_PREMIUM_AMOUNT)).intValue();
		String paymentCurrency = quotation.get(FIELD_PREMIUM_CURRENCY_ID).toString();
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: frecuencia: {} *****", frequency);
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: monto: {} *****", paymentAmount);
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: moneda: {} *****", paymentCurrency);

		Integer rangePaymentAmount = Integer.parseInt(this.applicationConfigurationService.getDefaultProperty(PROPERTY_RANGE_PAYMENT_AMOUNT, "5"));
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: rangePaymentAmount: {} *****", rangePaymentAmount);

		Integer amountQuotationMin = ((100 - rangePaymentAmount)*paymentAmount)/100;
		Integer amountQuotationMax = ((100 + rangePaymentAmount)*paymentAmount)/100;
		Integer amountTotalAmountMin = ((100 - rangePaymentAmount)*paymentAmount*12)/100;
		Integer amountTotalAmountMax = ((100 + rangePaymentAmount)*paymentAmount*12)/100;

		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: prima minimo: {} *****", amountQuotationMin);
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: prima maximo: {} *****", amountQuotationMax);
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: monto minimo: {} *****", amountTotalAmountMin);
		LOGGER.info("***** RBVDR211Impl - validateAmountQuotation: monto maximo: {} *****", amountTotalAmountMax);

		if(frequency.equals("A") && !(paymentCurrency.equals(request.getTotalAmount().getCurrency()) &&
				isValidateRange(request.getTotalAmount().getAmount().intValue(), amountQuotationMin, amountQuotationMax))) {
			throw RBVDValidation.build(RBVDErrors.BAD_REQUEST_CREATEINSURANCE);
		}

		if(frequency.equals("M") && !(paymentCurrency.equals(request.getTotalAmount().getCurrency()) &&
				isValidateRange(request.getTotalAmount().getAmount().intValue(), amountTotalAmountMin, amountTotalAmountMax))) {
			throw RBVDValidation.build(RBVDErrors.BAD_REQUEST_CREATEINSURANCE);
		}

		if(!(isValidateRange(request.getFirstInstallment().getPaymentAmount().getAmount().intValue(), amountQuotationMin, amountQuotationMax) &&
				paymentCurrency.equals(request.getFirstInstallment().getPaymentAmount().getCurrency()))){
			throw RBVDValidation.build(RBVDErrors.BAD_REQUEST_CREATEINSURANCE);
		}

		if(!(isValidateRange(request.getInstallmentPlan().getPaymentAmount().getAmount().intValue(), amountQuotationMin, amountQuotationMax) &&
				paymentCurrency.equals(request.getInstallmentPlan().getPaymentAmount().getCurrency()))){
			throw RBVDValidation.build(RBVDErrors.BAD_REQUEST_CREATEINSURANCE);
		}
	}
	private boolean isValidateRange(Integer value, Integer min, Integer max) {
		final ValueRange range = ValueRange.of(min, max);
		return range.isValidIntValue(value);
	}

	private String encodeB64(byte[] hash) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
	}

}
