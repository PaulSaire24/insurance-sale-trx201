package com.bbva.rbvd.lib.r211.impl.service;

import com.bbva.rbvd.dto.insrncsale.dao.RelatedContractDAO;

import java.util.List;

public interface IInsurncRelatedContract {

    boolean savedContractDetails(List<RelatedContractDAO> relatedContractsDao);

}
