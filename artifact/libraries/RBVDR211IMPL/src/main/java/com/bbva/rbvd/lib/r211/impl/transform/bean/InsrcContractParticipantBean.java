package com.bbva.rbvd.lib.r211.impl.transform.bean;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;

import java.math.BigDecimal;
import java.util.Objects;

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

}
