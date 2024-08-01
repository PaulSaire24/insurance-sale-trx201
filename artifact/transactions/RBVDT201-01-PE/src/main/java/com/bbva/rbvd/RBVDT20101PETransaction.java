package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.pattern.factory.InsuranceFactory;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.PropertiesKey.KEY_OBTAIN_PRODUCT_LIFE;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.FlowProcess;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Status;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.RBVDR211;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Collections;
import java.util.Calendar;
import java.util.Objects;
import java.util.Arrays;
import java.util.TimeZone;


public class RBVDT20101PETransaction extends AbstractRBVDT20101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT20101PETransaction.class);

	/**
	 * This method is the main execution point for the insurance emission process.
	 * It starts by logging the start of the process and retrieving the necessary services and data.
	 * It then creates a list of life product codes and logs them.
	 * An InsuranceFactory is created and used to create an insurance policy.
	 * The response from the insurance creation is logged and then processed.
	 * If the flow process is a legacy process, the method checks if the body of the response is not null.
	 * If it is not null, it sets the policy DTO and the HTTP response code to 200.
	 * If it is null, it sets the severity to ENR.
	 * If the flow process is not a legacy process, it checks the status of the process.
	 * If the status is OK, it sets the policy DTO and the HTTP response code to 200.
	 * If the status is EWR, it sets the severity to EWR.
	 * If the status is neither OK nor EWR, it sets the severity to ENR.
	 */
	@Override
	public void execute() {
		LOGGER.info(" :: InsuranceEmission :: [ START ]");

		RBVDR211 rbvdR211 = this.getServiceLibrary(RBVDR211.class);
		PolicyDTO requestBody = this.getPolicyDTO();

		LOGGER.info(" :: InsuranceEmission :: [ Body :: {} ]",requestBody);

		List<String> listOfLifeProductCodes = Objects.nonNull(this.getProperty(KEY_OBTAIN_PRODUCT_LIFE)) ? Arrays.asList(this.getProperty(KEY_OBTAIN_PRODUCT_LIFE).split(";")) : Collections.emptyList();

		LOGGER.info(" :: InsuranceEmission :: [ listOfLifeProductCodes :: {} ]",listOfLifeProductCodes);


		ResponseLibrary<PolicyDTO> responseBody = rbvdR211.executeEmissionPolicy(requestBody);
		LOGGER.info(" :: InsuranceEmission :: [ ResponseBody :: {} ]",responseBody.getBody());
		if(FlowProcess.LEGACY_FLOW_PROCESS.equalsIgnoreCase(responseBody.getFlowProcess())){
			if(Objects.nonNull(responseBody.getBody())) {
				this.setPolicyDTO(responseBody.getBody());
				this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
			} else {
				this.setSeverity(Severity.ENR);
			}
		}else{
			if(Status.OK.equalsIgnoreCase(responseBody.getStatusProcess())){
				this.setPolicyDTO(responseBody.getBody());
				this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
			}else if(Status.EWR.equalsIgnoreCase(responseBody.getStatusProcess())){
				this.setSeverity(Severity.EWR);
			} else {
				this.setSeverity(Severity.ENR);
			}
		}
	}


	private void setPolicyDTO(PolicyDTO responseBody) {
		this.setId(responseBody.getId());
		this.setPolicynumber(responseBody.getPolicyNumber());
		this.setQuotationid(responseBody.getQuotationId());
		this.setProductid(responseBody.getProductId());
		this.setProductdescription(responseBody.getProductDescription());
		this.setProductplan(responseBody.getProductPlan());
		this.setPaymentmethod(responseBody.getPaymentMethod());

		Calendar operationDate = Calendar.getInstance();
		operationDate.setTimeZone(TimeZone.getTimeZone("America/Lima"));
		operationDate.setTime(responseBody.getOperationDate());

		this.setOperationdate(operationDate);
		this.setValidityperiod(responseBody.getValidityPeriod());
		this.setLinks(responseBody.getLinks());
		this.setTotalamount(responseBody.getTotalAmount());
		this.setTotalamountwithouttax(responseBody.getTotalAmountWithoutTax());
		this.setInsuredamount(responseBody.getInsuredAmount());
		this.setIsdatatreatment(responseBody.getIsDataTreatment());
		this.setDeliveries(responseBody.getDeliveries());
		this.setHolder(responseBody.getHolder());
		this.setRelatedcontracts(responseBody.getRelatedContracts());
		this.setInstallmentplan(responseBody.getInstallmentPlan());
		this.setHasacceptedcontract(responseBody.getHasAcceptedContract());
		this.setInspection(responseBody.getInspection());
		this.setFirstinstallment(responseBody.getFirstInstallment());
		this.setParticipants(responseBody.getParticipants());
		this.setBusinessagent(responseBody.getBusinessAgent());
		this.setPromoter(responseBody.getPromoter());
		this.setInsurancecompany(responseBody.getInsuranceCompany());
		this.setExternalquotationid(responseBody.getExternalQuotationId());
		this.setExternalpolicynumber(responseBody.getExternalPolicyNumber());
		this.setStatus(responseBody.getStatus());
		this.setBank(responseBody.getBank());
		this.setIdentityverificationcode(responseBody.getIdentityVerificationCode());
		this.setCouponcode(responseBody.getCouponCode());
		this.setPolicyduration(responseBody.getPolicyDuration());
		this.setSalesupplier(responseBody.getSaleSupplier());
	}


	private PolicyDTO getPolicyDTO() {
		PolicyDTO requestBody = new PolicyDTO();
		requestBody.setId(this.getPreformalizationid());
		requestBody.setQuotationId(this.getQuotationid());
		requestBody.setProductId(this.getProductid());
		requestBody.setProductPlan(this.getProductplan());
		requestBody.setPaymentMethod(this.getPaymentmethod());
		requestBody.setValidityPeriod(this.getValidityperiod());
		requestBody.setTotalAmount(this.getTotalamount());
		requestBody.setInsuredAmount(this.getInsuredamount());
		requestBody.setIsDataTreatment(this.getIsdatatreatment());
		requestBody.setDeliveries(this.getDeliveries());
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
		requestBody.setCouponCode(this.getCouponcode());
		requestBody.setSaleSupplier(this.getSalesupplier());
		requestBody.setTraceId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.REQUESTID));
		requestBody.setSaleChannelId((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CHANNELCODE));
		requestBody.setCreationUser((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		requestBody.setUserAudit((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE));
		requestBody.setAap((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.AAP));
		requestBody.setIpv4((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.IPADDRESS));
		requestBody.setEnvironmentCode((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.ENVIRONCODE));
		requestBody.setProductCode((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.PRODUCTCODE));
		requestBody.setHeaderOperationDate((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.OPERATIONDATE));
		requestBody.setHeaderOperationTime((String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.OPERATIONTIME));
		return requestBody;
	}

}