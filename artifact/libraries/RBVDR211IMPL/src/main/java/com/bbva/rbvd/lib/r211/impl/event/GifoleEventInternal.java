package com.bbva.rbvd.lib.r211.impl.event;

import com.bbva.rbvd.dto.insrncsale.events.CreatedInsrcEventDTO;
import com.bbva.rbvd.lib.r201.RBVDR201;

public class GifoleEventInternal {

    private RBVDR201 rbvdR201;

    public void executePutEventUpsilonGenerateLeadGifole(CreatedInsrcEventDTO createdInsuranceEvent){
        this.rbvdR201.executePutEventUpsilonService(createdInsuranceEvent);
    }


    public void setRbvdR201(RBVDR201 rbvdR201) {
        this.rbvdR201 = rbvdR201;
    }
}
