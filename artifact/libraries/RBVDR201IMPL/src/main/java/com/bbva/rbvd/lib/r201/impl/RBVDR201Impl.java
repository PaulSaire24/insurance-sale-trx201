package com.bbva.rbvd.lib.r201.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;

import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.cicsconnection.icr2.ICR2Request;
import com.bbva.rbvd.dto.cicsconnection.icr2.ICR2Response;
import com.bbva.rbvd.dto.cicsconnection.icr3.ICR3Request;
import com.bbva.rbvd.dto.cicsconnection.icr3.ICR3Response;
import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;

import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

import com.bbva.rbvd.lib.r201.properties.EmissionServiceProperties;
import com.bbva.rbvd.lib.r201.transform.bean.ICR2Bean;
import com.bbva.rbvd.lib.r201.transform.bean.ICR3Bean;
import com.bbva.rbvd.lib.r201.util.AsoExceptionHandler;
import com.bbva.rbvd.lib.r201.util.FunctionsUtils;
import com.bbva.rbvd.lib.r201.util.JsonHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;

public class RBVDR201Impl extends RBVDR201Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR201Impl.class);
	private static final String CUSTOMER_ID = "customerId";
	private static final String ID_API_INSURANCES_CREATE_INSURANCE_ASO = "emission.aso";
	private static final String ID_PUT_EVENT_UPSILON_SERVICE = "createdInsurancePutEvent";
	private static final String ID_API_CYPHER = "executecypher";
	private static final String ERROR_SERVICE_ASO_ADVICE_CODE = "RBVD00000175";
	private static final String ERROR_SERVICE_ASO_MESSAGE = "Actualmente, estamos experimentando dificultades para establecer conexión con el servicio %s, utilizado en el servicio ASO '%s', debido a un error detectado: '%s'. Por favor, inténtalo de nuevo más tarde. Lamentamos los inconvenientes.";
	private static final String ERROR_SERVICE_TIMEOUT_ASO_ADVICE_CODE = "RBVD00000174";
	private static final String ERROR_SERVICE_TIMEOUT_ASO_MESSAGE = "Actualmente, el servicio %s no está disponible debido a un tiempo de espera en la conexión, al ser utilizado en el contexto del servicio ASO %s. Te recomendamos intentar acceder a este servicio en unos minutos, gracias.";
	private static final String CREATE_INSURANCE_SERVICE = "createInsurance";
	private static final String ICR2_COMMUNICATION_DESC = "con comunicación a la ICR2";
	private static final String PROPERTIES_TIMEOUT_EXCEPTION = "error.message.timeout";
	private static final String PROPERTIES_REST_EXCEPTION = "error.message.restException";

	/**
	 * Instance of BasicProductInsuranceProperties.
	 * This property is used to access the configuration properties related to basic insurance products.
	 */
	private EmissionServiceProperties emissionServiceProperties;

	@Override
	public GetContactDetailsASO executeGetContactDetailsService(String customerId) {
		LOGGER.info("***** RBVDR201Impl - executeGetContactDetailsService START *****");
		return this.rbvdR046.executeGetContactDetailsService(customerId);
	}

	@Override
	public PolicyASO executePrePolicyEmissionASO(DataASO requestBody) {
		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO START *****");

		String jsonString = getRequestBodyAsJsonFormat(requestBody);

		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO Request body: {}", jsonString);

		PolicyASO responseBody = null;

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeaders(true));

		try {
			responseBody = this.internalApiConnector.postForObject(ID_API_INSURANCES_CREATE_INSURANCE_ASO, entity, PolicyASO.class);
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO ***** Response: {}", getRequestBodyAsJsonFormat(responseBody));
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO END *****");
		} catch (HttpStatusCodeException sce){
			String errorMessage = AsoExceptionHandler.getErrorCode(sce.getResponseBodyAsString());
			throw new BusinessException(RBVDErrors.BAD_REQUEST_CREATEINSURANCE.getAdviceCode(), false, errorMessage);
		} catch (RestClientException ex) {
			LOGGER.debug("***** RBVDR201Impl - executePrePolicyEmissionASO ***** Exception: {}", ex.getMessage());
			String messageRestException = this.applicationConfigurationService.getDefaultProperty(PROPERTIES_REST_EXCEPTION, ERROR_SERVICE_ASO_MESSAGE);
			throw new BusinessException(ERROR_SERVICE_ASO_ADVICE_CODE, false,
					String.format(messageRestException,CREATE_INSURANCE_SERVICE, ICR2_COMMUNICATION_DESC, ex.getMessage()));
		} catch (TimeoutException toex) {
			LOGGER.debug("***** RBVDR201Impl - executePrePolicyEmissionASO ***** TimeoutException: {}", toex.getMessage());
			String messageTimeout = this.applicationConfigurationService.getDefaultProperty(PROPERTIES_TIMEOUT_EXCEPTION, ERROR_SERVICE_TIMEOUT_ASO_MESSAGE);
			throw new BusinessException(ERROR_SERVICE_TIMEOUT_ASO_ADVICE_CODE, false,
					String.format(messageTimeout,CREATE_INSURANCE_SERVICE, ICR2_COMMUNICATION_DESC));
		}
		return responseBody;
	}

	@Override
	public EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId, String productId) {
		LOGGER.info(" :: executePrePolicyEmissionService :: [ START ]");
		LOGGER.info(" :: executePrePolicyEmissionService :: [ EmisionBO :: {} ]",requestBody);
		LOGGER.info(" :: executePrePolicyEmissionService :: [ QuotationId :: {} , TraceId :: {}  ]",quotationId,traceId);
		LOGGER.info(" :: executePrePolicyEmissionService :: [ ProductId :: {} ]",productId);
		return this.pisdR352.executePrePolicyEmissionService(requestBody, quotationId, traceId ,productId);
	}

	@Override
	public CustomerListASO executeGetCustomerInformation(String customerId) {
		LOGGER.info("***** RBVDR201Impl - executeGetCustomerInformation START ***** customerId: {} ", customerId);


		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put(CUSTOMER_ID, customerId);

		try {
			CustomerListASO responseList = this.internalApiConnector.getForObject(PISDProperties.ID_API_CUSTOMER_INFORMATION.getValue(),CustomerListASO.class,pathParams);
			LOGGER.info("***** RBVDR201Impl - executeGetCustomerInformation END ***** ");
			return responseList;
		} catch(RestClientException e) {
			LOGGER.info("***** RBVDR201Impl - executeGetCustomerInformation ***** Exception: {}", e.getMessage());
			this.addAdvice(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE.getAdviceCode());
			return null;
		}
	}

	@Override
	public ListBusinessesASO executeGetListBusinesses(String customerId, String expands) {
		LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses START customerId: {} ***** ", customerId);
		LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses START expands: {} ***** ", expands);
		return this.rbvdR066.executeGetListBusinesses(customerId, expands);
	}

	@Override
	public String executeCypherService(CypherASO input) {
		LOGGER.info("***** RBVDR201Impl - executeCypherService START *****");
		LOGGER.info("***** RBVDR201Impl - executeCypherService ***** Param: {}", input);

		String output = null;

		HttpEntity<CypherASO> entity = new HttpEntity<>(input, createHttpHeaders(false));

		try {
			CypherASO out = this.internalApiConnector.postForObject(ID_API_CYPHER, entity,
					CypherASO.class);
			if (out != null && out.getData() != null) {
				output = out.getData().getDocument();
			}
		} catch(RestClientException e) {
			LOGGER.info("***** RBVDR201Impl - executeCypherService ***** Exception: {}", e.getMessage());
		}

		LOGGER.info("***** RBVDR201Impl - executeCypherService ***** Response: {}", output);
		LOGGER.info("***** RBVDR201Impl - executeCypherService END *****");
		return output;
	}

	@Override
	public Integer executePutEventUpsilonService(CreatedInsrcEventDTO createdInsuranceEvent) {
		LOGGER.info("***** RBVDR201Impl - executePutEventUpsilonService START *****");

		String jsonString = getRequestBodyAsJsonFormat(createdInsuranceEvent);

		LOGGER.info("***** RBVDR201Impl - executePutEventUpsilonService Request body: {}", jsonString);

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeaders(false));

		try {
			ResponseEntity<Void> responseEntity = this.internalApiConnectorImpersonation.
					exchange(ID_PUT_EVENT_UPSILON_SERVICE, HttpMethod.POST, entity, Void.class);

			Integer httpStatusCode = responseEntity.getStatusCode().value();

			LOGGER.info("***** RBVDR201Impl - executePutEventUpsilonService END *****");
			return httpStatusCode;
		} catch(RestClientException ex) {
			LOGGER.info("***** RBVDR201Impl - executePutEventUpsilonService ***** Exception: {}", ex.getMessage());
			return 0;
		}catch (TimeoutException toex) {
			LOGGER.debug("***** RBVDR201Impl - executePutEventUpsilonService ***** TimeoutException: {}", toex.getMessage());
			return 0;
		}

	}

	@Override
	public AgregarTerceroBO executeAddParticipantsService(AgregarTerceroBO requestBody, String quotationId, String productId, String traceId) {
		LOGGER.info(" :: executePrePolicyEmissionService :: [ START ]");
		LOGGER.info(" :: executePrePolicyEmissionService :: [ EmisionBO :: {} ]",requestBody);
		LOGGER.info(" :: executePrePolicyEmissionService :: [ QuotationId :: {} , TraceId :: {}  ]",quotationId,traceId);
		LOGGER.info(" :: executePrePolicyEmissionService :: [ ProductId :: {} ]",productId);
		return this.pisdR352.executeAddParticipantsService(requestBody, quotationId, productId, traceId);
	}

	/**
	 * This method is responsible for executing the pre-policy emission process in the CICS system.
	 * It takes a DataASO object and an INDICATOR_PRE_FORMALIZED object as input, maps them to an ICR2Request object, and then calls the executePreFormalizationContract method.
	 * If there are any host advice codes in the ICR2Response, it adds them to the advice list and returns a ResponseLibrary object with a status of ENR.
	 * If there are no host advice codes, it maps the ICR2Response to a new PolicyASO object and returns a ResponseLibrary object with a status of OK and the new PolicyASO object as the body.
	 *
	 * @param requestBody The DataASO object that contains the request body details.
	 * @param indicatorPreFormalized The INDICATOR_PRE_FORMALIZED object that indicates whether the policy is pre-formalized.
	 * @return ResponseLibrary<PolicyASO> A ResponseLibrary object that contains the status of the process and the new PolicyASO object if the process was successful.
	 */
	@Override
	public ResponseLibrary<PolicyASO> executePrePolicyEmissionCics(DataASO requestBody, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized) {
		LOGGER.info(" :: executePrePolicyEmissionCics :: [ START ]");
		LOGGER.info(" :: executePrePolicyEmissionCics :: [ DataASO :: {} ]",requestBody);
		if(emissionServiceProperties.enabledMockPrePolicyEmissionCics()){
			return ResponseLibrary.ResponseServiceBuilder
					.an()
					.statusIndicatorProcess(RBVDInternalConstants.Status.OK)
					.body(JsonHelper.getInstance().createMockPolicyASO());
		}
		ICR2Request icr2Request = ICR2Bean.mapIn(requestBody,indicatorPreFormalized);
		ICR2Response icr2Response = this.rbvdR047.executePreFormalizationContract(icr2Request);
		if(CollectionUtils.isEmpty(icr2Response.getHostAdviceCode())){
			PolicyASO policyASO = new PolicyASO();
			policyASO.setData(ICR2Bean.mapOut(icr2Response.getIcmrys2()));
			return ResponseLibrary.ResponseServiceBuilder.an()
					.statusIndicatorProcess(RBVDInternalConstants.Status.OK)
					.body(policyASO);
		}
		this.addAdviceWithDescription(RBVDInternalErrors.ERROR_GENERIC_HOST.getAdviceCode(), FunctionsUtils.getAdviceListOfString(icr2Response.getHostAdviceCode()));
		return ResponseLibrary.ResponseServiceBuilder.an()
				.statusIndicatorProcess(RBVDInternalConstants.Status.ENR)
				.build();
	}

	/**
	 * This method is used to execute the insurance payment and formalization process.
	 * It takes a PolicyASO object as input, maps it to an ICR3Request object, and then calls the executeFormalizationContractAndPayment method.
	 * If there are any host advice codes in the ICR3Response, it adds them to the advice list and returns a ResponseLibrary object with a status of EWR.
	 * If there are no host advice codes, it maps the ICR3Response to a new PolicyASO object and returns a ResponseLibrary object with a status of OK and the new PolicyASO object as the body.
	 *
	 * @param policyASO The PolicyASO object that contains the policy details.
	 * @return ResponseLibrary<PolicyASO> A ResponseLibrary object that contains the status of the process and the new PolicyASO object if the process was successful.
	 */
	@Override
	public ResponseLibrary<PolicyASO> executeInsurancePaymentAndFormalization(PolicyASO policyASO, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized) {
		LOGGER.info(" :: executeInsurancePaymentAndFormalization :: [ START ]");
		LOGGER.info(" :: executeInsurancePaymentAndFormalization :: [ Data :: {} ]",policyASO);
		ICR3Request icr3Request = ICR3Bean.mapIn(policyASO, (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE),indicatorPreFormalized);
		ICR3Response icr3Response = this.rbvdR602.executePreFormalizationInsurance(icr3Request);
		LOGGER.info(" :: executeInsurancePaymentAndFormalization :: [ ICR3Response :: {} ]",icr3Response);
		if (!CollectionUtils.isEmpty(icr3Response.getHostAdviceCode())) {
			this.addAdviceWithDescription(RBVDInternalErrors.ERROR_GENERIC_HOST.getAdviceCode(), FunctionsUtils.getAdviceListOfString(icr3Response.getHostAdviceCode()));
			return ResponseLibrary.ResponseServiceBuilder.an()
					.statusIndicatorProcess(RBVDInternalConstants.Status.EWR)
					.build();
		}
		PolicyASO policy = new PolicyASO();
		policy.setData(ICR2Bean.mapOut(icr3Response.getIcmrys2()));
		return ResponseLibrary.ResponseServiceBuilder.an()
				.statusIndicatorProcess(RBVDInternalConstants.Status.OK)
				.body(policyASO);
	}

	private String getRequestBodyAsJsonFormat(Object requestBody) {
		return JsonHelper.getInstance().toJsonString(requestBody);
	}

	private HttpHeaders createHttpHeaders(boolean isBcsHeaderRequired) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		if(isBcsHeaderRequired) {
			headers.set("BCS-Operation-Tracer", "1");
		}
		return headers;
	}

	public void setEmissionServiceProperties(EmissionServiceProperties emissionServiceProperties) {
		this.emissionServiceProperties = emissionServiceProperties;
	}
}
