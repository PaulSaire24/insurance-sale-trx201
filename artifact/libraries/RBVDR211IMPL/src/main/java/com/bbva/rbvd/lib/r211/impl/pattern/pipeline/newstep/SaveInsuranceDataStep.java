package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.IsrcContractMovBean;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.PolicyASOBean;
import com.bbva.rbvd.lib.r211.impl.transfor.list.RelatedContractsList;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class SaveInsuranceDataStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public SaveInsuranceDataStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void executeStepGenerationContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        PolicyASO asoResponse = Objects.isNull(processContextContractAndPolicy.getBody().getAsoResponse()) ?  PolicyASOBean.PolicyDTOtoPolicyASO(requestBody) : processContextContractAndPolicy.getBody().getAsoResponse();
        InsuranceContractDAO contractDao = processContextContractAndPolicy.getBody().getContractDao();
        IsrcContractMovDAO contractMovDao = IsrcContractMovBean.toIsrcContractMovDAO(asoResponse,requestBody.getCreationUser(),requestBody.getUserAudit());
        boolean isSavedContractMov = this.dependencyBuilder.getInsrncContractMovDAO().saveInsrncContractmov(contractMovDao);
        if(!isSavedContractMov){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CONTRACT_MOV);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }

        processContextContractAndPolicy.getBody().setPolicy(requestBody);
        processContextContractAndPolicy.getBody().setContractDao(contractDao);
        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);
    }
}
