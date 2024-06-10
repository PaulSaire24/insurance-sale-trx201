package com.bbva.rbvd.lib.r211.impl.service.api;

import com.bbva.ksmk.dto.caas.CredentialsDTO;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.ksmk.lib.r002.KSMKR002;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CryptoServiceInternalTest {

    @Mock
    private KSMKR002 ksmkR002;

    @InjectMocks
    private CryptoServiceInternal cryptoServiceInternal;

    @Test
    public void encryptContactDetailReturnsEncryptedValueWhenOutputIsNotEmpty() {
        String value = "testValue";
        String expectedOutput = "encryptedValue";
        OutputDTO outputDTO = new OutputDTO();
        outputDTO.setData(expectedOutput);
        when(ksmkR002.executeKSMKR002(anyList(), anyString(), anyString(), any(CredentialsDTO.class)))
                .thenReturn(Collections.singletonList(outputDTO));


        String result = cryptoServiceInternal.encryptContactDetail(value);

        assertEquals(expectedOutput, result);
    }

    @Test
    public void encryptContactDetailReturnsEmptyStringWhenOutputIsEmpty() {
        String value = "testValue";
        when(ksmkR002.executeKSMKR002(anyList(), anyString(), anyString(), any(CredentialsDTO.class)))
                .thenReturn(Collections.emptyList());

        String result = cryptoServiceInternal.encryptContactDetail(value);

        assertEquals(StringUtils.EMPTY, result);
    }

    @Test
    public void encryptContactDetailReturnsEmptyStringWhenOutputIsNull() {
        String value = "testValue";
        when(ksmkR002.executeKSMKR002(anyList(), anyString(), anyString(), any(CredentialsDTO.class)))
                .thenReturn(null);

        String result = cryptoServiceInternal.encryptContactDetail(value);

        assertEquals(StringUtils.EMPTY, result);
    }
}