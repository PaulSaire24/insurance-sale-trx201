package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.lib.r211.RBVDR211;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

public class RBVDT20101PETransaction extends AbstractRBVDT20101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT20101PETransaction.class);

	@Override
	public void execute() {
		LOGGER.info("RBVDT20101PETransaction - START");

		String traceId = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.REQUESTID);
		LOGGER.info("Cabecera traceId: {}", traceId);

		RBVDR211 rbvdR211 = this.getServiceLibrary(RBVDR211.class);

		PolicyDTO requestBody = new PolicyDTO();
		requestBody.setQuotationId(this.getQuotationid());
		requestBody.setProductId(this.getProductid());
		requestBody.setProductPlan(this.getProductplan());
		requestBody.setPaymentMethod(this.getPaymentmethod());
		requestBody.setValidityPeriod(this.getValidityperiod());
		requestBody.setTotalAmount(this.getTotalamount());
		requestBody.setInsuredAmount(this.getInsuredamount());
		requestBody.setIsDataTreatment(this.getIsdatatreatment());
		requestBody.setHolder(this.getHolder());
		requestBody.setRelatedContracts(this.getRelatedcontracts());
		requestBody.setInstallmentPlan(this.getInstallmentplan());
		requestBody.setHasAcceptedContract(this.getHasacceptedcontract());
		requestBody.setInspection(this.getInspection());
		requestBody.setFirstInstallment(this.getFirstinstallment());
		requestBody.setParticipants(this.getParticipants());
		requestBody.setBusinessAgent(this.getBusinessagent());
		requestBody.setPromoter(this.getPromoter());
		requestBody.setBank(this.getBank());
		requestBody.setIdentityVerificationCode(this.getIdentityverificationcode());
		requestBody.setInsuranceCompany(this.getInsurancecompany());

		requestBody.setTraceId(traceId);

		PolicyDTO responseBody = rbvdR211.executeBusinessLogicEmissionPrePolicy(requestBody);

		if(Objects.nonNull(responseBody)) {
			this.setId(responseBody.getId());

			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
		} else {
			this.setSeverity(Severity.ENR);
		}

	}

}
