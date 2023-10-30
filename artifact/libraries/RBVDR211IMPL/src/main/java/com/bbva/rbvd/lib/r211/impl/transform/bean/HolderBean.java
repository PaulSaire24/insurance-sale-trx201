package com.bbva.rbvd.lib.r211.impl.transform.bean;

import com.bbva.rbvd.dto.insrncsale.aso.DocumentTypeASO;
import com.bbva.rbvd.dto.insrncsale.aso.HolderASO;
import com.bbva.rbvd.dto.insrncsale.aso.IdentityDocumentASO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.lib.r211.impl.util.ConstantsUtil;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;

public class HolderBean {

    private HolderBean(){}

    public static HolderASO getHolderASO(PolicyDTO apxRequest) {
        HolderASO holder = new HolderASO();

        IdentityDocumentASO identityDocument = new IdentityDocumentASO();
        DocumentTypeASO documentType = new DocumentTypeASO();
        String customerId;
        String documentTypeId;
        String documentNumber;

        ParticipantDTO participantInsured = ValidationUtil.filterParticipantByType(
                apxRequest.getParticipants(), ConstantsUtil.Participant.INSURED);
        if(participantInsured != null &&
                ValidationUtil.validateOtherParticipants(participantInsured,ConstantsUtil.Participant.INSURED)){
            customerId = participantInsured.getCustomerId();
            documentTypeId = participantInsured.getIdentityDocument().getDocumentType().getId();
            documentNumber = participantInsured.getIdentityDocument().getNumber();
        }else{
            customerId = apxRequest.getHolder().getId();
            documentTypeId = apxRequest.getHolder().getIdentityDocument().getDocumentType().getId();
            documentNumber = apxRequest.getHolder().getIdentityDocument().getNumber();
        }

        documentType.setId(documentTypeId);
        identityDocument.setDocumentType(documentType);
        identityDocument.setNumber(documentNumber);

        holder.setIdentityDocument(identityDocument);
        holder.setId(customerId);

        return holder;
    }

}
