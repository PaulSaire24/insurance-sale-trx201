package com.bbva.rbvd.lib.r211.impl.transfor.map;

import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.utils.RBVDProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsurncCtrParticipantMap {

    public static Map<String, Object>[] participantsToMaps(List<IsrcContractParticipantDAO> participants) {
        Map<String, Object>[] participantsArguments = new HashMap[participants.size()];
        for(int i = 0; i < participants.size(); i++) {
            participantsArguments[i] = participantToMap(participants.get(i));
        }
        return participantsArguments;
    }

    public static Map<String, Object> participantToMap(IsrcContractParticipantDAO participantDao) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_ENTITY_ID.getValue(), participantDao.getEntityId());
        arguments.put(RBVDProperties.FIELD_INSURANCE_CONTRACT_BRANCH_ID.getValue(), participantDao.getBranchId());
        arguments.put(RBVDProperties.FIELD_INSRC_CONTRACT_INT_ACCOUNT_ID.getValue(), participantDao.getIntAccountId());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(), participantDao.getParticipantRoleId());
        arguments.put(RBVDProperties.FIELD_PARTY_ORDER_NUMBER.getValue(), participantDao.getPartyOrderNumber());
        arguments.put(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue(), participantDao.getPersonalDocType());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), participantDao.getParticipantPersonalId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), participantDao.getCustomerId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_RELATIONSHIP_TYPE.getValue(), participantDao.getCustomerRelationshipType());
        arguments.put(RBVDProperties.FIELD_REGISTRY_SITUATION_TYPE.getValue(), participantDao.getRegistrySituationType());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), participantDao.getCreationUserId());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), participantDao.getUserAuditId());
        arguments.put(RBVDProperties.FIELD_REFUND_PER.getValue(), participantDao.getRefundPer());
        arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(), participantDao.getInsuredCustomerName());
        arguments.put(RBVDProperties.FIELD_FIRST_LAST_NAME.getValue(), participantDao.getFirstLastName());
        arguments.put(RBVDProperties.FIELD_SECOND_LAST_NAME.getValue(), participantDao.getSecondLastName());
        arguments.put(RBVDProperties.FIELD_CONTACT_EMAIL_DESC.getValue(), participantDao.getContactEmailDesc());
        arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(), participantDao.getPhoneId());
        return arguments;
    }

}
