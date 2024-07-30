package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.list.RelatedContractsList;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class ValidateRelatedContractStep implements Step {

    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public ValidateRelatedContractStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        InsuranceContractDAO contractDao = processContextContractAndPolicy.getBody().getContractDao();
        if(!CollectionUtils.isEmpty(requestBody.getRelatedContracts())){
            List<RelatedContractDAO> relatedContractsDao = RelatedContractsList.toRelatedContractDAOList(requestBody, contractDao);
            boolean isSavedParticipant = this.dependencyBuilder.getInsurncRelatedContract().savedContractDetails(relatedContractsDao);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(), RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }
}
