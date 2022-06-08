package com.bbva.rbvd.lib.r201;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.BusinessASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.email.CreateEmailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;

public interface RBVDR201 {

	PolicyASO executePrePolicyEmissionASO(DataASO requestBody);
	EmisionBO executePrePolicyEmissionService(EmisionBO requestBody, String quotationId, String traceId, String productId);
	Integer executeCreateEmail(CreateEmailASO requestBody);
	CustomerListASO executeGetCustomerInformation(String customerId);
	Integer executeGifoleEmisionService(GifoleInsuranceRequestASO requestBody);
	ListBusinessesASO executeGetListBusinesses(String customerId, String expands);
}
