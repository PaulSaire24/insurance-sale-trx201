package com.bbva.rbvd.lib.r211.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.ContactoInspeccionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PayloadEmisionBO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.commons.PolicyInspectionDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RBVDR211Impl extends RBVDR211Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

	private static final String ELECTRONIC_DELIVERY_VALUE = "S";
	private static final String EMAIL_VALUE = "EMAIL";
	private static final String PHONE_NUMBER_VALUE = "PHONE";

	@Override
	public PolicyDTO executeBusinessLogicEmissionPrePolicy(PolicyDTO requestBody) {

		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy START *****");
		LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Param: {}", requestBody);

		PolicyDTO responseBody = null;

		try {
			PolicyASO responseASO = rbvdR201.executePrePolicyEmissionASO(requestBody);

			EmisionBO rimacRequest = createRequestBodyRimac(requestBody.getInspection());
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Rimac request body: {}", rimacRequest);

			String rimacQuotation = null;

			EmisionBO rimacResponse = null;

			if(Objects.nonNull(responseASO)) {
				rimacQuotation = responseASO.getData().getExternalQuotationId();
				rimacResponse = rbvdR201.executePrePolicyEmissionService(rimacRequest, rimacQuotation, requestBody.getTraceId());
			}

			if(Objects.nonNull(rimacResponse)) {
				responseBody = new PolicyDTO();
				responseBody.setId(rimacResponse.getPayload().getNumeroPoliza());
			}

			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy ***** Response: {}", responseBody);
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy END *****");

			return responseBody;
		} catch (BusinessException ex) {
			LOGGER.info("***** RBVDR211Impl - executeBusinessLogicEmissionPrePolicy | Business exception message: {} *****", ex.getMessage());
			return null;
		}

	}

	private EmisionBO createRequestBodyRimac(PolicyInspectionDTO inspection) {
		EmisionBO rimacRequest = new EmisionBO();

		PayloadEmisionBO payload = new PayloadEmisionBO();

		ContactoInspeccionBO contactoInspeccion = new ContactoInspeccionBO();
		contactoInspeccion.setNombre(inspection.getFullName());

		ContactDetailDTO contactEmail = inspection.getContactDetails().stream().
				filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(EMAIL_VALUE)).findFirst().orElse(null);

		ContactDetailDTO contactPhone = inspection.getContactDetails().stream().
				filter(contactDetail -> contactDetail.getContact().getContactDetailType().equals(PHONE_NUMBER_VALUE)).findFirst().orElse(null);

		if(Objects.nonNull(contactEmail)) {
			contactoInspeccion.setCorreo(contactEmail.getContact().getAddress());
		}
		if(Objects.nonNull(contactPhone)) {
			contactoInspeccion.setTelefono(contactPhone.getContact().getPhoneNumber());
		}

		payload.setContactoInspeccion(contactoInspeccion);
		payload.setEnvioElectronico(ELECTRONIC_DELIVERY_VALUE);

		rimacRequest.setPayload(payload);
		return rimacRequest;
	}

}
