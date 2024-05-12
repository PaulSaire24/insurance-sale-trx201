package com.bbva.rbvd.lib.r211.impl.service.api;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.GetContactDetailsASO;
import com.bbva.rbvd.lib.r201.RBVDR201;

public class CustomerRBVD066InternalService {

    protected RBVDR201 rbvdR201;


    public GetContactDetailsASO findByContactDetailByCustomerId(String customerId) {
        return this.rbvdR201.executeGetContactDetailsService(customerId);
    }

    public CustomerListASO findCustomerInformationByCustomerId(String customerId){
        return this.rbvdR201.executeGetCustomerInformation(customerId);
    }

    public void setRbvdR201(RBVDR201 rbvdR201) {
        this.rbvdR201 = rbvdR201;
    }
}
