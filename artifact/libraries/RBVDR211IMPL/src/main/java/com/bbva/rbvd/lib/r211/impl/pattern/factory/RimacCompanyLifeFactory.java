package com.bbva.rbvd.lib.r211.impl.pattern.factory;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.Insurance;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.LifeDefaultRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.NotLifeDefaultRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.VehicleInsuranceRimacDecorator;
import com.bbva.rbvd.lib.r211.impl.pattern.decorator.impl.RimacCompany;
import com.bbva.rbvd.lib.r211.impl.service.api.PolicyRBVDR201ServiceExternal;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

import static com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH;

public class RimacCompanyLifeFactory {

    private PolicyRBVDR201ServiceExternal policyRBVDR201ServiceExternal;
    private MapperHelper mapperHelper;

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
