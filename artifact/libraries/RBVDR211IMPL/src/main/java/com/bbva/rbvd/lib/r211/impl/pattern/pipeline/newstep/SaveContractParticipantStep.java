package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PolicyASOBean;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class SaveContractParticipantStep implements Step{
    private final DependencyBuilder dependencyBuilder;
    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public SaveContractParticipantStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void execute(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        RequiredFieldsEmissionDAO emissionDao = processContextContractAndPolicy.getBody().getRequiredFieldsEmission();
        PolicyASO asoResponse = Objects.isNull(processContextContractAndPolicy.getBody().getAsoResponse()) ?  PolicyASOBean.PolicyDTOtoPolicyASO(requestBody) : processContextContractAndPolicy.getBody().getAsoResponse();

        List<Map<String, Object>> rolesInMap = this.dependencyBuilder.getInsrncRoleModalityDAO().findByProductIdAndModalityType(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());
        if(!CollectionUtils.isEmpty(rolesInMap)){
            List<IsrcContractParticipantDAO> participants = CrossOperationsBusinessInsuranceContractBank.toIsrcContractParticipantDAOList(requestBody, rolesInMap, asoResponse.getData().getId(),this.dependencyBuilder.getApplicationConfigurationService());
            boolean isSavedParticipant = this.dependencyBuilder.getInsurncCtrParticipantDAO().savedContractParticipant(participants);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(), RBVDInternalConstants.Tables.T_PISD_INSRNC_CTR_PARTICIPANT);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
        stepsBankContract.execute(processContextContractAndPolicy, stepsBankContract);
    }
}
