package com.bbva.rbvd.lib.r211.impl.aspects.errors;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorManagementNotLifeCross {

    private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

    public void controlCommonFailuresOfUnexpected(JoinPoint joinPoint, Throwable error) {
        ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();
        String errorCause = StringUtils.defaultString(error.getMessage());
        architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_GENERIC_APX_IN_CALLED_RIMAC.getAdviceCode(),errorCause);
        LOGGER.info(" :: controlCommonFailuresOfUnexpected[ declaringTypeName :: {} , signatureName :: {} ]", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        throw new BusinessException(RBVDInternalErrors.ERROR_GENERIC_APX_IN_CALLED_RIMAC.getAdviceCode(),false,errorCause);
    }


}
