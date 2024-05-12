package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.lib.r211.impl.service.IInsrncRoleModalityDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class InsrncRoleModalityPISD012DAOImpl implements IInsrncRoleModalityDAO {

    private PISDR012 pisdR012;

    @Override
    public List<Map<String, Object>> findByProductIdAndModalityType(BigDecimal productId, String modalityType) {
        Map<String, Object> responseQueryRoles = this.pisdR012.executeGetRolesByProductAndModality(productId, modalityType);
        return (List<Map<String, Object>>) responseQueryRoles.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
    }

    public void setPisdR012(PISDR012 pisdR012) {
        this.pisdR012 = pisdR012;
    }
}
