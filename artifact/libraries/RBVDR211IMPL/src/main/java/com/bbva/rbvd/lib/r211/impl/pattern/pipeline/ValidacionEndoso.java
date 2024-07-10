package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;

import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import org.springframework.util.CollectionUtils;

public class ValidacionEndoso implements PasoPipeline{
    @Override
    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, PasoPipeline siguiente) {
        PolicyDTO requestBody = contexto.getBody().getPolicy();
        if( !CollectionUtils.isEmpty(requestBody.getParticipants())  && requestBody.getParticipants().size() > 1) {
            if (requestBody.getParticipants().get(1).getIdentityDocument() != null
                    && RBVDInternalConstants.Endorsement.ENDORSEMENT.equals(requestBody.getParticipants().get(1).getParticipantType().getId())
                    && RBVDInternalConstants.Endorsement.RUC.equals(requestBody.getParticipants().get(1).getIdentityDocument().getDocumentType().getId())
                    && requestBody.getParticipants().get(1).getBenefitPercentage() != null) {
                contexto.getBody().setEndorsement(true);
                siguiente.ejecutar(contexto, siguiente);
            }
        }
        contexto.getBody().setEndorsement(false);
        siguiente.ejecutar(contexto, siguiente);
    }
}
