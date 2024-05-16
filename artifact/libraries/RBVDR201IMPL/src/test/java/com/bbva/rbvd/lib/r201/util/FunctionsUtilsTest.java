package com.bbva.rbvd.lib.r201.util;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.*;

public class FunctionsUtilsTest {

    @Test
    public void generateCorrectDateFormat_shouldReturnFormattedDate_forSingleDigitDayAndMonth() {
        LocalDate localDate = new LocalDate(2022, 1, 1);

        String result = FunctionsUtils.generateCorrectDateFormat(localDate);

        assertEquals("2022-01-01", result);
    }

    @Test
    public void generateCorrectDateFormat_shouldReturnFormattedDate_forDoubleDigitDayAndMonth() {
        LocalDate localDate = new LocalDate(2022, 12, 31);

        String result = FunctionsUtils.generateCorrectDateFormat(localDate);

        assertEquals("2022-12-31", result);
    }

    @Test
    public void generateCorrectDateFormat_shouldReturnFormattedDate_forSingleDigitDayAndDoubleDigitMonth() {
        LocalDate localDate = new LocalDate(2022, 12, 1);

        String result = FunctionsUtils.generateCorrectDateFormat(localDate);

        assertEquals("2022-12-01", result);
    }

    @Test
    public void generateCorrectDateFormat_shouldReturnFormattedDate_forDoubleDigitDayAndSingleDigitMonth() {
        LocalDate localDate = new LocalDate(2022, 1, 31);

        String result = FunctionsUtils.generateCorrectDateFormat(localDate);

        assertEquals("2022-01-31", result);
    }

    @Test
    public void convertFromStringToBoolean_shouldReturnTrue_whenInputIsS() {
        String input = "S";

        Boolean result = FunctionsUtils.convertFromStringToBoolean(input);

        assertTrue(result);
    }

    @Test
    public void convertFromStringToBoolean_shouldReturnTrue_whenInputIsLowerCaseS() {
        String input = "s";

        Boolean result = FunctionsUtils.convertFromStringToBoolean(input);

        assertTrue(result);
    }

    @Test
    public void convertFromStringToBoolean_shouldReturnFalse_whenInputIsNotS() {
        String input = "A";

        Boolean result = FunctionsUtils.convertFromStringToBoolean(input);

        assertFalse(result);
    }

    @Test
    public void convertFromStringToBoolean_shouldReturnFalse_whenInputIsNull() {
        String input = null;

        Boolean result = FunctionsUtils.convertFromStringToBoolean(input);

        assertFalse(result);
    }

    @Test
    public void convertStringToLocalDate() {
        LocalDate result = FunctionsUtils.convertStringToLocalDate("2024-05-16");
        String day = (result.getDayOfMonth() < 10) ? "0" + result.getDayOfMonth() : String.valueOf(result.getDayOfMonth());
        String month = (result.getMonthOfYear() < 10) ? "0" + result.getMonthOfYear() : String.valueOf(result.getMonthOfYear());
        int year = result.getYear();
        assertEquals("2024-05-16", year + "-" + month + "-" + day);
    }
}