package com.bbva.rbvd.dto.insurancemissionsale.dto;


import com.bbva.rbvd.dto.insrncsale.aso.emision.DataASO;
import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EmisionBO;
import com.bbva.rbvd.dto.insrncsale.bo.emision.EndosatarioBO;
import com.bbva.rbvd.dto.insrncsale.dao.InsuranceContractDAO;
import com.bbva.rbvd.dto.insrncsale.dao.RequiredFieldsEmissionDAO;
import com.bbva.rbvd.dto.insrncsale.policy.PolicyDTO;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ProcessPrePolicyDTO {

    private PolicyDTO policy;
    private RequiredFieldsEmissionDAO requiredFieldsEmission;

    private PolicyASO asoResponse;
    private InsuranceContractDAO contractDao;

    private String  operationGlossaryDesc;
    private Boolean isEndorsement;

    private EmisionBO rimacRequest;
    private String quotationId;

    private String traceId;

    private String productId;

    private String quotationEmailDesc;
    private String quotationCustomerPhoneDesc;

    private Map<String,Object> responseQueryGetProductById;

    private CustomerListASO customerList ;
    private ListBusinessesASO listBusinessesASO ;
    private EmisionBO rimacResponse;

    private String insuranceBusinessName;

    private List<EndosatarioBO> endosatarios;

    private String rimacPaymentAccount;

    private DataASO dataASO;

    public DataASO getDataASO() {
        return dataASO;
    }

    public void setDataASO(DataASO dataASO) {
        this.dataASO = dataASO;
    }

    public String getRimacPaymentAccount() {
        return rimacPaymentAccount;
    }

    public void setRimacPaymentAccount(String rimacPaymentAccount) {
        this.rimacPaymentAccount = rimacPaymentAccount;
    }

    public String getInsuranceBusinessName() {
        return insuranceBusinessName;
    }

    public void setInsuranceBusinessName(String insuranceBusinessName) {
        this.insuranceBusinessName = insuranceBusinessName;
    }

    public List<EndosatarioBO> getEndosatarios() {
        return endosatarios;
    }

    public void setEndosatarios(List<EndosatarioBO> endosatarios) {
        this.endosatarios = endosatarios;
    }

    public EmisionBO getRimacResponse() {
        return rimacResponse;
    }

    public void setRimacResponse(EmisionBO rimacResponse) {
        this.rimacResponse = rimacResponse;
    }

    public CustomerListASO getCustomerList() {
        return customerList;
    }

    public void setCustomerList(CustomerListASO customerList) {
        this.customerList = customerList;
    }

    public ListBusinessesASO getListBusinessesASO() {
        return listBusinessesASO;
    }

    public void setListBusinessesASO(ListBusinessesASO listBusinessesASO) {
        this.listBusinessesASO = listBusinessesASO;
    }

    public Map<String, Object> getResponseQueryGetProductById() {
        return responseQueryGetProductById;
    }

    public void setResponseQueryGetProductById(Map<String, Object> responseQueryGetProductById) {
        this.responseQueryGetProductById = responseQueryGetProductById;
    }

    public String getQuotationEmailDesc() {
        return quotationEmailDesc;
    }

    public void setQuotationEmailDesc(String quotationEmailDesc) {
        this.quotationEmailDesc = quotationEmailDesc;
    }

    public String getQuotationCustomerPhoneDesc() {
        return quotationCustomerPhoneDesc;
    }

    public void setQuotationCustomerPhoneDesc(String quotationCustomerPhoneDesc) {
        this.quotationCustomerPhoneDesc = quotationCustomerPhoneDesc;
    }

    public EmisionBO getRimacRequest() {
        return rimacRequest;
    }

    public void setRimacRequest(EmisionBO rimacRequest) {
        this.rimacRequest = rimacRequest;
    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Boolean getIsEndorsement() {
        return isEndorsement;
    }

    public void setEndorsement(Boolean endorsement) {
        isEndorsement = endorsement;
    }

    public InsuranceContractDAO getContractDao() {
        return contractDao;
    }

    public void setContractDao(InsuranceContractDAO contractDao) {
        this.contractDao = contractDao;
    }

    public PolicyASO getAsoResponse() {
        return asoResponse;
    }

    public void setAsoResponse(PolicyASO asoResponse) {
        this.asoResponse = asoResponse;
    }

    public PolicyDTO getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyDTO policy) {
        this.policy = policy;
    }

    public void setRequiredFieldsEmission(RequiredFieldsEmissionDAO requiredFieldsEmission) {
        this.requiredFieldsEmission = requiredFieldsEmission;
    }

    public String getOperationGlossaryDesc() {
        return operationGlossaryDesc;
    }

    public void setOperationGlossaryDesc(String operationGlossaryDesc) {
        this.operationGlossaryDesc = operationGlossaryDesc;
    }

    public RequiredFieldsEmissionDAO getRequiredFieldsEmission() {
        return requiredFieldsEmission;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ProcessPrePolicyDTO.class.getSimpleName() + "[", "]")
                .add("policy=" + policy)
                .add("requiredFieldsEmission=" + requiredFieldsEmission)
                .add("asoResponse=" + asoResponse)
                .add("contractDao=" + contractDao)
                .add("operationGlossaryDesc='" + operationGlossaryDesc + "'")
                .add("isEndorsement=" + isEndorsement)
                .add("rimacRequest=" + rimacRequest)
                .add("quotationId='" + quotationId + "'")
                .add("traceId='" + traceId + "'")
                .add("productId='" + productId + "'")
                .add("quotationEmailDesc='" + quotationEmailDesc + "'")
                .add("quotationCustomerPhoneDesc='" + quotationCustomerPhoneDesc + "'")
                .add("responseQueryGetProductById=" + responseQueryGetProductById)
                .add("customerList=" + customerList)
                .add("listBusinessesASO=" + listBusinessesASO)
                .add("rimacResponse=" + rimacResponse)
                .add("insuranceBusinessName='" + insuranceBusinessName + "'")
                .add("endosatarios=" + endosatarios)
                .toString();
    }
}
