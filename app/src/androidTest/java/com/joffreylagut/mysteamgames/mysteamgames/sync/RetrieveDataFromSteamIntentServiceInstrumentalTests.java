package com.joffreylagut.mysteamgames.mysteamgames.sync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.facebook.stetho.Stetho;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.models.User;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator.generateUserWithoutIdAndWithoutGames;
import static junit.framework.Assert.assertTrue;

/**
 * RetrieveDataFromSteamIntentServiceInstrumentalTests.java
 * Purpose: Handle all the instrumental tests for RetrieveDataFromSteamIntentService.java
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-04-07
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RetrieveDataFromSteamIntentServiceInstrumentalTests {

    private UserDbHelper mUserDbHelper;
    private SQLiteDatabase mDb;

    @Before
    public void setUp(){
        Context mContext = InstrumentationRegistry.getTargetContext();
        Stetho.initializeWithDefaults(mContext);
        UserDbHelper.setTestDatabase();
        mUserDbHelper = UserDbHelper.getInstance(mContext);
        mDb = mUserDbHelper.getWritableDatabase();
    }

    @After
    public void finish() {
        mUserDbHelper.close();
    }

    @Test
    public void InsertSteamJsonUserInformationInDb(){
        // TODO Case when the string is not a good steam user json

        // USE CASE 1 Creation of a new user
        // We need to delete all the information in db
        mUserDbHelper.resetDb(mDb);

        // We execute the function
        RetrieveDataFromSteamIntentService.InsertSteamJsonUserInformationInDb(InstrumentationRegistry.getTargetContext(), SampleGenerator.STEAM_USER_JSON_SAMPLE);

        // And check if the user have been created in db
        User user = mUserDbHelper.getUserBySteamId(mDb, SampleGenerator.DEFAULT_STEAM_ID, false);
        assertTrue(user.getUserID() != 0);

        // USE CASE 2 User update
        // We execute the function a second time
        RetrieveDataFromSteamIntentService.InsertSteamJsonUserInformationInDb(InstrumentationRegistry.getTargetContext(), SampleGenerator.STEAM_USER_JSON_SAMPLE_UPDATED);

        // And check if the user have been created in db
        user = mUserDbHelper.getUserBySteamId(mDb, SampleGenerator.DEFAULT_STEAM_ID, false);
        assertTrue(user.getAccountName().compareTo("SlaydeBTW") == 0);
    }

    @Test
    public void InsertSteamJsonUserGamesInDb(){

        // We create a user in DB to assign the games that we will create
        mUserDbHelper.resetDb(mDb);
        User user = generateUserWithoutIdAndWithoutGames();
        user = mUserDbHelper.addNewUser(mDb, user);

        // USE CASE 1 Add the games in db
        // We execute the function
        RetrieveDataFromSteamIntentService.InsertSteamJsonUserGamesInDb(InstrumentationRegistry.getTargetContext(), SampleGenerator.STEAM_USER_GAMES_JSON_SAMPLE, user.getUserID());

        // We get the user games from db
        user = mUserDbHelper.getUserByID(mDb, user.getUserID(), true);
        // And check if the user have 3 games
        assertTrue(user.getOwnedGames().size() == 3);

        // We check if the Game with steamId xxxxx have the correct values
        for(OwnedGame ownedGame: user.getOwnedGames()){
            if(ownedGame.getGame().getSteamID() == 236110){
                assertTrue(ownedGame.getGame().getGameName().compareTo("Dungeon Defenders II") == 0);
                assertTrue(ownedGame.getTimePlayedForever() == 60000);
                assertTrue(ownedGame.getTimePlayed2Weeks() == 1200);
                assertTrue(ownedGame.getGame().getGameIcon().equals(SteamAPICalls.createGameImageURL("0ce07c2568f978a01c8ae5f4d9402c3253963641", 236110L)));
                assertTrue(ownedGame.getGame().getGameLogo().equals(SteamAPICalls.createGameImageURL("fe628ba79f4b23a5aee079d1c71f7c1ef24065f4", 236110L)));
            }
        }

        // USE CASE 2 Update the games in db
        // We execute the function a second time
        RetrieveDataFromSteamIntentService.InsertSteamJsonUserGamesInDb(InstrumentationRegistry.getTargetContext(), SampleGenerator.STEAM_USER_GAMES_JSON_SAMPLE_UPDATED, user.getUserID());

        // We get the updated user games from db
        User userUpdated = mUserDbHelper.getUserByID(mDb, user.getUserID(), true);
        // And check if the user still have the same amount of games
        assertTrue(userUpdated.getOwnedGames().size() == user.getOwnedGames().size());


        // We check if the updated games have the correct values
        for(OwnedGame ownedGame: userUpdated.getOwnedGames()){
            if(ownedGame.getGame().getSteamID() == 236110){
                assertTrue(ownedGame.getGame().getGameName().compareTo("Dungeon Defenders II9") == 0);
                assertTrue(ownedGame.getTimePlayedForever() == 60009);
                assertTrue(ownedGame.getTimePlayed2Weeks() == 1209);
                assertTrue(ownedGame.getGame().getGameIcon().equals(SteamAPICalls.createGameImageURL("0ce07c2568f978a01c8ae5f4d9402c3253963649", 236110L)));
                assertTrue(ownedGame.getGame().getGameLogo().equals(SteamAPICalls.createGameImageURL("fe628ba79f4b23a5aee079d1c71f7c1ef24065f9", 236110L)));
            }
        }
    }

}
