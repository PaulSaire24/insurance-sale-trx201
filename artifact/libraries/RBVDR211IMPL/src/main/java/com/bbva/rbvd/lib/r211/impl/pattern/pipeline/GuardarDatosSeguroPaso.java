package com.bbva.rbvd.lib.r211.impl.pattern.pipeline;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.*;
import com.bbva.rbvd.dto.insrncsale.policy.ParticipantDTO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ProcessPrePolicyDTO;
import com.bbva.rbvd.dto.insurancemissionsale.dto.ResponseLibrary;
import com.bbva.rbvd.lib.r211.impl.pattern.template.crossoperations.CrossOperationsBusinessInsuranceContractBank;
import com.bbva.rbvd.lib.r211.impl.properties.BasicProductInsuranceProperties;
import com.bbva.rbvd.lib.r211.impl.service.*;
import com.bbva.rbvd.lib.r211.impl.transfor.bean.IsrcContractMovBean;
import com.bbva.rbvd.lib.r211.impl.transfor.list.RelatedContractsList;
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

public class GuardarDatosSeguroPaso implements PasoPipeline{

    private IInsrncContractMovDAO insrncContractMovDAO;
    private IInsrncRoleModalityDAO insrncRoleModalityDAO;
    private IInsurncCtrParticipantDAO insurncCtrParticipantDAO;
    private ApplicationConfigurationService applicationConfigurationService;
    private BasicProductInsuranceProperties basicProductInsuranceProperties;
    private IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO;
    private IInsurncRelatedContract insurncRelatedContract;

