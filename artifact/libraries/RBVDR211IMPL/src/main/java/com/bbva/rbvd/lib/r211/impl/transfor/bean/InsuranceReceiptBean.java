package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.CuotaFinancimientoBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceCtrReceiptsDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Payment;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ReceiptDefaultValues;

public class InsuranceReceiptBean {

    public static InsuranceCtrReceiptsDAO generateNextReceipt(PolicyASO asoResponse, CuotaFinancimientoBO cuota) {
        InsuranceCtrReceiptsDAO nextReceipt = new InsuranceCtrReceiptsDAO();
        nextReceipt.setEntityId(asoResponse.getData().getId().substring(0, 4));
        nextReceipt.setBranchId(asoResponse.getData().getId().substring(4, 8));
        nextReceipt.setIntAccountId(asoResponse.getData().getId().substring(10));
        nextReceipt.setPolicyReceiptId(BigDecimal.valueOf(cuota.getCuota()));
        nextReceipt.setReceiptExpirationDate(FunctionsUtils.generateCorrectDateFormat(cuota.getFechaVencimiento()));
        return nextReceipt;
    }

    public static List<InsuranceCtrReceiptsDAO> toInsuranceCtrReceiptsDAO(PolicyASO asoResponse, PolicyDTO requestBody) {

        List<InsuranceCtrReceiptsDAO> receiptList = new ArrayList<>();

        InsuranceCtrReceiptsDAO firstReceipt = new InsuranceCtrReceiptsDAO();
        firstReceipt.setEntityId(asoResponse.getData().getId().substring(0, 4));
        firstReceipt.setBranchId(asoResponse.getData().getId().substring(4, 8));
        firstReceipt.setIntAccountId(asoResponse.getData().getId().substring(10));
        firstReceipt.setPolicyReceiptId(BigDecimal.ONE);
        firstReceipt.setPremiumPaymentReceiptAmount(BigDecimal.valueOf(requestBody.getFirstInstallment().getPaymentAmount().getAmount()));
        if(Objects.nonNull(asoResponse.getData().getFirstInstallment().getExchangeRate())) {
            firstReceipt.setFixingExchangeRateAmount(BigDecimal.valueOf(asoResponse.getData().getFirstInstallment().getExchangeRate().getDetail().getFactor().getRatio()));
            firstReceipt.setPremiumCurrencyExchAmount(BigDecimal.valueOf(asoResponse.getData().getFirstInstallment().getExchangeRate().getDetail().getFactor().getValue()));
        } else {
            firstReceipt.setFixingExchangeRateAmount(BigDecimal.ZERO);
            firstReceipt.setPremiumCurrencyExchAmount(BigDecimal.ZERO);
        }

        firstReceipt.setPremiumChargeOperationId(RBVDProperties.INSURANCE_PRODUCT_TYPE_VIDA_4.getValue().equals(requestBody.getProductId()) ? null : asoResponse.getData().getFirstInstallment().getOperationNumber().substring(1));
        firstReceipt.setCurrencyId(requestBody.getFirstInstallment().getPaymentAmount().getCurrency());

        if(requestBody.getFirstInstallment().getIsPaymentRequired()) {
            String correctFormatDate = FunctionsUtils.generateCorrectDateFormat(FunctionsUtils.convertDateToLocalDate(asoResponse.getData().getFirstInstallment().getOperationDate()));

            firstReceipt.setReceiptIssueDate(correctFormatDate);
            firstReceipt.setReceiptCollectionDate(correctFormatDate);
            firstReceipt.setReceiptsTransmissionDate(correctFormatDate);

            firstReceipt.setReceiptStatusType(ReceiptDefaultValues.FIRST_RECEIPT_STATUS_TYPE_VALUE);
        } else {
            firstReceipt.setReceiptIssueDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);
            firstReceipt.setReceiptCollectionDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);
            firstReceipt.setReceiptsTransmissionDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);

            firstReceipt.setReceiptStatusType(ReceiptDefaultValues.NEXT_RECEIPTS_STATUS_TYPE_VALUE);
        }

        firstReceipt.setReceiptStartDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);
        firstReceipt.setReceiptEndDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);

        LocalDate expirationDate = FunctionsUtils.convertDateToLocalDate(requestBody.getValidityPeriod().getStartDate());
        firstReceipt.setReceiptExpirationDate(FunctionsUtils.generateCorrectDateFormat(expirationDate));

        firstReceipt.setReceiptCollectionStatusType(ReceiptDefaultValues.COLLECTION_STATUS_FIRST_RECEIPT_VALUE);
        firstReceipt.setInsuranceCollectionMoveId(asoResponse.getData().getFirstInstallment().getTransactionNumber());
        firstReceipt.setPaymentMethodType(requestBody.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId().equals(Payment.CARD_PRODUCT_ID) ? Payment.CARD_METHOD_TYPE : Payment.ACCOUNT_METHOD_TYPE);
        firstReceipt.setDebitAccountId(requestBody.getPaymentMethod().getRelatedContracts().get(0).getContractId());
        firstReceipt.setDebitChannelType(requestBody.getSaleChannelId());
        firstReceipt.setCreationUserId(requestBody.getCreationUser());
        firstReceipt.setUserAuditId(requestBody.getUserAudit());
        firstReceipt.setManagementBranchId(requestBody.getBank().getBranch().getId());
        firstReceipt.setFixPremiumAmount(BigDecimal.valueOf(requestBody.getFirstInstallment().getPaymentAmount().getAmount()));
        firstReceipt.setSettlementFixPremiumAmount(BigDecimal.valueOf(requestBody.getTotalAmount().getAmount()));
        firstReceipt.setLastChangeBranchId(requestBody.getBank().getBranch().getId());
        firstReceipt.setInsuranceCompanyId(new BigDecimal(requestBody.getInsuranceCompany().getId()));
        firstReceipt.setGlBranchId(asoResponse.getData().getId().substring(4, 8));
        receiptList.add(firstReceipt);

        return receiptList;
    }

    public static List<InsuranceCtrReceiptsDAO> toGenerateMonthlyReceipts(InsuranceCtrReceiptsDAO firstReceipt) {
        List<InsuranceCtrReceiptsDAO> receiptList = new ArrayList<>();
        int receiptNumber = 2;
        for(int i = 0; i < 11; i++) {
            InsuranceCtrReceiptsDAO nextReceipt = new InsuranceCtrReceiptsDAO();
            nextReceipt.setEntityId(firstReceipt.getEntityId());
            nextReceipt.setBranchId(firstReceipt.getBranchId());
            nextReceipt.setIntAccountId(firstReceipt.getIntAccountId());
            nextReceipt.setInsuranceCompanyId(firstReceipt.getInsuranceCompanyId());
            nextReceipt.setPolicyReceiptId(BigDecimal.valueOf(receiptNumber++));
            nextReceipt.setPremiumPaymentReceiptAmount(BigDecimal.ZERO);
            nextReceipt.setFixingExchangeRateAmount(BigDecimal.ZERO);
            nextReceipt.setPremiumCurrencyExchAmount(BigDecimal.ZERO);
            nextReceipt.setCurrencyId(firstReceipt.getCurrencyId());
            nextReceipt.setReceiptIssueDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);
            nextReceipt.setReceiptStartDate(firstReceipt.getReceiptStartDate());
            nextReceipt.setReceiptEndDate(firstReceipt.getReceiptEndDate());
            nextReceipt.setReceiptCollectionDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);
            nextReceipt.setReceiptExpirationDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);
            nextReceipt.setReceiptsTransmissionDate(ReceiptDefaultValues.RECEIPT_DEFAULT_DATE_VALUE);
            nextReceipt.setReceiptCollectionStatusType(ReceiptDefaultValues.COLLECTION_STATUS_NEXT_VALUES);
            nextReceipt.setPaymentMethodType(firstReceipt.getPaymentMethodType());
            nextReceipt.setDebitAccountId(firstReceipt.getDebitAccountId());
            nextReceipt.setReceiptStatusType(ReceiptDefaultValues.NEXT_RECEIPTS_STATUS_TYPE_VALUE);
            nextReceipt.setCreationUserId(firstReceipt.getCreationUserId());
            nextReceipt.setUserAuditId(firstReceipt.getUserAuditId());
            nextReceipt.setManagementBranchId(firstReceipt.getManagementBranchId());
            nextReceipt.setFixPremiumAmount(firstReceipt.getFixPremiumAmount());
            nextReceipt.setSettlementFixPremiumAmount(BigDecimal.ZERO);
            nextReceipt.setLastChangeBranchId(firstReceipt.getLastChangeBranchId());
            nextReceipt.setGlBranchId(firstReceipt.getGlBranchId());

            receiptList.add(nextReceipt);
        }

        return receiptList;
    }

}
