package com.bbva.rbvd.lib.r211.impl.service;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;

public interface IEndorsementInsurncCtrDAO {

    boolean saveEndosermentInsurncCtr(InsuranceContractDAO contractDao, String endosatarioRuc, Double endosatarioPorcentaje) ;
}
