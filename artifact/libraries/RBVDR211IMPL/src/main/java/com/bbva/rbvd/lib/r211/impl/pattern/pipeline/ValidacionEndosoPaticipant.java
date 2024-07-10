package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;

import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.ConstantsUtil;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

import java.util.Objects;

import static com.bbva.rbvd.lib.r211.impl.util.ValidationUtil.filterParticipantByType;

public class ValidacionEndosoPaticipant implements PasoPipeline{
    @Override
    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, PasoPipeline siguiente) {
        PolicyDTO requestBody = contexto.getBody().getPolicy();

        if(Objects.nonNull(filterParticipantByType(requestBody.getParticipants(), ConstantsUtil.Participant.ENDORSEE))){
            ParticipantDTO endorseParticipant = filterParticipantByType(requestBody.getParticipants(),ConstantsUtil.Participant.ENDORSEE);
            contexto.getBody().setEndorsement(endorseParticipant != null
                    && endorseParticipant.getIdentityDocument() != null
                    && Objects.nonNull(endorseParticipant.getIdentityDocument().getDocumentType().getId())
                    && ConstantsUtil.DocumentType.RUC.equals(endorseParticipant.getIdentityDocument().getDocumentType().getId())
                    && Objects.nonNull(endorseParticipant.getIdentityDocument().getNumber())
                    && Objects.nonNull(endorseParticipant.getBenefitPercentage()));
            siguiente.ejecutar(contexto, siguiente);
        }
        contexto.getBody().setEndorsement(false);
        siguiente.ejecutar(contexto, siguiente);
    }
}
