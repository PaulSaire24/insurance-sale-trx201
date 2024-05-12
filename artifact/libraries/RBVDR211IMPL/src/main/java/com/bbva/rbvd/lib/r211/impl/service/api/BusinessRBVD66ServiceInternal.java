package com.bbva.rbvd.lib.r211.impl.service.api;

import com.bbva.rbvd.dto.insrncsale.aso.listbusinesses.ListBusinessesASO;
import com.bbva.rbvd.lib.r201.RBVDR201;

public class BusinessRBVD66ServiceInternal {

    protected RBVDR201 rbvdR201;


    public ListBusinessesASO getListBusinessesByCustomerId(String customerId) {
        return this.rbvdR201.executeGetListBusinesses(customerId,null);
    }

    public void setRbvdR201(RBVDR201 rbvdR201) {
        this.rbvdR201 = rbvdR201;
    }
}
