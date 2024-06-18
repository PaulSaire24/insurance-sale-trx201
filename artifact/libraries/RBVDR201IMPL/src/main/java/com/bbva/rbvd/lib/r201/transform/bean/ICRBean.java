package com.bbva.rbvd.lib.r201.transform.bean;

import com.bbva.rbvd.dto.cicsconnection.ic.ICContract;
import com.bbva.rbvd.dto.cicsconnection.icr2.ICMRYS2;
import com.bbva.rbvd.dto.cicsconnection.icr2.ICR2Request;
import com.bbva.rbvd.dto.cicsconnection.icr2.enums.YesNoIndicator;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BusinessAgentASO;
import com.bbva.rbvd.dto.insrncsale.aso.ContractDetailsASO;
import com.bbva.rbvd.dto.insrncsale.aso.DocumentTypeASO;
import com.bbva.rbvd.dto.insrncsale.aso.ExchangeRateASO;
import com.bbva.rbvd.dto.insrncsale.aso.HolderASO;
import com.bbva.rbvd.dto.insrncsale.aso.IdentityDocumentASO;
import com.bbva.rbvd.dto.insrncsale.aso.PaymentAmountASO;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractProductASO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.lib.r201.util.FunctionsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ASO_VALUES.INTERNAL_CONTRACT_OUT;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ASO_VALUES.EXTERNAL_CONTRACT_OUT;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ASO_VALUES.INTERNAL_CONTRACT;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.ASO_VALUES.EXTERNAL_CONTRACT;

import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.DetailASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.FactorASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.FirstInstallmentASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.InstallmentPlanASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.InsuranceCompanyASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.InsuredAmountASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.ParticipantASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PaymentMethodASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PaymentPeriodASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.ProductPlanASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PromoterASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.SalesSupplierASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.StatusASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.TotalAmountASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.ValidityPeriodASO;

public class ICRBean {


    private ICRBean(){}


    public static ICContract mapIn(final DataASO inputCreateInsurance, final RBVDInternalConstants.INDICATOR_PRE_FORMALIZED indicatorPreFormalized) {
        ICContract format = new ICContract();
        format.setCODPRO(inputCreateInsurance.getProductId());
        mapInProductPlan(format, inputCreateInsurance.getProductPlan());
        mapInPaymentMethod(format, inputCreateInsurance.getPaymentMethod());
        mapInValidityPeriod(format, inputCreateInsurance.getValidityPeriod());
        mapInTotalAmount(format, inputCreateInsurance.getTotalAmount());
        mapInInsuredAmount(format, inputCreateInsurance.getInsuredAmount());
        mapInHolder(format, inputCreateInsurance.getHolder());
        mapInRelatedContracts(format, inputCreateInsurance.getRelatedContracts());
        mapInInstallmentPlan(format, inputCreateInsurance.getInstallmentPlan());
        mapInFirstInstallment(format, inputCreateInsurance.getFirstInstallment());
        mapInParticipants(format, inputCreateInsurance.getParticipants());
        mapInBusinessAgent(format, inputCreateInsurance.getBusinessAgent());
        mapInPromoter(format, inputCreateInsurance.getPromoter());
        mapInBank(format, inputCreateInsurance.getBank());
        mapInInsuranceCompany(format, inputCreateInsurance.getInsuranceCompany());
        mapInSalesSupplier(format,inputCreateInsurance.getSalesSupplier());
        format.setINDPREF(indicatorPreFormalized.getValue());
        return format;
    }

    private static void mapInSalesSupplier(final ICContract format, final SalesSupplierASO salesSupplier) {
        if (salesSupplier == null) {
            return;
        }
        format.setSUBCANA(salesSupplier.getId());
    }

    private static void mapInInsuranceCompany(final ICContract format, final InsuranceCompanyASO insuranceCompany) {
        if (insuranceCompany == null) {
            return;
        }

        format.setCODCIA(insuranceCompany.getId());
    }

    public static void mapInBank(final ICContract format, final com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO bank) {
        if (bank == null) {
            return;
        }

        format.setCODBAN(bank.getId());
        format.setOFICON(bank.getBranch() == null ? null : bank.getBranch().getId());
    }

