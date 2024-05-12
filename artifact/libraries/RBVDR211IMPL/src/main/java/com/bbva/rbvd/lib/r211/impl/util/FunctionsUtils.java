package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalConstants;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.temporal.ValueRange;
import java.util.*;

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

    public static  Optional<String> validateString(String input) {
        if (!StringUtils.isEmpty(input)) {
            return Optional.of(input);
        }
        return Optional.empty();
    }

    public static <T> Optional<List<T>> validateList(List<T> list) {
        if(!CollectionUtils.isEmpty(list)){
            return Optional.of(list);
        }
        return Optional.empty();
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


    public static String encodeB64(byte[] hash) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

}
