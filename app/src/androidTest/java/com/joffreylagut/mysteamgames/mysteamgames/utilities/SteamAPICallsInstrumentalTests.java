package com.joffreylagut.mysteamgames.mysteamgames.utilities;


import androidx.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;

import static junit.framework.Assert.assertTrue;

/**
 * SteamAPICallsUnitTests.java
 * Purpose: Contains all the methods relatives to the Steam API.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-04-08
 */

@Ignore
@RunWith(AndroidJUnit4.class)
public class SteamAPICallsInstrumentalTests {

    @Test
    public void getURLPlayerProfileInformation() throws MalformedURLException {

        URL finalURL = new URL(SteamAPICalls.STEAM_API_BASE_URL + SteamAPICalls.STEAM_CALL_USER_PROFILE_URL + "?key=" + SteamAPICalls.API_KEY + "&steamids=" + SampleGenerator.DEFAULT_STEAM_ID);
        URL generatedURL = SteamAPICalls.getURLPlayerProfileInformation(SampleGenerator.DEFAULT_STEAM_ID);
        assertTrue(finalURL.equals(generatedURL));
    }

    @Test
    public void getURLPlayerOwnedGames() throws MalformedURLException {

        URL finalURL = new URL(SteamAPICalls.STEAM_API_BASE_URL + SteamAPICalls.STEAM_CALL_USER_OWNED_GAMES_URL + "?key=" + SteamAPICalls.API_KEY + "&include_appinfo=1" + "&steamid=" + SampleGenerator.DEFAULT_STEAM_ID);
        URL generatedURL = SteamAPICalls.getURLPlayerOwnedGames(SampleGenerator.DEFAULT_STEAM_ID);
        assertTrue(finalURL.equals(generatedURL));
    }

    @Test
    public void convertTimePlayed_resultMinutes(){
        String finalValue = "32mn";
        String generatedValue = UnitsConverterHelper.displayMinutesInHours(32);

        assertTrue(finalValue.equals(generatedValue));
    }

    @Test
    public void convertTimePlayed_resultHours(){
        String finalValue = "3h";
        String generatedValue = UnitsConverterHelper.displayMinutesInHours(195);

        assertTrue(finalValue.equals(generatedValue));
    }

    @Test
    public void convertTimePlayed_resultHoursAndMinutes(){
        String finalValue = "3h15mn";
        String generatedValue = UnitsConverterHelper.displayMinutesInHours(195, true);

        assertTrue(finalValue.equals(generatedValue));
    }

    @Test
    public void createGameImageURL() throws MalformedURLException {
        URL finalUrl = new URL("http://media.steampowered.com/steamcommunity/public/images/apps/35878/255555.jpg");
        URL generatedUrl = SteamAPICalls.createGameImageURL("255555", 35878L);

        assertTrue(finalUrl.equals(generatedUrl));
    }
}
