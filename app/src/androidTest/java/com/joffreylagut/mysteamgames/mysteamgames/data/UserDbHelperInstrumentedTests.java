package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.InstrumentationRegistry;
import androidx.test.filters.LargeTest;

import androidx.test.runner.AndroidJUnit4;
import androidx.test.runner.AndroidJUnitRunner;
import com.facebook.stetho.Stetho;
import com.joffreylagut.mysteamgames.mysteamgames.models.Game;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameBundle;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator.generateGameBundleWithoutId;
import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator.generateGameWithoutId;
import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SampleGenerator.generateUserWithoutIdAndWithoutGames;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * UserDbHelperUnitTests.java
 * Purpose: Handle all the instrumental tests of UserDbHelper.java.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-04-06
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserDbHelperInstrumentedTests {

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
    public void addBundleAndGetBundleById() {

        GameBundle gameBundle = generateGameBundleWithoutId();
        gameBundle = mUserDbHelper.addNewGameBundle(mDb, gameBundle);

        assertTrue(gameBundle.getId() != 0);

        GameBundle bundleFromDb = mUserDbHelper.getGameBundleByID(mDb, gameBundle.getId());

        assertTrue(bundleFromDb.getId() == gameBundle.getId());
        assertTrue(bundleFromDb.getName().compareTo(gameBundle.getName()) == 0);
        assertTrue(bundleFromDb.getPrice() == gameBundle.getPrice());

    }

    @Test
    public void getAllBundles_WithBundlesInDb(){

        GameBundle gameBundle = generateGameBundleWithoutId();
        mUserDbHelper.addNewGameBundle(mDb, gameBundle);

        List<GameBundle> gameBundles = mUserDbHelper.getAllGameBundles(mDb);
        assertTrue(gameBundles.size() > 0);
    }

    @Test
    public void getAllBundles_WithoutBundlesInDb(){

        mUserDbHelper.dropGameBundleTable(mDb);
        mUserDbHelper.createGameBundleTable(mDb);

        List<GameBundle> gameBundles = mUserDbHelper.getAllGameBundles(mDb);
        assertTrue(gameBundles.size() == 0);
    }

    @Test
    public void updateBundle(){

        GameBundle gameBundle = generateGameBundleWithoutId();
        gameBundle = mUserDbHelper.addNewGameBundle(mDb, gameBundle);

        assertTrue(gameBundle.getId() != 0);

        GameBundle bundleUpdated = new GameBundle();
        bundleUpdated.setId(gameBundle.getId());
        bundleUpdated.setName("Bundle updated");
        bundleUpdated.setPrice(25.00);

        mUserDbHelper.updateGameBundle(mDb, bundleUpdated);

        GameBundle bundleFromDb = mUserDbHelper.getGameBundleByID(mDb, gameBundle.getId());

        assertTrue(bundleFromDb.getId() == bundleUpdated.getId());
        assertTrue(bundleFromDb.getName().compareTo(bundleUpdated.getName()) == 0);
        assertTrue(bundleFromDb.getPrice() == bundleUpdated.getPrice());
    }

    @Test
    public void removeBundle_bundleFound(){
        GameBundle gameBundle = generateGameBundleWithoutId();

        gameBundle = mUserDbHelper.addNewGameBundle(mDb, gameBundle);

        assertTrue(mUserDbHelper.removeGameBundle(mDb, gameBundle));
        assertTrue(mUserDbHelper.getGameBundleByID(mDb, gameBundle.getId()) == null);
    }

    @Test
    public void removeBundle_bundleNotFound(){
        GameBundle gameBundle = generateGameBundleWithoutId();
        gameBundle.setId(-1);

        assertFalse(mUserDbHelper.removeGameBundle(mDb, gameBundle));
        assertTrue(mUserDbHelper.getGameBundleByID(mDb, gameBundle.getId()) == null);
    }
    // TODO test of the function getGameBundleByName
    // TODO test of the function getUserGameBundles


    @Test
    public void addUserAndGetUserByIdWithoutGames(){
        User newUser = generateUserWithoutIdAndWithoutGames();
        newUser = mUserDbHelper.addNewUser(mDb, newUser);

        assertTrue(newUser.getUserID() != 0);

        User userFromDb = mUserDbHelper.getUserByID(mDb, newUser.getUserID(), false);

        testIfSameUser(newUser, userFromDb);

    }

    @Test
    public void getUserByIdWithGames(){
        User newUser = generateUserWithoutIdAndWithoutGames();
        newUser = mUserDbHelper.addNewUser(mDb, newUser);
        assertTrue(newUser.getUserID() != 0);

        List<OwnedGame> ownedGames = new ArrayList<>();
        int nbGamesToAdd = 5;
        int i = 0;
        do {
            OwnedGame newOwnedGames = createOwnedGameWithoutId(newUser.getUserID());
            ownedGames.add(newOwnedGames);
            mUserDbHelper.addNewOwnedGame(mDb, newOwnedGames);
            newUser.setNbMinutesPlayed(newUser.getNbMinutesPlayed() + newOwnedGames.getTimePlayedForever());
            i++;
        }while(i<nbGamesToAdd);

        for (OwnedGame currentOwnedGame: ownedGames) {
            mUserDbHelper.addNewOwnedGame(mDb, currentOwnedGame);
        }
        newUser.setOwnedGames(ownedGames);

        User userFromDb = mUserDbHelper.getUserByID(mDb, newUser.getUserID(), true);

        testIfSameUser(newUser, userFromDb);

    }

    @Test
    public void getAllUsers_WithUsersInDb(){
        // TODO
    }

    @Test
    public void getAllUsers_WithoutUsersInDb(){
        // TODO
    }

    @Test
    public void getUserBySteamIdWithoutGames(){
        // TODO
    }

    @Test
    public void getUserBySteamIdWithGames(){
        // TODO
    }

    @Test
    public void removeUserById(){
        // TODO
    }

    @Test
    public void getUserBySteamIdWithoutGame(){
        // TODO
    }

    @Test
    public void addGameAndGetGameById() {
        Game game = generateGameWithoutId();
        game = mUserDbHelper.addNewGame(mDb, game);

        assertTrue(game.getGameID()>0);

        Game gameFromDb = mUserDbHelper.getGameById(mDb, game.getGameID());

        assertTrue(game.getGameID() == gameFromDb.getGameID());
        assertTrue(game.getSteamID() == gameFromDb.getSteamID());
        assertTrue(game.getGameIcon().equals(gameFromDb.getGameIcon()));
        assertTrue(game.getGameLogo().equals(gameFromDb.getGameLogo()));
        assertTrue(game.getMarketplace().compareTo(gameFromDb.getMarketplace()) == 0);
        assertTrue(game.getGameName().compareTo(gameFromDb.getGameName()) == 0  );
    }

    @Test
    public void getAllGames_WithGamesInDb(){
        Game game = generateGameWithoutId();
        mUserDbHelper.addNewGame(mDb, game);

        List<Game> games = mUserDbHelper.getAllGames(mDb);
        assertTrue(games.size() > 0);
    }

    @Test
    public void getAllGames_WithoutGamesInDb(){
        mUserDbHelper.dropGameTable(mDb);
        mUserDbHelper.createGameTable(mDb);

        List<Game> games = mUserDbHelper.getAllGames(mDb);
        assertTrue(games.size() == 0);
    }

    @Test
    public void updateGameById(){
        updateGame(false);
    }

    @Test
    @Ignore
    public void updateGameBySteamId(){
        updateGame(true);
    }

    /**
     * Test if the game is updated in DB.
     * The update is made on the gameId of steamId depending on the parameter.
     * @param updateBySteamId update via steamId if true, otherwise via gameId.
     */
    private void updateGame(Boolean updateBySteamId){
        Game game = generateGameWithoutId();
        game = mUserDbHelper.addNewGame(mDb, game);

        assertTrue(game.getGameID() > 0);

        Game updatedGame = game;
        updatedGame.setSteamID(222222222L);
        URL url = null;
        try {
            url = new URL("https://www.bing.fr");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        updatedGame.setGameIcon(url);
        updatedGame.setGameLogo(url);
        updatedGame.setMarketplace("Origin");
        updatedGame.setGameName("Instrumental test 2");

        if(updateBySteamId){
            assertTrue(mUserDbHelper.updateGameBySteamId(mDb, updatedGame) > 0);
        }else{
            assertTrue(mUserDbHelper.updateGameById(mDb, updatedGame) > 0);
        }


        game = mUserDbHelper.getGameById(mDb, updatedGame.getGameID());

        assertTrue(game.getGameID() == updatedGame.getGameID());
        assertTrue(game.getSteamID() == updatedGame.getSteamID());
        assertTrue(game.getGameName().compareTo(updatedGame.getGameName()) == 0);
        assertTrue(game.getGameLogo().equals(updatedGame.getGameLogo()));
        assertTrue(game.getGameIcon().equals(updatedGame.getGameIcon()));
        assertTrue(game.getMarketplace().compareTo(updatedGame.getMarketplace()) == 0);
    }

    @Test
    public void removeGame_gameFound(){
        Game game = generateGameWithoutId();
        game = mUserDbHelper.addNewGame(mDb, game);

        assertTrue(mUserDbHelper.removeGame(mDb, game));

        assertTrue(mUserDbHelper.getGameById(mDb, game.getGameID()).getGameID() == 0);
    }

    @Test
    public void removeGame_gameNotFound(){
        Game game = generateGameWithoutId();
        game.setGameID(-1);

        assertTrue(mUserDbHelper.getGameById(mDb, game.getGameID()).getGameID() == 0);
        assertFalse(mUserDbHelper.removeGame(mDb, game));
    }

    /**
     * Function that create a new OwnedGame.
     * Include a new game inserted in db.
     *
     * @return an OwnedGame
     */
    public static OwnedGame createOwnedGameWithoutId(int userId){

        Game game = generateGameWithoutId();
        UserDbHelper userDbHelper = UserDbHelper.getInstance(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase mDb = userDbHelper.getWritableDatabase();
        game = userDbHelper.addNewGame(mDb, game);

        GameBundle gameBundle = generateGameBundleWithoutId();
        gameBundle = userDbHelper.addNewGameBundle(mDb, gameBundle);

        OwnedGame ownedGame = new OwnedGame(userId, game);
        ownedGame.setTimePlayedForever(600);
        ownedGame.setTimePlayed2Weeks(120);
        ownedGame.setGamePrice(25.00);
        ownedGame.setFavorite(true);
        ownedGame.setGameBundle(gameBundle);

        return ownedGame;

    }

    /**
     * This function compare all the properties of User1 with User2 to be sure that the user
     * are identical.
     * @param user1 user1 that'll be compared to user2
     * @param user2 user2 that'll be compared to user1
     */
    private void testIfSameUser(User user1, User user2){
        assertTrue(user1.getUserID() == user2.getUserID());
        assertTrue(user1.getSteamID() == user2.getSteamID());
        assertTrue(user1.getAccountName().compareTo(user2.getAccountName())== 0);
        assertTrue(user1.getAccountPicture().equals(user2.getAccountPicture()));
        assertTrue(user1.getOwnedGames().size() == (user2.getOwnedGames().size()));
        assertTrue(user1.getNbMinutesPlayed() == user2.getNbMinutesPlayed());
    }
}
