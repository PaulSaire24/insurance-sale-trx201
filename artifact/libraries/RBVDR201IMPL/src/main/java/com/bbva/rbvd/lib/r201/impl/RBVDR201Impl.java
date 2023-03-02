package com.bbva.rbvd.lib.r201.impl;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;

import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import com.bbva.rbvd.lib.r201.impl.util.AsoExceptionHandler;
import com.bbva.rbvd.lib.r201.impl.util.JsonHelper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;

public class RBVDR201Impl extends RBVDR201Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR201Impl.class);
	private static final String CUSTOMER_ID = "customerId";
	private static final String GET_CONTACT_DETAILS_SERVICE_ID = "glomoContactDetails";
	private static final String ID_API_INSURANCES_CREATE_INSURANCE_ASO = "emission.aso";
	private static final String ID_PUT_EVENT_UPSILON_SERVICE = "createdInsurancePutEvent";
	private static final String ID_API_CYPHER = "executecypher";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String X_AMZ_DATE_HEADER = "X-Amz-Date";
	private static final String X_API_KEY_HEADER = "x-api-key";
	private static final String TRACE_ID_HEADER = "traceId";

	@Override
	public GetContactDetailsASO executeGetContactDetailsService(String customerId) {
		LOGGER.info("***** PISDR007Impl - executeGetContactDetailsService START *****");

		Map<String, String> pathParam = new HashMap<>();
		pathParam.put(CUSTOMER_ID, customerId);

		try {
			GetContactDetailsASO response = this.internalApiConnector.getForObject(GET_CONTACT_DETAILS_SERVICE_ID, GetContactDetailsASO.class, pathParam);
			LOGGER.info("***** PISDR007Impl - executeGetContactDetailsService END *****");
			return response;
		} catch(RestClientException ex) {
			LOGGER.debug("***** PISDR007Impl - executeGetContactDetailsService ***** Something went wrong: {}", ex.getMessage());
			return null;
		}
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
		} catch (RestClientException ex) {
			LOGGER.debug("***** RBVDR201Impl - executePrePolicyEmissionASO ***** Exception: {}", ex.getMessage());
			AsoExceptionHandler exceptionHandler = new AsoExceptionHandler();
			exceptionHandler.handler(ex);
		}
		return responseBody;
	}

	@Override
	public EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId, String productId) {
		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService START *****");

		String jsonString = getRequestBodyAsJsonFormat(requestBody);

		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService ***** Param: {}", jsonString);

		EmisionBO responseBody = null;

		SignatureAWS signature = this.pisdR014.executeSignatureConstruction(jsonString, HttpMethod.POST.toString(),
				this.rimacUrlForker.generateUriForSignatureAWS(productId, quotationId), null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeadersAWS(signature));

		Map<String, String> uriParam = new HashMap<>();
		uriParam.put("ideCotizacion", quotationId);

		try {
			responseBody = this.externalApiConnector.postForObject(this.rimacUrlForker.generatePropertyKeyName(productId), entity,
					EmisionBO.class, uriParam);
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService ***** Response: {}", getRequestBodyAsJsonFormat(responseBody));
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService END *****");
		} catch (RestClientException ex) {
			this.addAdviceWithDescription("RBVD10094932",ex.getMessage());
			this.addAdviceWithDescription("RBVD10094943","Error al devolver informacion de Rimac en Alta de Poliza");
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService ***** Exception: {}", ex.getMessage());
		}
		return responseBody;
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
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put(CUSTOMER_ID, customerId);
		if (StringUtils.isNotBlank(expands)) pathParams.put("expand", expands);
		ListBusinessesASO responseList = null;
		BusinessASO output = null;
		String responJsons = "";
		try {
			responseList = this.internalApiConnector.getForObject(RBVDProperties.ID_API_LIST_BUSINESSES.getValue()
					, ListBusinessesASO.class, pathParams);
			if (responseList != null && responseList.getData() != null && !responseList.getData().isEmpty()) {
				output = responseList.getData().get(0);
				LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses ***** output: {}", output);
				responJsons = getRequestBodyAsJsonFormat(responseList.getData().get(0));
			}
		} catch (RestClientException e) {
			LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses ***** Exception: {}", e.getMessage());
		}
		LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses output ***** Response: {}", responJsons);
		LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses END getSuccess ***** ");
		return responseList;
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
		}

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

	private HttpHeaders createHttpHeadersAWS(SignatureAWS signature) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set(AUTHORIZATION_HEADER, signature.getAuthorization());
		headers.set(X_AMZ_DATE_HEADER, signature.getxAmzDate());
		headers.set(X_API_KEY_HEADER, signature.getxApiKey());
		headers.set(TRACE_ID_HEADER, signature.getTraceId());
		return headers;
	}

}
