package com.bbva.pattern.factory;

import com.bbva.elara.online.common.AbstractCommon;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.RBVDR211;

import java.util.List;

/**
 * This class is responsible for creating insurance policies for both bank and company.
 * It extends the AbstractCommon class.
 */
public class InsuranceFactory extends AbstractCommon {

    /**
     * This method is used to create insurance policies for both bank and company.
     * It checks if the product type is in the list of life product codes.
     * If it is, it executes the business logic for emission pre-policy life product flow.
     * If it is not, it executes the business logic for emission policy not life flow.
     *
     * @param typeProduct The type of product for which the insurance policy is to be created.
     * @param rbvdR211 An instance of RBVDR211 which contains the business logic for creating the insurance policy.
     * @param listOfLifeProductCodes A list of life product codes.
     * @param requestBody The request body containing the details of the insurance policy to be created.
     * @return A ResponseLibrary object containing the created PolicyDTO.
     */
    public ResponseLibrary<PolicyDTO> createInsuranceBankAndCompany(String typeProduct, RBVDR211 rbvdR211, List<String> listOfLifeProductCodes,PolicyDTO requestBody){
        if(listOfLifeProductCodes.contains(typeProduct)){
            return rbvdR211.executeBusinessLogicEmissionPrePolicyLifeProductFlowNew(requestBody);
        }
        return rbvdR211.executeBusinessLogicEmissionPolicyNotLifeFlowNew(requestBody);
    }


}
