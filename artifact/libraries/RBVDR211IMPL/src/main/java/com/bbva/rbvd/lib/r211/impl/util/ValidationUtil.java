package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ValidationUtil {

    private ValidationUtil(){}

    public static ParticipantDTO filterParticipantByType(List<ParticipantDTO> participants, String participantType) {
        if(!CollectionUtils.isEmpty(participants)){
            Optional<ParticipantDTO> participant = participants.stream()
                    .filter(participantDTO -> participantType.equals(participantDTO.getParticipantType().getId())).findFirst();
            return participant.isPresent() ? participant.get() : null;
        }else{
            return null;
        }
    }

    public static boolean validateOtherParticipants(ParticipantDTO participantDTO, String participantType) {
        if(participantType.equals(participantDTO.getParticipantType().getId())){
            return participantDTO.getIdentityDocument() != null
                    && participantDTO.getIdentityDocument().getDocumentType().getId() != null
                    && participantDTO.getIdentityDocument().getNumber() != null;
        }
        return false;
    }

    public static boolean validateEndorsementInParticipantsRequest(PolicyDTO requestBody) {
        if(Objects.nonNull(filterParticipantByType(requestBody.getParticipants(),ConstantsUtil.Participant.ENDORSEE))){
            ParticipantDTO endorseParticipant = filterParticipantByType(requestBody.getParticipants(),ConstantsUtil.Participant.ENDORSEE);
            return  endorseParticipant != null
                    && endorseParticipant.getIdentityDocument() != null
                    && Objects.nonNull(endorseParticipant.getIdentityDocument().getDocumentType().getId())
                    && ConstantsUtil.DocumentType.RUC.equals(endorseParticipant.getIdentityDocument().getDocumentType().getId())
                    && Objects.nonNull(endorseParticipant.getIdentityDocument().getNumber())
                    && Objects.nonNull(endorseParticipant.getBenefitPercentage());
        }
        return false;
    }

}
