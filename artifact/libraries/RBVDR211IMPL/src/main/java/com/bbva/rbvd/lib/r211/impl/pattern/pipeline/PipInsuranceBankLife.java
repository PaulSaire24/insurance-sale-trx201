package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.factory.PipelineInsuranceContractFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.*;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Pipeline;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.BusinessRBVD66ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CryptoServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

public class PipInsuranceBankLife implements PipelineInsuranceContractFactory {

    private final DependencyBuilder dependencyBuilder;



    public PipInsuranceBankLife(IInsuranceContractDAO insuranceContractDAO,
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

    @Override
    public Pipeline configureRoyalContract() {
        return new Pipeline()
                .addStep(new ValidateConditionsContractLifeStep(dependencyBuilder))
                .addStep(new FetchRequiredDataLifeStep(dependencyBuilder))
                .addStep(new GenerateContractLifeStep(dependencyBuilder))
                .addStep(new SaveInsuranceDataLifeStep(dependencyBuilder))
                .addStep(new GeneratePaymentStep(dependencyBuilder));
    }






}