    private static void mapInPromoter(final ICContract format, final PromoterASO promoter) {
        if (promoter == null) {
            return;
        }

        format.setPRESEN(promoter.getId());
    }

    private static void mapInBusinessAgent(final ICContract format, final BusinessAgentASO businessAgent) {
        if (businessAgent == null) {
            return;
        }

        format.setGESTOR(businessAgent.getId());
    }

    public static void mapInParticipants(final ICContract format, final List<ParticipantASO> participants) {
        if (CollectionUtils.isEmpty(participants) ||
                participants.get(0).getParticipantType() == null) {
            return;
        }
        for (ParticipantASO participant : participants) {
            format.setPARTIC(participant.getParticipantType().getId());
            if ("PAYMENT_MANAGER".equalsIgnoreCase(participant.getParticipantType().getId())) {
                format.setCODRSP(participant.getCustomerId());
                mapInParticipantsIdentityDocument(format, participant.getIdentityDocument());
            } else if ("LEGAL_REPRESENTATIVE".equalsIgnoreCase(participant.getParticipantType().getId())) {
                format.setCODRPL(participant.getCustomerId());
                mapInParticipantsIdentityDocumentLegal(format, participant.getIdentityDocument());
            }
        }
    }

    private static void mapInParticipantsIdentityDocument(final ICContract format, final IdentityDocumentASO identityDocument) {
        if (identityDocument == null) {
            return;
        }

        format.setNUMRSP(identityDocument.getNumber());
        format.setTIPDO1(identityDocument.getDocumentType() == null ? null : identityDocument.getDocumentType().getId());
    }

    private static void mapInParticipantsIdentityDocumentLegal(final ICContract format, final IdentityDocumentASO identityDocument) {
        if (identityDocument == null) {
            return;
        }

        format.setNUMRPL(identityDocument.getNumber());
        format.setTIPDOR(identityDocument.getDocumentType() == null ? null : identityDocument.getDocumentType().getId());
    }

    private static void mapInFirstInstallment(final ICContract format, final FirstInstallmentASO firstInstallment) {
        if (firstInstallment == null) {
            return;
        }
        YesNoIndicator yesNoIndicator = firstInstallment.getIsPaymentRequired() ? YesNoIndicator.YES : YesNoIndicator.NO;
        format.setCOBRO(yesNoIndicator);
    }

    private static void mapInInstallmentPlan(final ICContract format, final InstallmentPlanASO installmentPlan) {
        if (installmentPlan == null) {
            return;
        }

        format.setFECPAG(FunctionsUtils.generateCorrectDateFormat(installmentPlan.getStartDate()));
        format.setNUMCUO(installmentPlan.getTotalNumberInstallments());
        mapInInstallmentPeriod(format, installmentPlan.getPeriod());
        mapInInstallmentPaymentAmount(format, installmentPlan.getPaymentAmount());
    }

    private static void mapInInstallmentPaymentAmount(final ICContract format, final PaymentAmountASO paymentAmount) {
        if (paymentAmount == null) {
            return;
        }

        format.setMTOCUO(BigDecimal.valueOf(paymentAmount.getAmount()));
        format.setDIVCUO(paymentAmount.getCurrency());
    }

    private static void mapInInstallmentPeriod(final ICContract format, final PaymentPeriodASO period) {
        if (period == null) {
            return;
        }

        format.setTFOPAG(period.getId());
    }

    private static void mapInRelatedContracts(final ICContract format, final List<RelatedContractASO> relatedContracts) {
        if (CollectionUtils.isEmpty(relatedContracts) || relatedContracts.get(0).getRelatedContracts() == null) {
            return;
        }
        format.setTCONVIN(relatedContracts.get(0).getContractDetails().getContractType());
        if (INTERNAL_CONTRACT.equalsIgnoreCase(format.getTCONVIN())) {
            format.setCONVIN(relatedContracts.get(0).getContractDetails().getContractId());
        } else if (EXTERNAL_CONTRACT.equalsIgnoreCase(format.getTCONVIN())) {
            format.setCONVIN(relatedContracts.get(0).getContractDetails().getNumber());
        }
    }

