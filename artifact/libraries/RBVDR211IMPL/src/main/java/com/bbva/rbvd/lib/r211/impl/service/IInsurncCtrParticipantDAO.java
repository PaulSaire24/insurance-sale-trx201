package com.bbva.rbvd.lib.r211.impl.service;

import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;

import java.util.List;

public interface IInsurncCtrParticipantDAO {

    boolean savedContractParticipant(List<IsrcContractParticipantDAO> participants);
}
