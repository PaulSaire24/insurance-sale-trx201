package com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractParticipantDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ContextEmission;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.dto.DependencyBuilder;
import com.bbva.rbvd.lib.r211.impl.pattern.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.pattern.pipeline.steps.config.Step;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.IsrcContractMovBean;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import com.bbva.rbvd.lib.r211.impl.util.ValidationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors.INSERTION_ERROR_IN_TABLE;
import static com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils.buildValidation;

public class SaveInsuranceDataLifeStep implements Step {

    private final DependencyBuilder dependencyBuilder;

    private final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();

    public SaveInsuranceDataLifeStep(DependencyBuilder dependencyBuilder) {
        this.dependencyBuilder = dependencyBuilder;
    }

    @Override
    public void executeStepGenerationContract(ResponseLibrary<ContextEmission> processContextContractAndPolicy, Step stepsBankContract) {
        List<EndosatarioBO> endosatarios = null;
        PolicyDTO requestBody = processContextContractAndPolicy.getBody().getPolicy();
        PolicyASO asoResponse = processContextContractAndPolicy.getBody().getAsoResponse();
        RequiredFieldsEmissionDAO emissionDao = processContextContractAndPolicy.getBody().getRequiredFieldsEmission();
        InsuranceContractDAO contractDao = processContextContractAndPolicy.getBody().getContractDao();
        IsrcContractMovDAO contractMovDao = IsrcContractMovBean.toIsrcContractMovDAO(asoResponse,requestBody.getCreationUser(),requestBody.getUserAudit());
        boolean isSavedContractMov = this.dependencyBuilder.getInsrncContractMovDAO().saveInsrncContractmov(contractMovDao);
        if(!isSavedContractMov){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CONTRACT_MOV);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<Map<String, Object>> rolesInMap = this.dependencyBuilder.getInsrncRoleModalityDAO().findByProductIdAndModalityType(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());
        if(!CollectionUtils.isEmpty(rolesInMap)){
            List<IsrcContractParticipantDAO> participants = CrossOperationsBusinessInsuranceContractBank.toIsrcContractParticipantDAOList(requestBody, rolesInMap, asoResponse.getData().getId(),this.dependencyBuilder.getApplicationConfigurationService());
            boolean isSavedParticipant = this.dependencyBuilder.getInsurncCtrParticipantDAO().savedContractParticipant(participants);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CTR_PARTICIPANT);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }

        if(processContextContractAndPolicy.getBody().getIsEndorsement()){
            ParticipantDTO participantEndorse = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),RBVDInternalConstants.Endorsement.ENDORSEMENT);
            String endosatarioRuc = Objects.isNull(participantEndorse) ? StringUtils.EMPTY : participantEndorse.getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = Objects.isNull(participantEndorse) ? 0.0 : participantEndorse.getBenefitPercentage();
            endosatarios = new ArrayList<>();
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
