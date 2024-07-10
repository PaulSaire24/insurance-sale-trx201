package com.bbva.rbvd.lib.r211.impl.pattern.template.product;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceContractBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceContractMap;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;


public class LifeProduct extends BasicProduct{

    @Override
    protected void executeValidateConditions(PolicyDTO requestBody) {
        super.executeValidateConditions(requestBody);
        CustomerListASO customerList = null;
        String insuranceBusinessName;
        String productCodesWithoutPartyValidation = basicProductInsuranceProperties.obtainProductCodesWithoutPartyValidation();
        if(!productCodesWithoutPartyValidation.contains(requestBody.getProductId())){
            customerList = customerRBVD066InternalService.findCustomerInformationByCustomerId(requestBody.getHolder().getId());
            crossOperationsBusinessInsuranceContractBank.validateCustomerList(customerList);
            Map<String, Object> quotationData = this.getResponseLibrary().getBody().getQuotationData();
            Map<String, Object> dataInsuredQuotationFromDB = this.insrncQuotationModDAO.getDataInsuredParticipantFromDB(requestBody,quotationData);
            Map<String,Object> responseQueryGetProductById = insuranceProductDAO.findByProductId(requestBody.getProductId());
            crossOperationsBusinessInsuranceContractBank.validateProductData(responseQueryGetProductById, requestBody.getProductId());
            insuranceBusinessName = this.mapperHelper.getInsuranceBusinessNameFromDB(responseQueryGetProductById);
            AgregarTerceroBO requestAddParticipants = this.mapperHelper.generateRequestAddParticipantsV2(insuranceBusinessName, requestBody, customerList, quotationData, dataInsuredQuotationFromDB,customerRBVD066InternalService);
            this.crossOperationsBusinessInsuranceContractBank.validateFilledAddress(requestAddParticipants);
            String insuranceQuotationCompany = (String) quotationData.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue());
            //moverlo a otro paso
            AgregarTerceroBO responseValidateParticipants = this.policyServiceExternal.executeAddParticipantsService(requestAddParticipants,insuranceQuotationCompany, requestBody.getProductId(), requestBody.getTraceId());
            this.crossOperationsBusinessInsuranceContractBank.validateResponseAddParticipantsService(responseValidateParticipants);
        }
        ProcessPrePolicyDTO processPrePolicyDTO = new ProcessPrePolicyDTO();
        processPrePolicyDTO.setCustomerList(customerList);
        this.setResponseLibrary(ResponseLibrary.ResponseServiceBuilder.an().body(processPrePolicyDTO));
    }
    
    @Override
    protected void executeFetchRequiredData(PolicyDTO requestBody){
        super.executeFetchRequiredData(requestBody);
        Map<String, Object> quotationData = this.getResponseLibrary().getBody().getQuotationData();
        this.getResponseLibrary().getBody().setRimacPaymentAccount((String) quotationData.get(RBVDProperties.FIELD_ACCOUNT_ID.getValue()) );
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
    protected void executeSaveInsuranceData() {
        super.executeSaveInsuranceData();
    }

    @Override
    protected void saveContractDetailsAndEndoserment() {
        List<EndosatarioBO> endosatarios = null;
        PolicyDTO requestBody = this.getResponseLibrary().getBody().getPolicy();
        InsuranceContractDAO contractDao = this.getResponseLibrary().getBody().getContractDao();
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
    protected void executeGeneratePayment() {
        super.executeGeneratePayment();
    }
}