    private static void mapInHolder(final ICContract format, final HolderASO holder) {
        if (holder == null) {
            return;
        }

        format.setCODASE(holder.getId());
        mapInHolderIdentityDocument(format, holder.getIdentityDocument());

    }

    private static void mapInHolderIdentityDocument(final ICContract format, final IdentityDocumentASO identityDocument) {
        if (identityDocument == null) {
            return;
        }

        format.setNUMASE(identityDocument.getNumber());
        format.setTIPDOC(identityDocument.getDocumentType() == null ? null : identityDocument.getDocumentType().getId());
    }

    private static void mapInInsuredAmount(final ICContract format, final InsuredAmountASO insuredAmount) {
        if (insuredAmount == null) {
            return;
        }

        format.setSUMASE(BigDecimal.valueOf(insuredAmount.getAmount()));
        format.setDIVSUM(insuredAmount.getCurrency());
    }

    private static void mapInTotalAmount(final ICContract format, final TotalAmountASO totalAmount) {
        if (totalAmount == null) {
            return;
        }

        format.setPRITOT(BigDecimal.valueOf(totalAmount.getAmount()));
        format.setDIVPRI(totalAmount.getCurrency());
    }

    private static void mapInValidityPeriod(final ICContract format, final ValidityPeriodASO validityPeriod) {
        if (validityPeriod == null) {
            return;
        }

        format.setFECINI(FunctionsUtils.generateCorrectDateFormat(validityPeriod.getStartDate()));
    }

    public static void mapInPaymentMethod(final ICContract format, final PaymentMethodASO paymentMethod) {
        if (paymentMethod == null) {
            return;
        }

        format.setMTDPGO(paymentMethod.getPaymentType());
        format.setTFOPAG( paymentMethod.getInstallmentFrequency());

        if (CollectionUtils.isEmpty(paymentMethod.getRelatedContracts())) {
            return;
        }
        format.setNROCTA(paymentMethod.getRelatedContracts().get(0).getNumber());
        mapInPaymentMethodRelatedContracts(format, paymentMethod.getRelatedContracts().get(0));
    }

    private static void mapInPaymentMethodRelatedContracts(final ICContract format, final RelatedContractASO dtoIntRelatedContract) {
        if (dtoIntRelatedContract == null || dtoIntRelatedContract.getProduct() == null) {
            return;
        }
        if (dtoIntRelatedContract.getProduct().getId() != null) {
            format.setMEDPAG(dtoIntRelatedContract.getProduct().getId());
        }
    }

    private static void mapInProductPlan(final ICContract format, final ProductPlanASO productPlan) {
        if (productPlan == null) {
            return;
        }

        format.setCODMOD(productPlan.getId());
    }




    public static DataASO mapOut(final ICMRYS2 formato) {
        DataASO result = new DataASO();
        result.setId(formato.getNUMCON());
        result.setPolicyNumber(formato.getNUMPOL());
        result.setProductId(formato.getCODPRO());
        result.setProductDescription(formato.getNOMPRO());
        result.setProductPlan(mapOutProductPlan(formato.getCODMOD(), formato.getNOMMOD()));
        result.setPaymentMethod(mapOutPaymentMethod(formato.getMTDPGO(), formato.getTFOPAG(), formato.getNROCTA(), formato.getMEDPAG()));
        result.setOperationDate(FunctionsUtils.convertStringToDate(formato.getFECCTR()));
        result.setValidityPeriod(mapOutValidityPeriod(formato.getFECINI(), formato.getFECFIN()));
        result.setTotalAmount(mapOutTotalAmount(formato.getPRITOT(), formato.getDIVPRI(), formato.getFCPRIB(), formato.getDIVPRI(), formato.getDIVTCM(), formato.getMTOTCM(), formato.getTCPRIB(), formato.getTCMBCV()));
        result.setInsuredAmount(mapOutInsuredAmount(formato.getSUMASE(), formato.getDIVSUM()));
        result.setHolder(mapOutHolder(formato.getCODASE(), formato.getTIPDOC(), formato.getNUMASE()));
        result.setRelatedContracts(mapOutRelatedContracts(formato.getTCONVIN(), formato.getCONVIN()));
        result.setInstallmentPlan(mapOutInstallmentPlan(formato));
        result.setFirstInstallment(mapOutFirstInstallment(formato));
        mapOutParticipants(formato, result);
        result.setBusinessAgent(mapOutBusinessAgent(formato.getGESTOR()));
        result.setPromoter(mapOutPromoter(formato.getPRESEN()));
        result.setInsuranceCompany(mapOutInsuranceCompany(formato.getCODCIA(), formato.getNOMCIA()));
        result.setStatus(mapOutStatus(formato.getCSTCON(), formato.getDSTCON()));
        result.setBank(mapOutBank(formato.getCODBAN(), formato.getOFICON()));
        result.setSalesSupplier(mapOutSalesSupplier(formato.getSUBCANL()));
        return result;
    }

