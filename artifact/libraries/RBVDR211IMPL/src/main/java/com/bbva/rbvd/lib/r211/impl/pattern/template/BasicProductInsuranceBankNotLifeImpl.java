package com.bbva.rbvd.lib.r211.impl.pattern.template;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Period;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.template.InsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.BusinessRBVD66ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CryptoServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceContractBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.IsrcContractMovBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.EmissionBean;
import com.bbva.rbvd.lib.r211.impl.transfor.list.RelatedContractsList;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceContractMap;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.*;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class BasicProductInsuranceBankNotLifeImpl extends InsuranceContractBank {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicProductInsuranceBankNotLifeImpl.class);

    protected IInsuranceContractDAO insuranceContractDAO;
    protected IInsurncRelatedContract insurncRelatedContract;
    protected com.bbva.rbvd.lib.r211.impl.service.IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO;
    protected IInsrncContractMovDAO insrncContractMovDAO;
    protected IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO;
    protected IInsrncRoleModalityDAO insrncRoleModalityDAO;
    protected IInsrncQuotationModDAO insrncQuotationModDAO;
    protected IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO;
    protected IInsurncCtrParticipantDAO insurncCtrParticipantDAO;
    protected BasicProductInsuranceProperties basicProductInsuranceProperties;
    protected ContractPISD201ServiceInternal contractPISD201ServiceInternal;
    protected CryptoServiceInternal cryptoServiceInternal;
    protected CustomerRBVD066InternalService customerRBVD066InternalService;
    protected IInsuranceProductDAO insuranceProductDAO;
    protected BusinessRBVD66ServiceInternal businessRBVD66ServiceInternal;

    protected ApplicationConfigurationService applicationConfigurationService;
    protected MapperHelper mapperHelper;
    protected CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank;
    protected PolicyServiceExternal policyServiceExternal;


    @Override
    protected void executeValidateConditions(PolicyDTO requestBody) {
        Boolean existContractWithQuotation = this.insuranceContractDAO.findExistenceInsuranceContract(requestBody.getQuotationId());
        if(Boolean.TRUE.equals(existContractWithQuotation)){
            String messageErrorContractWithQuotation = String.format(ERROR_POLICY_ALREADY_EXISTS.getMessage(),requestBody.getQuotationId());
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(),messageErrorContractWithQuotation);
            throw new BusinessException(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(), ERROR_POLICY_ALREADY_EXISTS.isRollback(), ERROR_POLICY_ALREADY_EXISTS.getMessage());
        }
        Boolean enableValidationQuotationAmount = this.basicProductInsuranceProperties.enableValidationQuotationAmountByProductIdAndChannelId(requestBody.getProductId(),requestBody.getSaleChannelId());
        Map<String, Object> quotationData = insrncQuotationModDAO.findQuotationByQuotationId(requestBody.getQuotationId());
        crossOperationsBusinessInsuranceContractBank.validateQuotationData(quotationData, requestBody.getQuotationId());
        this.crossOperationsBusinessInsuranceContractBank.validateQuotationAmount(enableValidationQuotationAmount,quotationData,requestBody);
        this.getResponseLibrary().getBody().setQuotationData(quotationData);
    }

    @Override
    protected void validateAddress(PolicyDTO requestBody) {
        CustomerListASO customerList = null;
        ListBusinessesASO listBusinessesASO = null;
        Map<String, Object> quotationData = this.getResponseLibrary().getBody().getQuotationData();
        if(!RBVDProperties.INSURANCE_PRODUCT_TYPE_VEH.getValue().equalsIgnoreCase(requestBody.getProductId())){
            customerList = customerRBVD066InternalService.findCustomerInformationByCustomerId(requestBody.getHolder().getId());
            crossOperationsBusinessInsuranceContractBank.validateCustomerList(customerList);
            CustomerBO customer = customerList.getData().get(0);
            if(crossOperationsBusinessInsuranceContractBank.isRucCustomer(customer)){
                String customerIdEncrypted = cryptoServiceInternal.encryptCustomerId(requestBody.getHolder().getId());
                crossOperationsBusinessInsuranceContractBank.validateCustomerIdEncryption(customerIdEncrypted);
                listBusinessesASO = businessRBVD66ServiceInternal.getListBusinessesByCustomerId(customerIdEncrypted);
                crossOperationsBusinessInsuranceContractBank.validateListBusinessesASO(listBusinessesASO);
                PersonaBO persona = mapperHelper.constructPerson(requestBody,customer,quotationData);
                String filledAddress = EmissionBean.fillAddress(customerList, persona, new StringBuilder(),requestBody.getSaleChannelId(),applicationConfigurationService);
                crossOperationsBusinessInsuranceContractBank.validateFilledAddress(filledAddress);
            }
        }
        this.getResponseLibrary().getBody().setCustomerList(customerList);
        this.getResponseLibrary().getBody().setListBusinessesASO(listBusinessesASO);

    }

    @Override
    protected void executeFetchRequiredData(PolicyDTO requestBody) {
        String frequencyType = basicProductInsuranceProperties.obtainFrequencyTypeByPeriodId(requestBody.getInstallmentPlan().getPeriod().getId());
        crossOperationsBusinessInsuranceContractBank.validateFrequencyType(frequencyType);
        Map<String,Object> paymentPeriodData = insrncPaymentPeriodDAO.findPaymentPeriodByFrequencyType(frequencyType);
        crossOperationsBusinessInsuranceContractBank.validatePaymentPeriodData(paymentPeriodData, frequencyType);
        Map<String, Object> quotationData = insrncQuotationModDAO.findQuotationByQuotationId(requestBody.getQuotationId());
        crossOperationsBusinessInsuranceContractBank.validateQuotationData(quotationData, requestBody.getQuotationId());
        Map<String,Object> responseQueryGetProductById = insuranceProductDAO.findByProductId(requestBody.getProductId());
        crossOperationsBusinessInsuranceContractBank.validateProductData(responseQueryGetProductById, requestBody.getProductId());

        RequiredFieldsEmissionDAO requiredFieldsEmissionDAO = PrePolicyTransfor.toRequiredFieldsEmissionDAO(quotationData, paymentPeriodData);
        ProcessPrePolicyDTO processPrePolicyDTO = new ProcessPrePolicyDTO();
        processPrePolicyDTO.setPolicy(requestBody);
        processPrePolicyDTO.setRequiredFieldsEmission(requiredFieldsEmissionDAO);
        processPrePolicyDTO.setOperationGlossaryDesc(quotationData.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString());
        processPrePolicyDTO.setQuotationEmailDesc((String) quotationData.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
        processPrePolicyDTO.setQuotationCustomerPhoneDesc((String) quotationData.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()) );
        processPrePolicyDTO.setResponseQueryGetProductById(responseQueryGetProductById);
        processPrePolicyDTO.setQuotationData(quotationData);
        this.setResponseLibrary(ResponseLibrary.ResponseServiceBuilder.an().body(processPrePolicyDTO));
    }

    @Override
    protected void executeGenerateContract() {
        PolicyDTO requestBody = this.getResponseLibrary().getBody().getPolicy();
        RequiredFieldsEmissionDAO emissionDao = this.getResponseLibrary().getBody().getRequiredFieldsEmission();

        Date startDate = crossOperationsBusinessInsuranceContractBank.validateStartDate(requestBody);
        Boolean isPaymentRequired = crossOperationsBusinessInsuranceContractBank.evaluateRequiredPayment(startDate);
        requestBody = PrePolicyTransfor.toIsPaymentRequired(requestBody,isPaymentRequired);
        DataASO dataASO = PrePolicyTransfor.toDataASO(requestBody);

        ResponseLibrary<PolicyASO> responseService = contractPISD201ServiceInternal.generateContractHost(dataASO, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_CONTRACT_ICR3);
        crossOperationsBusinessInsuranceContractBank.validateContractGeneration(responseService);

        PolicyASO asoResponse = responseService.getBody();
        String codeOfficeTelemarketing = this.basicProductInsuranceProperties.obtainOfficeTelemarketingCode();
        String saleChannelIdOffice = codeOfficeTelemarketing.equals(asoResponse.getData().getBank().getBranch().getId()) ? RBVDInternalConstants.Channel.TELEMARKETING_CODE : requestBody.getSaleChannelId();
        requestBody = PrePolicyTransfor.toMapBranchAndSaleChannelIdOfficial(asoResponse.getData().getBank().getBranch().getId(), saleChannelIdOffice, requestBody);
        crossOperationsBusinessInsuranceContractBank.handleNonDigitalSale(requestBody);
        boolean isEndorsement = CrossOperationsBusinessInsuranceContractBank.validateEndorsement(requestBody);
        BigDecimal totalNumberInstallments = (requestBody.getFirstInstallment().getIsPaymentRequired()) ? BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments() - 1) : BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments());
        InsuranceContractDAO contractDao = InsuranceContractBean.toInsuranceContractDAO(requestBody,emissionDao,asoResponse.getData().getId(),isEndorsement,totalNumberInstallments) ;
        Map<String, Object> argumentsForSaveContract = InsuranceContractMap.contractDaoToMap(contractDao);

        argumentsForSaveContract.forEach((key, value) -> LOGGER.info(" :: executeGenerateContract | argumentsForSaveContract [ key {} with value: {} ] ", key, value));

        Boolean isSavedInsuranceContract = this.insuranceContractDAO.saveInsuranceContract(argumentsForSaveContract);
        if(Boolean.TRUE.equals(!isSavedInsuranceContract)){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<InsuranceCtrReceiptsDAO> receiptsList = InsuranceReceiptBean.toInsuranceCtrReceiptsDAO(asoResponse, requestBody);

        this.getResponseLibrary().getBody().setDataASO(dataASO);
        this.getResponseLibrary().getBody().setAsoResponse(asoResponse);
        this.getResponseLibrary().getBody().setPolicy(requestBody);
        this.getResponseLibrary().getBody().setContractDao(contractDao);
        this.getResponseLibrary().getBody().setEndorsement(isEndorsement);
        this.getResponseLibrary().getBody().setReceiptsList(receiptsList);
        this.setResponseLibrary(this.getResponseLibrary());
    }

    @Override
    protected void generateMonthlyReceipts() {
        PolicyDTO requestBody = this.getResponseLibrary().getBody().getPolicy();
        List<InsuranceCtrReceiptsDAO> receiptsList = this.getResponseLibrary().getBody().getReceiptsList();
        List<String> productsNotGenerateMonthlyReceipts = this.basicProductInsuranceProperties.obtainProductsNotGenerateMonthlyReceipts();
        String  operationGlossaryDesc = this.getResponseLibrary().getBody().getOperationGlossaryDesc();
        if(RBVDInternalConstants.Period.MONTHLY_LARGE.equalsIgnoreCase(requestBody.getInstallmentPlan().getPeriod().getId()) && !productsNotGenerateMonthlyReceipts.contains(operationGlossaryDesc)){
            List<InsuranceCtrReceiptsDAO> receipts = InsuranceReceiptBean.toGenerateMonthlyReceipts(receiptsList.get(0));
            receiptsList.addAll(receipts);
        }
        this.getResponseLibrary().getBody().setReceiptsList(receiptsList);
        this.setResponseLibrary(this.getResponseLibrary());
    }

    @Override
    protected void saveReceiptsOfContract() {
        List<InsuranceCtrReceiptsDAO> receiptsList = this.getResponseLibrary().getBody().getReceiptsList();
        Map<String, Object>[] receiptsArguments = InsuranceReceiptMap.receiptsToMaps(receiptsList);
        Boolean isSavedInsuranceReceipts = this.insuranceCtrReceiptsDAO.saveInsuranceReceipts(receiptsArguments);
        if(Boolean.TRUE.equals(!isSavedInsuranceReceipts)){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CTR_RECEIPTS);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
    }

    @Override
    protected void executeSaveAdditionalInsuranceInformation() {
        PolicyDTO requestBody = this.getResponseLibrary().getBody().getPolicy();
        PolicyASO asoResponse = this.getResponseLibrary().getBody().getAsoResponse();
        RequiredFieldsEmissionDAO emissionDao = this.getResponseLibrary().getBody().getRequiredFieldsEmission();
        IsrcContractMovDAO contractMovDao = IsrcContractMovBean.toIsrcContractMovDAO(asoResponse, requestBody.getCreationUser(), requestBody.getUserAudit());
        boolean isSavedContractMov = this.insrncContractMovDAO.saveInsrncContractmov(contractMovDao);
        if(!isSavedContractMov){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CONTRACT_MOV);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<Map<String, Object>> rolesInMap = this.insrncRoleModalityDAO.findByProductIdAndModalityType(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());
        if(!CollectionUtils.isEmpty(rolesInMap)){
            List<IsrcContractParticipantDAO> participants = CrossOperationsBusinessInsuranceContractBank.toIsrcContractParticipantDAOList(requestBody, rolesInMap, asoResponse.getData().getId(),applicationConfigurationService);
            boolean isSavedParticipant = insurncCtrParticipantDAO.savedContractParticipant(participants);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CTR_PARTICIPANT);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }

    }

    @Override
    protected void saveContractDetailsAndEndoserment() {
        InsuranceContractDAO contractDao = this.getResponseLibrary().getBody().getContractDao();
        PolicyDTO requestBody = this.getResponseLibrary().getBody().getPolicy();
        if(!CollectionUtils.isEmpty(requestBody.getRelatedContracts())){
            List<RelatedContractDAO> relatedContractsDao = RelatedContractsList.toRelatedContractDAOList(requestBody, contractDao);
            boolean isSavedParticipant = insurncRelatedContract.savedContractDetails(relatedContractsDao);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }

        if(Boolean.TRUE.equals(this.getResponseLibrary().getBody().getIsEndorsement())){
            String endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();
            boolean isEndorsementSaved = this.IEndorsementInsurncCtrDAO.saveEndosermentInsurncCtr(contractDao,endosatarioRuc,endosatarioPorcentaje);
            if(!isEndorsementSaved){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_ENDORSEMENT_INSRNC_CTR);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
        this.getResponseLibrary().getBody().setPolicy(requestBody);
        this.setResponseLibrary(this.getResponseLibrary());
    }

    @Override
    protected void executeGeneratePayment() {
        if(Boolean.TRUE.equals(this.basicProductInsuranceProperties.enabledPaymentICR2())){
            DataASO asoRequest = this.getResponseLibrary().getBody().getDataASO();
            asoRequest.setId(this.getResponseLibrary().getBody().getAsoResponse().getData().getId());
            RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_ACCOUNTING_ICR2;
            if(this.getResponseLibrary().getBody().getPolicy().getFirstInstallment().getIsPaymentRequired()){
                indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_COLLECT_ACCOUNTING_ICR2;
            }
            ResponseLibrary<PolicyASO> responseGeneratePayment = this.contractPISD201ServiceInternal.generateFormalizationContractAndPayment(asoRequest, indicatorPreFormalized);
            if(!RBVDInternalConstants.Status.OK.equalsIgnoreCase(responseGeneratePayment.getStatusProcess())){
                throw buildValidation(ERROR_RESPONSE_SERVICE_ICR2);
            }
            this.getResponseLibrary().getBody().setAsoResponse(responseGeneratePayment.getBody());
        }
        this.setResponseLibrary(this.getResponseLibrary());
    }

    public void setCrossOperationsBusinessInsuranceContractBank(CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank) {
        this.crossOperationsBusinessInsuranceContractBank = crossOperationsBusinessInsuranceContractBank;
    }

    public void setBusinessRBVD66ServiceInternal(BusinessRBVD66ServiceInternal businessRBVD66ServiceInternal) {
        this.businessRBVD66ServiceInternal = businessRBVD66ServiceInternal;
    }

    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
    }

    public void setInsurncRelatedContract(IInsurncRelatedContract insurncRelatedContract) {
        this.insurncRelatedContract = insurncRelatedContract;
    }

    public void setIEndorsementInsurncCtrDAO(com.bbva.rbvd.lib.r211.impl.service.IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO) {
        this.IEndorsementInsurncCtrDAO = IEndorsementInsurncCtrDAO;
    }

    public void setInsrncContractMovDAO(IInsrncContractMovDAO insrncContractMovDAO) {
        this.insrncContractMovDAO = insrncContractMovDAO;
    }

    public void setInsuranceCtrReceiptsDAO(IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO) {
        this.insuranceCtrReceiptsDAO = insuranceCtrReceiptsDAO;
    }

    public void setInsrncRoleModalityDAO(IInsrncRoleModalityDAO insrncRoleModalityDAO) {
        this.insrncRoleModalityDAO = insrncRoleModalityDAO;
    }

    public void setInsrncQuotationModDAO(IInsrncQuotationModDAO insrncQuotationModDAO) {
        this.insrncQuotationModDAO = insrncQuotationModDAO;
    }

    public void setInsrncPaymentPeriodDAO(IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO) {
        this.insrncPaymentPeriodDAO = insrncPaymentPeriodDAO;
    }

    public void setInsurncCtrParticipantDAO(IInsurncCtrParticipantDAO insurncCtrParticipantDAO) {
        this.insurncCtrParticipantDAO = insurncCtrParticipantDAO;
    }

    public void setBasicProductInsuranceProperties(BasicProductInsuranceProperties basicProductInsuranceProperties) {
        this.basicProductInsuranceProperties = basicProductInsuranceProperties;
    }

    public void setContractPISD201ServiceInternal(ContractPISD201ServiceInternal contractPISD201ServiceInternal) {
        this.contractPISD201ServiceInternal = contractPISD201ServiceInternal;
    }

    public void setCryptoServiceInternal(CryptoServiceInternal cryptoServiceInternal) {
        this.cryptoServiceInternal = cryptoServiceInternal;
    }

    public void setCustomerRBVD066InternalService(CustomerRBVD066InternalService customerRBVD066InternalService) {
        this.customerRBVD066InternalService = customerRBVD066InternalService;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setInsuranceContractDAO(IInsuranceContractDAO insuranceContractDAO) {
        this.insuranceContractDAO = insuranceContractDAO;
    }

    public void setInsuranceProductDAO(IInsuranceProductDAO insuranceProductDAO) {
        this.insuranceProductDAO = insuranceProductDAO;
    }
}

