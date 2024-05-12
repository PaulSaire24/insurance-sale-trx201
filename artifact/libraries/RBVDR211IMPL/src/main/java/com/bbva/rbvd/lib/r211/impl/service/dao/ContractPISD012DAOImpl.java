package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ContractPISD012DAOImpl implements IInsuranceContractDAO {

    private PISDR012 pisdR012;


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

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
