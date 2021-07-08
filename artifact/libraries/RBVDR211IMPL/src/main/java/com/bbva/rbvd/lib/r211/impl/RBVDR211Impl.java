package com.bbva.rbvd.lib.r211.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceProductDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

	@Override
	public PolicyDTO executeBusinessLogicEmissionPrePolicy(PolicyDTO requestBody) {

		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy START *****");
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Param: {}", requestBody);

		PolicyDTO responseBody = null;

		try {
			Map<String, Object> responseQueryInsuranceProduct = this.pisdR012.
					executeInsuranceProduct(this.mapperHelper.insuranceProductFilterCreation(requestBody.getProductId()));

			InsuranceProductDAO insuranceProductDao = validateResponseQueryInsuranceProduct(responseQueryInsuranceProduct);

			Map<String, Object> responseQueryInsuranceProductModality = this.pisdR012.
					executeInsuranceProductModality(this.mapperHelper.productModalityFiltersCreation(insuranceProductDao.getInsuranceProductId(), requestBody.getProductPlan().getId()));

			Map<String, Object> productModality = validateResponseQueryInsuranceProductModality(responseQueryInsuranceProductModality);

			Map<String, Object> responseContainingRimacQuotation = this.pisdR012.executeRegisterAdditionalCompanyQuotaId(requestBody.getQuotationId());

			String rimacQuotationId = (String) responseContainingRimacQuotation.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue());

			PolicyASO asoResponse = rbvdR201.executePrePolicyEmissionASO(this.mapperHelper.buildAsoRequest(requestBody));

			//TENGO QUE VALIDAR QUE ASO ME HAYA RESPONDIDO CORRECTAMENTE!

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Building Rimac request *****");
			String secondParticularDataValue = createSecondDataValue(asoResponse);
			EmisionBO rimacRequest = this.mapperHelper.buildRequestBodyRimac(requestBody.getInspection(), secondParticularDataValue, requestBody.getSaleChannelId());

			EmisionBO rimacResponse = rbvdR201.executePrePolicyEmissionService(rimacRequest, rimacQuotationId, requestBody.getTraceId());

			InsuranceContractDAO contractDao = this.mapperHelper.buildInsuranceContract(rimacResponse, requestBody,
					insuranceProductDao.getInsuranceProductId(), asoResponse.getData().getId());
			contractDao.setValidityMonthsNumber((BigDecimal) productModality.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue()));
			Map<String, Object> argumentsForSaveContract = this.mapperHelper.createSaveContractArguments(contractDao);
			argumentsForSaveContract.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveContract parameter {} with value: {} *****", key, value));

			validateInsertion(this.pisdR012.executeSaveContract(argumentsForSaveContract), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);

			InsuranceCtrReceiptsDAO receiptDao = this.mapperHelper.buildInsuranceCtrReceipt(asoResponse, rimacResponse, requestBody);
			Map<String, Object> argumentsForSaveReceipt = this.mapperHelper.createSaveReceiptsArguments(receiptDao);
			argumentsForSaveReceipt.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy - SaveReceipt parameter {} with value: {} *****", key, value));

			validateInsertion(this.pisdR012.executeSaveFirstReceipt(argumentsForSaveReceipt), RBVDErrors.INSERTION_ERROR_IN_RECEIPTS_TABLE);

			IsrcContractMovDAO contractMovDao = this.mapperHelper.buildIsrcContractMov(asoResponse, requestBody.getCreationUser(), requestBody.getUserAudit());
			Map<String, Object> argumentsForContractMov = this.mapperHelper.createSaveContractMovArguments(contractMovDao);
			argumentsForContractMov.forEach(
					(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy | SaveContractMov parameter {} with value: {} *****", key, value));

			validateInsertion(this.pisdR012.executeSaveContractMove(argumentsForContractMov), RBVDErrors.INSERTION_ERROR_IN_CONTRACT_MOV_TABLE);

			Map<String, Object> responseQueryRoles = this.pisdR012.executeGetRolesByProductAndModality(insuranceProductDao.getInsuranceProductId(), requestBody.getProductId());

			if(!isEmpty((List) responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue()))) {
				List<IsrcContractParticipantDAO> participants = this.mapperHelper.buildIsrcContractParticipants(requestBody, responseQueryRoles, asoResponse.getData().getId());
				Map<String, Object>[] arguments = new HashMap[participants.size()];
				for(int i = 0; i < participants.size(); i++) {
					arguments[i] = this.mapperHelper.createSaveParticipantArguments(participants.get(i));
				}
				Arrays.stream(arguments).forEach(
						argumentsMap -> argumentsMap.forEach(
								(key, value) -> LOGGER.info("***** executeBusinessLogicEmissionPrePolicy | SaveParticipants parameter {} with value: {} *****", key, value)));
				validateInsertionParticipants(this.pisdR012.executeSaveParticipants(arguments), RBVDErrors.INSERTION_ERROR_IN_PARTICIPANT_TABLE);
			}

			responseBody = new PolicyDTO();
			responseBody.setId(asoResponse.getData().getId());
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Response: {}", responseBody);
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy END *****");

			return responseBody;
		} catch (BusinessException ex) {
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Business exception message: {} *****", ex.getMessage());
			this.addAdvice(ex.getAdviceCode());
			return null;
		}

	}

	private InsuranceProductDAO validateResponseQueryInsuranceProduct(Map<String, Object> responseQueryInsuranceProduct) {
		if(isEmpty(responseQueryInsuranceProduct)) {
			throw RBVDValidation.build(RBVDErrors.INCORRECT_PRODUCT_ID);
		}
		InsuranceProductDAO insuranceProduct = new InsuranceProductDAO();
		insuranceProduct.setInsuranceProductId((BigDecimal) responseQueryInsuranceProduct.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
		return insuranceProduct;
	}

	private Map<String, Object> validateResponseQueryInsuranceProductModality(Map<String, Object> responseQueryInsuranceProductModality) {
		List<Map<String, Object>> response = (List<Map<String, Object>>) responseQueryInsuranceProductModality.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
		if(isEmpty(response)) {
			throw RBVDValidation.build(RBVDErrors.INCORRECT_PLAN_ID);
		}
		return response.get(0);
	}

	private void validateInsertion(int insertedRows, RBVDErrors error) {
		if(insertedRows != 1) {
			throw RBVDValidation.build(error);
		}
	}

	private void validateInsertionParticipants(int[] executeSaveParticipants, RBVDErrors error) {
		if(Objects.isNull(executeSaveParticipants) || executeSaveParticipants.length == 0) {
			throw RBVDValidation.build(error);
		}
	}

	private String createSecondDataValue(PolicyASO asoResponse) {
		RelatedContractASO relatedContract = asoResponse.getData().getPaymentMethod().getRelatedContracts().get(0);
		String kindOfAccount = relatedContract.getProduct().getId().equals("CARD") ? "TARJETA" : "CUENTA";
		int beginIndex = relatedContract.getNumber().length() - 4;
		String accountNumber = "***".concat(relatedContract.getNumber().substring(beginIndex));
		String accountCurrency = Objects.nonNull(asoResponse.getData().getFirstInstallment().getExchangeRate()) ? "PEN" : "USD";
		return kindOfAccount.concat("||").concat(accountNumber).concat("||").concat(accountCurrency);
	}


}
