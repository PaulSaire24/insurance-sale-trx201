package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;


import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.strategy.EstrategiaGenerarContrato;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.service.api.ContractPISD201ServiceInternal;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceContractBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsuranceReceiptBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceContractMap;
import com.bbva.rbvd.lib.r211.impl.transfor.map.InsuranceReceiptMap;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class GenerarContratoPaso implements PasoPipeline{

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerarContratoPaso.class);

    private IInsuranceContractDAO insuranceContractDAO;
    private IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO;
    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private ContractPISD201ServiceInternal contractPISD201ServiceInternal;
    private CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank;
    private EstrategiaGenerarContrato estrategiaGenerarContrato;

    protected final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public GenerarContratoPaso(EstrategiaGenerarContrato estrategia) {
        this.estrategiaGenerarContrato = estrategia;
    }
    @Override
    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, PasoPipeline siguiente) {
        PolicyDTO requestBody = contexto.getBody().getPolicy();
        RequiredFieldsEmissionDAO emissionDao = contexto.getBody().getRequiredFieldsEmission();

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

        BigDecimal totalNumberInstallments;
        String productCodesLife = this.basicProductInsuranceProperties.obtainProductCodesLife();
        if(productCodesLife.contains(requestBody.getProductId())){
            totalNumberInstallments = crossOperationsBusinessInsuranceContractBank.getTotalNumberInstallments(requestBody, emissionDao);
        }else {
            totalNumberInstallments = (requestBody.getFirstInstallment().getIsPaymentRequired()) ? BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments() - 1) : BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments());
        }

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
        Map<String, Object>[] receiptsArguments = InsuranceReceiptMap.receiptsToMaps(receiptsList);
        Boolean isSavedInsuranceReceipts = this.insuranceCtrReceiptsDAO.saveInsuranceReceipts(receiptsArguments);
        if(Boolean.TRUE.equals(!isSavedInsuranceReceipts)){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CTR_RECEIPTS);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        contexto.getBody().setDataASO(dataASO);
        contexto.getBody().setAsoResponse(asoResponse);
        contexto.getBody().setPolicy(requestBody);
        contexto.getBody().setContractDao(contractDao);
        contexto.getBody().setEndorsement(isEndorsement);

        siguiente.ejecutar(contexto, siguiente);
    }

    public IInsuranceContractDAO getInsuranceContractDAO() {
        return insuranceContractDAO;
    }

    public void setInsuranceContractDAO(IInsuranceContractDAO insuranceContractDAO) {
        this.insuranceContractDAO = insuranceContractDAO;
    }

    public IInsuranceCtrReceiptsDAO getInsuranceCtrReceiptsDAO() {
        return insuranceCtrReceiptsDAO;
    }

    public void setInsuranceCtrReceiptsDAO(IInsuranceCtrReceiptsDAO insuranceCtrReceiptsDAO) {
        this.insuranceCtrReceiptsDAO = insuranceCtrReceiptsDAO;
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

    public CrossOperationsBusinessInsuranceContractBank getCrossOperationsBusinessInsuranceContractBank() {
        return crossOperationsBusinessInsuranceContractBank;
    }

    public void setCrossOperationsBusinessInsuranceContractBank(CrossOperationsBusinessInsuranceContractBank crossOperationsBusinessInsuranceContractBank) {
        this.crossOperationsBusinessInsuranceContractBank = crossOperationsBusinessInsuranceContractBank;
    }

    public EstrategiaGenerarContrato getEstrategiaGenerarContrato() {
        return estrategiaGenerarContrato;
    }

    public void setEstrategiaGenerarContrato(EstrategiaGenerarContrato estrategiaGenerarContrato) {
        this.estrategiaGenerarContrato = estrategiaGenerarContrato;
    }

    public ArchitectureAPXUtils getArchitectureAPXUtils() {
        return architectureAPXUtils;
    }
}
