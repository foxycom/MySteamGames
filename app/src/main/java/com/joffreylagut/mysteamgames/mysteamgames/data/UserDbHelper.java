package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joffreylagut.mysteamgames.mysteamgames.models.Game;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameBundle;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.models.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * UserDbHelper.java
 * Purpose: This class manage all the interactions with the database.
 *
 * @author Joffrey LAGUT
 * @version 1.5 2017-04-02
 */

public class UserDbHelper extends SQLiteOpenHelper {

    // Version of the database.
    // This var must be incremented every time we change the database schema.
    private static final int DATABASE_VERSION = 6;

    // Name of the database.
    private static String DATABASE_NAME = "myGameTimePrice.db";
    static String TEST_DATABASE_NAME = "testMyGameTimePrice.db";

    // Instance of the class. We wants to have a singleton to avoid conflicts
    private static UserDbHelper sInstance;

    private static SQLiteDatabase mDb;

    /**
     * Constructor of UserDbHelper.
     */
    private UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This function returned the instance of UserDbHelper that allow you to communicate
     * with the database.
     * @param context Current context needed when we create a new Instance.
     * @return a UserDbHelper object.
     */
    public static synchronized UserDbHelper getInstance(Context context) {

        // Use the application context, which will ensure that we
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new UserDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * This method is used when we are running tests to be sure to not delete any data from the
     * pre production db stored in the phone.
     */
    public static void setTestDatabase(){
        DATABASE_NAME = TEST_DATABASE_NAME;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database that we want to create.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // We create all of the tables
        createUserTable(db);
        createGameTable(db);
        createGameBundleTable(db);
        createOwnedGamesTable(db);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     *
     * @param db The database that we want to upgrade.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We alter all of the tables
        alterUserTable(db);
        alterGameTable(db);
        alterGameBundleTable(db);
        alterOwnedGamesTable(db);

    }

    public void resetDb(SQLiteDatabase db){
        dropOwnedGamesTable(db);
        dropGameTable(db);
        dropUserTable(db);
        dropGameBundleTable(db);

        onCreate(db);
    }

    //*******************************************************************************************
    // USER TABLE
    //*******************************************************************************************

    /**
     * Function that create the User table in the db in parameter.
     *
     * @param db Database that we want to create User table inside.
     */
    public void createUserTable(SQLiteDatabase db) {
        final String SQL_CREATE_USER_TABLE =
                "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                        UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        UserContract.UserEntry.COLUMN_STEAM_ID + " BIGINT, " +
                        UserContract.UserEntry.COLUMN_ACCOUNT_NAME + " VARCHAR(32), " +
                        UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE + " VARCHAR(256)" +
                        ");";
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    /**
     * Function that alter the User table in the db in parameter.
     *
     * @param db Database that we want to alter User table inside.
     */
    public void alterUserTable(SQLiteDatabase db) {
        final String SQL_ALTER_USER_TABLE = "";
        if (SQL_ALTER_USER_TABLE.length() > 0) {
            db.execSQL(SQL_ALTER_USER_TABLE);
        }
    }

    /**
     * Function that drop the User table in the database in parameter.
     *
     * @param db Database that we want to drop User table inside.
     */
    public void dropUserTable(SQLiteDatabase db) {
        final String SQL_DELETE_USER_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_USER_TABLE);
    }

    /**
     * Function doing the request in database and returning the result.
     * @param db Database to Request
     * @param select SQL Select - List of column that we wants to retrieve ; null = all.
     * @param where SQL Where condition ; null = no condition.
     * @param whereArgs Where arguments ; null = no argument.
     * @param groupBy SQL Group by ; null = none.
     * @param having SQL Having ; null = none.
     * @param order SQL Order ; null = none.
     * @param limit SQL Limit : null = none.
     * @return Cursor result
     */
    private Cursor selectUsers(SQLiteDatabase db,
                               String[] select,
                               String where,
                               String whereArgs[],
                               String groupBy,
                               String having,
                               String order,
                               String limit) {
        Cursor cursor = db.query(UserContract.UserEntry.TABLE_NAME,
                select,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning all of the Users in User table.
     *
     * @param db Database to query.
     * @return a list of users.
     */
    public List<User> getAllUsers(SQLiteDatabase db, boolean withGames) {

        // We request the users
        Cursor cursor = selectUsers(db, null, null, null, null, null, null, null);

        List<User> users = new ArrayList<>();

        // We have to check if there is results.
        if(cursor.getCount() > 0){
            users = createUsersFromCursor(db, cursor, withGames);
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getAllUsers: There is no user in DB.");
        }
        cursor.close();

        return users;
    }



    /**
     * Function returning the user owning the ID in parameter.
     *
     * @param db Database to query.
     * @param userId That we want to retrieve.
     * @param withGames If true, return the games owned by the user and the time played.
     * @return user owning this id.
     */
    public User getUserByID(SQLiteDatabase db, int userId, boolean withGames) {
        // We prepare the request
        String where = UserContract.BundleEntry._ID + " =? ";
        String whereArgs[] = {String.valueOf(userId)};

        // We execute the request
        Cursor cursor = selectUsers(db,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null);

        // Now we have to be sure that there is a result
        if (cursor.getCount() != 0) {
            // There is a result. We will take the first one and create a GameBundle
            List<User> users = createUsersFromCursor(db, cursor, withGames);

            // If there is bundles in the list
            if(users != null){
                // We return the first one
                cursor.close();
                return users.get(0);
            }else{
                // We return null
                cursor.close();
                return null;
            }
        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "getUserById: There is no user with the ID " + userId);
            cursor.close();
            // We return null
            return null;
        }
    }

    /**
     * Function returning the user owning the steamId in parameter.
     *
     * @param db      Database to query.
     * @param steamId That we want to find.
     * @param withGames If true, return the games owned by the user and the time played.
     * @return user with the steamId in parameter.
     */
    public User getUserBySteamId(SQLiteDatabase db, long steamId, boolean withGames) {
        // We have to do the request.
        String where = UserContract.UserEntry.COLUMN_STEAM_ID + " =?";
        String whereArgs[] = {String.valueOf(steamId)};

        // We execute the request
        Cursor cursor = selectUsers(db,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null);

        // Now we have to be sure that there is a result
        if (cursor.getCount() != 0) {
            // There is a result. We will take the first one and create a GameBundle
            List<User> users = createUsersFromCursor(db, cursor, withGames);

            // If there is bundles in the list
            if(users != null){
                // We return the first one
                cursor.close();
                return users.get(0);
            }
        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "getUserBySteamId: There is no user with the ID " + steamId);
        }
        cursor.close();
        // We return an empty user
        return new User();
    }

    /**
     * Add a new user in the User table.
     *
     * @param db Database that we are working on.
     * @param user User to add in database.
     * @return Number of line inserted in database.
     */
    public User addNewUser(SQLiteDatabase db, User user) {
        ContentValues cvUser = new ContentValues();
        cvUser.put(UserContract.UserEntry.COLUMN_STEAM_ID, user.getSteamID());
        cvUser.put(UserContract.UserEntry.COLUMN_ACCOUNT_NAME, user.getAccountName());
        cvUser.put(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE,
                user.getAccountPicture().toString());
        user.setUserID((int) db.insert(UserContract.UserEntry.TABLE_NAME, null, cvUser));
        return user;
    }

    /**
     * Update the user in the database based on his SteamID
     *
     * @param db Database that we are working on.
     * @param user User that we want to update
     * @return The number of rows updated.
     */
    public long updateUserBySteamID(SQLiteDatabase db, User user) {

        // We have to do the request.
        User oldUser = getUserBySteamId(db, user.getSteamID(), false);

        if (oldUser != null) {

            ContentValues userUpdated = new ContentValues();
            userUpdated.put(UserContract.UserEntry.COLUMN_STEAM_ID, user.getSteamID());
            userUpdated.put(UserContract.UserEntry.COLUMN_ACCOUNT_NAME, user.getAccountName());
            userUpdated.put(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE,
                    user.getAccountPicture().toString());

            return db.update(UserContract.UserEntry.TABLE_NAME, userUpdated,
                    UserContract.UserEntry._ID + "=" + oldUser.getUserID(), null);
        } else {
            Log.d(TAG, "updateUserBySteamID: There is no user with the steam id " +
                    user.getSteamID());
            return 0;
        }
    }

    /**
     * Remove a user from the User Table.
     *
     * @param db  Database that we are working on.
     * @param user That we want to delete.
     * @return True if the user is correctly removed.
     */
    public boolean removeUserById(SQLiteDatabase db, User user) {
        return db.delete(UserContract.UserEntry.TABLE_NAME,
                UserContract.UserEntry._ID + "=" + user.getUserID(), null) > 0;
    }

    /**
     * This function is creating a list of Users from the cursor in parameter.
     * @param cursor containing all the users
     * @return a list of users from the cursor.
     */
    private List<User> createUsersFromCursor(SQLiteDatabase db, Cursor cursor, boolean withGames){
        // We first have to be sure that there is information in the cursor
        if (cursor.getCount() != 0) {
            // There is information inside. We are going to create the user.

            // We create an empty list to put our Users inside
            List<User> users = new ArrayList<>();

            // We move the cursor to the first position
            cursor.moveToFirst();
            // And create a loop to go trough all the positions
            while(!cursor.isAfterLast()){
                // We create a new User and put the information inside
                User currentUser = new User();

                currentUser.setUserID(cursor.getInt(
                        cursor.getColumnIndex(UserContract.UserEntry._ID)));
                currentUser.setSteamID(cursor.getLong(
                        cursor.getColumnIndex(UserContract.UserEntry.COLUMN_STEAM_ID)));
                currentUser.setAccountName(cursor.getString(
                        cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT_NAME)));

                try {
                    currentUser.setAccountPicture(new URL(cursor.getString(
                            cursor.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE))));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if(withGames){
                    currentUser.setNbMinutesPlayed(0);
                    currentUser.setOwnedGames(getOwnedGamesByUserID(db, currentUser.getUserID()));
                    for (OwnedGame currentGame : currentUser.getOwnedGames()) {
                        currentUser.setNbMinutesPlayed(currentUser.getNbMinutesPlayed() +
                                currentGame.getTimePlayedForever());
                    }
                }

                // We add the User in the list
                users.add(currentUser);
                // And change the position of the cursor
                cursor.moveToNext();
            }
            // No that we have all the Users, we return the list
            return users;

        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "createUsersFromCursor: There is no users in the cursor.");
            // We return null
            return null;
        }
    }

    /********************************************************************************************
     * GAME TABLE
     ********************************************************************************************/

    /**
     * Function that create the Game table in the db in parameter.
     *
     * @param db Database that we want to create Game table inside.
     */
    public void createGameTable(SQLiteDatabase db) {
        final String SQL_CREATE_GAME_TABLE =
                "CREATE TABLE " + UserContract.GameEntry.TABLE_NAME + " (" +
                        UserContract.GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        UserContract.GameEntry.COLUMN_STEAM_ID + " BIGINT, " +
                        UserContract.GameEntry.COLUMN_GAME_NAME + " VARCHAR(56), " +
                        UserContract.GameEntry.COLUMN_GAME_LOGO + " VARCHAR(256), " +
                        UserContract.GameEntry.COLUMN_GAME_ICON + " VARCHAR(256), " +
                        UserContract.GameEntry.COLUMN_MARKETPLACE + " VARCHAR(50)" +
                        ");";
        db.execSQL(SQL_CREATE_GAME_TABLE);
    }

    /**
     * Function that alter the Game table in the db in parameter.
     *
     * @param db Database that we want to alter Game table inside.
     */
    private void alterGameTable(SQLiteDatabase db) {
        final String SQL_ALTER_GAME_TABLE = "";
        if (SQL_ALTER_GAME_TABLE.length() > 0) {
            db.execSQL(SQL_ALTER_GAME_TABLE);
        }
    }

    /**
     * Function that drop the Game table in the database in parameter.
     *
     * @param db Database that we want to drop Game table inside.
     */
    public void dropGameTable(SQLiteDatabase db) {
        final String SQL_DELETE_GAME_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.GameEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_GAME_TABLE);
    }

    /**
     * Function doing the request in database and returning the result.
     * @param db Database to Request
     * @param select SQL Select - List of column that we wants to retrieve ; null = all.
     * @param where SQL Where condition ; null = no condition.
     * @param whereArgs Where arguments ; null = no argument.
     * @param groupBy SQL Group by ; null = none.
     * @param having SQL Having ; null = none.
     * @param order SQL Order ; null = none.
     * @param limit SQL Limit : null = none.
     * @return Cursor result
     */
    private Cursor selectGames(SQLiteDatabase db,
                                String[] select,
                                String where,
                                String whereArgs[],
                                String groupBy,
                                String having,
                                String order,
                                String limit) {
        Cursor cursor = db.query(UserContract.GameEntry.TABLE_NAME,
                select,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning all of the Games in Game table.
     *
     * @param db Database to query.
     * @return a list of games.
     */
    public List<Game> getAllGames(SQLiteDatabase db) {

        // We request the games
        Cursor cursor = selectGames(db, null, null, null, null, null, null, null);

        List<Game> games = new ArrayList<>();

        // We have to check if there is results.
        if(cursor.getCount() > 0){
            games = createGamesFromCursor(cursor);
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getAllGames: There is no Bundle in DB.");
        }
        cursor.close();

        return games;
    }

    /**
     * Function returning row with the Game _ID in parameter.
     *
     * @param db  Database to query.
     * @param gameId Id of the game that we want to find.
     * @return the game that we are looking for.
     */
    public Game getGameById(SQLiteDatabase db, int gameId) {
        String where = UserContract.GameEntry._ID + " =?";
        String whereArgs[] = {String.valueOf(gameId)};

        Cursor cursor = selectGames(db, null, where, whereArgs, null, null, null, null);

        Game game = new Game();
        // We have to check if there is results.
        if(cursor.getCount() > 0){
            List<Game> gamesReturned = createGamesFromCursor(cursor);
            // There should be only one game so we are returning the first one in the list
            if(gamesReturned != null){
                game = gamesReturned.get(0);
            }
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getGameById: There is no Game in DB.");
        }
        cursor.close();

        return game;
    }

    /**
     * Function returning a game with the Game steamID in parameter.
     *
     * @param db      Database to query.
     * @param steamId That we want to find.
     * @return the game that have the steamId in parameter.
     */
    public Game getGameBySteamID(SQLiteDatabase db, long steamId) {
        String where = UserContract.GameEntry.COLUMN_STEAM_ID + " =?";
        String whereArgs[] = {String.valueOf(steamId)};

        Cursor cursor = selectGames(db, null, where, whereArgs, null, null, null, null);

        Game game = new Game();
        // We have to check if there is results.
        if(cursor.getCount() > 0){
            List<Game> gamesReturned = createGamesFromCursor(cursor);
            // There should be only one game so we are returning the first one in the list
            if(gamesReturned != null){
                game = gamesReturned.get(0);
            }
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getAllGameBundles: There is no Bundle in DB.");
        }
        cursor.close();

        return game;
    }

    /**
     * Add a new game in the Game table.
     *
     * @param db Database that we are working on.
     * @param game Game that we want to insert in database.
     * @return the game inserted with its id.
     */
    public Game addNewGame(SQLiteDatabase db, Game game) {
        ContentValues cvGame = new ContentValues();
        cvGame.put(UserContract.GameEntry.COLUMN_STEAM_ID, game.getSteamID());
        cvGame.put(UserContract.GameEntry.COLUMN_GAME_NAME, game.getGameName());
        cvGame.put(UserContract.GameEntry.COLUMN_GAME_LOGO, game.getGameLogo().toString());
        cvGame.put(UserContract.GameEntry.COLUMN_GAME_ICON, game.getGameIcon().toString());
        cvGame.put(UserContract.GameEntry.COLUMN_MARKETPLACE, game.getMarketplace());

        game.setGameID((int)db.insert(UserContract.GameEntry.TABLE_NAME, null, cvGame));

        return game;
    }

    /**
     * Update the game in the database based on his ID.
     * If a value doesn't have to be update, put null in parameter.
     *
     * @param db Database that we are working on.
     * @param game game that we want to update. ID have to be set.
     * @return The number of rows updated.
     */
    public long updateGameById(SQLiteDatabase db, Game game) {

        ContentValues cvGame = new ContentValues();
        if (game.getSteamID() != 0L) cvGame.put(
                UserContract.GameEntry.COLUMN_STEAM_ID, game.getSteamID());
        if (game.getGameName() != null) cvGame.put(
                UserContract.GameEntry.COLUMN_GAME_NAME, game.getGameName());
        if (game.getGameLogo() != null) cvGame.put(
                UserContract.GameEntry.COLUMN_GAME_LOGO, game.getGameLogo().toString());
        if (game.getGameIcon() != null) cvGame.put(
                UserContract.GameEntry.COLUMN_GAME_ICON, game.getGameIcon().toString());
        if (game.getMarketplace() != null) cvGame.put(
                UserContract.GameEntry.COLUMN_MARKETPLACE, game.getMarketplace());

        return db.update(UserContract.GameEntry.TABLE_NAME, cvGame,
                UserContract.GameEntry._ID + "=" + game.getGameID(), null);
    }

    /**
     * Update the game in the database based on his SteamID
     *
     * @param db          Database that we are working on.
     * @param game        Game that contain the information that we want to update.
     * @return The number of rows updated.
     */
    public long updateGameBySteamId(SQLiteDatabase db, Game game) {
        Game oldGame = getGameBySteamID(db, game.getSteamID());
        if (oldGame != null) {
            game.setGameID(oldGame.getGameID());

            ContentValues cvGame = new ContentValues();
            if (game.getSteamID() != 0L) cvGame.put(
                    UserContract.GameEntry.COLUMN_STEAM_ID, game.getSteamID());
            if (game.getGameName() != null) cvGame.put(
                    UserContract.GameEntry.COLUMN_GAME_NAME, game.getGameName());
            if (game.getGameLogo() != null) cvGame.put(
                    UserContract.GameEntry.COLUMN_GAME_LOGO, game.getGameLogo().toString());
            if (game.getGameIcon() != null) cvGame.put(
                    UserContract.GameEntry.COLUMN_GAME_ICON, game.getGameIcon().toString());
            if (game.getMarketplace() != null) cvGame.put(
                    UserContract.GameEntry.COLUMN_MARKETPLACE, game.getMarketplace());

            return db.update(UserContract.GameEntry.TABLE_NAME, cvGame,
                    UserContract.GameEntry._ID + "=" + game.getGameID(), null);
        } else {
            return 0;
        }
    }

    /**
     * Remove a game from the Game Table.
     *
     * @param db  Database that we are working on.
     * @param game Game that we want to delete.
     * @return True if the user is correctly removed.
     */
    public boolean removeGame(SQLiteDatabase db, Game game) {
        return db.delete(UserContract.GameEntry.TABLE_NAME,
                UserContract.GameEntry._ID + "=" + game.getGameID(), null) > 0;
    }

    /**
     * This function is creating a list of Game from the cursor in parameter.
     * @param cursor containing all the games
     * @return a list of games from the cursor.
     */
    private List<Game> createGamesFromCursor(Cursor cursor){
        // We first have to be sure that there is information in the cursor
        if (cursor.getCount() != 0) {
            // There is information inside. We are going to create the game.

            // We create an empty list to put our Games inside
            List<Game> games = new ArrayList<>();

            // We move the cursor to the first position
            cursor.moveToFirst();
            // And create a loop to go trough all the positions
            while(!cursor.isAfterLast()){
                // We create a new Game and put the information inside
                Game currentGame = new Game();

                currentGame.setGameID(cursor.getInt(
                        cursor.getColumnIndex(UserContract.GameEntry._ID)));
                currentGame.setSteamID(cursor.getLong(
                        cursor.getColumnIndex(UserContract.GameEntry.COLUMN_STEAM_ID)));
                currentGame.setGameName(cursor.getString(
                        cursor.getColumnIndex(UserContract.GameEntry.COLUMN_GAME_NAME)));
                currentGame.setMarketplace(cursor.getString(
                        cursor.getColumnIndex(UserContract.GameEntry.COLUMN_MARKETPLACE)));

                // We now have to create the URLs
                try {
                    currentGame.setGameLogo(new URL(cursor.getString(
                            cursor.getColumnIndex(UserContract.GameEntry.COLUMN_GAME_LOGO))));
                    currentGame.setGameIcon(new URL(cursor.getString(
                            cursor.getColumnIndex(UserContract.GameEntry.COLUMN_GAME_ICON))));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // We add the Game in the list
                games.add(currentGame);
                // And change the position of the cursor
                cursor.moveToNext();
            }
            // No that we have all the Games, we return the list
            return games;

        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "createGamesFromCursor: There is no games in the cursor.");
            // We return null
            return null;
        }
    }

    /********************************************************************************************
     * OWNEDGAMES TABLE
     ********************************************************************************************/

    /**
     * Function that create the OwnedGames table in the db in parameter.
     *
     * @param db Database that we want to create OwnedGames table inside.
     */
    private void createOwnedGamesTable(SQLiteDatabase db) {
        final String SQL_CREATE_OWNEDGAMES_TABLE =
                "CREATE TABLE " + UserContract.OwnedGamesEntry.TABLE_NAME + " (" +
                        UserContract.OwnedGamesEntry.COLUMN_USER_ID + " BIGINT, " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_ID + " BIGINT, " +
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER + " INTEGER, " +
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS + " INTEGER, " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE + " DOUBLE, " +
                        UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " INTEGER, " +
                        UserContract.OwnedGamesEntry.COLUMN_FAVORITE + " BOOLEAN, " +
                        "PRIMARY KEY(" + UserContract.OwnedGamesEntry.COLUMN_USER_ID + ", " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "), FOREIGN KEY (" +
                        UserContract.OwnedGamesEntry.COLUMN_USER_ID + ") REFERENCES " +
                        UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID +
                        "), FOREIGN KEY (" + UserContract.OwnedGamesEntry.COLUMN_GAME_ID +
                        ") REFERENCES " +
                        UserContract.GameEntry.TABLE_NAME + "(" + UserContract.GameEntry._ID + ")," +
                        "FOREIGN KEY (" +
                        UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + ") REFERENCES " +
                        UserContract.BundleEntry.TABLE_NAME + "(" + UserContract.BundleEntry._ID +
                        "));";
        db.execSQL(SQL_CREATE_OWNEDGAMES_TABLE);
    }

    /**
     * Function that alter the OwnedGames table in the db in parameter.
     * @param db Database that we want to alter OwnedGames table inside.
     */
    public void alterOwnedGamesTable(SQLiteDatabase db) {
        String SQL_ALTER_OWNEDGAMES_TABLE =
                "ALTER TABLE " + UserContract.OwnedGamesEntry.TABLE_NAME +
                        " ADD COLUMN " + UserContract.OwnedGamesEntry.COLUMN_FAVORITE +
                        " BOOLEAN DEFAULT false;";
        if (SQL_ALTER_OWNEDGAMES_TABLE.length() > 0) {
            db.execSQL(SQL_ALTER_OWNEDGAMES_TABLE);
        }

        ContentValues values = new ContentValues();
        values.put(UserContract.OwnedGamesEntry.COLUMN_FAVORITE,
                false);
        db.update(UserContract.OwnedGamesEntry.TABLE_NAME,
                values,
                UserContract.OwnedGamesEntry.COLUMN_FAVORITE + "=?",
                new String[]{"false"}
        );

    }

    /**
     * Function that drop the OwnedGames table in the database in parameter.
     * @param db Database that we want to drop OwnedGames table inside.
     */
    public void dropOwnedGamesTable(SQLiteDatabase db) {
        final String SQL_DELETE_OWNEDGAMES_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.OwnedGamesEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_OWNEDGAMES_TABLE);
    }

    /**
     * Function doing the request in database and returning the result.
     * @param db Database to query.
     * @param select SQL Select ; columns that we want to retrieve.
     * @param where SQL Where conditions
     * @param whereArgs Arguments in where conditions
     * @param groupBy SQL Group by
     * @param having SQL Having
     * @param order SQL Order
     * @param limit SQL Limit
     * @return Cursor result
     */
    private Cursor selectOwnedGame(SQLiteDatabase db,
                                   String[] select,
                                   String where,
                                   String whereArgs[],
                                   String groupBy,
                                   String having,
                                   String order,
                                   String limit) {
        Cursor cursor = db.query(UserContract.OwnedGamesEntry.TABLE_NAME,
                select,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning all of the OwnedGames in OwnedGame table.
     *
     * @param db Database to look into.
     * @return List<OwnedGame> containing all the OwnedGames in database.
     */
    public List<OwnedGame> getAllOwnedGames(SQLiteDatabase db) {

        // List that we will return.
        List<OwnedGame> ownedGames = new ArrayList<OwnedGame>();

        // We have to do a query in DB to have all the rows.
        Cursor cursorOwnedGames = selectOwnedGame(db, null, null, null, null, null, null, null);

        // We have to check if there is results.
        if(cursorOwnedGames.getCount() > 0){
            ownedGames = createOwnedGamesFromCursor(cursorOwnedGames, db);
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getAllOwnedGames: There is no Owned games in DB.");
        }
        cursorOwnedGames.close();

        return ownedGames;
    }



    /**
     * Function returning all of the OwnedGames of the user.
     *
     * @param db     Database to query.
     * @param userID user that we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public List<OwnedGame> getOwnedGamesByUserID(SQLiteDatabase db, int userID) {
        // List that we will return.
        List<OwnedGame> ownedGames = new ArrayList<>();

        // We prepare the request
        String where = UserContract.OwnedGamesEntry.COLUMN_USER_ID + " =?";
        String whereArgs[] = {String.valueOf(userID)};

        // We have to do a query in DB to have all the rows.
        Cursor cursorOwnedGames = selectOwnedGame(db, null, where, whereArgs,
                null, null, null, null);

        // We have to check if there is results.
        if(cursorOwnedGames.getCount() > 0){
            ownedGames = createOwnedGamesFromCursor(cursorOwnedGames, db);
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getAllGameBundles: There is no Bundle in DB.");
        }
        cursorOwnedGames.close();

        return ownedGames;
    }

    /**
     * Function returning a List<OwnedGame> owned by the user.
     * @param db     Database to query.
     * @param userID ID of the user that we want to find his game.
     * @return List<OwnedGame>
     */
    public List<OwnedGame> getFavoritesOwnedGamesByUserID(SQLiteDatabase db, int userID) {

        // List that we will return.
        List<OwnedGame> ownedGames = new ArrayList<>();

        // We prepare the request
        String where = UserContract.OwnedGamesEntry.COLUMN_USER_ID + " =? AND " +
                UserContract.OwnedGamesEntry.COLUMN_FAVORITE + " =?";
        String whereArgs[] = {String.valueOf(userID), "1"};

        // We have to do a query in DB to have all the rows.
        Cursor cursorOwnedGames = selectOwnedGame(db, null, where, whereArgs,
                null, null, null, null);

        // We have to check if there is results.
        if(cursorOwnedGames.getCount() > 0){
            ownedGames = createOwnedGamesFromCursor(cursorOwnedGames, db);
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getFavoritesOwnedGamesByUserID: There is no favorite games in DB.");
        }
        cursorOwnedGames.close();

        return ownedGames;

    }

    /**
     * Function returning the list of ownedGame with the bundleId in parameter.
     *
     * @param db       Database to query.
     * @param bundleID user that we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public List<OwnedGame> getOwnedGamesByBundleID(SQLiteDatabase db, int bundleID) {
        // List that we will return.
        List<OwnedGame> ownedGames = new ArrayList<>();

        // We prepare the request
        String where = UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " =?";
        String whereArgs[] = {String.valueOf(bundleID)};

        // We have to do a query in DB to have all the rows.
        Cursor cursorOwnedGames = selectOwnedGame(db, null, where, whereArgs,
                null, null, null, null);

        // We have to check if there is results.
        if(cursorOwnedGames.getCount() > 0){
            ownedGames = createOwnedGamesFromCursor(cursorOwnedGames, db);
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getFavoritesOwnedGamesByUserID: There is no favorite games in DB.");
        }
        cursorOwnedGames.close();

        return ownedGames;
    }

    /**
     * Function returning a OwnedGame.
     *
     * @param db     Database to query.
     * @param gameId That we want to find.
     * @param userId User that owned the game.
     * @return Cursor containing the rows matching the request.
     */
    public OwnedGame getOwnedGame(SQLiteDatabase db, int userId, int gameId) {

        String where = UserContract.OwnedGamesEntry.COLUMN_USER_ID + " =? "
                + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + " =? ";
        String whereArgs[] = {String.valueOf(userId), String.valueOf(gameId)};

        // We execute the request
        Cursor cursor = selectOwnedGame(db,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null);

        // Now we have to be sure that there is a result
        if (cursor.getCount() != 0) {
            // There is a result. We will take the first one and create a GameBundle
            List<OwnedGame> ownedGames = createOwnedGamesFromCursor(cursor, db);

            // If there is bundles in the list
            if(ownedGames != null){
                // We return the first one
                cursor.close();
                return ownedGames.get(0);
            }
        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "getOwnedGameByID: There is no owned game with the game ID " + gameId +
            " for the user id " + userId);
        }
        cursor.close();
        // We return null
        return null;
    }

    /**
     * Add a new owned game in the OwnedGame table.
     *
     * @param db                Database that we are working on.
     * @return Number of line inserted in database.
     */
    public OwnedGame addNewOwnedGame(SQLiteDatabase db, OwnedGame ownedGame) {
        ContentValues cvOwnedGame = new ContentValues();
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, ownedGame.getUserId());
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID,
                ownedGame.getGame().getGameID());
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER,
                ownedGame.getTimePlayedForever());
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE,
                ownedGame.getGamePrice());

        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS,
                ownedGame.getTimePlayed2Weeks());
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_FAVORITE, ownedGame.isFavorite());

        db.insert(UserContract.OwnedGamesEntry.TABLE_NAME, null, cvOwnedGame);

        return ownedGame;
    }

    /**
     * Update the game in the database based on his SteamID
     *
     * @param db                Database that we are working on.
     * @param ownedGame         OwnedGame to insert in DB.
     * @return The number of rows updated.
     */
    public long updateOwnedGame(SQLiteDatabase db, OwnedGame ownedGame, boolean updateFavorite) {

        ContentValues cvOwnedGame = new ContentValues();
        if(ownedGame.getUserId() != 0){
            cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, ownedGame.getUserId());
        }
        if(ownedGame.getGame().getGameID() != 0){
            cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID, ownedGame.getGame().getGameID());
        }
        if(ownedGame.getTimePlayedForever() != 0){
            cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER, ownedGame.getTimePlayedForever());
        }
        if(ownedGame.getTimePlayed2Weeks() != 0){
            cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS, ownedGame.getTimePlayed2Weeks());
        }
        if(ownedGame.getGamePrice() != -1.00){
            cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, ownedGame.getGamePrice());
        }

        if (ownedGame.getGameBundle() != null) {
            cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID,
                    ownedGame.getGameBundle().getId());
        }

        if (updateFavorite) {
            cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_FAVORITE, ownedGame.isFavorite());
        }

        return db.update(UserContract.OwnedGamesEntry.TABLE_NAME,
                cvOwnedGame,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + ownedGame.getUserId() + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" + ownedGame.getGame().getGameID(),
                null);
    }

    /**
     * Update the game in the database based on his SteamID
     *
     * @param db        Database that we are working on.
     * @param ownedGame OwnedGame that we want to update.
     * @return The number of rows updated.
     */
    public long updateOwnedGamePrice(SQLiteDatabase db, OwnedGame ownedGame) {

        ContentValues cvOwnedGame = new ContentValues();
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, ownedGame.getUserId());
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID,
                ownedGame.getGame().getGameID());
        cvOwnedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, ownedGame.getGamePrice());
        return db.update(UserContract.OwnedGamesEntry.TABLE_NAME, cvOwnedGame,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + ownedGame.getUserId()
                        + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" +
                ownedGame.getGame().getGameID(), null);
    }

    /**
     * Function used to update the price of the games included in a GameBundle.
     *
     * @param db         database to work on
     * @param bundleID ID of the bundle.
     * @return Number of game prices updated.
     */
    public long updateOwnedGamePriceFromBundle(SQLiteDatabase db, int bundleID) {

        // We get all the owned games in the bundle
        List<OwnedGame> ownedGames = getOwnedGamesByBundleID(db, bundleID);

        if (ownedGames.size() > 0) {
            // We divide the bundle price by the number of games inside
            double nbGames = (double) ownedGames.size();
            double pricePerGame = ownedGames.get(0).getGameBundle().getPrice() / nbGames;
            DecimalFormat df = new DecimalFormat("#.##");
            pricePerGame = Double.valueOf(df.format(pricePerGame));
            for(OwnedGame currentOwnedGame : ownedGames) {

                currentOwnedGame.setGamePrice(pricePerGame);

                // We update each owned game with the price calculated
                this.updateOwnedGamePrice(db, currentOwnedGame);
            }
        }
        return ownedGames.size();
    }

    /**
     * Remove a OwnedGame from OwnedGame Table.
     *
     * @param db     Database that we are working on.
     * @param ownedGame owned game that we want to delete.
     * @return True if the user is correctly removed.
     */
    public boolean removeOwnedGame(SQLiteDatabase db, OwnedGame ownedGame) {
        return db.delete(UserContract.OwnedGamesEntry.TABLE_NAME,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + ownedGame.getUserId()
                        + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" +
                        ownedGame.getGame().getGameID(), null) > 0;
    }

    /**
     * This function is creating a list of OwnedGame from the cursor in parameter.
     * @param cursor containing all the OwnedGames
     * @return a list of OwnedGame from the cursor.
     */
    private List<OwnedGame> createOwnedGamesFromCursor(Cursor cursor, SQLiteDatabase db){
        // We first have to be sure that there is information in the cursor
        if (cursor.getCount() != 0) {
            // There is information inside. We are going to create the OwnedGame.

            // We create an empty list to put our OwnedGames inside
            List<OwnedGame> OwnedGames = new ArrayList<>();

            // We move the cursor to the first position
            cursor.moveToFirst();
            // And create a loop to go trough all the positions
            while(!cursor.isAfterLast()){
                // We retrieve the objects that we need to include in the OwnedGame
                Game game = getGameById(db, cursor.getInt(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_GAME_ID)));
                GameBundle gameBundle = getGameBundleByID(db, cursor.getInt(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID)));

                // We can't directly get a boolean value from the cursor so we have to
                // get an integer and then put the right value in the object.
                boolean favorite = false;
                if(cursor.getInt(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_FAVORITE)) == 1){
                    favorite = true;
                }

                // We create a new OwnedGame and put the information inside
                OwnedGame currentOwnedGame = new OwnedGame(
                        cursor.getInt(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_USER_ID)),
                        game,
                        cursor.getInt(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER)),
                        cursor.getInt(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS)),
                        cursor.getDouble(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE)),
                        favorite,
                        gameBundle
                        );


                // We add the OwnedGame in the list
                OwnedGames.add(currentOwnedGame);
                // And change the position of the cursor
                cursor.moveToNext();
            }
            // No that we have all the OwnedGames, we return the list
            return OwnedGames;

        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "createOwnedGamesFromCursor: There is no OwnedGame in the cursor.");
            // We return null
            return null;
        }
    }

    /********************************************************************************************
     * BUNDLE TABLE
     ********************************************************************************************/

    /**
     * Function that create the GameBundle table in the db in parameter.
     *
     * @param db Database that we want to create OwnedGames table inside.
     */
    public void createGameBundleTable(SQLiteDatabase db) {
        final String SQL_CREATE_BUNDLE_TABLE =
                "CREATE TABLE " + UserContract.BundleEntry.TABLE_NAME + " (" +
                        UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        UserContract.BundleEntry.COLUMN_BUNDLE_NAME + " VARCHAR(64)," +
                        UserContract.BundleEntry.COLUMN_BUNDLE_PRICE + " DOUBLE " +
                        ");";
        db.execSQL(SQL_CREATE_BUNDLE_TABLE);
    }

    /**
     * Function that alter the GameBundle table in the db in parameter.
     *
     * @param db Database that we want to alter OwnedGames table inside.
     */
    public void alterGameBundleTable(SQLiteDatabase db) {
        final String SQL_ALTER_BUNDLE_TABLE = "";
        if (SQL_ALTER_BUNDLE_TABLE.length() > 0) {
            db.execSQL(SQL_ALTER_BUNDLE_TABLE);
        }
    }

    /**
     * Function that drop the GameBundle table in the database in parameter.
     *
     * @param db Database that we want to drop OwnedGames table.
     */
    public void dropGameBundleTable(SQLiteDatabase db) {
        final String SQL_DELETE_BUNDLE_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.BundleEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_BUNDLE_TABLE);
    }

    /**
     * Function doing the request in database and returning the result.
     * @param db Database to Request
     * @param select SQL Select - List of column that we wants to retrieve ; null = all.
     * @param where SQL Where condition ; null = no condition.
     * @param whereArgs Where arguments ; null = no argument.
     * @param groupBy SQL Group by ; null = none.
     * @param having SQL Having ; null = none.
     * @param order SQL Order ; null = none.
     * @param limit SQL Limit : null = none.
     * @return Cursor result
     */
    private Cursor selectGameBundles(SQLiteDatabase db,
                                     String[] select,
                                     String where,
                                     String whereArgs[],
                                     String groupBy,
                                     String having,
                                     String order,
                                     String limit) {
        return selectGameBundles(db,
                UserContract.BundleEntry.TABLE_NAME,
                select,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
    }

    /**
     * Function doing the request in database and returning the result.
     * @param db Database to Request
     * @param table SQL From
     * @param select SQL Select - List of column that we wants to retrieve ; null = all.
     * @param where SQL Where condition ; null = no condition.
     * @param whereArgs Where arguments ; null = no argument.
     * @param groupBy SQL Group by ; null = none.
     * @param having SQL Having ; null = none.
     * @param order SQL Order ; null = none.
     * @param limit SQL Limit : null = none.
     * @return Cursor result
     */
    private Cursor selectGameBundles(SQLiteDatabase db,
                                     String table,
                                     String[] select,
                                     String where,
                                     String whereArgs[],
                                     String groupBy,
                                     String having,
                                     String order,
                                     String limit) {
        Cursor cursor = db.query(
                table,
                select,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    /**
     * Function returning all of the bundles in GameBundle table.
     *
     * @param db Database to look into.
     * @return List<GameBundle> containing all the bundle in database.
     */
    public List<GameBundle> getAllGameBundles(SQLiteDatabase db) {

        // List that we will return.
        List<GameBundle> gameBundleList = new ArrayList<>();

        // We have to do a query in DB to have all the rows.
        Cursor cursorBundles = selectGameBundles(db, null, null, null, null, null, null, null);

        // We have to check if there is results.
        if(cursorBundles.getCount() > 0){
            gameBundleList = createGameBundlesFromCursor(cursorBundles);
        }else{
            // There is no bundle in database. We log debug message.
            Log.d(TAG, "getAllGameBundles: There is no Bundle in DB.");
        }
        cursorBundles.close();

        return gameBundleList;
    }

    /**
     * Function returning the GameBundle owning the ID in parameter.
     *
     * @param db Database to query.
     * @param bundleId That we want to retrieve.
     * @return GameBundle owning this id.
     */
    public GameBundle getGameBundleByID(SQLiteDatabase db, int bundleId) {
        // We prepare the request
        String where = UserContract.BundleEntry._ID + " =? ";
        String whereArgs[] = {String.valueOf(bundleId)};

        // We execute the request
        Cursor cursor = selectGameBundles(db,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null);

        // Now we have to be sure that there is a result
        if (cursor.getCount() != 0) {
            // There is a result. We will take the first one and create a GameBundle
            List<GameBundle> gameBundles = createGameBundlesFromCursor(cursor);

            // If there is bundles in the list
            if(gameBundles != null){
                // We return the first one
                cursor.close();
                return gameBundles.get(0);
            }
        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "getGameBundleByID: There is no Bundle with the ID " + bundleId);
        }
        cursor.close();
        // We return null
        return null;
    }
    /**
     * Function returning the GameBundle with the name in parameter and
     * owned by UserID in parameter.
     *
     * @param db         Database to query.
     * @param bundleName That we want to find.
     * @param userId     ID of the user who own the bundle
     * @return GameBundle containing the bundle.
     */
    public GameBundle getGameBundleByName(SQLiteDatabase db, String bundleName, int userId) {
        // We prepare the request
        String tables = UserContract.BundleEntry.TABLE_NAME + ", " + UserContract.OwnedGamesEntry.TABLE_NAME;
        String where = UserContract.BundleEntry.COLUMN_BUNDLE_NAME + " =? AND "
                + UserContract.OwnedGamesEntry.COLUMN_USER_ID + " =? AND "
                + UserContract.OwnedGamesEntry.TABLE_NAME + "."
                + UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " = "
                + UserContract.BundleEntry.TABLE_NAME + "." + UserContract.BundleEntry._ID;
        String whereArgs[] = {bundleName, String.valueOf(userId)};

        // We execute the request
        Cursor cursor = selectGameBundles(db,tables, null, where, whereArgs, null, null, null, null);

        // Now we have to be sure that there is a result
        if (cursor.getCount() != 0) {
            // There is a result. We will take the first one and create a GameBundle
            List<GameBundle> gameBundles = createGameBundlesFromCursor(cursor);

            // If there is bundles in the list
            if(gameBundles != null){
                // We return the first one
                cursor.close();
                return gameBundles.get(0);
            }
        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "getGameBundleByName: There is no Bundle with the ID " + bundleName +
                    " for the userId " + userId);
        }
        cursor.close();
        // We return null
        return new GameBundle();
    }

    /**
     * Function returning the list of bundles owned by the user.
     *
     * @param db     Database to query.
     * @param userId ID of the user that we want to retrieve his bundles
     * @return a list of the GameBundles owned by the user.
     */
    public List<GameBundle> getUserGameBundles(SQLiteDatabase db, int userId) {

        // We prepare the request
        String tables = UserContract.BundleEntry.TABLE_NAME + "," + UserContract.OwnedGamesEntry.TABLE_NAME;
        String select[] = {
                UserContract.BundleEntry._ID,
                UserContract.BundleEntry.COLUMN_BUNDLE_NAME,
                UserContract.BundleEntry.COLUMN_BUNDLE_PRICE,
        };
        String where = UserContract.OwnedGamesEntry.TABLE_NAME + "." +
                UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " = " +
                UserContract.BundleEntry.TABLE_NAME + "." + UserContract.BundleEntry._ID +
                " AND " + UserContract.OwnedGamesEntry.COLUMN_USER_ID + " =? ";
        String whereArg[] = {String.valueOf(userId)};
        String order = UserContract.BundleEntry.COLUMN_BUNDLE_NAME + " ASC";

        // We execute the request
        Cursor cursor = selectGameBundles(db, tables, select, where, whereArg, null, null, order, null);

        List<GameBundle> gameBundles = new ArrayList<>();

        // Now we have to be sure that there is a result
        if (cursor.getCount() != 0) {
            gameBundles = createGameBundlesFromCursor(cursor);
        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "getUserGameBundles: There is no Bundle for the userId " + userId);
        }
        cursor.close();
        // We return null
        return gameBundles;
    }

    /**
     * Add a new bundle in GameBundle table.
     *
     * @param db          Database that we are working on.
     * @param gameBundle  Bundle that we want to insert in database.
     * @return ID of the line inserted in database ; -1 if there was an error.
     */
    public GameBundle addNewGameBundle(SQLiteDatabase db, GameBundle gameBundle) {
        ContentValues bundle = new ContentValues();
        bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_NAME, gameBundle.getName());
        bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_PRICE, gameBundle.getPrice());
        int idNewBundle = (int) db.insert(UserContract.BundleEntry.TABLE_NAME, null, bundle);
        gameBundle.setId(idNewBundle);
        return gameBundle;
    }

    /**
     * Update the bundle in parameter in GameBundle table.
     *
     * @param db          Database that we are working on.
     * @param gameBundle  Bundle that we want to update in database.
     * @return Number of line inserted in database.
     */
    public long updateGameBundle(SQLiteDatabase db, GameBundle gameBundle) {
        ContentValues bundle = new ContentValues();
        if (gameBundle.getId() > 0){
            bundle.put(UserContract.BundleEntry._ID, gameBundle.getId());
        }else{
            Log.e(TAG, "updateGameBundle: The GameBundle should have an ID.");
            return -1;
        }
        if (gameBundle.getName() != null){
            bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_NAME, gameBundle.getName());
        }else{
            Log.e(TAG, "updateGameBundle: The GameBundle should have a Name.");
            return -1;
        }
        bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_PRICE, gameBundle.getPrice());
        return db.update(UserContract.BundleEntry.TABLE_NAME, bundle,
                UserContract.BundleEntry._ID + "=" + gameBundle.getId(), null);
    }

    /**
     * Remove a bundle from GameBundle Table.
     *
     * @param db       Database that we are working on.
     * @param gameBundle Bundle to remove.
     * @return True if the bundle is correctly removed.
     */
    public boolean removeGameBundle(SQLiteDatabase db, GameBundle gameBundle) {
        return db.delete(UserContract.BundleEntry.TABLE_NAME,
                UserContract.BundleEntry._ID + "=" + gameBundle.getId(), null) > 0;
    }

    /**
     * This function is creating a list of GameBundle from the cursor in parameter.
     * @param cursor containing all the GameBundles
     * @return a list of GameBundle from the cursor.
     */
    private List<GameBundle> createGameBundlesFromCursor(Cursor cursor){
        // We create an empty list to put our GameBundles inside
        List<GameBundle> gameBundles = new ArrayList<>();

        // We first have to be sure that there is information in the cursor
        if (cursor.getCount() != 0) {
            // There is information inside. We are going to create the GameBundle.

            // We move the cursor to the first position
            cursor.moveToFirst();
            // And create a loop to go trough all the positions
            while(!cursor.isAfterLast()){
                // We create a new GameBundle and put the information inside
                GameBundle currentGameBundle = new GameBundle();
                currentGameBundle.setId(cursor.getInt(cursor.getColumnIndex(UserContract.BundleEntry._ID)));
                currentGameBundle.setName(cursor.getString(
                        cursor.getColumnIndex(UserContract.BundleEntry.COLUMN_BUNDLE_NAME)));
                currentGameBundle.setPrice(cursor.getDouble(
                        cursor.getColumnIndex(UserContract.BundleEntry.COLUMN_BUNDLE_PRICE)));

                // We add the GameBundle in the list
                gameBundles.add(currentGameBundle);
                // And change the position of the cursor
                cursor.moveToNext();
            }

        }else{
            // Otherwise, we display a message in the log
            Log.d(TAG, "createGameBundlesFromCursor: There is no GameBundle in the cursor.");
        }
        cursor.close();
        // No that we have all the GameBundles, we return the list
        return gameBundles;
    }
}
