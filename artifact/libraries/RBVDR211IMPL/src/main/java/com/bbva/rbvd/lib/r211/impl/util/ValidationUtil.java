package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class ValidationUtil {

    private ValidationUtil(){}

    public static ParticipantDTO filterParticipantByType(List<ParticipantDTO> participants, String participantType) {
        if(!CollectionUtils.isEmpty(participants)){
            return participants.stream().filter(participantDTO -> participantType.equals(participantDTO.getParticipantType().getId())).findFirst().orElse(null);
        }else{
            return null;
        }
    }

    public static boolean validateOtherParticipants(PolicyDTO requestBody, String participantType) {
        if(!CollectionUtils.isEmpty(requestBody.getParticipants())) {
            if(ConstantsUtil.PARTICIPANT_TYPE_ENDORSEE.equals(participantType) && Objects.nonNull(filterParticipantByType(requestBody.getParticipants(),ConstantsUtil.PARTICIPANT_TYPE_ENDORSEE))){
                ParticipantDTO participantDTO = filterParticipantByType(requestBody.getParticipants(),ConstantsUtil.PARTICIPANT_TYPE_ENDORSEE);
                return participantDTO.getIdentityDocument() != null
                        && ConstantsUtil.DOCUMENT_TYPE_RUC.equals(participantDTO.getIdentityDocument().getDocumentType().getId())
                        && participantDTO.getBenefitPercentage() != null;
            }else if(Objects.nonNull(filterParticipantByType(requestBody.getParticipants(),participantType))){
                ParticipantDTO participantDTO = filterParticipantByType(requestBody.getParticipants(),participantType);
                return participantDTO.getIdentityDocument() != null
                        && participantDTO.getIdentityDocument().getDocumentType().getId() != null
                        && participantDTO.getIdentityDocument().getNumber() != null;
            }
        }
        return false;
    }

}
