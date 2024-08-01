package com.bbva.rbvd.lib.r211.impl.pattern.crossoperations;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.AgregarTerceroBO;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.commons.HolderDTO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.BusinessAgentDTO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PromoterDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDErrors;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDValidation;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalColumn;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.api.CryptoServiceInternal;
import com.bbva.rbvd.lib.r211.impl.service.api.CustomerRBVD066InternalService;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsrcContractParticipantBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Endorsement;
import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.*;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.isValidateRange;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

public class CrossOperationsBusinessInsuranceContractBank {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrossOperationsBusinessInsuranceContractBank.class);

    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private CryptoServiceInternal cryptoServiceInternal;
    private CustomerRBVD066InternalService customerRBVD066InternalService;
    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    /**
     * This method is used to validate the quotation amount of a policy.
     * It checks if the validation is enabled, and if so, it retrieves the quotation data and performs several validations.
     * These validations include checking the frequency type, payment currency ID, payment amount, and total amount.
     * It also validates the first installment and installment plan of the policy.
     * If any of these validations fail, it throws a validation exception.
     *
     * @param enableValidationQuotationAmount A boolean indicating whether the validation of the quotation amount is enabled.
     * @param quotationData The DAO used to retrieve the quotation data.
     * @param requestBody The policy data transfer object (DTO) that contains the data to be validated.
     * @throws BusinessException If any of the validations fail.
     */
    public void validateQuotationAmount(boolean enableValidationQuotationAmount, Map<String, Object> quotationData, PolicyDTO requestBody) {
        if(enableValidationQuotationAmount){
            String frequencyType = ofNullable(quotationData.get(RBVDInternalColumn.PaymentPeriod.FIELD_POLICY_PAYMENT_FREQUENCY_TYPE)).map(Object::toString).orElseThrow(() -> {
                String message = String.format(ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE.getMessage(), requestBody.getQuotationId());
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE.getAdviceCode(),message);
                return buildValidation(ERROR_NOT_VALUE_QUOTATION_FREQUENCY_TYPE, message);
            });
            String paymentCurrencyId = ofNullable(quotationData.get(RBVDInternalColumn.Quotation.FIELD_PREMIUM_CURRENCY_ID)).map(Object::toString).orElseThrow(() -> {
                String message = String.format(ERROR_NOT_VALUE_QUOTATION.getMessage(),RBVDInternalColumn.Quotation.FIELD_PREMIUM_CURRENCY_ID, requestBody.getQuotationId());
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_NOT_VALUE_QUOTATION.getAdviceCode(),message);
                return buildValidation(ERROR_NOT_VALUE_QUOTATION_CURRENCY_ID,message);
            });
            int paymentAmount = ofNullable(quotationData.get(RBVDInternalColumn.Quotation.FIELD_PREMIUM_AMOUNT)).map(premiumAmount -> new BigDecimal(premiumAmount.toString()).intValue()).orElseThrow(() -> {
                String message = String.format(ERROR_NOT_VALUE_QUOTATION.getMessage(),RBVDInternalColumn.Quotation.FIELD_PREMIUM_AMOUNT, requestBody.getQuotationId());
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_NOT_VALUE_QUOTATION.getAdviceCode(),message);
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
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getAdviceCode(),message);
                throw buildValidation(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID,message);
            }else if(RBVDInternalConstants.Period.ANNUAL.equalsIgnoreCase(frequencyType) && !isValidateRange(totalAmount, amountQuotationMin, amountQuotationMax) ){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), totalAmount, amountQuotationMin,amountQuotationMax);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }else if(RBVDInternalConstants.Period.MONTHLY.equalsIgnoreCase(frequencyType) && !isValidateRange(totalAmount, amountTotalAmountMin, amountTotalAmountMax) ){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), totalAmount, amountTotalAmountMin,amountTotalAmountMax);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }

            if(!paymentCurrencyId.equals(requestBody.getFirstInstallment().getPaymentAmount().getCurrency())){
                String message = String.format(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getMessage(), paymentCurrencyId, totalAmountCurrencyId);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getAdviceCode(),message);
                throw buildValidation(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID,message);
            }else if(!isValidateRange(requestBody.getFirstInstallment().getPaymentAmount().getAmount().intValue(), amountQuotationMin, amountQuotationMax)){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), requestBody.getFirstInstallment().getPaymentAmount().getAmount().intValue(), amountQuotationMin,amountQuotationMax);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }

            if(!paymentCurrencyId.equals(requestBody.getInstallmentPlan().getPaymentAmount().getCurrency())){
                String message = String.format(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getMessage(), paymentCurrencyId, totalAmountCurrencyId);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID.getAdviceCode(),message);
                throw buildValidation(ERROR_NOT_VALUE_REQUEST_CURRENCY_ID,message);
            }else if(!isValidateRange(requestBody.getInstallmentPlan().getPaymentAmount().getAmount().intValue(), amountQuotationMin, amountQuotationMax)){
                String message = String.format(ERROR_VALID_RANGE_AMOUNT.getMessage(), requestBody.getInstallmentPlan().getPaymentAmount().getAmount().intValue(), amountQuotationMin,amountQuotationMax);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_VALID_RANGE_AMOUNT.getAdviceCode(),message);
                throw buildValidation(ERROR_VALID_RANGE_AMOUNT,message);
            }

        }
    }

    /**
     * This method is used to transform a list of participants from a policy into a list of IsrcContractParticipantDAO objects.
     *
     * @param requestBody The policy data transfer object (DTO) that contains the participants to be transformed.
     * @param rolesFromDB A list of roles from the database. Each role is represented as a map with string keys and object values.
     * @param id The id to be used when creating the IsrcContractParticipantDAO objects.
     * @param applicationConfigurationService The service used for application configuration.
     *
     * @return A list of IsrcContractParticipantDAO objects created from the participants in the policy DTO.
     */
    public static List<IsrcContractParticipantDAO> toIsrcContractParticipantDAOList(PolicyDTO requestBody, List<Map<String, Object>> rolesFromDB, String id, ApplicationConfigurationService applicationConfigurationService){
        // Get the first participant from the policy DTO
        ParticipantDTO participant = requestBody.getParticipants().get(0);

        // Filter the participants to get the legal representative
        ParticipantDTO legalRepre = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),
                ConstantsUtil.Participant.LEGAL_REPRESENTATIVE);

        // Filter the participants to get the beneficiaries
        List<ParticipantDTO> beneficiary = ValidationUtil.filterBenficiaryType(requestBody.getParticipants(),
                ConstantsUtil.Participant.BENEFICIARY);

        // Filter the participants to get the insured
        ParticipantDTO insured = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),
                ConstantsUtil.Participant.INSURED);

        // Create a list to store the roles of the participants
        List<BigDecimal> participantRoles = new ArrayList<>();

        // Add the roles from the database to the list
        rolesFromDB.forEach(rol -> participantRoles.add((BigDecimal) rol.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())));

        // If there is a legal representative, remove their role from the list
        if(legalRepre != null){
            participantRoles.removeIf(rol -> rol.compareTo(new BigDecimal(ConstantsUtil.Number.TRES)) == 0);
        }

        // If there is an insured, remove their role from the list
        if(insured != null){
            participantRoles.removeIf(rol -> rol.compareTo(new BigDecimal(ConstantsUtil.Number.DOS)) == 0);
        }

        // Create a list of IsrcContractParticipantDAO objects from the participant roles
        List<IsrcContractParticipantDAO> listParticipants = participantRoles.stream()
                .map(rol -> InsrcContractParticipantBean.createParticipantDao(id,rol,participant,requestBody,applicationConfigurationService))
                .collect(Collectors.toList());

        // If there is a legal representative, add them to the list
        if(legalRepre != null){
            listParticipants.add(
                    InsrcContractParticipantBean.createParticipantDao(id,
                            new BigDecimal(ConstantsUtil.Number.TRES),
                            legalRepre,requestBody,applicationConfigurationService));
        }

        // If there is an insured, add them to the list
        if(insured != null){
            listParticipants.add(
                    InsrcContractParticipantBean.createParticipantDao(id,new BigDecimal(ConstantsUtil.Number.DOS),
                            insured,requestBody,applicationConfigurationService));
        }

        // If there are beneficiaries, add them to the list
        if(beneficiary != null){
            BigDecimal partyOrderNumber = BigDecimal.valueOf(1L);
            for(ParticipantDTO benef : beneficiary){
                partyOrderNumber = partyOrderNumber.add(BigDecimal.valueOf(1L));
                listParticipants.add(InsrcContractParticipantBean.createParticipantBeneficiaryDao(id,new BigDecimal(ConstantsUtil.Number.DOS),
                        benef,requestBody,applicationConfigurationService,partyOrderNumber));
            }
        }

        // Return the list of IsrcContractParticipantDAO objects
        return listParticipants;
    }

    public void validateResponseAddParticipantsService(AgregarTerceroBO responseAddParticipants) {
        if (responseAddParticipants == null || responseAddParticipants.getErrorRimacBO() != null) {
            throw RBVDValidation.build(RBVDErrors.ERROR_CALL_ADD_PARTICIPANTS_RIMAC_SERVICE);
        }
    }

    public static List<ContactDTO> obtainContactDetails(String emailEncrypt, String phoneEncrypt,GetContactDetailsASO contactDetails) {
        List<ContactDTO> contacts = new ArrayList<>();
        String emailContact = contactDetails.getData().stream().filter(contact -> emailEncrypt.contains(contact.getContactDetailId()))
                .findFirst().map(ContactDetailsBO::getContact)
                .orElse(String.format(RBVDInternalConstants.Messages.ERROR_DEFAULT_CONTACT_DETAIL, RBVDInternalConstants.ContactDetailNomenclature.ERROR_SPANISH_EMAIL));
        String phoneContact = contactDetails.getData().stream().filter(contact -> phoneEncrypt.contains(contact.getContactDetailId()))
                .findFirst().map(ContactDetailsBO::getContact)
                .orElse(String.format(RBVDInternalConstants.Messages.ERROR_DEFAULT_CONTACT_DETAIL, RBVDInternalConstants.ContactDetailNomenclature.ERROR_SPANISH_PHONE));
        ContactDTO firstContact = PrePolicyTransfor.toContactDTO(emailContact, PISDConstants.ContactDetail.EMAIL_TYPE, StringUtils.EMPTY);
        ContactDTO secondContact = PrePolicyTransfor.toContactDTO(StringUtils.EMPTY, PISDConstants.ContactDetail.MOBILE_TYPE, phoneContact);
        contacts.add(firstContact);
        contacts.add(secondContact);
        return contacts;
    }


    public Boolean evaluateRequiredPayment(Date startDate) {
        Date currentDate = FunctionsUtils.currentDate();
        Boolean isPaymentRequired;
        if(startDate.after(currentDate)) {
            isPaymentRequired = Boolean.FALSE;
        } else {
            isPaymentRequired = Boolean.TRUE;
        }
        return isPaymentRequired;
    }

    public static boolean validateEndorsement(PolicyDTO requestBody) {
        if( !CollectionUtils.isEmpty(requestBody.getParticipants())  && requestBody.getParticipants().size() > 1) {
            if (requestBody.getParticipants().get(1).getIdentityDocument() != null
                    && Endorsement.ENDORSEMENT.equals(requestBody.getParticipants().get(1).getParticipantType().getId())
                    && Endorsement.RUC.equals(requestBody.getParticipants().get(1).getIdentityDocument().getDocumentType().getId())
                    && requestBody.getParticipants().get(1).getBenefitPercentage() != null) {
                return true;
            }
        }
        return false;
    }

    public void validatePaymentPeriodData(Map<String, Object> paymentPeriodData, String frequencyType) {
        if(CollectionUtils.isEmpty(paymentPeriodData)){
            String message =  String.format( ERROR_EMPTY_RESULT_FREQUENCY_TYPE.getMessage(),frequencyType);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_EMPTY_RESULT_FREQUENCY_TYPE.getAdviceCode(),message);
            throw buildValidation(ERROR_EMPTY_RESULT_FREQUENCY_TYPE,message);
        }
    }

    public void validateQuotationData(Map<String, Object> quotationData, String quotationId) {
        if(CollectionUtils.isEmpty(quotationData)){
            String message =  String.format( ERROR_EMPTY_RESULT_QUOTATION_DATA.getMessage(),quotationId);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_EMPTY_RESULT_QUOTATION_DATA.getAdviceCode(),message);
            throw buildValidation(ERROR_EMPTY_RESULT_QUOTATION_DATA,message);
        }
    }

    public void validateProductData(Map<String, Object> productData, String productId) {
        if(CollectionUtils.isEmpty(productData)){
            String message =  String.format( ERROR_EMPTY_RESULT_PRODUCT_DATA.getMessage(),productId);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_EMPTY_RESULT_PRODUCT_DATA.getAdviceCode(),message);
            throw buildValidation(ERROR_EMPTY_RESULT_PRODUCT_DATA,message);
        }
    }

    public void validateCustomerList(CustomerListASO customerList) {
        if(Objects.isNull(customerList) || CollectionUtils.isEmpty(customerList.getData())){
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE.getAdviceCode(),PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE.getMessage());
            throw PISDValidation.build(PISDErrors.ERROR_CONNECTION_VALIDATE_CUSTOMER_SERVICE);
        }
    }

    public boolean isRucCustomer(CustomerBO customer) {
        String typeDocument = customer.getIdentityDocuments().get(0).getDocumentType().getId();
        String numberDocument = customer.getIdentityDocuments().get(0).getDocumentNumber();
        return RBVDInternalConstants.Endorsement.RUC.equalsIgnoreCase(typeDocument) && StringUtils.startsWith(numberDocument, "20");
    }

    public void validateFrequencyType(String frequencyType) {
        if(StringUtils.isEmpty(frequencyType)){
            String message =  String.format(ERROR_NOT_CONFIG_FREQUENCY_TYPE.getMessage(),frequencyType);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_NOT_CONFIG_FREQUENCY_TYPE.getAdviceCode(),message);
            throw buildValidation(ERROR_NOT_CONFIG_FREQUENCY_TYPE,message);
        }
    }


    public void validateCustomerIdEncryption(String customerIdEncrypted) {
        if(StringUtils.isEmpty(customerIdEncrypted)){
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.ERROR_CONNECTION_CYPHER_SERVICE.getAdviceCode(),RBVDErrors.ERROR_CONNECTION_CYPHER_SERVICE.getMessage());
            throw RBVDValidation.build(RBVDErrors.ERROR_CONNECTION_CYPHER_SERVICE);
        }
    }

    public void validateListBusinessesASO(ListBusinessesASO listBusinessesASO) {
        if(Objects.isNull(listBusinessesASO)  || CollectionUtils.isEmpty(listBusinessesASO.getData())){
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO.getAdviceCode(),RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO.getMessage());
            throw RBVDValidation.build(RBVDErrors.ERROR_CONNECTION_LIST_BUSINESSES_ASO);
        }
    }

    public BigDecimal getTotalNumberInstallments(PolicyDTO requestBody, RequiredFieldsEmissionDAO emissionDao) {
        BigDecimal totalNumberInstallments;
        List<String> lifeProduct = Arrays.asList(RBVDProperties.INSURANCE_PRODUCT_TYPE_VIDA_EASYYES.getValue(), RBVDProperties.INSURANCE_PRODUCT_TYPE_VIDA_2.getValue());
        if(lifeProduct.contains(requestBody.getProductId())){
            totalNumberInstallments = emissionDao.getContractDurationNumber();
        }else{
            totalNumberInstallments = (requestBody.getFirstInstallment().getIsPaymentRequired()) ? BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments() - 1) : BigDecimal.valueOf(requestBody.getInstallmentPlan().getTotalNumberInstallments());
        }
        return totalNumberInstallments;
    }


    public <T> void validateFilledAddress(T filledAddress) {
        if (isNull(filledAddress)) {
            String message =  String.format( ERROR_VALID_ADDRESS.getMessage(),"N/A");
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(ERROR_VALID_ADDRESS.getAdviceCode(),message);
            throw buildValidation(ERROR_VALID_ADDRESS,message);
        }
    }

    public Date validateStartDate(PolicyDTO requestBody) {
        return Optional.ofNullable(requestBody.getValidityPeriod().getStartDate())
                .map(FunctionsUtils::convertDateToLocalTimeZone)
                .orElseThrow(() -> {
                    String message = String.format(ERROR_NOT_VALUE_VALIDITY_PERIOD.getMessage(), "startDate", requestBody.getValidityPeriod().getStartDate());
                    return buildValidation(ERROR_NOT_VALUE_VALIDITY_PERIOD, message);
                });
    }

    public void validateContractGeneration(ResponseLibrary<PolicyASO> responseService) {
        if(!RBVDInternalConstants.Status.OK.equalsIgnoreCase(responseService.getStatusProcess())){
            throw buildValidation(ERROR_RESPONSE_SERVICE_ICR2);
        }
    }

    public void handleNonDigitalSale(PolicyDTO requestBody) {
        List<String> channelsSaleNotDigital = this.basicProductInsuranceProperties.obtainSaleChannelsNotDigital();
        if (!channelsSaleNotDigital.contains(requestBody.getSaleChannelId())) {
            handleAAPSSale(requestBody);
            handleDigitalPromoter(requestBody);
        }
    }

    private void handleAAPSSale(PolicyDTO requestBody) {
        List<String> aapSSearchInContactDetail = this.basicProductInsuranceProperties.obtainAapSSearchInContactDetail();
        if (aapSSearchInContactDetail.contains(requestBody.getSaleChannelId())) {
            String customerId = Optional.ofNullable(requestBody.getHolder()).map(HolderDTO::getId).orElse(StringUtils.EMPTY);
            String emailCode = Optional.ofNullable(requestBody.getHolder()).map(holderDTO -> CollectionUtils.isEmpty(holderDTO.getContactDetails()) ? StringUtils.EMPTY : holderDTO.getContactDetails().get(0).getId()).orElse(StringUtils.EMPTY);
            String phoneCode = Optional.ofNullable(requestBody.getHolder()).filter(holderDTO -> !CollectionUtils.isEmpty(holderDTO.getContactDetails()) && holderDTO.getContactDetails().size() > 1).map(holderDTO -> holderDTO.getContactDetails().get(1).getId()).orElse(StringUtils.EMPTY);
            String emailEncrypt = cryptoServiceInternal.encryptContactDetail(emailCode);
            String phoneEncrypt = cryptoServiceInternal.encryptContactDetail(phoneCode);
                GetContactDetailsASO contactDetails = customerRBVD066InternalService.findByContactDetailByCustomerId(customerId);
            List<ContactDTO> contacts = CrossOperationsBusinessInsuranceContractBank.obtainContactDetails(emailEncrypt, phoneEncrypt, contactDetails);
            requestBody.getHolder().getContactDetails().get(0).setContact(contacts.get(0));
            requestBody.getHolder().getContactDetails().get(1).setContact(contacts.get(1));
        }
        String promoterCodeDefaultSaleDigital = this.basicProductInsuranceProperties.obtainDefaultPromoterCodeSaleDigital();
        if (Objects.isNull(requestBody.getPromoter())) {
            PromoterDTO promoterDTO = PrePolicyTransfor.mapInPromoterId(promoterCodeDefaultSaleDigital);
            requestBody.setPromoter(promoterDTO);
        }
        BusinessAgentDTO businessAgentDTO = PrePolicyTransfor.mapInBusinessAgentSaleDigital(promoterCodeDefaultSaleDigital);
        requestBody.setBusinessAgent(businessAgentDTO);
    }

    private void handleDigitalPromoter(PolicyDTO requestBody) {
        String promoterCodeDefaultSaleDigital = this.basicProductInsuranceProperties.obtainDefaultPromoterCodeSaleDigital();
        if (Objects.isNull(requestBody.getPromoter())) {
            PromoterDTO promoterDTO = PrePolicyTransfor.mapInPromoterId(promoterCodeDefaultSaleDigital);
            requestBody.setPromoter(promoterDTO);
        }
        BusinessAgentDTO businessAgentDTO = PrePolicyTransfor.mapInBusinessAgentSaleDigital(promoterCodeDefaultSaleDigital);
        requestBody.setBusinessAgent(businessAgentDTO);
    }

    public void setBasicProductInsuranceProperties(BasicProductInsuranceProperties basicProductInsuranceProperties) {
        this.basicProductInsuranceProperties = basicProductInsuranceProperties;
    }

    public void setCryptoServiceInternal(CryptoServiceInternal cryptoServiceInternal) {
        this.cryptoServiceInternal = cryptoServiceInternal;
    }

    public void setCustomerRBVD066InternalService(CustomerRBVD066InternalService customerRBVD066InternalService) {
        this.customerRBVD066InternalService = customerRBVD066InternalService;
    }



}
