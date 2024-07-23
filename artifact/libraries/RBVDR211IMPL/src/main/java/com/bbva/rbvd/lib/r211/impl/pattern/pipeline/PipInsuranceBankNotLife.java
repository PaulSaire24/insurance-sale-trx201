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
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

public class PipInsuranceBankNotLife implements PipelineInsuranceContractFactory {

    private final DependencyBuilder dependencyBuilder;



    public PipInsuranceBankNotLife(IInsuranceContractDAO insuranceContractDAO,
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
                                   CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank) {
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
    }

    /**
     * This method is used to configure the pipeline for the Royal Contract.
     * It creates a new pipeline and adds various steps to it.
     * Each step represents a specific operation in the process of creating a contract.
     *
     * @return Pipeline - This returns the configured pipeline with all the steps added.
     */
    @Override
    public Pipeline configureRoyalContract() {
        return new Pipeline()
                .addStep(new ValidateConditionsContractNotLifeStep(dependencyBuilder))
                .addStep(new FetchRequiredDataStep(dependencyBuilder))
                .addStep(new GenerateContractNotLifeStep(dependencyBuilder))
                .addStep(new SaveInsuranceDataNotLifeStep(dependencyBuilder))
                .addStep(new SaveContractParticipantStep(dependencyBuilder))
                .addStep(new GeneratePaymentStep(dependencyBuilder));
    }






}
