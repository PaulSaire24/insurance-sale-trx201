package com.bbva.rbvd.lib.r211.impl.business;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessContextContractAndPolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.aspects.interfaces.ManagementOperation;
import com.bbva.rbvd.lib.r211.impl.pattern.factory.interfaces.InsuranceCompanyFactory;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.PipInsuranceBankNotLife;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.factory.PipelineInsuranceContractFactory;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmissionPolicyNotLifeBusinessImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmissionPolicyNotLifeBusinessImpl.class);

    private InsuranceCompanyFactory insuranceCompanyFactory;
    private ManagementOperation managementOperationsCross;
    private MapperHelper mapperHelper;
    private PipelineInsuranceContractFactory pipInsuranceBankNotLife;
    private PipelineInsuranceContractFactory pipInsuranceBankFlowPreEmission;


    /**
     * This method is responsible for executing the emission of a policy.
     * It first generates an insurance contract and creates an insurance product based on the pre-policy process.
     * If the process is successful, it returns a ResponseLibrary object with the status OK and the policy details.
     * If a BusinessException is caught during the process, it logs the exception and returns a ResponseLibrary object with the status EWR if a rollback is needed, or ENR if not.
     * The flow process is always set as NEW_FLOW_PROCESS.
     *
     * @param requestBody The request body containing the details of the policy to be emitted.
     * @return A ResponseLibrary object containing the status of the process and the details of the emitted policy.
     * @throws BusinessException If an error occurs during the process.
     */
    public ResponseLibrary<PolicyDTO> executeEmissionPolicy(PolicyDTO requestBody) {
        LOGGER.info(" EmissionPolicyBusinessImpl :: executeEmissionPolicy :: [ START ]");
        ProcessContextContractAndPolicyDTO processContextContractAndPolicyDTO = new ProcessContextContractAndPolicyDTO();
        processContextContractAndPolicyDTO.setPolicy(requestBody);
        ResponseLibrary<ProcessContextContractAndPolicyDTO> contractRoyalGenerated = ResponseLibrary.ResponseServiceBuilder.an().body(processContextContractAndPolicyDTO);
        try {
             if(StringUtils.isNotEmpty(requestBody.getId())){
                 contractRoyalGenerated = pipInsuranceBankFlowPreEmission.configureRoyalContract().executeGenerateInsuranceContractRoyal(contractRoyalGenerated);
             }else{
                 contractRoyalGenerated = pipInsuranceBankNotLife.configureRoyalContract().executeGenerateInsuranceContractRoyal(contractRoyalGenerated);
             }
             ResponseLibrary<ProcessContextContractAndPolicyDTO> contractRoyalAndPolicyGenerated = insuranceCompanyFactory.createInsuranceByProduct(contractRoyalGenerated.getBody());
             managementOperationsCross.afterProcessBusinessExecutionNotLifeCross(contractRoyalAndPolicyGenerated.getBody());
             return ResponseLibrary.ResponseServiceBuilder.an()
                     .flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
                     .statusIndicatorProcess(RBVDInternalConstants.Status.OK)
                     .body(contractRoyalAndPolicyGenerated.getBody().getPolicy());
        }catch (BusinessException exception){
            LOGGER.error(" :: executeEmissionPolicy[ exceptionCode :: {} ,  message :: {}]",exception.getAdviceCode(),exception.getMessage());
            if(RBVDInternalErrors.ERROR_GENERIC_APX_IN_CALLED_RIMAC.getAdviceCode().equalsIgnoreCase(exception.getAdviceCode())){
                this.mapperHelper.mappingOutputFields(contractRoyalGenerated.getBody().getPolicy(), contractRoyalGenerated.getBody().getAsoResponse(), contractRoyalGenerated.getBody().getRimacResponse(), contractRoyalGenerated.getBody().getRequiredFieldsEmission());
                return ResponseLibrary.ResponseServiceBuilder.an()
                        .statusIndicatorProcess(RBVDInternalConstants.Status.OK)
                        .flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
                        .body(contractRoyalGenerated.getBody().getPolicy());
            }
            return ResponseLibrary.ResponseServiceBuilder.an()
                    .statusIndicatorProcess( exception.isHasRollback() ? RBVDInternalConstants.Status.EWR : RBVDInternalConstants.Status.ENR)
                    .flowProcess(RBVDInternalConstants.FlowProcess.NEW_FLOW_PROCESS)
                    .build();
        }
    }

    public void setPipInsuranceBankFlowPreEmission(PipelineInsuranceContractFactory pipInsuranceBankFlowPreEmission) {
        this.pipInsuranceBankFlowPreEmission = pipInsuranceBankFlowPreEmission;
    }

    public void setPipInsuranceBankNotLife(PipInsuranceBankNotLife pipInsuranceBankNotLife) {
        this.pipInsuranceBankNotLife = pipInsuranceBankNotLife;
    }

    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
    }

    public void setInsuranceCompanyFactory(InsuranceCompanyFactory insuranceCompanyFactory) {
        this.insuranceCompanyFactory = insuranceCompanyFactory;
    }

    public void setManagementOperationsCross(ManagementOperation managementOperationsCross) {
        this.managementOperationsCross = managementOperationsCross;
    }

}