    private static com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO mapOutBank(final String id, final String branchId) {
        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(branchId)) {
            return null;
        }

        com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO result = new com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO();
        result.setId(id);
        result.setBranch(mapOutBranch(branchId));
        return result;
    }

    private static com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO mapOutBranch(final String branchId) {
        if (branchId == null) {
            return null;
        }

        com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO result = new com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO();
        result.setId(branchId);
        return result;
    }

    private static StatusASO mapOutStatus(final String id, final String description) {
        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(description)) {
            return null;
        }

        StatusASO result = new StatusASO();
        result.setId(id);
        result.setDescription(description);
        return result;
    }

    private static InsuranceCompanyASO mapOutInsuranceCompany(final String id, final String name) {
        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(name)) {
            return null;
        }

        InsuranceCompanyASO result = new InsuranceCompanyASO();
        result.setId(id);
        result.setName(name);
        return result;
    }

    private static PromoterASO mapOutPromoter(final String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }

        PromoterASO result = new PromoterASO();
        result.setId(id);
        return result;
    }

    private static SalesSupplierASO mapOutSalesSupplier(final String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }

        SalesSupplierASO result = new SalesSupplierASO();
        result.setId(id);
        return result;
    }

    private static BusinessAgentASO mapOutBusinessAgent(final String id) {
        if (StringUtils.isEmpty(id )) {
            return null;
        }

        BusinessAgentASO result = new BusinessAgentASO();
        result.setId(id);
        return result;
    }

    private static void mapOutParticipants(final ICMRYS2 formato, DataASO insurance) {
        if (formato.getCODRSP() == null && (StringUtils.isEmpty(formato.getTIPDO1()) || StringUtils.isEmpty(formato.getNUMRSP()))) {
            return;
        }
        if (CollectionUtils.isEmpty(insurance.getParticipants())) {
            insurance.setParticipants(new ArrayList<>());
        }
        if ("LEGAL_REPRESENTATIVE".equalsIgnoreCase(formato.getPARTIC())) {
            insurance.getParticipants().add(mapOutParticipant(formato.getCODRSP(), formato.getTIPDOR(), formato.getNUMRPL()));
        }
        insurance.getParticipants().add(mapOutParticipant(formato.getCODRSP(), formato.getTIPDO1(), formato.getNUMRSP()));
    }
    private static ParticipantASO mapOutParticipant(String id, String documentTypeId, String number) {
        ParticipantASO result = new ParticipantASO();
        result.setId(id);
        result.setCustomerId(id);
        result.setIdentityDocument(mapOutParticipantIdentityDocument(documentTypeId, number));
        return result;
    }

    private static IdentityDocumentASO mapOutParticipantIdentityDocument(final String documentTypeId, final String number) {
        if (StringUtils.isEmpty(documentTypeId) && StringUtils.isEmpty(number)) {
            return null;
        }

        IdentityDocumentASO result = new IdentityDocumentASO();
        result.setNumber(number);
        result.setDocumentType(mapOutParticipantDocumentType(documentTypeId));
        return result;
    }

    private static DocumentTypeASO mapOutParticipantDocumentType(final String documentTypeId) {
        if (StringUtils.isEmpty(documentTypeId)) {
            return null;
        }

        DocumentTypeASO result = new DocumentTypeASO();
        result.setId(documentTypeId);
        return result;
    }

    public static FirstInstallmentASO mapOutFirstInstallment(final ICMRYS2 formato) {
        if (StringUtils.isEmpty(formato.getFECPAG())  && formato.getCOBRO() == null &&
                formato.getMTOCUO() == null && StringUtils.isEmpty(formato.getDIVCUO()) &&
                StringUtils.isEmpty(formato.getFECTPC()) && formato.getTIPCAM() == null &&
                StringUtils.isEmpty(formato.getCVCAMB()) && StringUtils.isEmpty(formato.getIDNOPE()) &&
                StringUtils.isEmpty(formato.getNUMMOV()) && StringUtils.isEmpty(formato.getFECOPE()) &&
                StringUtils.isEmpty(formato.getDIVTCM2()) && formato.getMTOCAM() == null &&
                StringUtils.isEmpty(formato.getFECCON())) {
            return null;
        }

        FirstInstallmentASO result = new FirstInstallmentASO();
        result.setFirstPaymentDate(StringUtils.isEmpty(formato.getFECPAG())  ? null :  FunctionsUtils.convertStringToLocalDate(formato.getFECPAG()));
        result.setIsPaymentRequired(FunctionsUtils.convertFromStringToBoolean(formato.getCOBRO()));
        result.setPaymentAmount(mapOutPaymenAmount(formato.getMTOCUO(), formato.getDIVCUO()));
        result.setExchangeRate(mapOutExchangeRate(formato.getFECTPC(), formato.getDIVCUO(), formato.getDIVTCM2(), formato.getMTOCAM(), formato.getTIPCAM(), formato.getCVCAMB()));
        result.setOperationNumber(formato.getIDNOPE());
        result.setTransactionNumber(formato.getNUMMOV());
        result.setOperationDate(StringUtils.isEmpty(formato.getFECOPE()) ? null : FunctionsUtils.convertStringToDate(formato.getFECOPE()));
        result.setAccountingDate(StringUtils.isEmpty(formato.getFECCON()) ? null : FunctionsUtils.convertStringToLocalDate(formato.getFECCON()));
        return result;
    }


    public static ExchangeRateASO mapOutExchangeRate(final String date, final String baseCurrency, final String targetCurrency, final BigDecimal value, final BigDecimal ratio, final String priceType) {
        if (StringUtils.isEmpty(date) && StringUtils.isEmpty(baseCurrency) && StringUtils.isEmpty(targetCurrency) &&
                value == null && ratio == null && StringUtils.isEmpty(priceType)) {
            return null;
        }

        ExchangeRateASO result = new ExchangeRateASO();
        result.setDate(StringUtils.isEmpty(date) ? null : FunctionsUtils.convertStringToLocalDate(date));
        result.setBaseCurrency(baseCurrency);
        result.setTargetCurrency(targetCurrency);
        result.setDetail(mapOutDetail(value, ratio, priceType));
        return result;
    }

    public static DetailASO mapOutDetail(final BigDecimal value, final BigDecimal ratio, final String priceType) {
        if (value == null && ratio == null && priceType == null) {
            return null;
        }

        DetailASO result = new DetailASO();
        result.setPriceType(priceType);
        result.setFactor(mapOutFactor(value, ratio));
        return result;
    }

    public static FactorASO mapOutFactor(final BigDecimal value, final BigDecimal ratio) {
        if (value == null && ratio == null) {
            return null;
        }

        FactorASO result = new FactorASO();
        result.setValue(Optional.ofNullable(value).map(BigDecimal::doubleValue).orElse(null));
        result.setRatio((Optional.ofNullable(ratio).map(BigDecimal::doubleValue).orElse(null)));
        return result;
    }

    public static PaymentAmountASO mapOutPaymenAmount(final BigDecimal amount, final String currency) {
        if (amount == null && currency == null) {
            return null;
        }

        PaymentAmountASO result = new PaymentAmountASO();
        result.setAmount(Optional.ofNullable(amount).map(BigDecimal::doubleValue).orElse(null));
        result.setCurrency(currency);
        return result;
    }

    public static InstallmentPlanASO mapOutInstallmentPlan(final ICMRYS2 formato) {
        if (StringUtils.isEmpty(formato.getFECPAG())  &&  formato.getNUMCUO() == null && StringUtils.isEmpty(formato.getTFOPAG()) && StringUtils.isEmpty(formato.getDSCTPA()) &&
                formato.getMTOCUO() == null &&  StringUtils.isEmpty(formato.getFECTPC()) && StringUtils.isEmpty(formato.getDIVCUO()) &&
                StringUtils.isEmpty(formato.getDIVTCM2()) && formato.getMTOCAM() == null && formato.getTIPCAM() == null && StringUtils.isEmpty(formato.getCVCAMB())) {
            return null;
        }

        InstallmentPlanASO result = new InstallmentPlanASO();
        result.setStartDate(StringUtils.isEmpty(formato.getFECPAG()) ? null :  FunctionsUtils.convertStringToLocalDate(formato.getFECPAG()));
        result.setTotalNumberInstallments(Optional.ofNullable(formato.getNUMCUO()).map(Long::valueOf).orElse(null));
        result.setPeriod(mapOutPeriod(formato.getTFOPAG(), formato.getDSCTPA()));
        result.setPaymentAmount(mapOutPaymentAmount(formato.getMTOCUO(), formato.getDIVCUO()));
        result.setExchangeRate(mapOutExchangeRate(formato.getFECTPC() , formato.getDIVCUO() , formato.getDIVTCM2(), formato.getMTOCAM(), formato.getTIPCAM(), formato.getCVCAMB()));
        return result;
    }


    public static PaymentAmountASO mapOutPaymentAmount(final BigDecimal paymentAmountAmount, final String paymentAmountCurrency) {
        if (paymentAmountAmount == null && StringUtils.isEmpty(paymentAmountCurrency)) {
            return null;
        }

        PaymentAmountASO result = new PaymentAmountASO();
        result.setAmount(Optional.ofNullable(paymentAmountAmount).map(BigDecimal::doubleValue).orElse(null));
        result.setCurrency(paymentAmountCurrency);
        return result;
    }

    public static PaymentPeriodASO mapOutPeriod(final String periodId, final String periodName) {
        if (periodId == null && periodName == null) {
            return null;
        }

        PaymentPeriodASO result = new PaymentPeriodASO();
        result.setId(periodId);
        result.setName(periodName);
        return result;
    }

    public static List<RelatedContractASO> mapOutRelatedContracts(final String contractType, final String contractNumberId) {
        if (StringUtils.isEmpty(contractType) && StringUtils.isEmpty(contractNumberId)) {
            return null;
        }

        RelatedContractASO result = new RelatedContractASO();
        result.setContractDetails(mapOutContractDeatils(contractType, contractNumberId));
        return Collections.singletonList(result);
    }

    public static ContractDetailsASO mapOutContractDeatils(final String contractType, final String contractNumberId) {
        ContractDetailsASO result = null;
        if (INTERNAL_CONTRACT_OUT.equalsIgnoreCase(contractType)) {
            result = new ContractDetailsASO();
            result.setContractType(contractType);
            result.setContractId(contractNumberId);
        } else if (EXTERNAL_CONTRACT_OUT.equalsIgnoreCase(contractType)) {
            result = new ContractDetailsASO();
            result.setContractType(contractType);
            result.setNumber(contractNumberId);
        }
        return result;
    }

    public static HolderASO mapOutHolder(final String id, final String documentType, final String identityDocumentNumber) {
        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(documentType) && StringUtils.isEmpty(identityDocumentNumber)) {
            return null;
        }

        HolderASO result = new HolderASO();
        result.setId(id);
        result.setIdentityDocument(mapOutIdentityDocument(documentType, identityDocumentNumber));
        return result;
    }

    public static IdentityDocumentASO mapOutIdentityDocument(final String documentType, final String identityDocumentNumber) {
        if (StringUtils.isEmpty(documentType) && StringUtils.isEmpty(identityDocumentNumber)) {
            return null;
        }

        IdentityDocumentASO result = new IdentityDocumentASO();
        result.setDocumentType(mapOutDocumentType(documentType));
        result.setNumber(identityDocumentNumber);
        return result;
    }

    public static DocumentTypeASO mapOutDocumentType(final String documentType) {
        if (StringUtils.isEmpty(documentType)) {
            return null;
        }

        DocumentTypeASO result = new DocumentTypeASO();
        result.setId(documentType);
        return result;
    }

    public static InsuredAmountASO mapOutInsuredAmount(final BigDecimal amount, final String currency) {
        if (amount == null && StringUtils.isEmpty(currency)) {
            return null;
        }

        InsuredAmountASO result = new InsuredAmountASO();
        result.setAmount(Optional.ofNullable(amount).map(BigDecimal::doubleValue).orElse(null));
        result.setCurrency(currency);
        return result;
    }

    public static TotalAmountASO mapOutTotalAmount(final BigDecimal amount, final String currency, final String date, final String baseCurrency, final String targetCurrency, final BigDecimal value, final BigDecimal ratio, final String priceType) {
        if (amount == null &&  StringUtils.isEmpty(currency)) {
            return null;
        }

        TotalAmountASO result = new TotalAmountASO();
        result.setAmount(Optional.ofNullable(amount).map(BigDecimal::doubleValue).orElse(null));
        result.setCurrency(currency);
        result.setExchangeRate(mapOutExchangeRate(date, baseCurrency, targetCurrency, value, ratio, priceType));
        return result;
    }

    public static ValidityPeriodASO mapOutValidityPeriod(final String startDate, final String endDate) {
        if (StringUtils.isEmpty(startDate)  && StringUtils.isEmpty(endDate)) {
            return null;
        }

        ValidityPeriodASO result = new ValidityPeriodASO();
        result.setStartDate(StringUtils.isEmpty(startDate) ? null : FunctionsUtils.convertStringToLocalDate(startDate));
        result.setEndDate(StringUtils.isEmpty(startDate) ? null : FunctionsUtils.convertStringToLocalDate(endDate));
        return result;
    }

    public static PaymentMethodASO mapOutPaymentMethod(final String paymentType, final String installmentFrequency, final String relatedContractNumber, final String relatedContractProductId) {
        if (StringUtils.isEmpty(paymentType) && StringUtils.isEmpty(installmentFrequency) &&
                StringUtils.isEmpty(relatedContractNumber) && StringUtils.isEmpty(relatedContractProductId)) {
            return null;
        }

        PaymentMethodASO result = new PaymentMethodASO();
        result.setPaymentType(paymentType);
        result.setInstallmentFrequency(installmentFrequency);
        result.setRelatedContracts(mapOutPaymentMethodRelatedContracts(relatedContractNumber, relatedContractProductId));
        return result;
    }

    public static List<RelatedContractASO> mapOutPaymentMethodRelatedContracts(final String relatedContractNumber, final String relatedContractProductId) {
        if (StringUtils.isEmpty( relatedContractNumber)  && StringUtils.isEmpty(relatedContractProductId)) {
            return null;
        }

        RelatedContractASO result = new RelatedContractASO();
        result.setNumber(relatedContractNumber);
        result.setProduct(mapOutPaymentMethodRelatedContractsProduct(relatedContractProductId));
        return Collections.singletonList(result);
    }

    public static RelatedContractProductASO mapOutPaymentMethodRelatedContractsProduct(final String relatedContractProductId) {
        if (StringUtils.isEmpty(relatedContractProductId)) {
            return null;
        }

        RelatedContractProductASO result = new RelatedContractProductASO();
        result.setId(relatedContractProductId);
        return result;
    }

    public static ProductPlanASO mapOutProductPlan(final String id, final String description) {
        if (StringUtils.isEmpty(id) && StringUtils.isEmpty(description)) {
            return null;
        }

        ProductPlanASO result = new ProductPlanASO();
        result.setId(id);
        result.setDescription(description);
        return result;
    }



}
