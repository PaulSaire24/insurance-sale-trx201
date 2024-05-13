package com.bbva.rbvd.lib.r211.impl.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insurancemissionsale.constans.RBVDInternalErrors;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class FunctionsUtilsTest {


    @Test
    public void isValidateRangeReturnsTrueWhenValueIsWithinRange() {
        assertTrue(FunctionsUtils.isValidateRange(5, 1, 10));
    }

    @Test
    public void isValidateRangeReturnsFalseWhenValueIsBelowRange() {
        assertFalse(FunctionsUtils.isValidateRange(0, 1, 10));
    }

    @Test
    public void isValidateRangeReturnsFalseWhenValueIsAboveRange() {
        assertFalse(FunctionsUtils.isValidateRange(11, 1, 10));
    }

    @Test
    public void isValidateRangeReturnsTrueWhenValueIsEqualToMinRange() {
        assertTrue(FunctionsUtils.isValidateRange(1, 1, 10));
    }

    @Test
    public void isValidateRangeReturnsTrueWhenValueIsEqualToMaxRange() {
        assertTrue(FunctionsUtils.isValidateRange(10, 1, 10));
    }

    @Test
    public void validateMapReturnsOptionalOfMapWhenMapIsNotEmpty() {
        Map<String, String> testMap = new HashMap<>();
        testMap.put("key", "value");

        Optional<Map<String, String>> result = FunctionsUtils.validateMap(testMap);

        assertTrue(result.isPresent());
        assertEquals(testMap, result.get());
    }

    @Test
    public void validateMapReturnsEmptyOptionalWhenMapIsEmpty() {
        Map<String, String> testMap = new HashMap<>();

        Optional<Map<String, String>> result = FunctionsUtils.validateMap(testMap);

        assertFalse(result.isPresent());
    }

    @Test
    public void validateMapReturnsEmptyOptionalWhenMapIsNull() {
        Map<String, String> testMap = null;

        Optional<Map<String, String>> result = FunctionsUtils.validateMap(testMap);

        assertFalse(result.isPresent());
    }

    @Test
    public void buildValidationReturnsBusinessExceptionWithCorrectValues() {
        RBVDInternalErrors error = RBVDInternalErrors.ERROR_EMPTY_RESULT_QUOTATION_DATA;
        BusinessException result = FunctionsUtils.buildValidation(error);

        assertEquals(error.getAdviceCode(), result.getAdviceCode());
        assertEquals(error.isRollback(), result.isHasRollback());
        assertEquals(error.getMessage(), result.getMessage());
    }
}