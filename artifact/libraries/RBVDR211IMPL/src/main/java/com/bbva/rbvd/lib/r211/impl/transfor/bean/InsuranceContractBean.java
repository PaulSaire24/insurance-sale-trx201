package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;
import com.bbva.rbvd.dto.insrncsale.policy.SaleSupplierDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Optional;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Period;

public class InsuranceContractBean {

    public static InsuranceContractDAO toInsuranceContractDAO(PolicyDTO apxRequest, RequiredFieldsEmissionDAO emissionDao, String asoId, Boolean isEndorsement,BigDecimal prevPendBillRcptsNumber) {
        InsuranceContractDAO contractDao = new InsuranceContractDAO();
        contractDao.setEntityId(asoId.substring(0, 4));
        contractDao.setBranchId(asoId.substring(4, 8));
        contractDao.setIntAccountId(asoId.substring(10));
        contractDao.setFirstVerfnDigitId(asoId.substring(8,9));
        contractDao.setSecondVerfnDigitId(asoId.substring(9,10));
        contractDao.setPolicyQuotaInternalId(apxRequest.getQuotationId());
        contractDao.setInsuranceProductId(emissionDao.getInsuranceProductId());
        contractDao.setInsuranceModalityType(apxRequest.getProductPlan().getId());
        contractDao.setInsuranceCompanyId(new BigDecimal(apxRequest.getInsuranceCompany().getId()));
        contractDao.setInsuranceManagerId(Optional.ofNullable(apxRequest.getBusinessAgent()).map(BusinessAgentDTO::getId).orElse(null));
        contractDao.setInsurancePromoterId(Optional.ofNullable(apxRequest.getPromoter()).map(PromoterDTO::getId).orElse(null));
        contractDao.setOriginalPaymentSubchannelId(Optional.ofNullable(apxRequest.getSaleSupplier()).map(SaleSupplierDTO::getId).orElse(null));
        contractDao.setContractManagerBranchId(apxRequest.getBank().getBranch().getId());
        contractDao.setContractInceptionDate(FunctionsUtils.generateCorrectDateFormat(new LocalDate()));
        contractDao.setEndLinkageDate(FunctionsUtils.generateCorrectDateFormat(FunctionsUtils.convertDateToLocalDate(apxRequest.getInstallmentPlan().getMaturityDate())));
        contractDao.setInsuranceContractStartDate(FunctionsUtils.generateCorrectDateFormat(FunctionsUtils.convertDateToLocalDate(apxRequest.getValidityPeriod().getStartDate())));
        contractDao.setValidityMonthsNumber(emissionDao.getContractDurationType().equals(Period.ANNUAL) ? emissionDao.getContractDurationNumber().multiply(BigDecimal.valueOf(12)) : emissionDao.getContractDurationNumber());
        contractDao.setCustomerId(apxRequest.getHolder().getId());
        contractDao.setDomicileContractId(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        contractDao.setIssuedReceiptNumber(BigDecimal.valueOf(apxRequest.getInstallmentPlan().getTotalNumberInstallments()));
        contractDao.setPaymentFrequencyId(emissionDao.getPaymentFrequencyId());
        contractDao.setPremiumAmount(BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()));
        contractDao.setSettlePendingPremiumAmount(BigDecimal.valueOf(apxRequest.getTotalAmount().getAmount()));
        contractDao.setCurrencyId(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency());
        contractDao.setInstallmentPeriodFinalDate(FunctionsUtils.generateCorrectDateFormat(new LocalDate()));
        contractDao.setInsuredAmount(BigDecimal.valueOf(apxRequest.getInsuredAmount().getAmount()));
        contractDao.setCtrctDisputeStatusType(apxRequest.getSaleChannelId());
        contractDao.setEndorsementPolicyIndType((isEndorsement) ? PISDConstants.LETTER_SI : PISDConstants.LETTER_NO );
        contractDao.setInsrncCoContractStatusType(RBVDInternalConstants.ContractStatusCompany.ERROR);
        contractDao.setCreationUserId(apxRequest.getCreationUser());
        contractDao.setUserAuditId(apxRequest.getUserAudit());
        contractDao.setInsurPendingDebtIndType((apxRequest.getFirstInstallment().getIsPaymentRequired()) ? PISDConstants.LETTER_NO : PISDConstants.LETTER_SI);
        contractDao.setTotalDebtAmount((apxRequest.getFirstInstallment().getIsPaymentRequired()) ? BigDecimal.ZERO : BigDecimal.valueOf(apxRequest.getFirstInstallment().getPaymentAmount().getAmount()));
        contractDao.setPrevPendBillRcptsNumber(prevPendBillRcptsNumber);
        contractDao.setSettlementFixPremiumAmount(BigDecimal.valueOf(apxRequest.getTotalAmount().getAmount()));
        contractDao.setAutomaticDebitIndicatorType((apxRequest.getPaymentMethod().getPaymentType().equals(RBVDInternalConstants.Payment.METHOD_DIRECT)) ? PISDConstants.LETTER_SI  : PISDConstants.LETTER_NO);
        contractDao.setBiometryTransactionId(apxRequest.getIdentityVerificationCode());
        return contractDao;
    }
}
