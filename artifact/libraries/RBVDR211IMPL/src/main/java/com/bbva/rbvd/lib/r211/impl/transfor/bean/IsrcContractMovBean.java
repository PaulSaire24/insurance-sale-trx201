package com.bbva.rbvd.lib.r211.impl.transfor.bean;

import com.bbva.rbvd.dto.insrncsale.aso.emision.PolicyASO;
import com.bbva.rbvd.dto.insrncsale.dao.IsrcContractMovDAO;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;

import java.math.BigDecimal;
import java.util.Objects;

public class IsrcContractMovBean {

    public static IsrcContractMovDAO toIsrcContractMovDAO(PolicyASO asoResponse, String creationUser, String userAudit) {
        IsrcContractMovDAO isrcContractMovDao = new IsrcContractMovDAO();
        isrcContractMovDao.setEntityId(asoResponse.getData().getId().substring(0, 4));
        isrcContractMovDao.setBranchId(asoResponse.getData().getId().substring(4, 8));
        isrcContractMovDao.setIntAccountId(asoResponse.getData().getId().substring(10));
        isrcContractMovDao.setPolicyMovementNumber(BigDecimal.valueOf(1));
        isrcContractMovDao.setGlAccountDate(Objects.isNull(asoResponse.getData().getFirstInstallment().getAccountingDate()) ? null : FunctionsUtils.generateCorrectDateFormat(asoResponse.getData().getFirstInstallment().getAccountingDate()));
        isrcContractMovDao.setGlBranchId(asoResponse.getData().getId().substring(4, 8));
        isrcContractMovDao.setCreationUserId(creationUser);
        isrcContractMovDao.setUserAuditId(userAudit);
        return isrcContractMovDao;
    }

}

