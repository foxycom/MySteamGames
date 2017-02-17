package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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


    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        alterOwnedGamesTable(db);

        dropOwnedGamesTable(db);
        dropUserTable(db);
        dropGameTable(db);
        onCreate(db);
    }

    /********************************************************************************************
     * USER TABLE
     ********************************************************************************************/

    /**
     * Function that create the User table in the db in parameter.
     * @param db Database that we want to create User table inside.
     */
    public void createUserTable(SQLiteDatabase db){
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
     * @param db Database that we want to alter User table inside.
     */
    public void alterUserTable(SQLiteDatabase db){
        final String SQL_ALTER_USER_TABLE = "";
        if(SQL_ALTER_USER_TABLE.length()>0){
            db.execSQL(SQL_ALTER_USER_TABLE);
        }
    }

    /**
     * Function that drop the User table in the database in parameter.
     * @param db Database that we want to drop User table inside.
     */
    public void dropUserTable(SQLiteDatabase db){
        final String SQL_DELETE_USER_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_USER_TABLE);
    }

    /**
     * Function returning all of the rows in User table.
     * @param db Database to look into.
     * @return cursor Cursor containing all of the rows.
     */
    public Cursor getAllUsers(SQLiteDatabase db){
        String where = null;
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
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning row with the User _ID in parameter.
     * @param db Database to query.
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
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning row with the User steamID in parameter.
     * @param db Database to query.
     * @param steamID That we want to find.
     * @return Cursor containing the rows matching the request.
     */
    public Cursor getUserBySteamID(SQLiteDatabase db, String steamID) {
        String where = UserContract.UserEntry.COLUMN_STEAM_ID + " = " + steamID;
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
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Add a new user in the User table.
     * @param db Database that we are working on.
     * @param steamID Column of the User table.
     * @param accountName Column of the User table.
     * @param accountPicture Column of the User table.
     * @return Number of line inserted in database.
     */
    public long addNewUser(SQLiteDatabase db, String steamID, String accountName, String accountPicture){
        ContentValues user = new ContentValues();
        user.put(UserContract.UserEntry.COLUMN_STEAM_ID, steamID);
        user.put(UserContract.UserEntry.COLUMN_ACCOUNT_NAME, accountName);
        user.put(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE, accountPicture);
        return db.insert(UserContract.UserEntry.TABLE_NAME, null, user);
    }

    /**
     * Update the user in the database based on his SteamID
     * @param db Database that we are working on.
     * @param steamID Column of the User table.
     * @param accountName Column of the User table.
     * @param accountPicture Column of the User table.
     * @return The number of rows updated.
     */
    public long updateUserBySteamID(SQLiteDatabase db, String steamID, String accountName, String accountPicture){

        Cursor oldUser = getUserBySteamID(db, steamID);
        if(oldUser != null){
        int _ID = oldUser.getInt(oldUser.getColumnIndex(UserContract.UserEntry._ID));

        ContentValues userUpdated = new ContentValues();
        userUpdated.put(UserContract.UserEntry.COLUMN_STEAM_ID, steamID);
        userUpdated.put(UserContract.UserEntry.COLUMN_ACCOUNT_NAME, accountName);
        userUpdated.put(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE, accountPicture);

        return db.update(UserContract.UserEntry.TABLE_NAME, userUpdated,
                UserContract.UserEntry._ID + "=" + _ID, null);
        }else{
            return 0;
        }
    }

    /**
     * Remove a user from the User Table.
     * @param db Database that we are working on.
     * @param _ID Column of the User table.
     * @return True if the user is correctly removed.
     */
    public boolean removeUserBy_ID(SQLiteDatabase db, int _ID){
        return db.delete(UserContract.UserEntry.TABLE_NAME,
                UserContract.UserEntry._ID + "=" + _ID, null) > 0;
    }

    /********************************************************************************************
     * GAME TABLE
     ********************************************************************************************/

    /**
     * Function that create the Game table in the db in parameter.
     * @param db Database that we want to create Game table inside.
     */
    public void createGameTable(SQLiteDatabase db){
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
     * @param db Database that we want to alter Game table inside.
     */
    public void alterGameTable(SQLiteDatabase db){
        final String SQL_ALTER_GAME_TABLE = "";
        if(SQL_ALTER_GAME_TABLE.length()>0){
            db.execSQL(SQL_ALTER_GAME_TABLE);
        }
    }

    /**
     * Function that drop the Game table in the database in parameter.
     * @param db Database that we want to drop Game table inside.
     */
    public void dropGameTable(SQLiteDatabase db){
        final String SQL_DELETE_GAME_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.GameEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_GAME_TABLE);
    }

    /**
     * Function returning all of the rows in Game table.
     * @param db Database to look into.
     * @return cursor Cursor containing all of the rows.
     */
    public Cursor getAllGames(SQLiteDatabase db){
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
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning row with the Game _ID in parameter.
     * @param db Database to query.
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
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning row with the Game steamID in parameter.
     * @param db Database to query.
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
        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Add a new game in the Game table.
     * @param db Database that we are working on.
     * @param steamID Column of the Game table.
     * @param gameName Column of the Game table.
     * @param gameLogo URL, column of the Game table.
     * @param gameIcon URL, column of the Game table.
     * @param marketplace Column of the Game table.
     * @return Number of line inserted in database.
     */
    public long addNewGame(SQLiteDatabase db, String steamID, String gameName, String gameLogo,
                           String gameIcon, String marketplace){
        ContentValues game = new ContentValues();
        game.put(UserContract.GameEntry.COLUMN_STEAM_ID, steamID);
        game.put(UserContract.GameEntry.COLUMN_GAME_NAME, gameName);
        game.put(UserContract.GameEntry.COLUMN_GAME_LOGO, gameLogo);
        game.put(UserContract.GameEntry.COLUMN_GAME_ICON, gameIcon);
        game.put(UserContract.GameEntry.COLUMN_MARKETPLACE, marketplace);
        return db.insert(UserContract.GameEntry.TABLE_NAME, null, game);
    }

    /**
     * Update the game in the database based on his SteamID
     * @param db Database that we are working on.
     * @param steamID Column of the Game table.
     * @param gameName Column of the Game table.
     * @param gameLogo URL, column of the Game table.
     * @param gameIcon URL, column of the Game table.
     * @param marketplace Column of the Game table.
     * @return The number of rows updated.
     */
    public long updateGameBySteamID(SQLiteDatabase db, String steamID, String gameName, String gameLogo,
                                    String gameIcon, String marketplace){

        Cursor oldGame = getGameBySteamID(db, steamID);
        if(oldGame != null){
            int _ID = oldGame.getInt(oldGame.getColumnIndex(UserContract.UserEntry._ID));

            ContentValues game = new ContentValues();
            game.put(UserContract.GameEntry.COLUMN_STEAM_ID, steamID);
            game.put(UserContract.GameEntry.COLUMN_GAME_NAME, gameName);
            game.put(UserContract.GameEntry.COLUMN_GAME_LOGO, gameLogo);
            game.put(UserContract.GameEntry.COLUMN_GAME_ICON, gameIcon);
            game.put(UserContract.GameEntry.COLUMN_MARKETPLACE, marketplace);

            return db.update(UserContract.GameEntry.TABLE_NAME, game,
                    UserContract.GameEntry._ID + "=" + _ID, null);
        }else{
            return 0;
        }
    }

    /**
     * Remove a game from the Game Table.
     * @param db Database that we are working on.
     * @param _ID Column of the game table.
     * @return True if the user is correctly removed.
     */
    public boolean removeGameBy_ID(SQLiteDatabase db, int _ID){
        return db.delete(UserContract.GameEntry.TABLE_NAME,
                UserContract.GameEntry._ID + "=" + _ID, null) > 0;
    }

    /********************************************************************************************
     * OWNEDGAMES TABLE
     ********************************************************************************************/

    /**
     * Function that create the OwnedGames table in the db in parameter.
     * @param db Database that we want to create OwnedGames table inside.
     */
    public void createOwnedGamesTable(SQLiteDatabase db){
        final String SQL_CREATE_OWNEDGAMES_TABLE =
                "CREATE TABLE " + UserContract.OwnedGamesEntry.TABLE_NAME + " (" +
                        UserContract.OwnedGamesEntry.COLUMN_USER_ID + " BIGINT, " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_ID + " BIGINT, " +
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER + " INTEGER, " +
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS + " INTEGER, " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE + " DOUBLE, " +
                        UserContract.GameEntry.COLUMN_MARKETPLACE + " VARCHAR(50), " +
                        "PRIMARY KEY(" + UserContract.OwnedGamesEntry.COLUMN_USER_ID + ", " +
                        UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "), FOREIGN KEY (" +
                        UserContract.OwnedGamesEntry.COLUMN_USER_ID +") REFERENCES " +
                        UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID +
                        "), FOREIGN KEY (" + UserContract.OwnedGamesEntry.COLUMN_GAME_ID +
                        ") REFERENCES " +
                        UserContract.GameEntry.TABLE_NAME +"(" + UserContract.GameEntry._ID +
                        "));";
        db.execSQL(SQL_CREATE_OWNEDGAMES_TABLE);
    }

    /**
     * Function that alter the OwnedGames table in the db in parameter.
     * @param db Database that we want to alter OwnedGames table inside.
     */
    public void alterOwnedGamesTable(SQLiteDatabase db){
        final String SQL_ALTER_OWNEDGAMES_TABLE = "";
        if(SQL_ALTER_OWNEDGAMES_TABLE.length()>0){
            db.execSQL(SQL_ALTER_OWNEDGAMES_TABLE);
        }
    }

    /**
     * Function that drop the OwnedGames table in the database in parameter.
     * @param db Database that we want to drop OwnedGames table inside.
     */
    public void dropOwnedGamesTable(SQLiteDatabase db){
        final String SQL_DELETE_OWNEDGAMES_TABLE =
                "DROP TABLE IF EXISTS " + UserContract.OwnedGamesEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_OWNEDGAMES_TABLE);
    }

    /**
     * Function returning all of the rows in OwnedGames table.
     * @param db Database to look into.
     * @return cursor Cursor containing all of the rows.
     */
    public Cursor getAllOwnedGames(SQLiteDatabase db){
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
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning rows with the userID in parameter.
     * @param db Database to query.
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
        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Function returning row with the Game steamID in parameter.
     * @param db Database to query.
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
        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Add a new game in the OwnedGame table.
     * @param db Database that we are working on.
     * @param userID Column of the OwnedGame table.
     * @param gameID Column of the OwnedGame table.
     * @param timePlayedForever Column of the OwnedGame table.
     * @param timePlayed2Weeks Column of the OwnedGame table.
     * @param gamePrice Column of the OwnedGame table.
     * @return Number of line inserted in database.
     */
    public long addNewOwnedGame(SQLiteDatabase db, String userID, String gameID,
                                String timePlayedForever, String timePlayed2Weeks,
                                String gamePrice){
        ContentValues ownedGame = new ContentValues();
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, userID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID, gameID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER, timePlayedForever);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS, timePlayed2Weeks);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, gamePrice);
        return db.insert(UserContract.OwnedGamesEntry.TABLE_NAME, null, ownedGame);
    }

    /**
     * Update the game in the database based on his SteamID
     * @param db Database that we are working on.
     * @param userID Column of the OwnedGame table.
     * @param gameID Column of the OwnedGame table.
     * @param timePlayedForever Column of the OwnedGame table.
     * @param timePlayed2Weeks Column of the OwnedGame table.
     * @param gamePrice Column of the OwnedGame table.
     * @return The number of rows updated.
     */
    public long updateOwnedGame(SQLiteDatabase db, String userID, String gameID,
                                    String timePlayedForever, String timePlayed2Weeks,
                                    String gamePrice){

        ContentValues ownedGame = new ContentValues();
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, userID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID, gameID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER, timePlayedForever);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS, timePlayed2Weeks);
        if(!gamePrice.equals("")){
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, gamePrice);
        }

            return db.update(UserContract.OwnedGamesEntry.TABLE_NAME, ownedGame,
                    UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + userID
                    + " AND "+ UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" + gameID, null);
    }

    /**
     * Update the game in the database based on his SteamID
     * @param db Database that we are working on.
     * @param userID Column of the OwnedGame table.
     * @param gameID Column of the OwnedGame table.
     * @param gamePrice Column of the OwnedGame table.
     * @return The number of rows updated.
     */
    public long updateOwnedGamePrice(SQLiteDatabase db, String userID, String gameID,
                                String gamePrice){

        ContentValues ownedGame = new ContentValues();
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_USER_ID, userID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_ID, gameID);
        ownedGame.put(UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE, gamePrice);

        return db.update(UserContract.OwnedGamesEntry.TABLE_NAME, ownedGame,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + userID
                        + " AND "+ UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" + gameID, null);
    }

    /**
     * Remove a OwnedGame from OwnedGame Table.
     * @param db Database that we are working on.
     * @param userID Column of the game table.
     * @param gameID Column of the game table.
     * @return True if the user is correctly removed.
     */
    public boolean removeOwnedGame(SQLiteDatabase db, String userID, String gameID){
        return db.delete(UserContract.OwnedGamesEntry.TABLE_NAME,
                UserContract.OwnedGamesEntry.COLUMN_USER_ID + "=" + userID
                        + " AND "+ UserContract.OwnedGamesEntry.COLUMN_GAME_ID + "=" + gameID
                , null) > 0;
    }
}
