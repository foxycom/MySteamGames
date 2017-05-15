package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * UnitsConverterHelperTest.java
 * Purpose: Handle the test for UnitsConverterHelper.java
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-15
 */

public class UnitsConverterHelperTest {

    @Test
    public void formatDouble_correct() {
        double doubleToFormat[] = {25.2223, 35.9, 789.11, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        double expectedValue[] = {25.22, 35.9, 789.11, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};

        for (int i = 0; i < doubleToFormat.length; i++) {
            double formattedDouble = UnitsConverterHelper.formatDouble(doubleToFormat[i]);
            assertTrue(expectedValue[i] == formattedDouble);
        }
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void formatDouble_null() {
        UnitsConverterHelper.formatDouble(null);
    }

    @Test
    public void displayMinutesInHours_onlyHours() {
        int totalMinutes[] = {150, 120, 25};
        String expectedResult[] = {"2h", "2h", "25mn"};

        checkResultDisplayMinutes(totalMinutes, expectedResult);
    }

    @Test
    public void displayMinutesInHours_hoursAndMinutes() {
        int totalMinutes[] = {150, 120, 25};
        String expectedResult[] = {"2h30mn", "2h", "25mn"};

        checkResultDisplayMinutes(totalMinutes, expectedResult, true);

    }

    /**
     * Check if the result returned by the function displayMinutesInHours is the expected result.
     *
     * @param totalMinutes    Values that we want to display in a String.
     * @param expectedResults Expected results.
     */
    private void checkResultDisplayMinutes(int totalMinutes[], String expectedResults[], boolean hoursAndMinutes) {
        for (int i = 0; i < totalMinutes.length; i++) {
            String result = UnitsConverterHelper.displayMinutesInHours(totalMinutes[i], hoursAndMinutes);
            assertTrue(result.equals(expectedResults[i]));
        }
    }

    /**
     * Check if the result returned by the function displayMinutesInHours is the expected result.
     *
     * @param totalMinutes    Values that we want to display in a String.
     * @param expectedResults Expected results.
     */
    private void checkResultDisplayMinutes(int totalMinutes[], String expectedResults[]) {
        for (int i = 0; i < totalMinutes.length; i++) {
            String result = UnitsConverterHelper.displayMinutesInHours(totalMinutes[i]);
            assertTrue(result.equals(expectedResults[i]));
        }
    }


    @Test
    public void createPricePerHour_success() {
        Double pricePerHour[] = {15.25, 120.2555, 98.9, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        String currency[] = {"€", "$", "£", "€", "€"};

        String expectedResult[] = {"15.25€/h", "120.26$/h", "98.9£/h", Double.POSITIVE_INFINITY + "€/h", Double.NEGATIVE_INFINITY + "€/h"};

        for (int i = 0; i < pricePerHour.length; i++) {
            String result = UnitsConverterHelper.createPricePerHour(pricePerHour[i], currency[i]);
            assertTrue(result.equals(expectedResult[i]));
        }
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void createPricePerHour_pricePerHourIsNull() {
        UnitsConverterHelper.createPricePerHour(null, "€");
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void createPricePerHour_currencyIsNull() {
        UnitsConverterHelper.createPricePerHour(25.3, null);

    }

    @Test
    public void createProgressionInHours() {
        double nbHoursPlayed[] = {12.25, 1, 47.98, 2.0};
        double nbHoursToReachThreshold[] = {25, 12, 96.12, 98.0};

        String expectedResult[] = {"12.25 / 25h", "1 / 12h", "47.98 / 96.12h", "2 / 98h"};

        for (int i = 0; i < nbHoursPlayed.length; i++) {
            String result = UnitsConverterHelper.createProgressionInHours(nbHoursPlayed[i], nbHoursToReachThreshold[i]);
            assertTrue(result.equals(expectedResult[i]));
        }
    }
}
