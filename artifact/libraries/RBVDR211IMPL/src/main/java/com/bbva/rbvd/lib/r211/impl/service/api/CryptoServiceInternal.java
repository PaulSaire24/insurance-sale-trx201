package com.bbva.rbvd.lib.r211.impl.service.api;

import com.bbva.ksmk.dto.caas.CredentialsDTO;
import com.bbva.ksmk.dto.caas.InputDTO;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.ksmk.lib.r002.KSMKR002;
import com.bbva.rbvd.lib.r211.impl.util.FunctionsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants.Crypto;
import static java.util.Collections.singletonList;

public class CryptoServiceInternal {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoServiceInternal.class);

    private KSMKR002 ksmkR002;

    public String encryptContactDetail(String value){
        LOGGER.info(" CryptoServiceInternal :: encryptByValue :: [ value :: {}  ]",value);
        String b64Value = FunctionsUtils.encodeB64(value.getBytes(StandardCharsets.UTF_8));
        List<OutputDTO> output = this.ksmkR002.executeKSMKR002(singletonList(new InputDTO(b64Value, Crypto.BASE64_URL)), "", Crypto.INPUT_CONTEXT_CRYPTO_CONTACT_DETAIL,
                new CredentialsDTO(Crypto.APP_NAME, "", Crypto.CRED_EXTRA_PARAMS));
        return CollectionUtils.isEmpty(output) ? StringUtils.EMPTY : output.get(0).getData();
    }

    public String encryptCustomerId(String value){
        LOGGER.info(" CryptoServiceInternal :: encryptCustomerId :: [ value :: {}  ]",value);
        String b64Value = FunctionsUtils.encodeB64(StringUtils.defaultString(value).getBytes(StandardCharsets.UTF_8));
        List<OutputDTO> output = this.ksmkR002.executeKSMKR002(singletonList(new InputDTO(b64Value, Crypto.BASE64_URL)), "", Crypto.INPUT_CONTEXT_CRYPTO_CUSTOMER_ID, new CredentialsDTO(Crypto.APP_NAME, "", Crypto.CRED_EXTRA_PARAMS));
        return CollectionUtils.isEmpty(output) ? StringUtils.EMPTY : output.get(0).getData();
    }

    public void setKsmkR002(KSMKR002 ksmkR002) {
        this.ksmkR002 = ksmkR002;
    }
}