    protected final ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();
    @Override
    public void ejecutar(ResponseLibrary<ProcessPrePolicyDTO> contexto, PasoPipeline siguiente) {
        List<EndosatarioBO> endosatarios = null;
        PolicyDTO requestBody = contexto.getBody().getPolicy();
        PolicyASO asoResponse = contexto.getBody().getAsoResponse();
        RequiredFieldsEmissionDAO emissionDao = contexto.getBody().getRequiredFieldsEmission();
        InsuranceContractDAO contractDao = contexto.getBody().getContractDao();
        IsrcContractMovDAO contractMovDao = IsrcContractMovBean.toIsrcContractMovDAO(asoResponse,requestBody.getCreationUser(),requestBody.getUserAudit());
        boolean isSavedContractMov = this.insrncContractMovDAO.saveInsrncContractmov(contractMovDao);
        if(!isSavedContractMov){
            String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(), RBVDInternalConstants.Tables.T_PISD_INSRNC_CONTRACT_MOV);
            this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
            throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
        }
        List<Map<String, Object>> rolesInMap = this.insrncRoleModalityDAO.findByProductIdAndModalityType(emissionDao.getInsuranceProductId(), requestBody.getProductPlan().getId());
        if(!CollectionUtils.isEmpty(rolesInMap)){
            List<IsrcContractParticipantDAO> participants = CrossOperationsBusinessInsuranceContractBank.toIsrcContractParticipantDAOList(requestBody, rolesInMap, asoResponse.getData().getId(),applicationConfigurationService);
            boolean isSavedParticipant = insurncCtrParticipantDAO.savedContractParticipant(participants);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSRNC_CTR_PARTICIPANT);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }

        String productCodesLife = this.basicProductInsuranceProperties.obtainProductCodesLife();
        if(productCodesLife.contains(requestBody.getProductPlan().getId())){
            endosatarios = getEndosatarioAndSaveLife(contexto, requestBody, endosatarios, contractDao);
        }else{
            getEndosatarioAndSaveNoLife(contexto, requestBody, contractDao);
        }

        contexto.getBody().setEndosatarios(endosatarios);
        contexto.getBody().setPolicy(requestBody);
        siguiente.ejecutar(contexto, siguiente);
    }

    private void getEndosatarioAndSaveNoLife(ResponseLibrary<ProcessPrePolicyDTO> contexto, PolicyDTO requestBody, InsuranceContractDAO contractDao) {
        if(!CollectionUtils.isEmpty(requestBody.getRelatedContracts())){
            List<RelatedContractDAO> relatedContractsDao = RelatedContractsList.toRelatedContractDAOList(requestBody, contractDao);
            boolean isSavedParticipant = insurncRelatedContract.savedContractDetails(relatedContractsDao);
            if(!isSavedParticipant){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_INSURANCE_CONTRACT);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }

        if(Boolean.TRUE.equals(contexto.getBody().getIsEndorsement())){
            String endosatarioRuc = requestBody.getParticipants().get(1).getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = requestBody.getParticipants().get(1).getBenefitPercentage();
            boolean isEndorsementSaved = this.IEndorsementInsurncCtrDAO.saveEndosermentInsurncCtr(contractDao,endosatarioRuc,endosatarioPorcentaje);
            if(!isEndorsementSaved){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_ENDORSEMENT_INSRNC_CTR);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
    }

    private List<EndosatarioBO> getEndosatarioAndSaveLife(ResponseLibrary<ProcessPrePolicyDTO> contexto, PolicyDTO requestBody, List<EndosatarioBO> endosatarios, InsuranceContractDAO contractDao) {
        if(Boolean.TRUE.equals(contexto.getBody().getIsEndorsement())){
            ParticipantDTO participantEndorse = ValidationUtil.filterParticipantByType(requestBody.getParticipants(),RBVDInternalConstants.Endorsement.ENDORSEMENT);
            String endosatarioRuc = Objects.isNull(participantEndorse) ? StringUtils.EMPTY : participantEndorse.getIdentityDocument().getNumber();
            Double endosatarioPorcentaje = Objects.isNull(participantEndorse) ? 0.0 : participantEndorse.getBenefitPercentage();
            endosatarios = new ArrayList<>();
            EndosatarioBO endosatario = new EndosatarioBO(endosatarioRuc,endosatarioPorcentaje.intValue());
            endosatarios.add(endosatario);

            boolean isEndorsementSaved = this.IEndorsementInsurncCtrDAO.saveEndosermentInsurncCtr(contractDao,endosatarioRuc,endosatarioPorcentaje);
            if(!isEndorsementSaved){
                String message = String.format(INSERTION_ERROR_IN_TABLE.getMessage(),RBVDInternalConstants.Tables.T_PISD_ENDORSEMENT_INSRNC_CTR);
                this.architectureAPXUtils.addAdviceWithDescriptionLibrary(INSERTION_ERROR_IN_TABLE.getAdviceCode(),message);
                throw buildValidation(INSERTION_ERROR_IN_TABLE,message);
            }
        }
        return endosatarios;
    }

    public IInsrncContractMovDAO getInsrncContractMovDAO() {
        return insrncContractMovDAO;
    }

    public void setInsrncContractMovDAO(IInsrncContractMovDAO insrncContractMovDAO) {
        this.insrncContractMovDAO = insrncContractMovDAO;
    }

    public IInsrncRoleModalityDAO getInsrncRoleModalityDAO() {
        return insrncRoleModalityDAO;
    }

    public void setInsrncRoleModalityDAO(IInsrncRoleModalityDAO insrncRoleModalityDAO) {
        this.insrncRoleModalityDAO = insrncRoleModalityDAO;
    }

    public IInsurncCtrParticipantDAO getInsurncCtrParticipantDAO() {
        return insurncCtrParticipantDAO;
    }

    public void setInsurncCtrParticipantDAO(IInsurncCtrParticipantDAO insurncCtrParticipantDAO) {
        this.insurncCtrParticipantDAO = insurncCtrParticipantDAO;
    }

    public ApplicationConfigurationService getApplicationConfigurationService() {
        return applicationConfigurationService;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public BasicProductInsuranceProperties getBasicProductInsuranceProperties() {
        return basicProductInsuranceProperties;
    }

    public void setBasicProductInsuranceProperties(BasicProductInsuranceProperties basicProductInsuranceProperties) {
        this.basicProductInsuranceProperties = basicProductInsuranceProperties;
    }

    public com.bbva.rbvd.lib.r211.impl.service.IEndorsementInsurncCtrDAO getIEndorsementInsurncCtrDAO() {
        return IEndorsementInsurncCtrDAO;
    }

    public void setIEndorsementInsurncCtrDAO(IEndorsementInsurncCtrDAO IEndorsementInsurncCtrDAO) {
        this.IEndorsementInsurncCtrDAO = IEndorsementInsurncCtrDAO;
    }

    public IInsurncRelatedContract getInsurncRelatedContract() {
        return insurncRelatedContract;
    }

    public void setInsurncRelatedContract(IInsurncRelatedContract insurncRelatedContract) {
        this.insurncRelatedContract = insurncRelatedContract;
    }
}
