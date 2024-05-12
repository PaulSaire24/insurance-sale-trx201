package com.bbva.rbvd.lib.r211.impl.pattern.factory;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.Insurance;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.NotLifeDefaultRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.VehicleInsuranceRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.impl.RimacCompany;
import com.bbva.rbvd.lib.r211.impl.service.api.PolicyRBVDR201ServiceExternal;

import static com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH;

public class RimacCompanyNotLifeFactory {

    private PolicyRBVDR201ServiceExternal policyRBVDR201ServiceExternal;
    private ApplicationConfigurationService applicationConfigurationService;

    public ResponseLibrary<ProcessPrePolicyDTO> createInsuranceByProduct(ProcessPrePolicyDTO processPrePolicyDTO){
        if(INSURANCE_PRODUCT_TYPE_VEH.getValue().equalsIgnoreCase(processPrePolicyDTO.getPolicy().getProductId())){
            Insurance rimacCompany = new RimacCompany(policyRBVDR201ServiceExternal);
            rimacCompany = new VehicleInsuranceRimacDecorator(rimacCompany);
            return rimacCompany.createPolicyOfCompany(processPrePolicyDTO);
        }
        Insurance rimacCompany = new RimacCompany(policyRBVDR201ServiceExternal);
        rimacCompany = new NotLifeDefaultRimacDecorator(rimacCompany,applicationConfigurationService);
        return rimacCompany.createPolicyOfCompany(processPrePolicyDTO);
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setPolicyRBVDR201ServiceExternal(PolicyRBVDR201ServiceExternal policyRBVDR201ServiceExternal) {
        this.policyRBVDR201ServiceExternal = policyRBVDR201ServiceExternal;
    }
}
