package com.bbva.rbvd.lib.r201.impl;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.lib.r201.impl.util.AsoExceptionHandler;
import com.bbva.rbvd.lib.r201.impl.util.JsonHelper;
import com.bbva.rbvd.lib.r201.impl.util.RimacExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import javax.ws.rs.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RBVDR201Impl extends RBVDR201Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR201Impl.class);

	private static final String ID_API_INSURANCES_CREATE_INSURANCE_ASO = "emission.aso";

	private static final String ID_API_PRE_POLICY_EMISSION_RIMAC = "emission.rimac";
	private static final String URI_EMISSION = "/vehicular/V1/cotizacion/-/emitir";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String X_AMZ_DATE_HEADER = "X-Amz-Date";
	private static final String X_API_KEY_HEADER = "x-api-key";
	private static final String TRACE_ID_HEADER = "traceId";

	@Override
	public PolicyASO executePrePolicyEmissionASO(DataASO requestBody) {
		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO START *****");

		String jsonString = getRequestBodyInStringFormat(requestBody);

		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO Request body: {}", jsonString);

		PolicyASO responseBody = null;

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeaders());

		try {
			responseBody = this.internalApiConnector.postForObject(ID_API_INSURANCES_CREATE_INSURANCE_ASO, entity, PolicyASO.class);
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO ***** Response: {}", getRequestBodyInStringFormat(responseBody));
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO END *****");
		} catch (RestClientException ex) {
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionASO ***** Exception: {}", ex.getMessage());
			AsoExceptionHandler exceptionHandler = new AsoExceptionHandler();
			exceptionHandler.handler(ex);
		}
		return responseBody;
	}

	@Override
	public EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId) {
		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService START *****");

		String jsonString = getRequestBodyInStringFormat(requestBody);

		LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService ***** Param: {}", jsonString);

		EmisionBO responseBody = null;

		String uri = URI_EMISSION.replace("-", quotationId);

		SignatureAWS signature = this.pisdR014.executeSignatureConstruction(jsonString, HttpMethod.POST,
				uri, null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeadersAWS(signature));

		Map<String, String> uriParam = new HashMap<>();
		uriParam.put("ideCotizacion", quotationId);

		try {
			responseBody = this.externalApiConnector.postForObject(ID_API_PRE_POLICY_EMISSION_RIMAC, entity,
					EmisionBO.class, uriParam);
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService ***** Response: {}", getRequestBodyInStringFormat(responseBody));
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService END *****");
		} catch (RestClientException ex) {
			LOGGER.info("***** RBVDR201Impl - executePrePolicyEmissionService ***** Exception: {}", ex.getMessage());
			RimacExceptionHandler exceptionHandler = new RimacExceptionHandler();
			exceptionHandler.handler(ex);
		}
		return responseBody;
	}

	private HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set("BCS-Operation-Tracer", "1");
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

	private String getRequestBodyInStringFormat(Object requestBody) {
		return JsonHelper.getInstance().toJsonString(requestBody);
	}

}
