package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;


import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceProductDAO;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncQuotationModDAO;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.ERROR_POLICY_ALREADY_EXISTS;

public class ValidarConditionsPaso implements PasoPipeline{

    private IInsuranceContractDAO insuranceContractDAO;
    private IInsrncQuotationModDAO insrncQuotationModDAO;
    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private ContractPISD201ServiceInternal contractPISD201ServiceInternal;
    private CustomerRBVD066InternalService customerRBVD066InternalService;
    private IInsuranceProductDAO insuranceProductDAO;
    private ApplicationConfigurationService applicationConfigurationService;
    private MapperHelper mapperHelper;
    private CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank;
    private PolicyServiceExternal policyServiceExternal;
    protected final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    @Override
    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, PasoPipeline siguiente) {

        PolicyDTO requestBody =  contexto.getBody().getPolicy();
        Boolean existContractWithQuotation = this.insuranceContractDAO.findExistenceInsuranceContract(requestBody.getQuotationId());
        CustomerListASO customerList = null;
        String insuranceBusinessName = null;
        if(Boolean.TRUE.equals(existContractWithQuotation)){
            String messageErrorContractWithQuotation = String.format(ERROR_POLICY_ALREADY_EXISTS.getMessage(),requestBody.getQuotationId());
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(),messageErrorContractWithQuotation);
            throw new BusinessException(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(), ERROR_POLICY_ALREADY_EXISTS.isRollback(), ERROR_POLICY_ALREADY_EXISTS.getMessage());
        }

        Boolean enableValidationQuotationAmount = this.basicProductInsuranceProperties.enableValidationQuotationAmountByProductIdAndChannelId(requestBody.getProductId(),requestBody.getSaleChannelId());
        Map<String, Object> quotationData = this.insrncQuotationModDAO.findQuotationByQuotationId(requestBody.getQuotationId());
        crossOperationsBusinessInsuranceContractBank.validateQuotationData(quotationData, requestBody.getQuotationId());
        this.crossOperationsBusinessInsuranceContractBank.validateQuotationAmount(enableValidationQuotationAmount,quotationData,requestBody);

        String productCodesWithoutPartyValidation = this.basicProductInsuranceProperties.obtainProductCodesWithoutPartyValidation();
        if(!productCodesWithoutPartyValidation.contains(requestBody.getProductId())){
            customerList = customerRBVD066InternalService.findCustomerInformationByCustomerId(requestBody.getHolder().getId());
            crossOperationsBusinessInsuranceContractBank.validateCustomerList(customerList);
            Map<String, Object> dataInsuredQuotationFromDB = this.insrncQuotationModDAO.getDataInsuredParticipantFromDB(requestBody,quotationData);
            Map<String,Object> responseQueryGetProductById = insuranceProductDAO.findByProductId(requestBody.getProductId());
            crossOperationsBusinessInsuranceContractBank.validateProductData(responseQueryGetProductById, requestBody.getProductId());
            insuranceBusinessName = this.mapperHelper.getInsuranceBusinessNameFromDB(responseQueryGetProductById);
            AgregarTerceroBO requestAddParticipants = this.mapperHelper.generateRequestAddParticipantsV2(insuranceBusinessName, requestBody, customerList, quotationData, dataInsuredQuotationFromDB,customerRBVD066InternalService);
            this.crossOperationsBusinessInsuranceContractBank.validateFilledAddress(requestAddParticipants);
            String insuranceQuotationCompany = (String) quotationData.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue());
            AgregarTerceroBO responseValidateParticipants = this.policyServiceExternal.executeAddParticipantsService(requestAddParticipants,insuranceQuotationCompany, requestBody.getProductId(), requestBody.getTraceId());
            this.crossOperationsBusinessInsuranceContractBank.validateResponseAddParticipantsService(responseValidateParticipants);
        }
        contexto.getBody().setCustomerList(customerList);
        siguiente.ejecutar(contexto, siguiente);
    }

    public IInsuranceContractDAO getInsuranceContractDAO() {
        return insuranceContractDAO;
    }

    public void setInsuranceContractDAO(IInsuranceContractDAO insuranceContractDAO) {
        this.insuranceContractDAO = insuranceContractDAO;
    }

    public IInsrncQuotationModDAO getInsrncQuotationModDAO() {
        return insrncQuotationModDAO;
    }

    public void setInsrncQuotationModDAO(IInsrncQuotationModDAO insrncQuotationModDAO) {
        this.insrncQuotationModDAO = insrncQuotationModDAO;
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

    public PolicyServiceExternal getPolicyServiceExternal() {
        return policyServiceExternal;
    }

    public void setPolicyServiceExternal(PolicyServiceExternal policyServiceExternal) {
        this.policyServiceExternal = policyServiceExternal;
    }

    public ArchitectureAPXUtils getArchitectureAPXUtils() {
        return architectureAPXUtils;
    }
}
