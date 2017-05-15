package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import java.text.DecimalFormat;

/**
 * UnitsConverterHelper.java
 * Purpose: Contains helpers that convert units into an other format.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-15
 */

public final class UnitsConverterHelper {

    // Empty constructor to be sure that no one will try to create an object
    private UnitsConverterHelper() {
    }

    /**
     * Convert the minutes into hours in a formatted string (xxh). If there is less than 60 minutes,
     * return xxmn.
     *
     * @param totalMinutes    The amount of minutes that we want to convert into hours.
     * @param hoursAndMinutes return hours and minutes if true.
     * @return a string containing the time played with a correct formatting.
     */
    public static String displayMinutesInHours(int totalMinutes, boolean hoursAndMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        String formattedString = "";
        if (!hoursAndMinutes) {
            if (hours == 0) {
                formattedString = String.valueOf(minutes) + "mn";
            } else {
                formattedString = String.valueOf(hours) + "h";
            }
        } else {
            if (hours == 0) {
                formattedString = String.valueOf(minutes) + "mn";
            } else if (minutes == 0) {
                formattedString = String.valueOf(hours) + "h";
            } else {
                formattedString = String.valueOf(hours) + "h" + String.valueOf(minutes) + "mn";
            }
        }

        return formattedString;

    }

    /**
     * Convert the minutes into hours in a formatted string (xxh). If there is less than 60 minutes,
     * return xxmn.
     *
     * @param totalMinutes The amount of minutes that we want to convert into hours.
     * @return a string containing the time played with a correct formatting.
     */
    public static String displayMinutesInHours(int totalMinutes) {
        return displayMinutesInHours(totalMinutes, false);
    }


    /**
     * Convert the double in parameter into a double with only 2 digits after decimal.
     * For example, 7.4566666 will become 7.46.
     *
     * @param doubleToConvert double that we wants to convert.
     * @return a double with only 2 digits after decimal.
     */
    public static double formatDouble(Double doubleToConvert) {
        if (doubleToConvert.equals(Double.POSITIVE_INFINITY) || doubleToConvert.equals(Double.NEGATIVE_INFINITY)) {
            return doubleToConvert;
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            return Double.valueOf(df.format(doubleToConvert));
        }
    }

    /**
     * Create a String representing the price per hour.
     * The format is 2.36€/h.
     *
     * @param pricePerHour that we want to display.
     * @param currency     symbol to put after the price.
     * @return a formatted string ready to be displayed.
     */
    public static String createPricePerHour(Double pricePerHour, String currency) {

        if (pricePerHour == null || currency == null) {
            throw new NullPointerException("pricePerHour or currency cannot be null. pricePerHour=" + pricePerHour + " & currency=" + currency);

        }
        pricePerHour = UnitsConverterHelper.formatDouble(pricePerHour);
        return pricePerHour + currency + "/h";
    }

    /**
     * Create a String representing the completion in hours.
     * The format is 2.35 / 2.99h.
     *
     * @param nbHoursPlayed           number of hours played.
     * @param nbHoursToReachThreshold number of hours needed to reach the threshold.
     * @return a string well formatted.
     */
    public static String createProgressionInHours(Double nbHoursPlayed, Double nbHoursToReachThreshold) {

        String returnValue = "";
        // We clean the nbHoursPlayed and get rid of the decimal if equal to 0
        Double cleanNbHoursPlayed = UnitsConverterHelper.formatDouble(nbHoursPlayed);
        if (cleanNbHoursPlayed % 1 == 0) {
            returnValue += cleanNbHoursPlayed.intValue();
        } else {
            returnValue += cleanNbHoursPlayed;
        }

        returnValue += " / ";

        // We do the same with nbHoursToReachThreshold
        Double cleanNbHoursToReachThreshold = UnitsConverterHelper.formatDouble(nbHoursToReachThreshold);
        if (cleanNbHoursToReachThreshold % 1 == 0) {
            returnValue += cleanNbHoursToReachThreshold.intValue();
        } else {
            returnValue += cleanNbHoursToReachThreshold;
        }

        returnValue += "h";

        return returnValue;
    }
}