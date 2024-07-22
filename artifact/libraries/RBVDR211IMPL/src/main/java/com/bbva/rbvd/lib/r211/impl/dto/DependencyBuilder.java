package com.bbva.rbvd.lib.r211.impl.dto;


import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.lib.r211.impl.pattern.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.BusinessRBVD66ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CryptoServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

public class DependencyBuilder {
    private IInsuranceContractDAO insuranceContractDAO;
    private IInsurncRelatedContract insurncRelatedContract;
    private IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO;
    private IInsrncContractMovDAO insrncContractMovDAO;
    private IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO;
    private IInsrncRoleModalityDAO insrncRoleModalityDAO;
    private IInsrncQuotationModDAO insrncQuotationModDAO;
    private IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO;
    private IInsurncCtrParticipantDAO insurncCtrParticipantDAO;
    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private ContractPISD201ServiceInternal contractPISD201ServiceInternal;
    private CryptoServiceInternal cryptoServiceInternal;
    private CustomerRBVD066InternalService customerRBVD066InternalService;
    private IInsuranceProductDAO insuranceProductDAO;
    private BusinessRBVD66ServiceInternal businessRBVD66ServiceInternal;
    private ApplicationConfigurationService applicationConfigurationService;
    private MapperHelper mapperHelper;
    private CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank;
    private PolicyServiceExternal policyServiceExternal;

    public PolicyServiceExternal getPolicyServiceExternal() {
        return policyServiceExternal;
    }

    public void setPolicyServiceExternal(PolicyServiceExternal policyServiceExternal) {
        this.policyServiceExternal = policyServiceExternal;
    }

    public IInsuranceContractDAO getInsuranceContractDAO() {
        return insuranceContractDAO;
    }

    public void setInsuranceContractDAO(IInsuranceContractDAO insuranceContractDAO) {
        this.insuranceContractDAO = insuranceContractDAO;
    }

    public IInsurncRelatedContract getInsurncRelatedContract() {
        return insurncRelatedContract;
    }

    public void setInsurncRelatedContract(IInsurncRelatedContract insurncRelatedContract) {
        this.insurncRelatedContract = insurncRelatedContract;
    }

    public com.bbva.rbvd.lib.r211.impl.service.IEndorsementInsurncCtrDAO getIEndorsementInsurncCtrDAO() {
        return IEndorsementInsurncCtrDAO;
    }

    public void setIEndorsementInsurncCtrDAO(com.bbva.rbvd.lib.r211.impl.service.IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO) {
        this.IEndorsementInsurncCtrDAO = IEndorsementInsurncCtrDAO;
    }

    public IInsrncContractMovDAO getInsrncContractMovDAO() {
        return insrncContractMovDAO;
    }

    public void setInsrncContractMovDAO(IInsrncContractMovDAO insrncContractMovDAO) {
        this.insrncContractMovDAO = insrncContractMovDAO;
    }

    public IInsuranceCtrReceiptsDAO getInsuranceCtrReceiptsDAO() {
        return insuranceCtrReceiptsDAO;
    }

    public void setInsuranceCtrReceiptsDAO(IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO) {
        this.insuranceCtrReceiptsDAO = insuranceCtrReceiptsDAO;
    }

    public IInsrncRoleModalityDAO getInsrncRoleModalityDAO() {
        return insrncRoleModalityDAO;
    }

    public void setInsrncRoleModalityDAO(IInsrncRoleModalityDAO insrncRoleModalityDAO) {
        this.insrncRoleModalityDAO = insrncRoleModalityDAO;
    }

    public IInsrncQuotationModDAO getInsrncQuotationModDAO() {
        return insrncQuotationModDAO;
    }

    public void setInsrncQuotationModDAO(IInsrncQuotationModDAO insrncQuotationModDAO) {
        this.insrncQuotationModDAO = insrncQuotationModDAO;
    }

    public IInsrncPaymentPeriodDAO getInsrncPaymentPeriodDAO() {
        return insrncPaymentPeriodDAO;
    }

    public void setInsrncPaymentPeriodDAO(IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO) {
        this.insrncPaymentPeriodDAO = insrncPaymentPeriodDAO;
    }

    public IInsurncCtrParticipantDAO getInsurncCtrParticipantDAO() {
        return insurncCtrParticipantDAO;
    }

    public void setInsurncCtrParticipantDAO(IInsurncCtrParticipantDAO insurncCtrParticipantDAO) {
        this.insurncCtrParticipantDAO = insurncCtrParticipantDAO;
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

    public CryptoServiceInternal getCryptoServiceInternal() {
        return cryptoServiceInternal;
    }

    public void setCryptoServiceInternal(CryptoServiceInternal cryptoServiceInternal) {
        this.cryptoServiceInternal = cryptoServiceInternal;
    }

    public CustomerRBVD066InternalService getCustomerRBVD066InternalService() {
        return customerRBVD066InternalService;
    }

    public void setCustomerRBVD066InternalService(CustomerRBVD066InternalService customerRBVD066InternalService) {
        this.customerRBVD066InternalService = customerRBVD066InternalService;
    }

    public IInsuranceProductDAO getInsuranceProductDAO() {
        return insuranceProductDAO;
    }

    public void setInsuranceProductDAO(IInsuranceProductDAO insuranceProductDAO) {
        this.insuranceProductDAO = insuranceProductDAO;
    }

    public BusinessRBVD66ServiceInternal getBusinessRBVD66ServiceInternal() {
        return businessRBVD66ServiceInternal;
    }

    public void setBusinessRBVD66ServiceInternal(BusinessRBVD66ServiceInternal businessRBVD66ServiceInternal) {
        this.businessRBVD66ServiceInternal = businessRBVD66ServiceInternal;
    }

    public ApplicationConfigurationService getApplicationConfigurationService() {
        return applicationConfigurationService;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public MapperHelper getMapperHelper() {
        return mapperHelper;
    }

    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
    }

    public CrossOperationsBusinessInsuranceContractBank getCrossOperationsBusinessInsuranceContractBank() {
        return crossOperationsBusinessInsuranceContractBank;
    }

    public void setCrossOperationsBusinessInsuranceContractBank(CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank) {
        this.crossOperationsBusinessInsuranceContractBank = crossOperationsBusinessInsuranceContractBank;
    }
}
