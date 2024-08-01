package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.time.temporal.ValueRange;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Optional;
import java.util.Base64;

public class FunctionsUtils {

    public static Map<String, Object> createSingleArgument(String argument, String parameterName) {
        Map<String, Object> mapArgument = new HashMap<>();
        mapArgument.put(parameterName, argument);
        return mapArgument;
    }

    public static BusinessException buildValidation(RBVDInternalErrors error) {
        return new BusinessException(error.getAdviceCode(), error.isRollback(), error.getMessage());
    }

    public static BusinessException buildValidation(RBVDInternalErrors error,String message) {
        return new BusinessException(error.getAdviceCode(), error.isRollback(),message);
    }


    public static <T,K> Optional<Map<T,K>> validateMap(Map<T,K> list) {
        if(!CollectionUtils.isEmpty(list)){
            return Optional.of(list);
        }
        return Optional.empty();
    }
    public static boolean isValidateRange(Integer value, Integer min, Integer max) {
        final ValueRange range = ValueRange.of(min, max);
        return range.isValidIntValue(value);
    }

    public static Date currentDate() {
        DateTimeZone dateTimeZone = DateTimeZone.forID(RBVDInternalConstants.TimeUtil.LIMA_TIME_ZONE);
        DateTime currentLocalDate = new DateTime(new Date(), dateTimeZone);
        return currentLocalDate.toDate();
    }

    public static Date convertDateToLocalTimeZone(Date date) {
        DateTimeZone dateTimeZone = DateTimeZone.forID(RBVDInternalConstants.TimeUtil.GMT_TIME_ZONE);
        DateTime startDate = new DateTime(date, dateTimeZone);
        return startDate.toDate();
    }
    public static LocalDate convertDateToLocalDate(Date date) {
        return new LocalDate(date, DateTimeZone.forID(RBVDInternalConstants.TimeUtil.GMT_TIME_ZONE));
    }

    public static String generateCorrectDateFormat(LocalDate localDate) {
        String day = (localDate.getDayOfMonth() < 10) ? "0" + localDate.getDayOfMonth() : String.valueOf(localDate.getDayOfMonth());
        String month = (localDate.getMonthOfYear() < 10) ? "0" + localDate.getMonthOfYear() : String.valueOf(localDate.getMonthOfYear());
        return day + "/" + month + "/" + localDate.getYear();
    }

    public static String[] generateCodeToSearchContractInOracle(String certifyBank){
       String[] codeToSearchContractInOracle = new String[5];
       codeToSearchContractInOracle[0] = certifyBank.substring(0,4);
       codeToSearchContractInOracle[1] = certifyBank.substring(4,8);
       codeToSearchContractInOracle[2] = certifyBank.substring(8,9);
       codeToSearchContractInOracle[3] = certifyBank.substring(9,10);
       codeToSearchContractInOracle[4] = certifyBank.substring(10,20);
       return codeToSearchContractInOracle;
    }

    public static void loggerAutomatic(String message,String body, Logger LOGGER){
        int limit = 10124;
        int total = body.length();
        int limitInit = 0;
        int limitProx = 0;
        int positionEndValue = 0;
        int tamEndValue = 0;
        if(total <= limit){
            LOGGER.info(message,body);
        }else{
            double division2 =  (double) total / limit;
            double valueInt = Math.ceil(division2);
            for(int i = 1; i<= (int) valueInt; i++){
                limitInit = limitProx;
                limitProx += limit;
                String bodyResult;
                if(i == valueInt){
                    bodyResult = body.substring(positionEndValue+tamEndValue);
                }else{
                    bodyResult = body.substring(limitInit, limitProx);
                    positionEndValue      = body.indexOf(bodyResult);
                    tamEndValue           = bodyResult.length();
                }
                LOGGER.info(message.concat(String.valueOf(i)),bodyResult);
            }
        }

    }


    public static String encodeB64(byte[] hash) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

}
