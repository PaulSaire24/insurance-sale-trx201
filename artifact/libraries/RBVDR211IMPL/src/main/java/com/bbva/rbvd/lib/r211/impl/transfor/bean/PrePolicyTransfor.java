package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.insrncsale.aso.*;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BankASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BranchASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.BusinessAgentASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.*;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.convertDateToLocalDate;

public class PrePolicyTransfor {

    public static RequiredFieldsEmissionDAO toRequiredFieldsEmissionDAO(Map<String, Object> responseQueryGetRequiredFields, Map<String, Object> responseQueryGetPaymentPeriod){
        RequiredFieldsEmissionDAO emissionDao = new RequiredFieldsEmissionDAO();
        emissionDao.setInsuranceProductId((BigDecimal) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_ID.getValue()));
        emissionDao.setContractDurationNumber((BigDecimal) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_CONTRACT_DURATION_NUMBER.getValue()));
        emissionDao.setContractDurationType((String) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_CONTRACT_DURATION_TYPE.getValue()));
        emissionDao.setPaymentFrequencyId((BigDecimal) responseQueryGetPaymentPeriod.get(RBVDProperties.FIELD_PAYMENT_FREQUENCY_ID.getValue()));
        emissionDao.setInsuranceCompanyQuotaId((String) responseQueryGetRequiredFields.get(RBVDProperties.FIELD_INSURANCE_COMPANY_QUOTA_ID.getValue()));
        emissionDao.setInsuranceProductDesc((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue()));
        emissionDao.setInsuranceModalityName((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_INSURANCE_MODALITY_NAME.getValue()));
        emissionDao.setPaymentFrequencyName((String) responseQueryGetPaymentPeriod.get(PISDProperties.FIELD_PAYMENT_FREQUENCY_NAME.getValue()));
        emissionDao.setVehicleBrandName((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_BRAND_NAME.getValue()));
        emissionDao.setVehicleModelName((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_MODEL_NAME.getValue()));
        emissionDao.setVehicleYearId((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_YEAR_ID.getValue()));
        emissionDao.setVehicleLicenseId((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_LICENSE_ID.getValue()));
        emissionDao.setGasConversionType((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_GAS_CONVERSION_TYPE.getValue()));
        emissionDao.setVehicleCirculationType((String) responseQueryGetRequiredFields.get(PISDProperties.FIELD_VEHICLE_CIRCULATION_SCOPE_TYPE.getValue()));
        emissionDao.setCommercialVehicleAmount((BigDecimal) responseQueryGetRequiredFields.get(PISDProperties.FIELD_COMMERCIAL_VEHICLE_AMOUNT.getValue()));
        return emissionDao;
    }

    public static DataASO toDataASO(PolicyDTO apxRequest) {
        DataASO requestAso = new DataASO();

        requestAso.setQuotationId(apxRequest.getQuotationId());
        requestAso.setProductId(apxRequest.getProductId());

        ProductPlanASO productPlan = new ProductPlanASO();
        productPlan.setId(apxRequest.getProductPlan().getId());
        requestAso.setProductPlan(productPlan);

        PaymentMethodASO paymentMethod = new PaymentMethodASO();

        RelatedContractASO paymentRelatedContract = new RelatedContractASO();

        RelatedContractProductASO product = new RelatedContractProductASO();
        product.setId(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId());
        paymentRelatedContract.setProduct(product);
        paymentRelatedContract.setNumber(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getNumber());

        paymentMethod.setRelatedContracts(Collections.singletonList(paymentRelatedContract));
        paymentMethod.setPaymentType(apxRequest.getPaymentMethod().getPaymentType());
        paymentMethod.setInstallmentFrequency(apxRequest.getPaymentMethod().getInstallmentFrequency());
        requestAso.setPaymentMethod(paymentMethod);

        ValidityPeriodASO validityPeriod = new ValidityPeriodASO();
        validityPeriod.setStartDate(convertDateToLocalDate(apxRequest.getValidityPeriod().getStartDate()));

        requestAso.setValidityPeriod(validityPeriod);

        TotalAmountASO totalAmount = new TotalAmountASO();
        totalAmount.setAmount(apxRequest.getTotalAmount().getAmount());
        totalAmount.setCurrency(apxRequest.getTotalAmount().getCurrency());

        requestAso.setTotalAmount(totalAmount);

        InsuredAmountASO insuredAmount = new InsuredAmountASO();
        insuredAmount.setAmount(apxRequest.getInsuredAmount().getAmount());
        insuredAmount.setCurrency(apxRequest.getInsuredAmount().getCurrency());

        requestAso.setInsuredAmount(insuredAmount);

        HolderASO holder = HolderBean.toHolderASO(apxRequest);

        requestAso.setHolder(holder);

        InstallmentPlanASO installmentPlan = new InstallmentPlanASO();
        installmentPlan.setStartDate(convertDateToLocalDate(apxRequest.getInstallmentPlan().getStartDate()));
        installmentPlan.setMaturityDate(convertDateToLocalDate(apxRequest.getInstallmentPlan().getMaturityDate()));
        installmentPlan.setTotalNumberInstallments(apxRequest.getInstallmentPlan().getTotalNumberInstallments());

        PaymentPeriodASO period = new PaymentPeriodASO();
        period.setId(apxRequest.getInstallmentPlan().getPeriod().getId());

        installmentPlan.setPeriod(period);

        PaymentAmountASO paymentAmount = new PaymentAmountASO();
        paymentAmount.setAmount(apxRequest.getInstallmentPlan().getPaymentAmount().getAmount());
        paymentAmount.setCurrency(apxRequest.getInstallmentPlan().getPaymentAmount().getCurrency());

        installmentPlan.setPaymentAmount(paymentAmount);

        requestAso.setInstallmentPlan(installmentPlan);

        FirstInstallmentASO firstInstallment = new FirstInstallmentASO();
        firstInstallment.setIsPaymentRequired(apxRequest.getFirstInstallment().getIsPaymentRequired());

        requestAso.setFirstInstallment(firstInstallment);

        requestAso.setParticipants(getParticipantASO(apxRequest.getParticipants()));

        if(Objects.nonNull(apxRequest.getBusinessAgent())) {
            BusinessAgentASO businessAgent = new BusinessAgentASO();
            businessAgent.setId(apxRequest.getBusinessAgent().getId());
            requestAso.setBusinessAgent(businessAgent);
        }

        if(Objects.nonNull(apxRequest.getPromoter())) {
            PromoterASO promoter = new PromoterASO();
            promoter.setId(apxRequest.getPromoter().getId());
            requestAso.setPromoter(promoter);
        }

        if(Objects.nonNull(apxRequest.getSaleSupplier())) {
            SalesSupplierASO salesSupplier = new SalesSupplierASO();
            salesSupplier.setId(apxRequest.getSaleSupplier().getId());
            requestAso.setSalesSupplier(salesSupplier);
        }

        BankASO bank = new BankASO();

        BranchASO branch = new BranchASO();
        branch.setId(apxRequest.getBank().getBranch().getId());
        bank.setBranch(branch);
        bank.setId(apxRequest.getBank().getId());

        requestAso.setBank(bank);

        InsuranceCompanyASO insuranceCompany = new InsuranceCompanyASO();
        insuranceCompany.setId(apxRequest.getInsuranceCompany().getId());

        requestAso.setInsuranceCompany(insuranceCompany);

        return requestAso;
    }


    private static PaymentMethodASO mapInPaymentMethod(PolicyDTO apxRequest) {

        PaymentMethodASO paymentMethod = new PaymentMethodASO();

        RelatedContractASO paymentRelatedContract = new RelatedContractASO();
        RelatedContractProductASO product = new RelatedContractProductASO();
        product.setId(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getProduct().getId());
        paymentRelatedContract.setProduct(product);
        paymentRelatedContract.setNumber(apxRequest.getPaymentMethod().getRelatedContracts().get(0).getNumber());

        paymentMethod.setRelatedContracts(Collections.singletonList(paymentRelatedContract));
        paymentMethod.setPaymentType(apxRequest.getPaymentMethod().getPaymentType());
        paymentMethod.setInstallmentFrequency(apxRequest.getPaymentMethod().getInstallmentFrequency());
        return paymentMethod;
    }

    private static ProductPlanASO mapInProductPlan(String planId) {
        if(StringUtils.isEmpty(planId)){
           return null;
        }
        ProductPlanASO productPlanASO = new ProductPlanASO();
        productPlanASO.setId(planId);
        return productPlanASO;
    }

    private static List<ParticipantASO> getParticipantASO(List<ParticipantDTO> participants) {
        if (Objects.isNull(participants)) return null;
        return participants.stream().map(PrePolicyTransfor::createParticipantASO).collect(Collectors.toList());
    }

    private static ParticipantASO createParticipantASO(ParticipantDTO dto){
        ParticipantASO participant = new ParticipantASO();
        ParticipantTypeASO participantType = new ParticipantTypeASO();
        participantType.setId(dto.getParticipantType().getId());
        participant.setParticipantType(participantType);
        participant.setCustomerId(dto.getCustomerId());

        if (Objects.nonNull(dto.getIdentityDocument())){
            IdentityDocumentASO participantIdentityDocument = new IdentityDocumentASO();
            DocumentTypeASO participantDocumentType = new DocumentTypeASO();
            participantDocumentType.setId(dto.getIdentityDocument().getDocumentType().getId());
            participantIdentityDocument.setNumber(dto.getIdentityDocument().getNumber());
            participantIdentityDocument.setDocumentType(participantDocumentType);
            participant.setIdentityDocument(participantIdentityDocument);
        }
        return participant;
    }


    public static PolicyDTO toMapBranchAndSaleChannelIdOfficial(String branchId, String codeOfficeTelemarketing, PolicyDTO requestBody) {
        requestBody.getBank().getBranch().setId(branchId);
        requestBody.setSaleChannelId(codeOfficeTelemarketing);
        return requestBody;
    }




    public static PromoterDTO mapInPromoterId(String promoterCodeDefaultSaleDigital) {
        PromoterDTO promoter = new PromoterDTO();
        promoter.setId(promoterCodeDefaultSaleDigital);
        return promoter;
    }

    public static BusinessAgentDTO mapInBusinessAgentSaleDigital(String promoterCodeDefaultSaleDigital) {
        BusinessAgentDTO businessAgent = new BusinessAgentDTO();
        businessAgent.setId(promoterCodeDefaultSaleDigital);
        return businessAgent;
    }

    public static ContactDTO toContactDTO(String emailContact,String contactDetailType,String phoneContact) {
        ContactDTO firstContact = new ContactDTO();
        firstContact.setContactDetailType(contactDetailType);
        firstContact.setAddress(emailContact);
        firstContact.setPhoneNumber(phoneContact);
        return firstContact;
    }


    public static PolicyDTO toIsPaymentRequired(PolicyDTO requestBody, Boolean isPaymentRequired) {
        requestBody.getFirstInstallment().setIsPaymentRequired(isPaymentRequired);
        return requestBody;
    }
}
