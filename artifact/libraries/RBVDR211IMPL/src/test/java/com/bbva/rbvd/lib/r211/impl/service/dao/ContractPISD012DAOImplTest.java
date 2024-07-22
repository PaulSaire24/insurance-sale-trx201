package com.bbva.rbvd.lib.r211.impl.service.dao;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.pisd.dto.contract.constants.PISDErrors;
import com.bbva.pisd.dto.insurancedao.entities.ContractEntity;
import com.bbva.pisd.lib.r226.PISDR226;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.lib.r211.impl.service.IInsuranceContractDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ContractPISD012DAOImplTest {

    @Spy
    private Context context;

    @InjectMocks
    private  ContractPISD012DAOImpl insuranceContractDAO ;

    @Mock
    private PISDR226 pisdR226;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        context = new Context();
        ThreadContext.set(context);
        getObjectIntrospection();
    }

    private Object getObjectIntrospection() throws Exception{
        Object result = this.insuranceContractDAO;
        if(this.insuranceContractDAO instanceof Advised){
            Advised advised = (Advised) this.insuranceContractDAO;
            result = advised.getTargetSource().getTarget();
        }
        return result;
    }


    @Test(expected = BusinessException.class)
    public void update_insurance_contract_error_validate_field_exception() {

        /**
         * Preparación de data
         * */
        ContractEntity contractEntity = ContractEntity.ContractBuilder.an().build();
        /**
         * Mocks
         * */
        String message = String.format(PISDErrors.VALIDATE_FIELD_CONTRACT.getMessage(),"USER_AUDIT");
        when(pisdR226.executeUpdateInsuranceContractByCertifyBank(Mockito.anyObject()))
                .thenThrow(new BusinessException(PISDErrors.VALIDATE_FIELD_CONTRACT.getAdviceCode(),PISDErrors.VALIDATE_FIELD_CONTRACT.isRollback(),
                        message));
        /**
         * Ejecución de proceso
         * */
        insuranceContractDAO.updateInsuranceContractByCertifyBank(contractEntity);

        Assert.assertEquals(this.context.getAdviceList().get(0).getDescription(),message);

    }

    @Test(expected = BusinessException.class)
    public void update_insurance_contract_error_timeout_exception() {

        /**
         * Preparación de data
         * */
        ContractEntity contractEntity = ContractEntity.ContractBuilder.an().build();
        /**
         * Mocks
         * */
        when(pisdR226.executeUpdateInsuranceContractByCertifyBank(Mockito.anyObject()))
                .thenThrow(new BusinessException(PISDErrors.ERROR_TIME_OUT.getAdviceCode(), PISDErrors.ERROR_TIME_OUT.isRollback(),
                        PISDErrors.ERROR_TIME_OUT.getMessage()));
        /**
         * Ejecución de proceso
         * */
        insuranceContractDAO.updateInsuranceContractByCertifyBank(contractEntity);

    }

    @Test(expected = BusinessException.class)
    public void update_insurance_contract_error_update_exception() {

        /**
         * Preparación de data
         * */
        ContractEntity contractEntity = ContractEntity.ContractBuilder.an().build();
        /**
         * Mocks
         * */
        when(pisdR226.executeUpdateInsuranceContractByCertifyBank(Mockito.anyObject()))
                .thenReturn(false);
        /**
         * Ejecución de proceso
         * */
        insuranceContractDAO.updateInsuranceContractByCertifyBank(contractEntity);

    }
}