package com.bbva.rbvd.lib.r211.impl.pattern.decorator;

import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;

public class InsuranceDecorator implements Insurance {

    protected Insurance decoratedInsurance;
    public InsuranceDecorator(Insurance decoratedInsurance){
        this.decoratedInsurance = decoratedInsurance;
    }

    @Override
    public ResponseLibrary<ProcessContextContractAndPolicyDTO> createPolicyOfCompany(ProcessContextContractAndPolicyDTO processContextContractAndPolicyDTO) {
        return decoratedInsurance.createPolicyOfCompany(processContextContractAndPolicyDTO);
    }

}
