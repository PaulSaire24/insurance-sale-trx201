package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.PipelineFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.VehicularProduct;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.facttory.VidaLeyProduct;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.BusinessRBVD66ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CryptoServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

public class FactoryProduct {

    private DependencyBuilder dependencyBuilder;

    public FactoryProduct(IInsuranceContractDAO insuranceContractDAO,
                                IInsurncRelatedContract insurncRelatedContract,
                                IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO,
                                IInsrncContractMovDAO insrncContractMovDAO,
                                IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO,
                                IInsrncRoleModalityDAO insrncRoleModalityDAO,
                                IInsrncQuotationModDAO insrncQuotationModDAO,
                                IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO,
                                IInsurncCtrParticipantDAO insurncCtrParticipantDAO,
                                BasicProductInsuranceProperties basicProductInsuranceProperties,
                                ContractPISD201ServiceInternal contractPISD201ServiceInternal,
                                CryptoServiceInternal cryptoServiceInternal,
                                CustomerRBVD066InternalService customerRBVD066InternalService,
                                IInsuranceProductDAO insuranceProductDAO,
                                BusinessRBVD66ServiceInternal businessRBVD66ServiceInternal,
                                ApplicationConfigurationService applicationConfigurationService,
                                MapperHelper mapperHelper,
                                CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank,
                                PolicyServiceExternal policyServiceExternal) {
        dependencyBuilder = new DependencyBuilder();
        dependencyBuilder.setInsuranceContractDAO(insuranceContractDAO);
        dependencyBuilder.setInsurncRelatedContract(insurncRelatedContract);
        dependencyBuilder.setIEndorsementInsurncCtrDAO(IEndorsementInsurncCtrDAO);
        dependencyBuilder.setInsrncContractMovDAO(insrncContractMovDAO);
        dependencyBuilder.setInsuranceCtrReceiptsDAO(insuranceCtrReceiptsDAO);
        dependencyBuilder.setInsrncRoleModalityDAO(insrncRoleModalityDAO);
        dependencyBuilder.setInsrncQuotationModDAO(insrncQuotationModDAO);
        dependencyBuilder.setInsrncPaymentPeriodDAO(insrncPaymentPeriodDAO);
        dependencyBuilder.setInsurncCtrParticipantDAO(insurncCtrParticipantDAO);
        dependencyBuilder.setBasicProductInsuranceProperties(basicProductInsuranceProperties);
        dependencyBuilder.setContractPISD201ServiceInternal(contractPISD201ServiceInternal);
        dependencyBuilder.setCryptoServiceInternal(cryptoServiceInternal) ;
        dependencyBuilder.setCustomerRBVD066InternalService(customerRBVD066InternalService) ;
        dependencyBuilder.setInsuranceProductDAO(insuranceProductDAO) ;
        dependencyBuilder.setBusinessRBVD66ServiceInternal(businessRBVD66ServiceInternal) ;
        dependencyBuilder.setApplicationConfigurationService(applicationConfigurationService) ;
        dependencyBuilder.setMapperHelper(mapperHelper) ;
        dependencyBuilder.setCrossOperationsBusinessInsuranceContractBank(crossOperationsBusinessInsuranceContractBank) ;
        dependencyBuilder.setPolicyServiceExternal(policyServiceExternal);
    }

    public PipelineFactory getInstance(String productId){
        switch (productId) {
            case "830":
                return new VehicularProduct(dependencyBuilder);
            case "842":
                return new VidaLeyProduct(dependencyBuilder);
            default:
                return null;
        }
    }
}
