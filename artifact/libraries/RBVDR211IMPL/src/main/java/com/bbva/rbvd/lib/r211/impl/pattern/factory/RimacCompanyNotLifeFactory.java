package com.bbva.rbvd.lib.r211.impl.pattern.factory;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.Insurance;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.NotLifeDefaultRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.VehicleInsuranceRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.impl.RimacCompany;
import com.bbva.rbvd.lib.r211.impl.pattern.factory.interfaces.InsuranceCompanyFactory;
import com.bbva.rbvd.lib.r211.impl.service.api.PolicyRBVDR201ServiceExternal;

import static com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH;

public class RimacCompanyNotLifeFactory implements InsuranceCompanyFactory {

    private PolicyRBVDR201ServiceExternal policyRBVDR201ServiceExternal;
    private ApplicationConfigurationService applicationConfigurationService;

    /**
     * This method is used to create an insurance policy based on the provided product details.
     * It first checks if the product type is vehicle insurance. If it is, it creates an instance of RimacCompany with the external policy service
     * and decorates this instance with VehicleInsuranceRimacDecorator.
     * If the product type is not vehicle insurance, it creates an instance of RimacCompany with the external policy service
     * and decorates this instance with NotLifeDefaultRimacDecorator.
     * Finally, it calls the createPolicyOfCompany method on the decorated instance to create the policy.
     *
     * @param processPrePolicyDTO This is the data transfer object containing the details of the product for which the policy is to be created.
     * @return ResponseLibrary<ProcessPrePolicyDTO> This returns the response of the policy creation process wrapped in a ResponseLibrary object.
     */
    @Override
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
