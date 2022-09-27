package com.bbva.rbvd.lib.r201.impl;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.insrncsale.aso.cypher.CypherASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;

import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import com.bbva.rbvd.lib.r201.impl.util.AsoExceptionHandler;
import com.bbva.rbvd.lib.r201.impl.util.JsonHelper;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import javax.ws.rs.HttpMethod;

import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.Map;

public class RBVDR201Impl extends RBVDR201Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR201Impl.class);

	private static final String ID_API_INSURANCES_CREATE_INSURANCE_ASO = "emission.aso";
	private static final String ID_API_CYPHER = "executecypher";

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String X_AMZ_DATE_HEADER = "X-Amz-Date";
	private static final String X_API_KEY_HEADER = "x-api-key";
	private static final String TRACE_ID_HEADER = "traceId";

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

		SignatureAWS signature = this.pisdR014.executeSignatureConstruction(jsonString, HttpMethod.POST,
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
			LOGGER.debug("***** RBVDR201Impl - executePrePolicyEmissionService ***** Exception: {}", ex.getMessage());
		}
		return responseBody;
	}

	@Override
	public Integer executeCreateEmail(CreateEmailASO requestBody) {
		LOGGER.info("***** RBVDR201Impl - executeCreateEmail START *****");

		String jsonFormat = getRequestBodyAsJsonFormat(requestBody);

		LOGGER.info("***** RBVDR201Impl - executeCreateEmail ***** Request body: {}", jsonFormat);

		Integer httpStatus = null;

		try {
			HttpEntity<String> entity = new HttpEntity<>(jsonFormat, createHttpHeaders(false));
			ResponseEntity<String> response = this.internalApiConnector.exchange(PISDProperties.ID_API_NOTIFICATIONS_GATEWAY_CREATE_EMAIL_SERVICE.getValue(),
					org.springframework.http.HttpMethod.POST, entity, String.class);
			httpStatus = response.getStatusCode().value();
			LOGGER.info("***** RBVDR201Impl - executeCreateEmail ***** Http code response: {}", httpStatus);
		} catch(RestClientException ex) {
			LOGGER.debug("***** RBVDR201Impl - executeCreateEmail ***** Exception: {}", ex.getMessage());
			LOGGER.debug("***** RBVDR201Impl - executeCreateEmail | No se envió el correo con el detalle de la poliza al cliente *****");
		}

		LOGGER.info("***** RBVDR201Impl - executeCreateEmail END *****");
		return httpStatus;
	}

	@Override
	public CustomerListASO executeGetCustomerInformation(String customerId) {
		LOGGER.info("***** RBVDR201Impl - executeGetCustomerInformation START ***** customerId: {} ", customerId);


		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("customerId", customerId);

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
	public Integer executeGifoleEmisionService(GifoleInsuranceRequestASO requestBody) {
		LOGGER.info("***** RBVDR201Impl - executeGifoleEmisionService START *****");

		String jsonString = getRequestBodyAsJsonFormat(requestBody);

		LOGGER.info("***** RBVDR201Impl - executeGifoleEmisionService ***** Request body: {}", jsonString);

		ResponseEntity<Void> response = null;
		Integer httpStatus = null;

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeaders(false));

		try {
			response = this.internalApiConnector.exchange(PISDProperties.ID_API_GIFOLE_ROYAL_INSURANCE_REQUEST_SERVICE.getValue(),
					org.springframework.http.HttpMethod.POST, entity, Void.class);
			httpStatus = response.getStatusCode().value();
			LOGGER.info("***** RBVDR201Impl - executeGifoleEmisionService ***** Http code response: {}", httpStatus);
		} catch(RestClientException ex) {
			LOGGER.debug("***** RBVDR201Impl - executeGifoleEmisionService ***** Exception: {}", ex.getMessage());
			this.addAdvice(PISDErrors.ERROR_CONNECTION_GIFOLE_ROYAL_INSURANCE_REQUEST_ASO_SERVICE.getAdviceCode());
		}

		LOGGER.info("***** RBVDR201Impl - executeGifoleEmisionService END *****");
		return httpStatus;
	}

	@Override
	public ListBusinessesASO executeGetListBusinesses(String customerId, String expands) {
		LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses START customerId: {} ***** ", customerId);
		LOGGER.info("***** RBVDR201Impl - executeGetListBusinesses START expands: {} ***** ", expands);
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("customerId", customerId);
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
