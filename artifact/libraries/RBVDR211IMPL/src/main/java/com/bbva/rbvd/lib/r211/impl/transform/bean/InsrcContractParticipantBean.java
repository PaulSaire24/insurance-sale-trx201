package com.bbva.rbvd.lib.r211.impl.transform.bean;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insrncsale.commons.ContactDetailDTO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class InsrcContractParticipantBean {

    private InsrcContractParticipantBean(){}

    public static IsrcContractParticipantDAO createParticipantDao(String id,
                      BigDecimal rol, ParticipantDTO participant, PolicyDTO requestBody,
                      ApplicationConfigurationService applicationConfigurationService) {
        IsrcContractParticipantDAO participantDao = new IsrcContractParticipantDAO();
        participantDao.setEntityId(id.substring(0,4));
        participantDao.setBranchId(id.substring(4, 8));
        participantDao.setIntAccountId(id.substring(10));
        participantDao.setParticipantRoleId(rol);
        participantDao.setPersonalDocType(applicationConfigurationService.getProperty(participant.getIdentityDocument().getDocumentType().getId()));
        participantDao.setParticipantPersonalId(participant.getIdentityDocument().getNumber());
        participantDao.setCustomerId(Objects.nonNull(participant.getCustomerId()) ? participant.getCustomerId() : null);
        participantDao.setCreationUserId(requestBody.getCreationUser());
        participantDao.setUserAuditId(requestBody.getUserAudit());
        return participantDao;
    }

    public static IsrcContractParticipantDAO createParticipantBeneficiaryDao(String id,
                      BigDecimal rol, ParticipantDTO participant, PolicyDTO requestBody,
                      ApplicationConfigurationService applicationConfigurationService, BigDecimal partyOrderNumber) {
        IsrcContractParticipantDAO participantDao = new IsrcContractParticipantDAO();
        participantDao.setEntityId(id.substring(0,4));
        participantDao.setBranchId(id.substring(4, 8));
        participantDao.setIntAccountId(id.substring(10));
        participantDao.setParticipantRoleId(rol);
        participantDao.setPersonalDocType(applicationConfigurationService.getProperty(participant.getIdentityDocument().getDocumentType().getId()));
        participantDao.setParticipantPersonalId(participant.getIdentityDocument().getNumber());
        participantDao.setCustomerId(Objects.nonNull(participant.getCustomerId()) ? participant.getCustomerId() : null);
        participantDao.setCustomerRelationshipType(participant.getRelationship().getId());
        participantDao.setCreationUserId(requestBody.getCreationUser());
        participantDao.setUserAuditId(requestBody.getUserAudit());
        participantDao.setRefundPer(participant.getBenefitPercentage());
        participantDao.setInsuredCustomerName(participant.getFullName());
        participantDao.setFirstLastName(participant.getLastName());
        participantDao.setSecondLastName(participant.getSecondLastName());
        participantDao.setPartyOrderNumber(partyOrderNumber);
        Optional<ContactDetailDTO> phoneContact = participant.getContactDetails().stream()
                .filter(phone -> "PHONE".equals(phone.getContact().getContactDetailType())).findFirst();
        Optional<ContactDetailDTO> emailContact = participant.getContactDetails().stream()
                .filter(email -> "EMAIL".equals(email.getContact().getContactDetailType())).findFirst();
        participantDao.setContactEmailDesc(
                emailContact.isPresent() ? emailContact.get().getContact().getAddress() : null);
        participantDao.setPhoneId(
                phoneContact.isPresent() ? phoneContact.get().getContact().getPhoneNumber() : null);

        return participantDao;
    }

}