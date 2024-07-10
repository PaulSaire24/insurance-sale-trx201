package com.bbva.rbvd.lib.r211.impl.pattern.template.product;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

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
        super.executeGenerateContract();
    }
    @Override
    protected void saveListReceipts() {
        super.saveListReceipts();
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
