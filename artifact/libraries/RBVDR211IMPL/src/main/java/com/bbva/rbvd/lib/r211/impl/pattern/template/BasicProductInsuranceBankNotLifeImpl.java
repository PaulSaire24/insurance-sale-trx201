package com.bbva.rbvd.lib.r211.impl.pattern.template;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.PersonaBO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.commons.HolderDTO;
import com.bbva.rbvd.dto.insrncsale.dao.*;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalColumn;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Period;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.BusinessRBVD66ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CryptoServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.*;
import com.bbva.rbvd.lib.r211.impl.transfor.list.RelatedContractsList;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceContractMap;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Channel;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.*;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.*;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;


public class BasicProductInsuranceBankNotLifeImpl extends InsuranceContractBank {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicProductInsuranceBankNotLifeImpl.class);

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


    @Override
    protected void executeValidateConditions(PolicyDTO requestBody) {
        Boolean existContractWithQuotation = this.insuranceContractDAO.findExistenceInsuranceContract(requestBody.getQuotationId());
        if(existContractWithQuotation){
            String messageErrorContractWithQuotation = String.format(ERROR_POLICY_ALREADY_EXISTS.getMessage(),requestBody.getQuotationId());
            this.addAdviceWithDescription(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(),messageErrorContractWithQuotation);
            throw new BusinessException(ERROR_POLICY_ALREADY_EXISTS.getAdviceCode(), ERROR_POLICY_ALREADY_EXISTS.isRollback(), ERROR_POLICY_ALREADY_EXISTS.getMessage());
        }

        Boolean enableValidationQuotationAmount = this.basicProductInsuranceProperties.enableValidationQuotationAmountByProductIdAndChannelId(requestBody.getProductId(),requestBody.getSaleChannelId());
        if(enableValidationQuotationAmount){
            Map<String, Object> quotationData = validateMap(this.insrncQuotationModDAO.findQuotationByQuotationId(requestBody.getQuotationId())).orElseThrow(() -> buildValidation(ERROR_EMPTY_RESULT_QUOTATION_DATA));
            String frequencyType = ofNullable(quotationData.get(RBVDInternalColumn.PaymentPeriod.FIELD_POLICY_PAYMENT_FREQUENCY_TYPE)).map(Object::toString).orElseThrow(() -> {
                String message = String.format(ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE.getMessage(), requestBody.getQuotationId());
                this.addAdviceWithDescription(ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE.getAdviceCode(),message);
                return buildValidation(ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE, message);
            });
            String paymentCurrencyId = ofNullable(quotationData.get(RBVDInternalColumn.Quotation.FIELD_PREMIUM_CURRENCY_ID)).map(Object::toString).orElseThrow(() -> {
                String message = String.format(ERROR_NOT_VALUE_QUOTATION.getMessage(),RBVDInternalColumn.Quotation.FIELD_PREMIUM_CURRENCY_ID, requestBody.getQuotationId());
                this.addAdviceWithDescription(ERROR_NOT_VALUE_QUOTATION.getAdviceCode(),message);
                return buildValidation(ERROR_NOT_VALUE_QUOTATION_CURRENCY_ID,message);
            });
            int paymentAmount = ofNullable(quotationData.get(RBVDInternalColumn.Quotation.FIELD_PREMIUM_AMOUNT)).map(premiumAmount -> new BigDecimal(premiumAmount.toString()).intValue()).orElseThrow(() -> {
                String message = String.format(ERROR_NOT_VALUE_QUOTATION.getMessage(),RBVDInternalColumn.Quotation.FIELD_PREMIUM_AMOUNT, requestBody.getQuotationId());
                this.addAdviceWithDescription(ERROR_NOT_VALUE_QUOTATION.getAdviceCode(),message);
                return buildValidation(ERROR_NOT_VALUE_PREMIUM_AMOUNT,message);
            });

            String dataToConditionsLog = String.format(" FrequencyType :: %s,PaymentAmount :: %s, PaymentCurrency :: %s", frequencyType, paymentAmount, paymentCurrencyId);
            LOGGER.info(" :: executeValidateConditions :: [ {} ]",dataToConditionsLog );
            int rangeVariationPremiumAmount = this.basicProductInsuranceProperties.obtainRangePaymentAmount();
            Integer amountQuotationMin   = ((100 - rangeVariationPremiumAmount)*paymentAmount)/100;
            Integer amountQuotationMax   = ((100 + rangeVariationPremiumAmount)*paymentAmount)/100;
            Integer amountTotalAmountMin = ((100 - rangeVariationPremiumAmount)*paymentAmount*12)/100;
            Integer amountTotalAmountMax = ((100 + rangeVariationPremiumAmount)*paymentAmount*12)/100;
            String dataAmountQuotation = String.format(" AmountQuotationMin :: %s ,AmountQuotationMax :: %s ,AmountTotalAmountMin :: %s , AmountTotalAmountMax :: %s ",amountQuotationMin, amountQuotationMax , amountTotalAmountMin ,amountTotalAmountMax );
            LOGGER.info(" :: executeValidateConditions :: [ {} ] ", dataAmountQuotation);

            String  totalAmountCurrencyId = requestBody.getTotalAmount().getCurrency();
            int     totalAmount           = requestBody.getTotalAmount().getAmount().intValue();

            if(!paymentCurrencyId.equals(totalAmountCurrencyId)){
                String message = String.format(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getMessage(), paymentCurrencyId, totalAmountCurrencyId);
                this.addAdviceWithDescription(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getAdviceCode(),message);
                throw buildValidation(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID,message);
            }else if(Period.ANNUAL.equalsIgnoreCase(frequencyType) && !isValidateRange(totalAmount, amountQuotationMin, amountQuotationMax) ){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), totalAmount, amountQuotationMin,amountQuotationMax);
                this.addAdviceWithDescription(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }else if(Period.MONTHLY.equalsIgnoreCase(frequencyType) && !isValidateRange(totalAmount, amountTotalAmountMin, amountTotalAmountMax) ){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), totalAmount, amountTotalAmountMin,amountTotalAmountMax);
                this.addAdviceWithDescription(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }

            if(!paymentCurrencyId.equals(requestBody.getFirstInstallment().getPaymentAmount().getCurrency())){
                String message = String.format(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getMessage(), paymentCurrencyId, totalAmountCurrencyId);
                this.addAdviceWithDescription(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getAdviceCode(),message);
                throw buildValidation(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID,message);
            }else if(!isValidateRange(requestBody.getFirstInstallment().getPaymentAmount().getAmount().intValue(), amountQuotationMin, amountQuotationMax)){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), requestBody.getFirstInstallment().getPaymentAmount().getAmount().intValue(), amountQuotationMin,amountQuotationMax);
                this.addAdviceWithDescription(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }

            if(!paymentCurrencyId.equals(requestBody.getInstallmentPlan().getPaymentAmount().getCurrency())){
                String message = String.format(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getMessage(), paymentCurrencyId, totalAmountCurrencyId);
                this.addAdviceWithDescription(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getAdviceCode(),message);
                throw buildValidation(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID,message);
            }else if(!isValidateRange(requestBody.getInstallmentPlan().getPaymentAmount().getAmount().intValue(), amountQuotationMin, amountQuotationMax)){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), requestBody.getInstallmentPlan().getPaymentAmount().getAmount().intValue(), amountQuotationMin,amountQuotationMax);
                this.addAdviceWithDescription(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }

        }

    }


    @Override
    protected void executeFetchRequiredData(PolicyDTO requestBody) {
        CustomerListASO customerList = null;
        ListBusinessesASO listBusinessesASO = null;
        String frequencyType = basicProductInsuranceProperties.obtainFrequencyTypeByPeriodId(requestBody.getInstallmentPlan().getPeriod().getId());
        crossOperationsBusinessInsuranceContractBank.validateFrequencyType(frequencyType);
        Map<String,Object> paymentPeriodData = insrncPaymentPeriodDAO.findPaymentPeriodByFrequencyType(frequencyType);
        crossOperationsBusinessInsuranceContractBank.validatePaymentPeriodData(paymentPeriodData, frequencyType);
        Map<String, Object> quotationData = insrncQuotationModDAO.findQuotationByQuotationId(requestBody.getQuotationId());
        crossOperationsBusinessInsuranceContractBank.validateQuotationData(quotationData, requestBody.getQuotationId());
        Map<String,Object> responseQueryGetProductById = insuranceProductDAO.findByProductId(requestBody.getProductId());
        crossOperationsBusinessInsuranceContractBank.validateProductData(responseQueryGetProductById, requestBody.getProductId());

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
        RequiredFieldsEmissionDAO requiredFieldsEmissionDAO = PrePolicyTransfor.toRequiredFieldsEmissionDAO(quotationData, paymentPeriodData);
        ProcessPrePolicyDTO processPrePolicyDTO = new ProcessPrePolicyDTO();
        processPrePolicyDTO.setCustomerList(customerList);
        processPrePolicyDTO.setListBusinessesASO(listBusinessesASO);
        processPrePolicyDTO.setPolicy(requestBody);
        processPrePolicyDTO.setRequiredFieldsEmission(requiredFieldsEmissionDAO);
        processPrePolicyDTO.setOperationGlossaryDesc(quotationData.get(RBVDProperties.FIELD_OPERATION_GLOSSARY_DESC.getValue()).toString());
        processPrePolicyDTO.setQuotationEmailDesc((String) quotationData.get(PISDProperties.FIELD_CONTACT_EMAIL_DESC.getValue()));
        processPrePolicyDTO.setQuotationCustomerPhoneDesc((String) quotationData.get(PISDProperties.FIELD_CUSTOMER_PHONE_DESC.getValue()) );
        processPrePolicyDTO.setResponseQueryGetProductById(responseQueryGetProductById);
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

        ResponseLibrary<PolicyASO> responseService = contractPISD201ServiceInternal.generateContractHost(dataASO, RBVDInternalConstants.INDICATOR_PRE_FORMALIZED.PRE_FORMALIZED_S);
        crossOperationsBusinessInsuranceContractBank.validateContractGeneration(responseService);

        PolicyASO asoResponse = responseService.getBody();
        String codeOfficeTelemarketing = this.basicProductInsuranceProperties.obtainOfficeTelemarketingCode();
        String saleChannelIdOffice = codeOfficeTelemarketing.equals(asoResponse.getData().getBank().getBranch().getId()) ? Channel.TELEMARKETING_CODE : requestBody.getSaleChannelId();
        requestBody = PrePolicyTransfor.toMapBranchAndSaleChannelIdOfficial(asoResponse.getData().getBank().getBranch().getId(), saleChannelIdOffice, requestBody);
        crossOperationsBusinessInsuranceContractBank.handleNonDigitalSale(requestBody);
        boolean isEndorsement = CrossOperationsBusinessInsuranceContractBank.validateEndorsement(requestBody);
        BigDecimal totalNumberInstallments = (requestBody.getFirstInstallment().getIsPaymentRequired()) ? BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments() - 1) : BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments());
        InsuranceContractDAO contractDao = InsuranceContractBean.toInsuranceContractDAO(requestBody,emissionDao,asoResponse.getData().getId(),isEndorsement,totalNumberInstallments) ;
        Map<String, Object> argumentsForSaveContract = InsuranceContractMap.contractDaoToMap(contractDao);
        Boolean isSavedInsuranceContract = this.insuranceContractDAO.saveInsuranceContract(argumentsForSaveContract);
        if(!isSavedInsuranceContract){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
            this.addAdviceWithDescription(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<InsuranceCtrReceiptsDAO> receiptsList = InsuranceReceiptBean.toInsuranceCtrReceiptsDAO(asoResponse, requestBody);
        List<String> productsNotGenerateMonthlyReceipts = this.basicProductInsuranceProperties.obtainProductsNotGenerateMonthlyReceipts();
        String  operationGlossaryDesc = this.getResponseLibrary().getBody().getOperationGlossaryDesc();
        if(Period.MONTHLY_LARGE.equalsIgnoreCase(requestBody.getInstallmentPlan().getPeriod().getId()) && !productsNotGenerateMonthlyReceipts.contains(operationGlossaryDesc)){
            List<InsuranceCtrReceiptsDAO> receipts = InsuranceReceiptBean.toGenerateMonthlyReceipts(receiptsList.get(0));
            receiptsList.addAll(receipts);
        }
        Map<String, Object>[] receiptsArguments = InsuranceReceiptMap.receiptsToMaps(receiptsList);
        Boolean isSavedInsuranceReceipts = this.insuranceCtrReceiptsDAO.saveInsuranceReceipts(receiptsArguments);
        if(!isSavedInsuranceReceipts){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CTR_RECEIPTS);
            this.addAdviceWithDescription(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        this.getResponseLibrary().getBody().setAsoResponse(asoResponse);
        this.getResponseLibrary().getBody().setPolicy(requestBody);
        this.getResponseLibrary().getBody().setContractDao(contractDao);
        this.getResponseLibrary().getBody().setEndorsement(isEndorsement);
        this.setResponseLibrary(this.getResponseLibrary());
    }

    @Override
    protected void executeSaveInsuranceData() {
        PolicyDTO requestBody = this.getResponseLibrary().getBody().getPolicy();
        PolicyASO asoResponse = this.getResponseLibrary().getBody().getAsoResponse();
        RequiredFieldsEmissionDAO emissionDao = this.getResponseLibrary().getBody().getRequiredFieldsEmission();
        InsuranceContractDAO contractDao = this.getResponseLibrary().getBody().getContractDao();
        IsrcContractMovDAO contractMovDao = IsrcContractMovBean.toIsrcContractMovDAO(asoResponse,requestBody.getCreationUser(),requestBody.getUserAudit());
        boolean isSavedContractMov = this.insrncContractMovDAO.saveInsrncContractmov(contractMovDao);
        if(!isSavedContractMov){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CONTRACT_MOV);
            this.addAdviceWithDescription(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<Map<String, Object>> rolesInMap = this.insrncRoleModalityDAO.findByProductIdAndModalityType(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());
        if(!CollectionUtils.isEmpty(rolesInMap)){
            List<IsrcContractParticipantDAO> participants = CrossOperationsBusinessInsuranceContractBank.toIsrcContractParticipantDAOList(requestBody, rolesInMap, asoResponse.getData().getId(),applicationConfigurationService);
            boolean isSavedParticipant = insurncCtrParticipantDAO.savedContractParticipant(participants);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CTR_PARTICIPANT);
                this.addAdviceWithDescription(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }

        if(!CollectionUtils.isEmpty(requestBody.getRelatedContracts())){
            List<RelatedContractDAO> relatedContractsDao = RelatedContractsList.toRelatedContractDAOList(requestBody, contractDao);
            boolean isSavedParticipant = insurncRelatedContract.savedContractDetails(relatedContractsDao);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
                this.addAdviceWithDescription(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }

        if(this.getResponseLibrary().getBody().getIsEndorsement()){
           String endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
           Double endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();
           boolean isEndorsementSaved = this.IEndorsementInsurncCtrDAO.saveEndosermentInsurncCtr(contractDao,endosatarioRuc,endosatarioPorcentaje);
            if(!isEndorsementSaved){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_ENDORSEMENT_INSRNC_CTR);
                this.addAdviceWithDescription(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
        this.getResponseLibrary().getBody().setPolicy(requestBody);
        this.setResponseLibrary(this.getResponseLibrary());
    }

    @Override
    protected void executeGeneratePayment() {
        if(this.basicProductInsuranceProperties.enabledPaymentICR3()){
            PolicyASO asoResponse = this.getResponseLibrary().getBody().getAsoResponse();
            ResponseLibrary<PolicyASO> responseGeneratePayment = this.contractPISD201ServiceInternal.generateFormalizationContractAndPayment(asoResponse);
            if(!RBVDInternalConstants.Status.OK.equalsIgnoreCase(responseGeneratePayment.getStatusProcess())){
                throw buildValidation(ERROR_RESPONSE_SERVICE_ICR2);
            }
        }

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

