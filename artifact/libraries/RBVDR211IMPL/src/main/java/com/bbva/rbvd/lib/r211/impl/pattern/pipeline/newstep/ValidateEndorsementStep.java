package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.newstep;

import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class ValidateEndorsementStep implements Step {
    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public ValidateEndorsementStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }
    @Override
    public void executeStepGenerationContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        InsuranceContractDAO contractDao = processContextContractAndPolicy.getBody().getContractDao();
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        List<EndosatarioBO>  endosatarios = new ArrayList<>();
        if(processContextContractAndPolicy.getBody().getIsEndorsement()){
            ParticipantDTO participantEndorse = ValidationUtil.filterParticipantByType(requestBody.getParticipants(), RBVDInternalConstants.Endorsement.ENDORSEMENT);
            String endosatarioRuc = Objects.isNull(participantEndorse) ? StringUtils.EMPTY : participantEndorse.getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = Objects.isNull(participantEndorse) ? 0.0 : participantEndorse.getBenefitPercentage();
            EndosatarioBO endosatario = new EndosatarioBO(endosatarioRuc,endosatarioPorcentaje.intValue());
            endosatarios.add(endosatario);

            boolean isEndorsementSaved = this.dependencyBuilder.getIEndorsementInsurncCtrDAO().saveEndosermentInsurncCtr(contractDao,endosatarioRuc,endosatarioPorcentaje);
            if(!isEndorsementSaved){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_ENDORSEMENT_INSRNC_CTR);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
        processContextContractAndPolicy.getBody().setEndosatarios(endosatarios);
        processContextContractAndPolicy.getBody().setPolicy(requestBody);
        stepsBankContract.executeStepGenerationContract(processContextContractAndPolicy, stepsBankContract);

    }
}
