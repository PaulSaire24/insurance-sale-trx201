package com.bbva.rbvd.lib.r211.impl.pattern.factory;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.Insurance;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.LifeDefaultRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.impl.RimacCompany;
import com.bbva.rbvd.lib.r211.impl.pattern.factory.interfaces.InsuranceCompanyFactory;
import com.bbva.rbvd.lib.r211.impl.service.api.PolicyRBVDR201ServiceExternal;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

public class RimacCompanyLifeFactory implements InsuranceCompanyFactory {

    private PolicyRBVDR201ServiceExternal policyRBVDR201ServiceExternal;
    private MapperHelper mapperHelper;

    /**
     * This method is used to create an insurance policy based on the provided product details.
     * It first creates an instance of RimacCompany with the external policy service.
     * Then it decorates this instance with LifeDefaultRimacDecorator, which might add or override some functionalities.
     * Finally, it calls the createPolicyOfCompany method on the decorated instance to create the policy.
     *
     * @param processPrePolicyDTO This is the data transfer object containing the details of the product for which the policy is to be created.
     * @return ResponseLibrary<ProcessPrePolicyDTO> This returns the response of the policy creation process wrapped in a ResponseLibrary object.
     */
    @Override
    public ResponseLibrary<ProcessPrePolicyDTO> createInsuranceByProduct(ProcessPrePolicyDTO processPrePolicyDTO){
        Insurance rimacCompany = new RimacCompany(policyRBVDR201ServiceExternal);
        rimacCompany = new LifeDefaultRimacDecorator(mapperHelper,rimacCompany);
        return rimacCompany.createPolicyOfCompany(processPrePolicyDTO);
    }

    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
    }

    public void setPolicyRBVDR201ServiceExternal(PolicyRBVDR201ServiceExternal policyRBVDR201ServiceExternal) {
        this.policyRBVDR201ServiceExternal = policyRBVDR201ServiceExternal;
    }
}
