package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameBundle;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.User;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Joffrey on 14/02/2017.
 */

public class UserDbHelper extends SQLiteOpenHelper {

    /**
     * Version of the database.
     * This var must be incremented everytime you change the database schema.
     */
    public static final int DATABASE_VERSION = 5;
    /**
     * Name of the database.
     */
    public static final String DATABASE_NAME = "myGameTimePrice.db";
    private static UserDbHelper sInstance;

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized UserDbHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new UserDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // We create all of the tables
        createUserTable(db);
        createGameTable(db);
        createBundleTable(db);
        createOwnedGamesTable(db);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We alter all of the table

        alterUserTable(db);
        alterGameTable(db);
        alterBundleTable(db);
        alterOwnedGamesTable(db);

    }

    /********************************************************************************************
     * USER TABLE
     ********************************************************************************************/
    // TODO 4 Refactor this code to send user objects

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
     * Function returning all of the rows in User table.
     *
     * @param db Database to look into.
     * @return cursor Cursor containing all of the rows.
     */
    private Cursor getUsers(SQLiteDatabase db,
                            String where,
                            String whereArgs[],
                            String groupBy,
                            String having,
                            String order,
                            String limit) {

        Cursor cursor = db.query(UserContract.UserEntry.TABLE_NAME,
                null,
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
     * Function returning all of the rows in User table.
     *
     * @param db Database to look into.
     * @return cursor Cursor containing all of the rows.
     */
    public Cursor getAllUsers(SQLiteDatabase db) {
        return getUsers(db, null, null, null, null, null, null);
    }

    /**
     * Function returning row with the User _ID in parameter.
     *
     * @param db  Database to query.
     * @param _ID ID That we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getUserBy_ID(SQLiteDatabase db, String _ID) {
        String where = UserContract.UserEntry._ID + " = " + _ID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.UserEntry.TABLE_NAME,
                null,
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
     * Function returning row with the User steamID in parameter.
     *
     * @param db      Database to query.
     * @param steamID That we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public User getUserBySteamID(SQLiteDatabase db, long steamID) {
        // We have to do the request.
        String where = UserContract.UserEntry.COLUMN_STEAM_ID + " = " + steamID;
        Cursor cursorUser = getUsers(db, where, null, null, null, null, null);

        // We create a new user to return completed
        User userToReturn;

        // Now that we have the result, we have to check if there is a result.
        if (cursorUser.getCount() > 0) {
            // There is a result. We have to check if there is one or more rows.
            if (cursorUser.getCount() > 1) {
                // There is more than one, we log an error and then continue the operation
                // by using the first row.
                Log.e(TAG, "getUserBySteamID: There is more than one user in DB with the steamID "
                        + steamID + ". The first one will be returned!");
            }
            cursorUser.moveToFirst();
            // We can initiate the new user
            userToReturn = new User();
            // We create a new user and insert all the information needed inside
            userToReturn.setUserID(cursorUser.getInt(cursorUser.getColumnIndex(
                    UserContract.UserEntry._ID)));
            userToReturn.setSteamID(cursorUser.getLong(cursorUser.getColumnIndex(
                    UserContract.UserEntry.COLUMN_STEAM_ID)));
            userToReturn.setAccountName(cursorUser.getString(cursorUser.getColumnIndex(
                    UserContract.UserEntry.COLUMN_ACCOUNT_NAME)));
            URL accountPicture = null;
            try {
                accountPicture = new URL(cursorUser.getString(cursorUser.getColumnIndex(
                        UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE)));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            userToReturn.setAccountPicture(accountPicture);

            // TODO : Retrieve all of the user OwnedGame and add them in the user


            // TODO : Calculate the number of minutes played and add them in the user


        } else {
            // No result, we log an error and return a null user.
            Log.e(TAG, "getUserBySteamID: There is no user in DB with the steamID " + steamID);
            return null;
        }
        // We close the cursor
        cursorUser.close();

        // We return the user
        return userToReturn;
    }

    /**
     * Add a new user in the User table.
     *
     * @param db             Database that we are working on.
     * @param steamID        Column of the User table.
     * @param accountName    Column of the User table.
     * @param accountPicture Column of the User table.
     * @return Number of line inserted in database.
     */
    public long addNewUser(SQLiteDatabase db, String steamID, String accountName, String accountPicture) {
        ContentValues user = new ContentValues();
        user.put(UserContract.UserEntry.COLUMN_STEAM_ID, steamID);
        user.put(UserContract.UserEntry.COLUMN_ACCOUNT_NAME, accountName);
        user.put(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE, accountPicture);
        return db.insert(UserContract.UserEntry.TABLE_NAME, null, user);
    }

    /**
     * Update the user in the database based on his SteamID
     *
     * @param db             Database that we are working on.
     * @param steamID        Column of the User table.
     * @param accountName    Column of the User table.
     * @param accountPicture Column of the User table.
     * @return The number of rows updated.
     */
    public long updateUserBySteamID(SQLiteDatabase db, String steamID, String accountName, String accountPicture) {

        // We have to do the request.
        String where = UserContract.UserEntry.COLUMN_STEAM_ID + " = " + steamID;
        Cursor oldUser = getUsers(db, where, null, null, null, null, null);
        if (oldUser != null) {
            int _ID = oldUser.getInt(oldUser.getColumnIndex(UserContract.UserEntry._ID));

            ContentValues userUpdated = new ContentValues();
            userUpdated.put(UserContract.UserEntry.COLUMN_STEAM_ID, steamID);
            userUpdated.put(UserContract.UserEntry.COLUMN_ACCOUNT_NAME, accountName);
            userUpdated.put(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE, accountPicture);

            return db.update(UserContract.UserEntry.TABLE_NAME, userUpdated,
                    UserContract.UserEntry._ID + "=" + _ID, null);
        } else {
            return 0;
        }
    }

    /**
     * Remove a user from the User Table.
     *
     * @param db  Database that we are working on.
     * @param _ID Column of the User table.
     * @return True if the user is correctly removed.
     */
    public boolean removeUserBy_ID(SQLiteDatabase db, int _ID) {
        return db.delete(UserContract.UserEntry.TABLE_NAME,
                UserContract.UserEntry._ID + "=" + _ID, null) > 0;
    }

    /********************************************************************************************
     * GAME TABLE
     ********************************************************************************************/
    // TODO 2 Refactor this code to send bacl Game object

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
    public void alterGameTable(SQLiteDatabase db) {
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
     * Function returning all of the rows in Game table.
     *
     * @param db Database to look into.
     * @return cursor Cursor containing all of the rows.
     */
    public Cursor getAllGames(SQLiteDatabase db) {
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.GameEntry.TABLE_NAME,
                null,
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
     * Function returning row with the Game _ID in parameter.
     *
     * @param db  Database to query.
     * @param _ID ID That we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getGameBy_ID(SQLiteDatabase db, String _ID) {
        String where = UserContract.GameEntry._ID + " = " + _ID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.GameEntry.TABLE_NAME,
                null,
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
     * Function returning row with the Game steamID in parameter.
     *
     * @param db      Database to query.
     * @param steamID That we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getGameBySteamID(SQLiteDatabase db, String steamID) {
        String where = UserContract.GameEntry.COLUMN_STEAM_ID + " = " + steamID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.GameEntry.TABLE_NAME,
                null,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Add a new game in the Game table.
     *
     * @param db          Database that we are working on.
     * @param steamID     Column of the Game table.
     * @param gameName    Column of the Game table.
     * @param gameLogo    URL, column of the Game table.
     * @param gameIcon    URL, column of the Game table.
     * @param marketplace Column of the Game table.
     * @return Number of line inserted in database.
     */
    public long addNewGame(SQLiteDatabase db, String steamID, String gameName, String gameLogo,
                           String gameIcon, String marketplace) {
        ContentValues game = new ContentValues();
        game.put(UserContract.GameEntry.COLUMN_STEAM_ID, steamID);
        game.put(UserContract.GameEntry.COLUMN_GAME_NAME, gameName);
        game.put(UserContract.GameEntry.COLUMN_GAME_LOGO, gameLogo);
        game.put(UserContract.GameEntry.COLUMN_GAME_ICON, gameIcon);
        game.put(UserContract.GameEntry.COLUMN_MARKETPLACE, marketplace);
        return db.insert(UserContract.GameEntry.TABLE_NAME, null, game);
    }

    /**
     * Update the game in the database based on his ID.
     * If a value doesn't have to be update, put null in parameter.
     *
     * @param db          Database that we are working on.
     * @param steamID     Column of the Game table.
     * @param gameName    Column of the Game table.
     * @param gameLogo    URL, column of the Game table.
     * @param gameIcon    URL, column of the Game table.
     * @param marketplace Column of the Game table.
     * @return The number of rows updated.
     */
    public long updateGameByID(SQLiteDatabase db, String _ID, String steamID, String gameName, String gameLogo,
                               String gameIcon, String marketplace) {

        ContentValues game = new ContentValues();
        if (steamID != null) game.put(UserContract.GameEntry.COLUMN_STEAM_ID, steamID);
        if (gameName != null) game.put(UserContract.GameEntry.COLUMN_GAME_NAME, gameName);
        if (gameLogo != null) game.put(UserContract.GameEntry.COLUMN_GAME_LOGO, gameLogo);
        if (gameIcon != null) game.put(UserContract.GameEntry.COLUMN_GAME_ICON, gameIcon);
        if (marketplace != null) game.put(UserContract.GameEntry.COLUMN_MARKETPLACE, marketplace);

        return db.update(UserContract.GameEntry.TABLE_NAME, game,
                UserContract.GameEntry._ID + "=" + _ID, null);
    }

    /**
     * Update the game in the database based on his SteamID
     *
     * @param db          Database that we are working on.
     * @param steamID     Column of the Game table.
     * @param gameName    Column of the Game table.
     * @param gameLogo    URL, column of the Game table.
     * @param gameIcon    URL, column of the Game table.
     * @param marketplace Column of the Game table.
     * @return The number of rows updated.
     */
    public long updateGameBySteamID(SQLiteDatabase db, String steamID, String gameName, String gameLogo,
                                    String gameIcon, String marketplace) {

        Cursor oldGame = getGameBySteamID(db, steamID);
        if (oldGame != null) {
            int _ID = oldGame.getInt(oldGame.getColumnIndex(UserContract.UserEntry._ID));

            ContentValues game = new ContentValues();
            game.put(UserContract.GameEntry.COLUMN_STEAM_ID, steamID);
            game.put(UserContract.GameEntry.COLUMN_GAME_NAME, gameName);
            game.put(UserContract.GameEntry.COLUMN_GAME_LOGO, gameLogo);
            game.put(UserContract.GameEntry.COLUMN_GAME_ICON, gameIcon);
            game.put(UserContract.GameEntry.COLUMN_MARKETPLACE, marketplace);

            return db.update(UserContract.GameEntry.TABLE_NAME, game,
                    UserContract.GameEntry._ID + "=" + _ID, null);
        } else {
            return 0;
        }
    }

    /**
     * Remove a game from the Game Table.
     *
     * @param db  Database that we are working on.
     * @param _ID Column of the game table.
     * @return True if the user is correctly removed.
     */
    public boolean removeGameBy_ID(SQLiteDatabase db, int _ID) {
        return db.delete(UserContract.GameEntry.TABLE_NAME,
                UserContract.GameEntry._ID + "=" + _ID, null) > 0;
    }

    /********************************************************************************************
     * OWNEDGAMES TABLE
     ********************************************************************************************/
    // TODO 3 Refactor this code to send OwnedGame objects

    /**
     * Function that create the OwnedGames table in the db in parameter.
     *
     * @param db Database that we want to create OwnedGames table inside.
     */
    public void createOwnedGamesTable(SQLiteDatabase db) {
        final String SQL_CREATE_OWNEDGAMES_TABLE =
                "CREATE TABLE " + UserContract.OwnedGamesEntry.TABLE_NAME + " (" +
                        UserContract.OwnedGamesEntry.COLUMN_USER_ID + " BIGINT, " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_ID + " BIGINT, " +
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER + " INTEGER, " +
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS + " INTEGER, " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE + " DOUBLE, " +
                        UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " INTEGER, " +
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
     *
     * @param db Database that we want to alter OwnedGames table inside.
     */
    public void alterOwnedGamesTable(SQLiteDatabase db) {
        /**final String SQL_ALTER_OWNEDGAMES_TABLE = "";
         if(SQL_ALTER_OWNEDGAMES_TABLE.length()>0){
         db.execSQL(SQL_ALTER_OWNEDGAMES_TABLE);
         }**/
        /**String SQL_ALTER_OWNEDGAMES_TABLE = "UPDATE OWNEDGAMES SET gamePrice = '-1' WHERE gamePrice = '';";
         db.execSQL(SQL_ALTER_OWNEDGAMES_TABLE);
         SQL_ALTER_OWNEDGAMES_TABLE = "ALTER TABLE OWNEDGAMES RENAME TO OWNEDGAMES_Temp";
         db.execSQL(SQL_ALTER_OWNEDGAMES_TABLE);
         createOwnedGamesTable(db);
         SQL_ALTER_OWNEDGAMES_TABLE = "INSERT INTO OWNEDGAMES SELECT userID, gameID, timePlayedForever, timePlayed2Weeks, gamePrice FROM OWNEDGAMES_Temp;";
         db.execSQL(SQL_ALTER_OWNEDGAMES_TABLE);
         SQL_ALTER_OWNEDGAMES_TABLE = "DROP Table OWNEDGAMES_Temp";
         db.execSQL(SQL_ALTER_OWNEDGAMES_TABLE);**/
    }

    /**
     * Function that drop the OwnedGames table in the database in parameter.
     *
     * @param db Database that we want to drop OwnedGames table inside.
     */
    public void dropOwnedGamesTable(SQLiteDatabase db) {
        final String SQL_DELETE_OWNEDGAMES_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.OwnedGamesEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_OWNEDGAMES_TABLE);
    }

    /**
     * Function returning all of the rows in OwnedGames table.
     *
     * @param db Database to look into.
     * @return cursor Cursor containing all of the rows.
     */
    public Cursor getAllOwnedGames(SQLiteDatabase db) {
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.OwnedGamesEntry.TABLE_NAME,
                null,
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
     * Function returning rows with the userID in parameter.
     *
     * @param db     Database to query.
     * @param userID user that we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getOwnedGamesByUserID(SQLiteDatabase db, String userID) {
        String where = UserContract.OwnedGamesEntry.COLUMN_USER_ID + " = " + userID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.OwnedGamesEntry.TABLE_NAME,
                null,
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
     * Function returning rows with the bundleID in parameter.
     *
     * @param db       Database to query.
     * @param bundleID user that we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getOwnedGamesByBundleID(SQLiteDatabase db, String bundleID) {
        String where = UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " = " + bundleID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.OwnedGamesEntry.TABLE_NAME,
                null,
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
     * Function returning row with the Game steamID in parameter.
     *
     * @param db     Database to query.
     * @param gameID That we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getOwnedGame(SQLiteDatabase db, String userID, String gameID) {
        String where = UserContract.OwnedGamesEntry.COLUMN_USER_ID + " = " + userID
                + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + " = " + gameID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.OwnedGamesEntry.TABLE_NAME,
                null,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Add a new game in the OwnedGame table.
     *
     * @param db                Database that we are working on.
     * @param userID            Column of the OwnedGame table.
     * @param gameID            Column of the OwnedGame table.
     * @param timePlayedForever Column of the OwnedGame table.
     * @param timePlayed2Weeks  Column of the OwnedGame table.
     * @param gamePrice         Column of the OwnedGame table.
     * @return Number of line inserted in database.
     */
    public long addNewOwnedGame(SQLiteDatabase db, String userID, String gameID,
                                String timePlayedForever, String timePlayed2Weeks,
                                String gamePrice) {
        ContentValues ownedGame = new ContentValues();
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, userID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID, gameID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER, timePlayedForever);
        if (gamePrice != null) {
            ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, gamePrice);
        } else {
            ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, "-1.00");
        }
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS, timePlayed2Weeks);
        return db.insert(UserContract.OwnedGamesEntry.TABLE_NAME, null, ownedGame);
    }

    /**
     * Update the game in the database based on his SteamID
     *
     * @param db                Database that we are working on.
     * @param userID            Column of the OwnedGame table.
     * @param gameID            Column of the OwnedGame table.
     * @param timePlayedForever Column of the OwnedGame table.
     * @param timePlayed2Weeks  Column of the OwnedGame table.
     * @param gamePrice         Column of the OwnedGame table.
     * @return The number of rows updated.
     */
    public long updateOwnedGame(SQLiteDatabase db, String userID, String gameID,
                                String timePlayedForever, String timePlayed2Weeks,
                                String gamePrice, String bundleID) {

        ContentValues ownedGame = new ContentValues();
        if (userID != null) ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, userID);
        if (gameID != null) ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID, gameID);
        if (timePlayedForever != null)
            ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER, timePlayedForever);
        if (timePlayed2Weeks != null)
            ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS, timePlayed2Weeks);
        if (gamePrice != null)
            ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, gamePrice);
        if (bundleID != null) {
            if (bundleID.equals("-1")) {
                bundleID = null;
                ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID, bundleID);
            }
            ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID, bundleID);
        }
        return db.update(UserContract.OwnedGamesEntry.TABLE_NAME, ownedGame,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + userID
                        + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" + gameID, null);
    }

    /**
     * Update the game in the database based on his SteamID
     *
     * @param db        Database that we are working on.
     * @param userID    Column of the OwnedGame table.
     * @param gameID    Column of the OwnedGame table.
     * @param gamePrice Column of the OwnedGame table.
     * @return The number of rows updated.
     */
    public long updateOwnedGamePrice(SQLiteDatabase db, String userID, String gameID,
                                     String gamePrice) {

        ContentValues ownedGame = new ContentValues();
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, userID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID, gameID);
        if (gamePrice != null) {
            ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, gamePrice);
        }
        return db.update(UserContract.OwnedGamesEntry.TABLE_NAME, ownedGame,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + userID
                        + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" + gameID, null);
    }

    /**
     * Function used to update the price of the games included in a GameBundle.
     *
     * @param db         database to work on
     * @param bundleName Name of the bundle.
     * @return Number of game prices updated.
     */
    public long updateOnwedGamePriceFromBundle(SQLiteDatabase db, String bundleID) {
        // First, we need to get the bundle price
        double bundlePrice = 0;
        Cursor cursor = this.getBundleByID(db, bundleID);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            bundlePrice = cursor.getDouble(
                    cursor.getColumnIndex(UserContract.BundleEntry.COLUMN_BUNDLE_PRICE));
        }

        // Now that we have all the information, we have to find the number of game that are currently
        // in the bundle
        cursor = this.getOwnedGamesByBundleID(db, String.valueOf(bundleID));

        if (cursor.getCount() != 0) {
            // We divide the bundle price by the number of games inside
            double nbGames = Double.valueOf(cursor.getCount());
            double pricePerGame = bundlePrice / nbGames;
            DecimalFormat df = new DecimalFormat("#.##");
            pricePerGame = Double.valueOf(df.format(pricePerGame));
            while (cursor.isAfterLast() == false) {

                // We update each ownedgame with the price calculated
                this.updateOwnedGamePrice(db,
                        cursor.getString(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(UserContract.OwnedGamesEntry.COLUMN_GAME_ID)),
                        String.valueOf(pricePerGame));
                cursor.moveToNext();
            }
        }
        return cursor.getCount();
    }

    /**
     * Remove a OwnedGame from OwnedGame Table.
     *
     * @param db     Database that we are working on.
     * @param userID Column of the game table.
     * @param gameID Column of the game table.
     * @return True if the user is correctly removed.
     */
    public boolean removeOwnedGame(SQLiteDatabase db, String userID, String gameID) {
        return db.delete(UserContract.OwnedGamesEntry.TABLE_NAME,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + userID
                        + " AND " + UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" + gameID
                , null) > 0;
    }

    /********************************************************************************************
     * BUNDLE TABLE
     ********************************************************************************************/
    // TODO 1 Refactor this code to send object with bundle type

    /**
     * Function that create the GameBundle table in the db in parameter.
     *
     * @param db Database that we want to create OwnedGames table inside.
     */
    public void createBundleTable(SQLiteDatabase db) {
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
    public void alterBundleTable(SQLiteDatabase db) {
        final String SQL_ALTER_BUNDLE_TABLE = "";
        if (SQL_ALTER_BUNDLE_TABLE.length() > 0) {
            db.execSQL(SQL_ALTER_BUNDLE_TABLE);
        }
    }

    /**
     * Function that drop the GameBundle table in the database in parameter.
     *
     * @param db Database that we want to drop OwnedGames table inside.
     */
    public void dropBundleTable(SQLiteDatabase db) {
        final String SQL_DELETE_BUNDLE_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.BundleEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_BUNDLE_TABLE);
    }

    // TODO 1.3 Refactor this method to send back an ArrayList of Bundles

    /**
     * Function doing the request in database and returning the result.
     * @param db
     * @param columns
     * @param where
     * @param whereArgs
     * @param groupBy
     * @param having
     * @param order
     * @param limit
     * @return Cursor result
     */
    private Cursor selectBundle(SQLiteDatabase db,
                                String[] columns,
                                String where,
                                String whereArgs[],
                                String groupBy,
                                String having,
                                String order,
                                String limit) {
        Cursor cursor = db.query(UserContract.BundleEntry.TABLE_NAME,
                columns,
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
    public List<GameBundle> getAllBundles(SQLiteDatabase db) {

        // List that we will return.
        List<GameBundle> bundleList = new ArrayList<GameBundle>();

        // We have to do a query in DB to have all the rows.
        Cursor cursorBundles = selectBundle(db, null, null, null, null, null, null, null);

        // We have to check if there is results.
        if(cursorBundles.getCount() > 0){
            // There is bundles. For each of them, we will create a bundle and add it in the list
            cursorBundles.moveToFirst();
            while (!cursorBundles.isAfterLast()){
                GameBundle currentBundle = new GameBundle();
                currentBundle.setId(cursorBundles.getInt(cursorBundles.getColumnIndex(
                        UserContract.BundleEntry._ID)));
                currentBundle.setName(cursorBundles.getString(cursorBundles.getColumnIndex(
                        UserContract.BundleEntry.COLUMN_BUNDLE_NAME)));
                currentBundle.setPrice(cursorBundles.getDouble(cursorBundles.getColumnIndex(
                        UserContract.BundleEntry.COLUMN_BUNDLE_PRICE)));
                bundleList.add(currentBundle);
                cursorBundles.moveToNext();
            }

        }else{
            // There is no bundle in database. We log an error.
            Log.e(TAG, "getAllBundles: There is no Bundle in DB.");
        }
        cursorBundles.close();

        return bundleList;
    }

    // TODO 1.4 Refactor this method to send back a bundles

    /**
     * Function returning row with the bundle ID in parameter.
     *
     * @param db       Database to query.
     * @param bundleID That we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getBundleByID(SQLiteDatabase db, String bundleID) {
        String where = UserContract.BundleEntry._ID + " = " + bundleID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.BundleEntry.TABLE_NAME,
                null,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // TODO 1.5

    /**
     * Function returning row with the bundle name and the userID in parameter.
     *
     * @param db         Database to query.
     * @param bundleName That we want to find.
     * @param userID     ID of the user who own the bundle
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getBundleByName(SQLiteDatabase db, String bundleName, String userID) {
        String where = UserContract.BundleEntry.COLUMN_BUNDLE_NAME + " = '" + bundleName
                + "' AND " + UserContract.OwnedGamesEntry.COLUMN_USER_ID + " = " + userID +
                " AND " + UserContract.OwnedGamesEntry.TABLE_NAME + "." + UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " = " +
                UserContract.BundleEntry.TABLE_NAME + "." + UserContract.BundleEntry._ID;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(UserContract.BundleEntry.TABLE_NAME + "," + UserContract.OwnedGamesEntry.TABLE_NAME,
                null,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // TODO 1.6

    /**
     * Function returning the list of bundles owned by the user.
     *
     * @param db     Database to query.
     * @param userID ID of the user who own the bundle
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getUserBundles(SQLiteDatabase db, String userID) {
        String where = UserContract.OwnedGamesEntry.COLUMN_USER_ID + " = " + userID +
                " AND " + UserContract.OwnedGamesEntry.TABLE_NAME + "." + UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " = " +
                UserContract.BundleEntry.TABLE_NAME + "." + UserContract.BundleEntry._ID;
        String columns[] = {
                UserContract.BundleEntry._ID,
                UserContract.BundleEntry.COLUMN_BUNDLE_NAME,
                UserContract.BundleEntry.COLUMN_BUNDLE_PRICE,
        };
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String limit = null;

        Cursor cursor = db.query(true,
                UserContract.BundleEntry.TABLE_NAME + "," + UserContract.OwnedGamesEntry.TABLE_NAME,
                columns,
                where,
                whereArgs,
                groupBy,
                having,
                order,
                limit);
        /**String rawQuery = "SELECT * FROM " + UserContract.BundleEntry.TABLE_NAME +", "+ UserContract.OwnedGamesEntry.TABLE_NAME +
         " WHERE " + UserContract.OwnedGamesEntry.COLUMN_USER_ID + " = " + userID +
         " AND " + UserContract.OwnedGamesEntry.TABLE_NAME +"." + UserContract.OwnedGamesEntry.COLUMN_BUNDLE_ID + " = " +
         UserContract.BundleEntry.TABLE_NAME +"."+ UserContract.BundleEntry._ID;
         Cursor cursor = db.rawQuery(rawQuery, null);**/

        if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // TODO 1.7 Put a GameBundle in parameter and send back a bundle

    /**
     * Add a new bundle in GameBundle table.
     *
     * @param db          Database that we are working on.
     * @param bundleName  Column of the GameBundle table.
     * @param bundlePrice Column of the GameBundle table.
     * @return Number of line inserted in database.
     */
    public long addNewBundle(SQLiteDatabase db, String bundleName, String bundlePrice) {
        ContentValues bundle = new ContentValues();
        bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_NAME, bundleName);
        bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_PRICE, bundlePrice);
        return db.insert(UserContract.BundleEntry.TABLE_NAME, null, bundle);
    }

    // TODO 1.8 Put a GameBundle in parameter and send back a bundle

    /**
     * Update the bundle based on his GameBundle ID.
     *
     * @param db          Database that we are working on.
     * @param bundleName  Column of the GameBundle table.
     * @param bundlePrice Column of the GameBundle table.
     * @return Number of line inserted in database.
     */
    public long updateBundle(SQLiteDatabase db, String bundleID, String bundleName, String bundlePrice) {
        ContentValues bundle = new ContentValues();
        if (bundleName != null) bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_NAME, bundleName);
        if (bundlePrice != null)
            bundle.put(UserContract.BundleEntry.COLUMN_BUNDLE_PRICE, bundlePrice);
        return db.update(UserContract.BundleEntry.TABLE_NAME, bundle,
                UserContract.BundleEntry._ID + "=" + bundleID, null);
    }

    // TODO 1.9 Put a GameBundle in parameter and send back a boolean

    /**
     * Remove a bundle from GameBundle Table.
     *
     * @param db       Database that we are working on.
     * @param bundleID Column of the bundle table.
     * @return True if the user is correctly removed.
     */
    public boolean removeBundle(SQLiteDatabase db, String bundleID) {
        return db.delete(UserContract.BundleEntry.TABLE_NAME,
                UserContract.BundleEntry._ID + "=" + bundleID, null) > 0;
    }
}
