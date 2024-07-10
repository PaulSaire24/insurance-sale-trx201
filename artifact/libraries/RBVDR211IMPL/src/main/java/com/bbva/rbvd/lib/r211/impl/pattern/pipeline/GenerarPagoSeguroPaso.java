package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.ERROR_RESPONSE_SERVICE_ICR2;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class GenerarPagoSeguroPaso implements PasoPipeline {
    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private ContractPISD201ServiceInternal contractPISD201ServiceInternal;

    @Override
    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, PasoPipeline siguiente) {
        if(this.basicProductInsuranceProperties.enabledPaymentICR2()){
            DataASO asoRequest = contexto.getBody().getDataASO();
            asoRequest.setId(contexto.getBody().getAsoResponse().getData().getId());
            RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_ACCOUNTING_ICR2;
            if(contexto.getBody().getPolicy().getFirstInstallment().getIsPaymentRequired()){
                indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_COLLECT_ACCOUNTING_ICR2;
            }
            ResponseLibrary<PolicyASO> responseGeneratePayment = this.contractPISD201ServiceInternal.generateFormalizationContractAndPayment(asoRequest,indicatorPreFormalized);
            if(!RBVDInternalConstants.Status.OK.equalsIgnoreCase(responseGeneratePayment.getStatusProcess())){
                throw buildValidation(ERROR_RESPONSE_SERVICE_ICR2);
            }
            contexto.getBody().setAsoResponse(responseGeneratePayment.getBody());
        }
        siguiente.ejecutar(contexto, siguiente);
    }

    public BasicProductInsuranceProperties getBasicProductInsuranceProperties() {
        return basicProductInsuranceProperties;
    }

    public void setBasicProductInsuranceProperties(BasicProductInsuranceProperties basicProductInsuranceProperties) {
        this.basicProductInsuranceProperties = basicProductInsuranceProperties;
    }

    public ContractPISD201ServiceInternal getContractPISD201ServiceInternal() {
        return contractPISD201ServiceInternal;
    }

    public void setContractPISD201ServiceInternal(ContractPISD201ServiceInternal contractPISD201ServiceInternal) {
        this.contractPISD201ServiceInternal = contractPISD201ServiceInternal;
    }
}
