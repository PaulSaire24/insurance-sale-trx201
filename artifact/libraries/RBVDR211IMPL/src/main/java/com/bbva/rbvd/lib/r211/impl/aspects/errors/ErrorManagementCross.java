package com.bbva.rbvd.lib.r211.impl.aspects.errors;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import com.bbva.rbvd.lib.r211.impl.RBVDR211Impl;
import com.bbva.rbvd.lib.r211.impl.util.ArchitectureAPXUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorManagementCross {

    private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR211Impl.class);

    public void controlCommonCompanyFailuresOfUnexpected(JoinPoint joinPoint, Throwable error) {
        ArchitectureAPXUtils architectureAPXUtils = new ArchitectureAPXUtils();
        String errorOcurredTrace = StringUtils.left(ExceptionUtils.getStackTrace(error).replace(SystemUtils.LINE_SEPARATOR,""), 300);
        architectureAPXUtils.addAdviceWithDescriptionLibrary(RBVDInternalErrors.ERROR_GENERIC_APX_IN_CALLED_RIMAC.getAdviceCode(),errorOcurredTrace);
        LOGGER.info(" :: controlCommonCompanyFailuresOfUnexpected[ declaringTypeName :: {} , signatureName :: {} ]", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        throw new BusinessException(RBVDInternalErrors.ERROR_GENERIC_APX_IN_CALLED_RIMAC.getAdviceCode(),false,errorOcurredTrace);
    }


}
