package com.bbva.rbvd.lib.r211.impl.pattern.template;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.*;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.service.api.interfaces.PolicyServiceExternal;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.*;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceContractMap;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Channel;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.*;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;


public class BasicProductInsuranceBankLifeImpl extends InsuranceContractBank {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicProductInsuranceBankLifeImpl.class);

    private IInsuranceContractDAO insuranceContractDAO;
    private IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO;
    private IInsrncContractMovDAO insrncContractMovDAO;
    private IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO;
    private IInsrncRoleModalityDAO insrncRoleModalityDAO;
    private IInsrncQuotationModDAO insrncQuotationModDAO;
    private IInsrncPaymentPeriodDAO insrncPaymentPeriodDAO;
    private IInsurncCtrParticipantDAO insurncCtrParticipantDAO;
    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private ContractPISD201ServiceInternal contractPISD201ServiceInternal;
    private CustomerRBVD066InternalService customerRBVD066InternalService;
    private IInsuranceProductDAO insuranceProductDAO;
    private ApplicationConfigurationService applicationConfigurationService;
    private MapperHelper mapperHelper;
    private CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank;
    private PolicyServiceExternal policyServiceExternal;


    @Override
    protected void executeValidateConditions(PolicyDTO requestBody) {
        Boolean existContractWithQuotation = this.insuranceContractDAO.findExistenceInsuranceContract(requestBody.getQuotationId());
        CustomerListASO customerList = null;
        String insuranceBusinessName = null;
        if(existContractWithQuotation){
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
        ProcessPrePolicyDTO processPrePolicyDTO = new ProcessPrePolicyDTO();
        processPrePolicyDTO.setCustomerList(customerList);
        this.setResponseLibrary(ResponseLibrary.ResponseServiceBuilder.an().body(processPrePolicyDTO));
    }

    @Override
    protected void validateAddress(PolicyDTO requestBody) {

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
        this.getResponseLibrary().getBody().setPolicy(requestBody);
        this.getResponseLibrary().getBody().setRequiredFieldsEmission(requiredFieldsEmissionDAO);
        this.getResponseLibrary().getBody().setOperationGlossaryDesc(quotationData.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString());
        this.getResponseLibrary().getBody().setQuotationEmailDesc((String) quotationData.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
        this.getResponseLibrary().getBody().setQuotationCustomerPhoneDesc((String) quotationData.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()) );
        this.getResponseLibrary().getBody().setRimacPaymentAccount((String) quotationData.get(RBVDProperties.FIELD_ACCOUNT_ID.getValue()) );
        this.getResponseLibrary().getBody().setResponseQueryGetProductById(responseQueryGetProductById);
        this.setResponseLibrary(this.getResponseLibrary());
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
        String saleChannelIdOffice = codeOfficeTelemarketing.equals(asoResponse.getData().getBank().getBranch().getId()) ? Channel.TELEMARKETING_CODE : requestBody.getSaleChannelId();
        requestBody = PrePolicyTransfor.toMapBranchAndSaleChannelIdOfficial(asoResponse.getData().getBank().getBranch().getId(), saleChannelIdOffice, requestBody);
        crossOperationsBusinessInsuranceContractBank.handleNonDigitalSale(requestBody);
        boolean isEndorsement = ValidationUtil.validateEndorsementInParticipantsRequest(requestBody);
        BigDecimal totalNumberInstallments = crossOperationsBusinessInsuranceContractBank.getTotalNumberInstallments(requestBody, emissionDao);

        InsuranceContractDAO contractDao = InsuranceContractBean.toInsuranceContractDAO(requestBody,emissionDao,asoResponse.getData().getId(),isEndorsement,totalNumberInstallments) ;
        Map<String, Object> argumentsForSaveContract = InsuranceContractMap.contractDaoToMap(contractDao);

        argumentsForSaveContract.forEach((key, value) -> LOGGER.info(" :: executeGenerateContract | argumentsForSaveContract [ key {} with value: {} ] ", key, value));

        Boolean isSavedInsuranceContract = this.insuranceContractDAO.saveInsuranceContract(argumentsForSaveContract);
        if(!isSavedInsuranceContract){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<InsuranceCtrReceiptsDAO> receiptsList = InsuranceReceiptBean.toInsuranceCtrReceiptsDAO(asoResponse, requestBody);
        Map<String, Object>[] receiptsArguments = InsuranceReceiptMap.receiptsToMaps(receiptsList);
        Boolean isSavedInsuranceReceipts = this.insuranceCtrReceiptsDAO.saveInsuranceReceipts(receiptsArguments);
        if(!isSavedInsuranceReceipts){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CTR_RECEIPTS);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        this.getResponseLibrary().getBody().setDataASO(dataASO);
        this.getResponseLibrary().getBody().setAsoResponse(asoResponse);
        this.getResponseLibrary().getBody().setPolicy(requestBody);
        this.getResponseLibrary().getBody().setContractDao(contractDao);
        this.getResponseLibrary().getBody().setEndorsement(isEndorsement);
        this.setResponseLibrary(this.getResponseLibrary());
    }

    @Override
    protected void generateMonthlyReceipts() {

    }

    @Override
    protected void saveReceiptsOfContract() {

    }


    @Override
    protected void executeSaveAdditionalInsuranceInformation() {
        List<EndosatarioBO> endosatarios = null;
        PolicyDTO requestBody = this.getResponseLibrary().getBody().getPolicy();
        PolicyASO asoResponse = this.getResponseLibrary().getBody().getAsoResponse();
        RequiredFieldsEmissionDAO emissionDao = this.getResponseLibrary().getBody().getRequiredFieldsEmission();
        InsuranceContractDAO contractDao = this.getResponseLibrary().getBody().getContractDao();
        IsrcContractMovDAO contractMovDao = IsrcContractMovBean.toIsrcContractMovDAO(asoResponse,requestBody.getCreationUser(),requestBody.getUserAudit());
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

        if(this.getResponseLibrary().getBody().getIsEndorsement()){
            ParticipantDTO participantEndorse = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),RBVDInternalConstants.Endorsement.ENDORSEMENT);
            String endosatarioRuc = Objects.isNull(participantEndorse) ? StringUtils.EMPTY : participantEndorse.getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = Objects.isNull(participantEndorse) ? 0.0 : participantEndorse.getBenefitPercentage();
            endosatarios = new ArrayList<>();
            EndosatarioBO endosatario = new EndosatarioBO(endosatarioRuc,endosatarioPorcentaje.intValue());
            endosatarios.add(endosatario);

           boolean isEndorsementSaved = this.IEndorsementInsurncCtrDAO.saveEndosermentInsurncCtr(contractDao,endosatarioRuc,endosatarioPorcentaje);
            if(!isEndorsementSaved){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_ENDORSEMENT_INSRNC_CTR);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
        this.getResponseLibrary().getBody().setEndosatarios(endosatarios);
        this.getResponseLibrary().getBody().setPolicy(requestBody);
        this.setResponseLibrary(this.getResponseLibrary());
    }

    @Override
    protected void saveContractDetailsAndEndoserment() {

    }

    @Override
    protected void executeGeneratePayment() {
        if(this.basicProductInsuranceProperties.enabledPaymentICR2()){
            DataASO asoRequest = this.getResponseLibrary().getBody().getDataASO();
            asoRequest.setId(this.getResponseLibrary().getBody().getAsoResponse().getData().getId());
            RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_ACCOUNTING_ICR2;
            if(this.getResponseLibrary().getBody().getPolicy().getFirstInstallment().getIsPaymentRequired()){
                indicatorPreFormalized = RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.FORMALIZED_COLLECT_ACCOUNTING_ICR2;
            }
            ResponseLibrary<PolicyASO> responseGeneratePayment = this.contractPISD201ServiceInternal.generateFormalizationContractAndPayment(asoRequest,indicatorPreFormalized);
            if(!RBVDInternalConstants.Status.OK.equalsIgnoreCase(responseGeneratePayment.getStatusProcess())){
                throw buildValidation(ERROR_RESPONSE_SERVICE_ICR2);
            }
        }
        this.setResponseLibrary(this.getResponseLibrary());
    }

    public void setCrossOperationsBusinessInsuranceContractBank(CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank) {
        this.crossOperationsBusinessInsuranceContractBank = crossOperationsBusinessInsuranceContractBank;
    }

    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
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

    public void setPolicyServiceExternal(PolicyServiceExternal policyServiceExternal) {
        this.policyServiceExternal = policyServiceExternal;
    }

}

