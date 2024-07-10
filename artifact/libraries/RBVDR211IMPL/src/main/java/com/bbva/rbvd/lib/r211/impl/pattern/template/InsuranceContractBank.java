package com.bbva.rbvd.lib.r211.impl.pattern.template;


import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;

public abstract class InsuranceContractBank {

    private ResponseLibrary<ProcessPrePolicyDTO> responseLibrary;
    protected abstract void executeValidateConditions(PolicyDTO requestBody);
    protected abstract void validateAdress(PolicyDTO requestBody);
    protected abstract void executeFetchRequiredData(PolicyDTO requestBody);
    protected abstract void executeGenerateContract();
    protected abstract void getListReceipts();
    protected abstract void saveListReceipts();
    protected abstract void executeSaveInsuranceData();
    protected abstract void saveContractDetailsAndEndoserment();
    protected abstract void executeGeneratePayment();
    protected final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    /**
     * This method is responsible for generating an insurance contract.
     * It follows a series of steps to complete the process:
     * 1. Validates the conditions of the policy request.
     * 2. Fetches the required data for the policy.
     * 3. Generates the contract based on the fetched data and validated conditions.
     * 4. Saves the insurance data.
     * 5. Generates the payment for the insurance.
     * After all these steps, it returns the response library which contains the details of the process.
     *
     * @param requestBody The request body containing the details of the policy.
     * @return A ResponseLibrary object containing the details of the process.
     */
    public final ResponseLibrary<ProcessPrePolicyDTO> executeGenerateInsuranceContractRoyal(PolicyDTO requestBody){
        this.executeValidateConditions(requestBody);
        this.validateAdress(requestBody);
        this.executeFetchRequiredData(requestBody);
        this.executeGenerateContract();
        this.getListReceipts();
        this.saveListReceipts();
        this.executeSaveInsuranceData();
        this.saveContractDetailsAndEndoserment();
        this.executeGeneratePayment();
        return this.getResponseLibrary();
    }

    public void setResponseLibrary(ResponseLibrary<ProcessPrePolicyDTO> responseLibrary) {
        this.responseLibrary = responseLibrary;
    }

    public ResponseLibrary<ProcessPrePolicyDTO> getResponseLibrary() {
        return responseLibrary;
    }
}
