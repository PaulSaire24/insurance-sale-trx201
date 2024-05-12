package com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.utils.PISDConstants;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDTO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.InsrcContractParticipantBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PrePolicyTransfor;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Endorsement;

public class CrossOperationsBusinessInsuranceContractBank {

    public static List<IsrcContractParticipantDAO> toIsrcContractParticipantDAOList(PolicyDTO requestBody, List<Map<String, Object>> rolesFromDB, String id, ApplicationConfigurationService applicationConfigurationService){
        ParticipantDTO participant = requestBody.getParticipants().get(0);
        ParticipantDTO legalRepre = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),
                ConstantsUtil.Participant.LEGAL_REPRESENTATIVE);
        List<ParticipantDTO> beneficiary = ValidationUtil.filterBenficiaryType(requestBody.getParticipants(),
                ConstantsUtil.Participant.BENEFICIARY);
        ParticipantDTO insured = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),
                ConstantsUtil.Participant.INSURED);

        List<BigDecimal> participantRoles = new ArrayList<>();

        rolesFromDB.forEach(rol -> participantRoles.add((BigDecimal) rol.get(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue())));

        if(legalRepre != null){
            participantRoles.removeIf(rol -> rol.compareTo(new BigDecimal(ConstantsUtil.Number.TRES)) == 0);
        }

        if(insured != null){
            participantRoles.removeIf(rol -> rol.compareTo(new BigDecimal(ConstantsUtil.Number.DOS)) == 0);
        }

        List<IsrcContractParticipantDAO> listParticipants = participantRoles.stream()
                .map(rol -> InsrcContractParticipantBean.createParticipantDao(id,rol,participant,requestBody,applicationConfigurationService))
                .collect(Collectors.toList());

        if(legalRepre != null){
            listParticipants.add(
                    InsrcContractParticipantBean.createParticipantDao(id,
                            new BigDecimal(ConstantsUtil.Number.TRES),
                            legalRepre,requestBody,applicationConfigurationService));
        }

        if(insured != null){
            listParticipants.add(
                    InsrcContractParticipantBean.createParticipantDao(id,new BigDecimal(ConstantsUtil.Number.DOS),
                            insured,requestBody,applicationConfigurationService));
        }

        if(beneficiary != null){
            BigDecimal partyOrderNumber = BigDecimal.valueOf(1L);
            for(ParticipantDTO benef : beneficiary){
                partyOrderNumber = partyOrderNumber.add(BigDecimal.valueOf(1L));
                listParticipants.add(InsrcContractParticipantBean.createParticipantBeneficiaryDao(id,new BigDecimal(ConstantsUtil.Number.DOS),
                        benef,requestBody,applicationConfigurationService,partyOrderNumber));
            }
        }

        return listParticipants;
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


    public static Boolean evaluateRequiredPayment(Date startDate) {
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

}
