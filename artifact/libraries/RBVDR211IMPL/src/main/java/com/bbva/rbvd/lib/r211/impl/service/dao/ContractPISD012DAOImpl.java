package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.contract.constants.PISDErrors;
import com.bbva.pisd.dto.contract.search.CertifyBankCriteria;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r226.PISDR226;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContractPISD012DAOImpl implements IInsuranceContractDAO {

    private PISDR012 pisdR012;
    private PISDR226 pisdR226;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();


    @Override
    public Boolean findExistenceInsuranceContract(String quotationId) {
        Map<String, Object> mapQuotationId = FunctionsUtils.createSingleArgument(quotationId, RBVDProperties.FIELD_POLICY_QUOTA_INTERNAL_ID.getValue());
        Map<String, Object> result = this.pisdR012.executeGetASingleRow(RBVDProperties.QUERY_VALIDATE_IF_POLICY_EXISTS.getValue(), mapQuotationId);
        BigDecimal resultNumber = (BigDecimal) result.get(RBVDProperties.FIELD_RESULT_NUMBER.getValue());
        if(Objects.nonNull(resultNumber) && resultNumber.compareTo(BigDecimal.ONE) == 0){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean saveInsuranceContract(Map<String, Object> argumentsForSaveContract) {
        int insertedContract = this.pisdR012.executeInsertSingleRow(PISDProperties.QUERY_INSERT_INSURANCE_CONTRACT.getValue(), argumentsForSaveContract,
                               RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(),
                               RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue(), RBVDProperties.FIELD_INSURANCE_MODALITY_TYPE.getValue(),
                               RBVDProperties.FIELD_INSURANCE_COMPANY_ID.getValue(), RBVDProperties.FIELD_INSURANCE_CONTRACT_START_DATE.getValue(),
                               RBVDProperties.FIELD_CUSTOMER_ID.getValue(), RBVDProperties.FIELD_INSRNC_CO_CONTRACT_STATUS_TYPE.getValue(),
                               RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), RBVDProperties.FIELD_USER_AUDIT_ID.getValue());
        if(insertedContract != 1) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateEndorsementInContract(String policyNumber, String intAccountId) {
        Map<String, Object> policyIdForEndorsementTable = new HashMap<>();
        policyIdForEndorsementTable.put(RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue(), policyNumber);
        policyIdForEndorsementTable.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), intAccountId);
        int updatedRows = this.pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT_ENDORSEMENT", policyIdForEndorsementTable, RBVDProperties.FIELD_ENDORSEMENT_POLICY_ID.getValue());
        if(updatedRows != 1) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean updateInsuranceContract(Map<String, Object> argumentsRimacContractInformation) {
        int updatedContract = this.pisdR012.executeInsertSingleRow("PISD.UPDATE_CONTRACT", argumentsRimacContractInformation,
                RBVDProperties.FIELD_INSURANCE_CONTRACT_END_DATE.getValue(),
                RBVDProperties.FIELD_INSURANCE_POLICY_END_DATE.getValue(),
                RBVDProperties.FIELD_LAST_INSTALLMENT_DATE.getValue(),
                RBVDProperties.FIELD_PERIOD_NEXT_PAYMENT_DATE.getValue());
        if(updatedContract != 1) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public ContractEntity executeFindByCertifiedBank(CertifyBankCriteria certifyBankCriteria) {
        ContractEntity contractEntity;
        try{
            contractEntity = this.pisdR226.executeFindByCertifiedBank(certifyBankCriteria);
            if(Objects.isNull(contractEntity)){
                throw new BusinessException(PISDErrors.QUERY_EMPTY_RESULT.getAdviceCode(),PISDErrors.QUERY_EMPTY_RESULT.isRollback(),PISDErrors.QUERY_EMPTY_RESULT.getMessage());
            }
        }catch (BusinessException businessException){
           if(PISDErrors.QUERY_EMPTY_RESULT.getAdviceCode().equalsIgnoreCase(businessException.getAdviceCode())){
               String certifyBank = certifyBankCriteria.getInsuranceContractEntityId() + certifyBankCriteria.getInsuranceContractBranchId() + certifyBankCriteria.getContractFirstVerfnDigitId() + certifyBankCriteria.getContractSecondVerfnDigitId() + certifyBankCriteria.getInsrcContractIntAccountId();
               String message = String.format(RBVDInternalErrors.ERROR_NOT_RESULT_CONTRACT.getMessage(), certifyBank);
               this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_NOT_RESULT_CONTRACT.getAdviceCode(),message);
               throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_NOT_RESULT_CONTRACT,message);
           }
           this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL.getAdviceCode(),RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL.getMessage());
           throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL);
        }
        return contractEntity;
    }

    @Override
    public void updateInsuranceContractByCertifyBank(ContractEntity contractEntity) {
        boolean isUpdateInsuranceContract;
        try{
            isUpdateInsuranceContract = this.pisdR226.executeUpdateInsuranceContractByCertifyBank(contractEntity);
        }catch (BusinessException businessException){
            if(PISDErrors.VALIDATE_FIELD_CONTRACT.getAdviceCode().equalsIgnoreCase(businessException.getAdviceCode())){
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(businessException.getAdviceCode(),businessException.getMessage());
                throw new BusinessException(businessException.getAdviceCode(), true, businessException.getMessage());
            }
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL.getAdviceCode(),RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL.getMessage());
            throw FunctionsUtils.buildValidation(RBVDInternalErrors.ERROR_NOT_TIMEOUT_GENERAL);
        }
        if(!isUpdateInsuranceContract) {
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.UPDATE_ERROR_IN_CONTRACT_TABLE.getAdviceCode(), RBVDInternalErrors.UPDATE_ERROR_IN_CONTRACT_TABLE.getMessage());
            throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_CONTRACT_TABLE);
        }
    }

    public void setPisdR226(PISDR226 pisdR226) {
        this.pisdR226 = pisdR226;
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
