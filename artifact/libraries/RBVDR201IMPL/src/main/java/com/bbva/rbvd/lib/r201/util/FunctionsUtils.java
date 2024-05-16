package com.bbva.rbvd.lib.r201.util;

import com.bbva.rbvd.dto.cicsconnection.utils.HostAdvice;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class FunctionsUtils {

    private FunctionsUtils(){}

    public static String generateCorrectDateFormat(LocalDate localDate) {
        String day = (localDate.getDayOfMonth() < 10) ? "0" + localDate.getDayOfMonth() : String.valueOf(localDate.getDayOfMonth());
        String month = (localDate.getMonthOfYear() < 10) ? "0" + localDate.getMonthOfYear() : String.valueOf(localDate.getMonthOfYear());
        return  localDate.getYear()+ "-" + month + "-" + day  ;
    }

    public static Date convertStringToDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static LocalDate convertStringToLocalDate(String dateString) {
        org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = formatter.parseDateTime(dateString);
        return dateTime.toLocalDate();
    }

    public static Boolean convertFromStringToBoolean(final String value) {
        return "S".equalsIgnoreCase(value) ? Boolean.TRUE : Boolean.FALSE;
    }

    public static String getAdviceListOfString(List<HostAdvice> hostAdviceCode){
        return hostAdviceCode.stream().map(hostAdvice -> hostAdvice.getCode().concat("|").concat(hostAdvice.getDescription())).collect(Collectors.joining(";","[","]"));
    }

}
