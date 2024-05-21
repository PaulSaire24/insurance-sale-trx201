package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.rbvd.dto.insrncsale.aso.RelatedContractASO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ValidationUtil {

    private ValidationUtil(){}

    public static ParticipantDTO filterParticipantByType(List<ParticipantDTO> participants, String participantType) {
        if(!CollectionUtils.isEmpty(participants)){
            Optional<ParticipantDTO> participant = participants.stream()
                    .filter(participantDTO -> participantType.equals(participantDTO.getParticipantType().getId())).findFirst();
            return participant.orElse(null);
        }else{
            return null;
        }
    }

    public static List<ParticipantDTO> filterBenficiaryType(List<ParticipantDTO> participants, String participantType) {
        if(!CollectionUtils.isEmpty(participants)){
            List<ParticipantDTO> participant = participants.stream()
                    .filter(participantDTO -> participantType.equals(participantDTO.getParticipantType().getId())).collect(Collectors.toList());
            return participant.isEmpty() ? null : participant;
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
        if(Objects.nonNull(filterParticipantByType(requestBody.getParticipants(), ConstantsUtil.Participant.ENDORSEE))){
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


    public static String getKindOfAccount(RelatedContractASO relatedContract){
        if(relatedContract != null && relatedContract.getProduct() != null && Objects.nonNull(relatedContract.getProduct().getId())){
            return relatedContract.getProduct().getId().equals("CARD") ? "TARJETA" : "CUENTA";
        }else{
            return "";
        }
    }

    public static String getAccountNumberInDatoParticular(RelatedContractASO relatedContract){
        if(relatedContract != null && Objects.nonNull(relatedContract.getNumber())){
            int beginIndex = relatedContract.getNumber().length() - 4;
            return "***".concat(relatedContract.getNumber().substring(beginIndex));
        }else{
            return "";
        }
    }

    public static boolean validateisNotEmptyOrNull(String parameter){
        return (parameter != null && !parameter.equals(""));
    }

}